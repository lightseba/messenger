import io.ktor.auth.*
import io.ktor.auth.*
import kotlin.*
import kotlin.collections.*

data class User(
	val username: String,
	val email: String,
	val hash: String) {
	var chats = HashSet<Convo>()
}

interface UserDatabase {
    fun getByEmail(email: String): User?
    fun getByUsername(handle: String): User?
	fun addUser(user: User)
}

class UserDatabaseImpl : UserDatabase {
    private val emailTable = HashMap<String, User>()
    private val usernameTable = HashMap<String, User>()

    override fun getByEmail(email: String) = emailTable.get(email)
    override fun getByUsername(username: String) = usernameTable.get(username)
	override fun addUser(user: User) {
        emailTable.put(user.email, user)
        usernameTable.put(user.username, user)
    }
}
