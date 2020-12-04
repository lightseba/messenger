import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.*
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.*

import kotlinx.serialization.*
import kotlinx.serialization.json.*

class ChatServer {

	val format = Json
    val usersCounter = AtomicInteger()
    val memberNames = ConcurrentHashMap<String, String>()
    val members = ConcurrentHashMap<String, MutableList<WebSocketSession>>()
    val lastMessages = LinkedList<String>()


    suspend fun memberJoin(member: String, socket: WebSocketSession, username: String) {
        memberNames.computeIfAbsent(member) { username }
        val list = members.computeIfAbsent(member) { CopyOnWriteArrayList<WebSocketSession>() }
        list.add(socket)

        // Send old messages
        val messages = synchronized(lastMessages) { lastMessages.toList() }
        for (message in messages) {
            socket.send(Frame.Text(message))
        }
    }

    suspend fun memberRenamed(member: String, to: String) {
        val oldName = memberNames.put(member, to) ?: member
		message(ChatMessage("server", "Member renamed from $oldName to $to"))
    }

    suspend fun memberLeft(member: String, socket: WebSocketSession) {
        // Removes the socket connection for this member
        val connections = members[member]
        connections?.remove(socket)

        // If no more sockets are connected for this member, let's remove it from the server
        // and notify the rest of the users about this event.
        if (connections != null && connections.isEmpty()) {
            memberNames.remove(member)
			// message(ChatMessage("server", "Member left: $name."))
        }
    }

    suspend fun message(formatted: String) = broadcast(formatted)
	suspend fun message(message: ChatMessage) = message(format.encodeToString(message))

	suspend fun message(text: String, session: String) {
		val author = memberNames.getOrDefault(session, "???")

        if (text.startsWith("/name"))
            memberRenamed(session, text.substringAfter("/name").strip())
        else
		    message(ChatMessage(author, text))
	}

    private suspend fun broadcast(message: String) {
        members.values.forEach { socket ->
            socket.send(Frame.Text(message))
        }
    }

    suspend fun List<WebSocketSession>.send(frame: Frame) {
        forEach {
            try {
                it.send(frame.copy())
            } catch (t: Throwable) {
                try {
                    it.close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, ""))
                } catch (ignore: ClosedSendChannelException) {
                    // at some point it will get closed
                }
            }
        }
    }
}
