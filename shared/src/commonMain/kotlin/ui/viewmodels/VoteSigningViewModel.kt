package ui.viewmodels

import androidx.lifecycle.ViewModel
import data.VoteSigningRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VoteSigningViewModel : ViewModel() {

    private val _pendingRequests = MutableStateFlow<List<VoteSigningRequest>>(emptyList())
    val pendingRequests: StateFlow<List<VoteSigningRequest>> = _pendingRequests.asStateFlow()

    init {
        // Загружаем моковые данные для демонстрации
        loadMockRequests()
    }

    private fun loadMockRequests() {
        _pendingRequests.value = listOf(
            VoteSigningRequest(
                id = "1",
                pollTitle = "Budget Allocation 2024",
                pollId = "poll_001",
                selectedOption = "Option A: Increase education budget by 15%"
            ),
            VoteSigningRequest(
                id = "2",
                pollTitle = "New Infrastructure Project",
                pollId = "poll_002",
                selectedOption = "Option B: Build new metro line"
            ),
            VoteSigningRequest(
                id = "3",
                pollTitle = "Environmental Policy",
                pollId = "poll_003",
                selectedOption = "Option C: Ban single-use plastics"
            )
        )
    }

    fun signVote(requestId: String, privateKey: String) {
        // TODO: Implement actual signing logic
        println("Signing vote for request: $requestId with key: ${privateKey.take(10)}...")

        // Удаляем из списка после подписи
        _pendingRequests.value = _pendingRequests.value.filter { it.id != requestId }
    }

    fun rejectVote(requestId: String) {
        println("Rejecting vote for request: $requestId")

        // Удаляем из списка после отклонения
        _pendingRequests.value = _pendingRequests.value.filter { it.id != requestId }
    }

    fun refreshRequests() {
        // TODO: Implement actual API call
        loadMockRequests()
    }
}