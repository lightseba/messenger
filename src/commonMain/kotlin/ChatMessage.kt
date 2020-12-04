import kotlinx.serialization.Serializable
import kotlinx.datetime.*

private fun getCurrentTime(): String {
	val current = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
	val minute = (if (current.minute < 10) "0" else "") + current.minute.toString()
	return "${current.date.toString()} ${current.hour}:$minute"
}

@Serializable
data class ChatMessage(
	val author: String, 
	val content: String,
) {
	val time = getCurrentTime()
}
