import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.reactivestreams.KMongo
import com.mongodb.ConnectionString
import io.ktor.http.cio.websocket.*
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.websocket.*
import kotlinx.coroutines.channels.*
import kotlinx.html.*
import java.time.*
import io.ktor.http.content.*
import io.ktor.sessions.*
import io.ktor.util.*
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
import kotlin.collections.set
import java.security.MessageDigest

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.html.respondHtml
import io.ktor.response.respondRedirect
import io.ktor.routing.*
import io.ktor.sessions.clear
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import kotlinx.html.*

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.*
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.*
import java.time.*
import io.ktor.http.content.*
import io.ktor.sessions.*
import io.ktor.util.*
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
import kotlin.collections.set

import HomeData
import User
// import Util
import Convo
import ChatServer
// import Constants

val server = ChatServer()
val users: UserDatabase = UserDatabaseImpl()

data class ChatSession(val id: String)

val convos = HashMap<String, Convo>()

fun main() {
    embeddedServer(Netty, 9090) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }
        install(DefaultHeaders)
        install(CallLogging)
        install(WebSockets) {
            pingPeriod = Duration.ofMinutes(1)
        }
        install(Sessions) {
            configureCookies()
        }
        install(Authentication) {
            configureSessionAuth()
            configureFormAuth()
        }
        intercept(ApplicationCallPipeline.Features) {
            if (call.sessions.get<ChatSession>() == null)
                call.sessions.set(ChatSession(generateNonce()))
        }

        routing {
            homepageRoute()
            loginRoute()
            logoutRoute()
            profileRoute()
            websocketRoute()
            authenticate(AuthName.SESSION) {
                get("/home") {
                    val username = call.sessions.get<UserIdPrincipal>()!!.name
                    val user = users.getByUsername(username)!!

                    var chatEntries = user.chats.map { ChatEntry(it.id, it.name) }
                    call.respond(HomeData(user.username, chatEntries))
                }
                post("/search/{query}") {
                    val query = call.parameters["query"]
                    println("got query $query")

                    if (query != null) {
                        val username = call.sessions.get<UserIdPrincipal>()!!.name
                        val user = users.getByUsername(username)!!
                        val other = users.getByUsername(query)

                        if (other != null) {
                            val convo = Convo(generateNonce(), "$username and ${other.username}", mutableListOf(user, other))
                            user.chats.add(convo)
                            other.chats.add(convo)
                            call.respond(true)

                        }
                        else
                            call.respond(false)
                    }
                    else
                        call.respond(false)
                }
            }
            static("/") {
                resources("")
            }
        }
    }.start(wait = true)
}

private fun Sessions.Configuration.configureCookies() {
    cookie<ChatSession>("SESSION") {
        cookie.path = "/"
        cookie.extensions["SameSite"] = "lax"
    }

    cookie<UserIdPrincipal>(
        // We set a cookie by this name upon login.
        Cookies.AUTH_COOKIE,
        // Stores session contents in memory...good for development only.
        storage = SessionStorageMemory()
    ) {
        cookie.path = "/"
        // CSRF protection in modern browsers. Make sure your important side-effect-y operations, like ordering,
        // uploads, and changing settings, use "unsafe" HTTP verbs like POST and PUT, not GET or HEAD.
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Cookies#SameSite_cookies
        cookie.extensions["SameSite"] = "lax"
    }
}

private fun Authentication.Configuration.configureFormAuth() {
    form(AuthName.FORM) {
        userParamName = FormFields.USERNAME
        passwordParamName = FormFields.PASSWORD
        validate { cred: UserPasswordCredential ->

            val user = users.getByEmail(cred.name) ?: users.getByUsername(cred.name)
            val hash = cred.password.sha256()
            
            if (user != null && user.hash == hash)
                UserIdPrincipal(user.username) 
            else
                null
        }
        challenge {
            // I don't think form auth supports multiple errors, but we're conservatively assuming there will be at
            // most one error, which we handle here. Worst case, we just send the user to login with no context.
            val errors: Map<Any, AuthenticationFailedCause> = call.authentication.errors
            when (errors.values.singleOrNull()) {
                AuthenticationFailedCause.InvalidCredentials ->
                    call.respondRedirect("${CommonRoutes.LOGIN}?invalid")

                AuthenticationFailedCause.NoCredentials ->
                    call.respondRedirect("${CommonRoutes.LOGIN}?no")

                else ->
                    call.respondRedirect(CommonRoutes.LOGIN)
            }
        }
    }
}

