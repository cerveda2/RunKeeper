package cz.dcervenka.runkeeper

data class MainState(
    val isLoggedIn: Boolean = false,
    val isCheckingAuth: Boolean = false
)