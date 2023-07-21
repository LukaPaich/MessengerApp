package ge.lpaichadze.messengerapp.utils

private const val EMAIL_SUFFIX = "freeuni.edu.ge"

fun String.toEmail(id: Int = 0): String {
    return this.replace(' ', '_') + "@" + id + EMAIL_SUFFIX
}

fun String.isValidNickname(): Boolean {
    val regex = Regex("^[a-zA-Z0-9 ]+\$")
    return this.matches(regex)
}
