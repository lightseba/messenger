import io.ktor.auth.UserIdPrincipal

object FormFields {
    const val USERNAME = "username"
    const val PASSWORD = "password"
}

object AuthName {
    const val SESSION = "session"
    const val FORM = "form"
}

object CommonRoutes {
    const val LOGIN = "/login"
    const val LOGOUT = "/logout"
    const val PROFILE = "/profile"
}

object Cookies {
    const val AUTH_COOKIE = "auth"
}

object TestCredentials {
    const val USERNAME = "foo"
    const val PASSWORD = "bar"
}
