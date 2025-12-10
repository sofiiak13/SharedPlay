package me.sofiiak.sharedplay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.sofiiak.sharedplay.data.UserRepository
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor (
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        UiState()
    )
    val state = _state.asStateFlow()

    fun onUiEvent(uiEvent: UiEvent) {
        when (uiEvent) {
            UiEvent.SignedIn -> viewModelScope.launch {
                userRepository.signIn()
            }
        }
    }

    data class UiState(
        val dd: String = "",
    )

    sealed interface UiEvent {
        data object SignedIn : UiEvent
    }


}