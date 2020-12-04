import kotlinx.serialization.Serializable

@Serializable
data class ChatEntry(val id: String, val name: String)

@Serializable
data class HomeData(
	val username: String,
	val chats: List<ChatEntry>
)
