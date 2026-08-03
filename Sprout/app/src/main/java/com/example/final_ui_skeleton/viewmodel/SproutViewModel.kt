package com.example.final_ui_skeleton.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_ui_skeleton.data.FirebaseRepository
import com.example.final_ui_skeleton.data.SharedGoalsRepository
import com.example.final_ui_skeleton.model.SavingsGoal
import com.example.final_ui_skeleton.model.Spending
import com.example.final_ui_skeleton.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class ChatMessage(val text: String, val isUser: Boolean)

class SproutViewModel : ViewModel() {

    val isLoggedIn: Boolean get() = FirebaseRepository.isLoggedIn

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _expenses = MutableStateFlow<List<Pair<String, Spending>>>(emptyList())
    val expenses: StateFlow<List<Pair<String, Spending>>> = _expenses.asStateFlow()

    private val _authMessage = MutableStateFlow<String?>(null)
    val authMessage: StateFlow<String?> = _authMessage.asStateFlow()

    // persists chat across tab switches
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    val savedAmount: StateFlow<Double> get() = _savedAmount
    private val _savedAmount = MutableStateFlow(0.0)

    var selectedBudgetTab by mutableStateOf(0)
    var selectedSpending: Spending? by mutableStateOf(null)
    var selectedSpendingId: String? by mutableStateOf(null)
    fun selectSpending(spending: Spending, docId: String = "") {
        selectedSpending = spending
        selectedSpendingId = docId
    }

    private var hasGeneratedSummary = false

    fun addChatMessage(message: ChatMessage) {
        _chatMessages.value = _chatMessages.value + message
        viewModelScope.launch {
            FirebaseRepository.saveChatMessages(_chatMessages.value.map { mapOf("text" to it.text, "isUser" to it.isUser) })
        }
    }

    fun generateSummaryIfNeeded(summary: ChatMessage) {
        if (!hasGeneratedSummary) {
            hasGeneratedSummary = true
            if (_chatMessages.value.isEmpty()) {
                _chatMessages.value = listOf(summary)
                viewModelScope.launch {
                    FirebaseRepository.saveChatMessages(listOf(mapOf("text" to summary.text, "isUser" to summary.isUser)))
                }
            }
        }
    }

    fun updateOpeningSummary(summary: ChatMessage) {
        val current = _chatMessages.value
        _chatMessages.value = if (current.isEmpty()) listOf(summary) else listOf(summary) + current.drop(1)
        viewModelScope.launch {
            FirebaseRepository.saveChatMessages(_chatMessages.value.map { mapOf("text" to it.text, "isUser" to it.isUser) })
        }
    }

