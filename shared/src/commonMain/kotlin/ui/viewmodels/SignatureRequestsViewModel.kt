package ui.viewmodels

import androidx.lifecycle.ViewModel
import data.SignatureRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignatureRequestsViewModel : ViewModel() {

    private val _requests = MutableStateFlow<List<SignatureRequest>>(emptyList())
    val requests: StateFlow<List<SignatureRequest>> = _requests.asStateFlow()

    init {
        // Загружаем моковые данные для демонстрации
        loadMockRequests()
    }

    private fun loadMockRequests() {
        _requests.value = listOf(
            SignatureRequest(
                id = "req_001",
                pollTitle = "Budget Allocation 2024",
                pollId = "poll_budget_2024",
                selectedOption = "Option A: Increase education budget by 15%"
            ),
            SignatureRequest(
                id = "req_002",
                pollTitle = "New Infrastructure Project",
                pollId = "poll_infrastructure_001",
                selectedOption = "Option B: Build new metro line connecting districts 5 and 7"
            ),
            SignatureRequest(
                id = "req_003",
                pollTitle = "Environmental Policy",
                pollId = "poll_env_2024",
                selectedOption = "Option C: Ban single-use plastics by 2026"
            )
        )
    }

    fun signRequest(requestId: String) {
        println("Signing request: $requestId")
        // TODO: Implement actual signing logic

        // Удаляем запрос после подписи
        _requests.value = _requests.value.filter { it.id != requestId }
    }

    fun rejectRequest(requestId: String) {
        println("Rejecting request: $requestId")

        // Удаляем запрос после отклонения
        _requests.value = _requests.value.filter { it.id != requestId }
    }

    fun refreshRequests() {
        // TODO: Implement actual API call
        loadMockRequests()
    }
}