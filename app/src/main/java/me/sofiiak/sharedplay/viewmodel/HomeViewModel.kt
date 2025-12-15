package me.sofiiak.sharedplay.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.sofiiak.sharedplay.data.PlaylistsRepository
import me.sofiiak.sharedplay.data.UserRepository
import me.sofiiak.sharedplay.data.dto.PlaylistResponse
import me.sofiiak.sharedplay.data.dto.UserResponse
import me.sofiiak.sharedplay.data.formatDate
import retrofit2.HttpException
import javax.inject.Inject

private const val TAG = "HomeViewModel"
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val playlistsRepository: PlaylistsRepository,
    private val savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(
        UiState()
    )
    val state = _state.asStateFlow()

    private fun initScreen() {
        viewModelScope.launch {
            userRepository.signIn()
                .onSuccess { user: UserResponse ->
                    _state.update {
                        it.copy(curUser = user)
                    }

                    // If there is an inviteId in savedStateHandle, trigger an event
                    savedStateHandle.get<String>("inviteId")?.let { inviteId ->
                        handleInvite(inviteId = inviteId, userId = user.id)
                        savedStateHandle["inviteId"] = null
                    }

                    loadPlaylists()
                }
                .onFailure {
                    setNeedSignIn(true)
                }
        }
    }


    private fun List<PlaylistResponse>.toUiState() =
        this.map { playlistResponse ->
            UiState.Playlist(
                id = playlistResponse.id,
                name = playlistResponse.name,
                lastUpdated = formatDate(playlistResponse.last_updated),
            )
        }

    fun clearInviteMessage() {
        _state.update { it.copy(inviteMessage = null) }
    }

    private fun setNeedSignIn(value: Boolean) {
        _state.update { current ->
            current.copy(needSignIn = value)
        }
    }

    private suspend fun handleInvite(inviteId: String, userId: String) {
        try {
            val invite = playlistsRepository.validateInvite(inviteId).getOrNull()
            if (invite == null) {
                _state.update {
                    it.copy(error = "Invalid invite.")
                }
            } else {
                _state.update {
                    it.copy(
                        inviteMessage = "You've been invited as a collaborator to a playlist!"
                    )
                }
                playlistsRepository.addEditor(invite.playlist_id, userId)
                Log.e(TAG, "handleInvite: reached successful invite.")
            }
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is HttpException -> {
                    when (e.code()) {
                        410 -> "This invite has expired."
                        404 -> "Invalid invite."
                        else -> "Failed to accept the invite."
                    }
                }
                else -> "Failed to accept the invite."
            }
            _state.update {
                it.copy(error = errorMessage)
            }
        }
    }

    fun onHomeUiEvent(event: UiEvent) {
        when (event) {
            is UiEvent.AddPlaylistConfirmButtonClick ->
                state.value.curUser?.id?.let { userId ->
                    viewModelScope.launch {

                        val newPlaylist = PlaylistResponse(
                            id = "",
                            name = event.newName,
                            owner = userId,
                        )
                        playlistsRepository.createPlaylist(newPlaylist)
                        hideAddPlaylistDialog()
                        _state.update {
                            it.copy(isLoading = true)
                        }
                        loadPlaylists()
                    }
                }

            UiEvent.AddPlaylistDialogDismiss -> hideAddPlaylistDialog()

            UiEvent.AddPlaylistButtonClick -> _state.update {
                it.copy(
                    addPlaylistDialog = getAddPlaylistDialog()
                )
            }

            UiEvent.LoadPlaylists -> loadPlaylists()

            UiEvent.InitScreen -> initScreen()

            UiEvent.NavigatedToSignIn -> setNeedSignIn(false)
        }
    }

    private fun hideAddPlaylistDialog() {
        _state.update {
            it.copy(addPlaylistDialog = null)
        }
    }

    private fun getAddPlaylistDialog() = UiState.AddPlaylistDialog(
        title = "Create new playlist",
        placeholder = "Enter playlist name",
        buttonConfirm = "Create",
        buttonCancel = "Cancel",
    )

    private fun loadPlaylists() {
        val userId = state.value.curUser?.id ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = playlistsRepository.getPlaylistsForUser(userId = userId)

            result.onSuccess { playlistResponses ->
                if (playlistResponses.isNotEmpty()) {
                    // Success with data: update playlists and stop loading
                    _state.update {
                        it.copy(
                            playlists = playlistResponses.toUiState(),
                            isLoading = false,
                        )
                    }
                } else {
                    // Success but no data: show a message and stop loading
                    _state.update {
                        it.copy(
                            playlists = emptyList(),
                            isLoading = false,
                            error = "Add your first playlist by pressing '+' at the top-right corner!"
                        )
                    }
                }
            }.onFailure { throwable ->
                // Failure: log the error, show a message, and stop loading
                Log.e(TAG, "Couldn't load playlists for user $userId", throwable)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Couldn't load playlists"
                    )
                }
            }
        }
    }


    data class UiState(
        val playlists: List<Playlist> = emptyList(),
        val addPlaylistDialog: AddPlaylistDialog? = null,
        val inviteMessage: String? = null,
        val curUser: UserResponse? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val needSignIn: Boolean = false,
    ) {
        data class Playlist(
            val id: String,
            val name: String,
            val lastUpdated: String,
        )

        data class AddPlaylistDialog(
            val title: String,
            val placeholder: String,
            val buttonConfirm: String,
            val buttonCancel: String,
        )

        companion object {
            fun preview(): UiState =
                UiState(
                    playlists = listOf(
                        Playlist(
                            id = "1",
                            name = "Playlist 1",
                            lastUpdated = "Apr 11, 2025"
                        ),
                        Playlist(
                            id = "2",
                            name = "Playlist 2",
                            lastUpdated = "Sep 30, 2025"
                        )
                    )
                )
        }
    }

    sealed interface UiEvent {
        data object LoadPlaylists : UiEvent
        data object AddPlaylistButtonClick : UiEvent
        data object AddPlaylistDialogDismiss : UiEvent
        data class AddPlaylistConfirmButtonClick(
            val newName: String,
        ) : UiEvent

        data object InitScreen : UiEvent

        data object NavigatedToSignIn : UiEvent
    }
}