    fun loadChatHistory() {
        viewModelScope.launch {
            val result = FirebaseRepository.loadChatMessages()
            if (result.isSuccess) {
                val loaded = result.getOrNull()?.map {
                    ChatMessage(
                        text = it["text"] as? String ?: "",
                        isUser = it["isUser"] as? Boolean ?: false
                    )
                } ?: emptyList()
                if (loaded.isNotEmpty()) {
                    _chatMessages.value = loaded
                    hasGeneratedSummary = true
                }
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
        hasGeneratedSummary = false
        viewModelScope.launch { FirebaseRepository.saveChatMessages(emptyList()) }
    }

    fun clearAuthMessage() { _authMessage.value = null }

    fun signUp(email: String, password: String, user: User, onSuccess: () -> Unit) {
        _authMessage.value = null
        viewModelScope.launch {
            val result = FirebaseRepository.signUp(email, password)
            if (result.isSuccess) {
                FirebaseRepository.saveUserProfile(user)
                _userProfile.value = user
                onSuccess()
            } else {
                _authMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        _authMessage.value = null
        viewModelScope.launch {
            val result = FirebaseRepository.signIn(email, password)
            if (result.isSuccess) {
                loadUserProfile()
                loadChatHistory()
                onSuccess()
            } else {
                _authMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun signOut(onDone: () -> Unit) {
        FirebaseRepository.signOut()
        _userProfile.value = null
        _expenses.value = emptyList()
        _savedAmount.value = 0.0
        _chatMessages.value = emptyList()
        _authMessage.value = null
        hasGeneratedSummary = false
        onDone()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            val result = FirebaseRepository.getUserProfile()
            if (result.isSuccess) {
                _userProfile.value = result.getOrNull()
                recalculateSavings()
                if (_chatMessages.value.isEmpty()) loadChatHistory()
            }
        }
    }

    fun updateProfileField(field: String, value: String) {
        viewModelScope.launch {
            FirebaseRepository.updateUserProfile(field, value)
            loadUserProfile()
        }
    }

    fun updateSavingsTarget(target: Double) {
        viewModelScope.launch {
            FirebaseRepository.updateUserProfileDouble("savingsTarget", target)
            loadUserProfile()
        }
    }

    fun updateMonthlyBudget(budget: Double) {
        viewModelScope.launch {
            FirebaseRepository.updateUserProfileDouble("monthlyBudget", budget)
            loadUserProfile()
        }
    }

    fun updatePassword(newPassword: String) {
        viewModelScope.launch { FirebaseRepository.updatePassword(newPassword) }
    }

    fun addExpense(spending: Spending, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = FirebaseRepository.addExpense(spending)
            if (result.isSuccess) onSuccess()
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch { FirebaseRepository.deleteExpense(expenseId) }
    }

    fun updateExpense(expenseId: String, spending: Spending) {
        viewModelScope.launch { FirebaseRepository.updateExpense(expenseId, spending) }
    }

    fun startListeningToExpenses() {
        viewModelScope.launch {
            FirebaseRepository.getExpensesFlow().collect { list ->
                // migrate old category names
                list.forEach { (docId, spending) ->
                    val migrated = when (spending.category) {
                        "Food" -> spending.copy(category = "Groceries")
                        else -> null
                    }
                    if (migrated != null) {
                        FirebaseRepository.updateExpense(docId, migrated)
                    }
                }
                _expenses.value = list.map { (docId, spending) ->
                    docId to when (spending.category) {
                        "Food" -> spending.copy(category = "Groceries")
                        else -> spending
                    }
                }
                recalculateSavings()
            }
        }
    }

    private fun recalculateSavings() {
        val budget = _userProfile.value?.monthlyBudget ?: return
        val allExpenses = _expenses.value
        if (allExpenses.isEmpty()) { _savedAmount.value = 0.0; return }

        val fmt = SimpleDateFormat("M/d/yyyy", Locale.getDefault())
        val monthFmt = SimpleDateFormat("MM/yyyy", Locale.getDefault())

        val byMonth = allExpenses
            .mapNotNull { (_, s) ->
                try { monthFmt.format(fmt.parse(s.date)!!) to s.amount } catch (e: Exception) { null }
            }
            .groupBy { it.first }
            .mapValues { e -> e.value.sumOf { it.second } }

        val totalSaved = byMonth.entries.sumOf { (_, spent) ->
            val remainder = budget - spent
            if (remainder > 0) remainder else 0.0
        }

        _savedAmount.value = totalSaved
        viewModelScope.launch {
            FirebaseRepository.updateUserProfileDouble("savedAmount", totalSaved)
        }
    }

    private val _sharedGoals = MutableStateFlow<List<Pair<String, SavingsGoal>>>(emptyList())
    val sharedGoals: StateFlow<List<Pair<String, SavingsGoal>>> = _sharedGoals.asStateFlow()

    private var goalsListenerJob: kotlinx.coroutines.Job? = null

    fun startListeningToGoals() {
        goalsListenerJob?.cancel()
        goalsListenerJob = viewModelScope.launch {
            SharedGoalsRepository.myGoalsFlow().collect { _sharedGoals.value = it }
        }
    }

    fun updateGoal(goalId: String, name: String, target: Double, groupName: String, deadlineMonth: Int, deadlineYear: Int) {
        viewModelScope.launch {
            SharedGoalsRepository.updateGoal(goalId, name, target, groupName, deadlineMonth, deadlineYear)
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            SharedGoalsRepository.deleteGoal(goalId)
        }
    }

    fun createGoal(name: String, target: Double, groupName: String, deadlineMonth: Int, deadlineYear: Int, onResult: (String?) -> Unit = {}) {
        viewModelScope.launch {
            onResult(SharedGoalsRepository.createGoal(name, target, groupName, deadlineMonth, deadlineYear).getOrNull())
        }
    }

    fun joinGoal(code: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val r = SharedGoalsRepository.joinGoal(code)
            onResult(r.isSuccess, r.exceptionOrNull()?.message)
        }
    }

    fun contribute(goalId: String, amount: Double) {
        viewModelScope.launch {
            val name = userProfile.value?.name ?: "Someone"
            SharedGoalsRepository.contribute(goalId, amount, name)
        }
    }

    fun setMemberPlan(goalId: String, totalCommitment: Double, savingsPct: Double, deadlineMonth: Int, deadlineYear: Int, monthlySurplus: Double = _savedAmount.value) {
        viewModelScope.launch {
            val userName = userProfile.value?.name ?: "Someone"
            SharedGoalsRepository.setMemberPlan(goalId, totalCommitment, savingsPct, deadlineMonth, deadlineYear, userName, monthlySurplus)
        }
    }

    fun contributionsFlow(goalId: String) = SharedGoalsRepository.contributionsFlow(goalId)
}