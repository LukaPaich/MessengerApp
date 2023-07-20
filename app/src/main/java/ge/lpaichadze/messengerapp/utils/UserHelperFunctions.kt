package ge.lpaichadze.messengerapp.utils

private const val EMAIL_SUFFIX = "freeuni.edu.ge"

fun String.toEmail(id: Int = 0): String {
    return this.replace(' ', '_') + "@" + id + EMAIL_SUFFIX
}
