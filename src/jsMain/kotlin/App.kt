import react.*
import react.dom.*
import react.router.dom.*

import kotlinext.js.*
import kotlinx.html.js.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer

import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*

import kotlinx.browser.window
import kotlinx.browser.document

import HomeData

val client = HttpClient {
    install(WebSockets)
    install(JsonFeature) { serializer = KotlinxSerializer() }
}

val scope = MainScope()
val endpoint = window.location.origin // only needed until https://github.com/ktorio/ktor/issues/1695 is resolved
val messageList = ArrayList<ChatMessage>()

external interface ChatProps : RProps {
    val chatId: String
}

val Chat = functionalComponent<RProps> { _ ->
    val chatId = useParams<ChatProps>()?.chatId ?: return@functionalComponent

    val (messageCount, setMessageCount) = useState(0)
    val (connected, setConnected) = useState(false)
    val (socket, setSocket) = useState<Deferred<DefaultClientWebSocketSession>?>(null)

    useEffect(dependencies = listOf()) {
        if (socket == null)
            setSocket(scope.async { 
                client.webSocketSession(HttpMethod.Get, "localhost", 9090, "chat/$chatId/ws") 
            })
    }

    h1 { +"Chat Room" }

    ul {
        messageList.forEach { item -> 
            li { +"${item.time} [${item.author}] ${item.content}" }
        }
    }

    +"Chat: "
    child(
        InputComponent,
        props = jsObject {
            onSubmit = { input ->
                scope.launch { socket!!.await().send(Frame.Text(input)) }
            }
        }
    )

    if (socket != null)
        scope.launch {
            try {
                val frame = scope.async { socket.await().incoming.receive() }.await()
                if (frame is Frame.Text) {
                    val msg: ChatMessage = Json.decodeFromString(frame.readText())
                    messageList.add(msg)
                    setMessageCount(messageCount + 1)
                }
            } catch (e: Throwable) { // reconnect
                delay(5000)
                setSocket(scope.async { 
                    messageList.clear()
                    client.webSocketSession(HttpMethod.Get, "localhost", 9090, "chat/$chatId/ws") 
                })
            }
        }
}

val Home = functionalComponent<RProps> { _ ->
    val (username, setUsername) = useState("")
    val (chats, setChats) = useState(emptyList<ChatEntry>())

    useEffect(dependencies = listOf()) {
        scope.launch {
            val ret: HomeData = client.get(endpoint + "/home")
            setUsername(ret.username)
            setChats(ret.chats)
        }
    }
    ul {
        chats.forEach { 
            li {
                routeLink("/chat/${it.id}") { +"${it.name}" }
            }
        }
    }

    switch {
        route("/chat/:chatId") { child(Chat) }
    }
}

val Logout = functionalComponent<RProps> { _ ->
    scope.launch{
        client.get<Unit>(endpoint + "/logout")
    }
    window.close()
}

val App = functionalComponent<RProps> { _ ->
    val (username, setUsername) = useState("")
    val (chats, setChats) = useState(emptyList<ChatEntry>())
    val (queries, setQueries) = useState(0)

    browserRouter {
        div {
            ul {
                li {
                    routeLink("/") { +"Home" }
                }
                li {
                    routeLink("/logout") { +"Logout" }
                }
            }
        }

        useEffect(dependencies = listOf(queries)) {
            scope.launch {
                val ret: HomeData = client.get(endpoint + "/home")
                setUsername(ret.username)
                setChats(ret.chats)
            }
        }

        +"Search user to chat with: "
        child(
            InputComponent,
            props = jsObject {
                onSubmit = { input ->
                    scope.launch { 
                        client.post<Unit>(endpoint + "/search/$input")
                        setQueries(queries + 1)
                    }
                }
            }
        )

        ul {
            chats.forEach { 
                li {
                    routeLink("/chat/${it.id}") { +"${it.name}" }
                }
            }
        }

        switch {
            route("/chat/:chatId") { child(Chat) }
            route("/logout") { child(Logout) }
        }
    }
}
