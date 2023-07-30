package ge.lpaichadze.messengerapp.persistence.model

data class FullConversationData(
    val conversation: Conversation,
    val user: User
): Comparable<FullConversationData> {
    override fun compareTo(other: FullConversationData): Int {
        return ((conversation.time ?: 0) - (other.conversation.time ?: 0)).toInt()
    }
}
