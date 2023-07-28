package ge.lpaichadze.messengerapp.utils

import android.net.Uri

private const val EMAIL_SUFFIX = "freeuni.edu.ge"

fun String.toEmail(id: Int = 0): String {
    return this.replace(' ', '_') + "@" + id + EMAIL_SUFFIX
}

fun String.isValidNickname(): Boolean {
    val regex = Regex("^[a-zA-Z0-9 ]+\$")
    return this.matches(regex)
}

fun Uri.isLocal(): Boolean {
    return this.scheme != null && (this.scheme == "file" || this.scheme == "content")
}
