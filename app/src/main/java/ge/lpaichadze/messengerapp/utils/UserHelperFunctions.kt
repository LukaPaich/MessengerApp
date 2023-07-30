package ge.lpaichadze.messengerapp.utils

import android.net.Uri
import androidx.lifecycle.LiveData
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val EMAIL_SUFFIX = "freeuni.edu.ge"

const val DEBOUNCE_DELAY = 300L

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

fun Instant.toFormatted(): String {
    val now = Instant.now()

    val duration = Duration.between(this, now)
    val seconds = duration.seconds

    return when {
        seconds < 3600 -> {
            val mins = seconds / 60
            if (mins > 1) {
                return "$mins mins"
            } else {
                return "$mins min"
            }
        }
        seconds < 86400 -> {
            val hours = seconds / 3600
            if (hours > 1) {
                return "$hours hours"
            } else {
                return "$hours hour"
            }
        }
        else -> {
            val formatter = DateTimeFormatter.ofPattern("d MMM")
            val zoneId = ZoneId.systemDefault()
            this.atZone(zoneId).format(formatter)
        }
    }
}
fun generateIdentifier(userUid: String, otherUserUid: String): String {
    return if (userUid < otherUserUid) {
        "${userUid}<->${otherUserUid}"
    } else {
        "${otherUserUid}<->${userUid}"
    }
}
