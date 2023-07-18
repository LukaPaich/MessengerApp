package ge.lpaichadze.messengerapp.utils

private const val EMAIL_SUFFIX = "@freeuni.edu.ge"

fun String.toEmail(): String {
    return this.replace(' ', '_') + EMAIL_SUFFIX
}