private fun Authentication.Configuration.configureSessionAuth() {
    session<UserIdPrincipal>(AuthName.SESSION) {
        validate { session: UserIdPrincipal ->
            // If you need to do additional validation on session data, you can do so here.
            session
        }
        challenge {
            // What to do if the user isn't authenticated
            call.respondRedirect("${CommonRoutes.LOGIN}?no")
        }
    }
}

internal fun Routing.websocketRoute() {
    webSocket("/chat/{id}/ws") {
        val session = call.sessions.get<ChatSession>()
        val username = call.sessions.get<UserIdPrincipal>()!!.name
        val chatId = call.parameters["id"] ?: error("Invalid chat request")

        System.err.println("server attempted to open websocket")

        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }

        // open websocket
        server.memberJoin(session.id, this, username)
        System.err.println("server opened websocket ${session.id}")

        try {
            incoming.consumeEach { frame -> 

                if (frame is Frame.Text) {
                    // receive message
                    server.message(frame.readText(), session.id)
                    // val text = frame.readText();
                    // val author = 
                    System.err.println("server got message websocket ${session.id}, msg: ${frame.readText()}")
                }

            }
        } finally {
            // exit websocket
            server.memberLeft(session.id, this)
            System.err.println("server closed websocket ${session.id}")
        }
    }
}

internal fun Routing.homepageRoute() {
    authenticate(AuthName.SESSION, optional = true) {
        get("/") {
            // Redirect user to login if they're not already logged in.
            // Otherwise redirect them to a page that requires auth.
            if (call.principal<UserIdPrincipal>() == null) {
                call.respondRedirect(CommonRoutes.LOGIN)
            } else {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
        }
    }
}

internal fun Routing.loginRoute() {
    route(CommonRoutes.LOGIN) {
        get {
            call.respondHtml {
                body {
                    // Create a form that POSTs back to this same route
                    form(method = FormMethod.post) {
                        // handle any possible errors
                        val queryParams = call.request.queryParameters
                        val errorMsg = when {
                            "invalid" in queryParams -> "Sorry, incorrect username or password."
                            "no" in queryParams -> "Sorry, you need to be logged in to do that."
                            else -> null
                        }
                        if (errorMsg != null) {
                            div {
                                style = "color:red;"
                                +errorMsg
                            }
                        }
                        textInput(name = FormFields.USERNAME) {
                            placeholder = "username/email"
                        }
                        br
                        passwordInput(name = FormFields.PASSWORD) {
                            placeholder = "password"
                        }
                        br
                        submitInput {
                            value = "Log in"
                        }
                    }
                }
            }
        }

        authenticate(AuthName.FORM) {
            post {
                // Get the principal (which we know we'll have)
                val principal = call.principal<UserIdPrincipal>()!!
                // Set the cookie
                call.sessions.set(principal)
                call.respondRedirect("/")
            }
        }
    }
}

internal fun Routing.logoutRoute() {
    get(CommonRoutes.LOGOUT) {
        // Purge ExamplePrinciple from cookie data
        call.sessions.clear<UserIdPrincipal>()
        call.respondRedirect(CommonRoutes.LOGIN)
    }
}

internal fun Route.profileRoute() {
    authenticate(AuthName.SESSION) {
        get(CommonRoutes.PROFILE) {
            val principal = call.principal<UserIdPrincipal>()!!
            call.respondHtml {
                body {
                    div {
                        +"Hello, $principal!"
                    }
                    div {
                        a(href = CommonRoutes.LOGOUT) {
                            +"Log out"
                        }
                    }
                }
            }
        }
    }
}
