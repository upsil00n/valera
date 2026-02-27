package data

import kotlinx.datetime.Clock

data class SignatureRequest(
    val id: String,
    val pollTitle: String,
    val pollId: String,
    val selectedOption: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)