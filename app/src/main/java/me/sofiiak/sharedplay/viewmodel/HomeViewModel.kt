package me.sofiiak.sharedplay.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.sofiiak.sharedplay.data.PlaylistsRepository
import me.sofiiak.sharedplay.data.dto.PlaylistResponse
import me.sofiiak.sharedplay.data.dto.PlaylistUpdate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "HomeViewModel"
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val playlistsRepository: PlaylistsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(
        UiState(
            toolbar = UiState.Toolbar(
                    title = "Home",
                    backButtonContentDescription = "Back"
            )
        )
    )
    val state = _state.asStateFlow()

    private fun List<PlaylistResponse>.toUiState() = UiState(
        toolbar = UiState.Toolbar(
            title = "Home",
            backButtonContentDescription = "Back"
        ),
        playlists = this.map { playlistResponse ->
            UiState.Playlist(
                id = playlistResponse.id,
                name = playlistResponse.name,
                lastUpdated = formatDate(playlistResponse.last_updated),
            )
        }
    )


    fun onHomeUiEvent(event: UiEvent) {
        when (event) {
            is UiEvent.AddPlaylistConfirmButtonClick ->
                viewModelScope.launch {
                    val newPlaylist = PlaylistUpdate(
                        name = event.newName,
                        owner = "-Odv6YSev5ZNYnDdis9d"
                    )
                    playlistsRepository.createPlaylist(
                        "-Odv6YSev5ZNYnDdis9d",
                        newPlaylist)
                    hideAddPlaylistDialog()
                    _state.update {
                        it.copy(isLoading = true)
                    }
                    loadPlaylists()
                }

            UiEvent.AddPlaylistDialogDismiss -> hideAddPlaylistDialog()

            UiEvent.AddPlaylistButtonClick -> _state.update {
                it.copy(
                    addPlaylistDialog = getAddPlaylistDialog()
                )
            }

            UiEvent.LoadPlaylists -> loadPlaylists()
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
        viewModelScope.launch {
            val result = playlistsRepository.getPlaylistsForUser(userId = "-Odv6YSev5ZNYnDdis9d")
            result.onSuccess { playlists ->
                _state.value = playlists.toUiState()
            }.onFailure {
                Log.e(TAG,"Couldn't load playlists")
                _state.value = _state.value.copy(error = "Couldn't load playlists")
            }
        }
    }


    fun deletePlaylist(id: String) {
        viewModelScope.launch {
            playlistsRepository.deletePlaylist(id)
                .onSuccess {
                    loadPlaylists() // refresh list
                }
                .onFailure {
                    // show error message
                }
        }
    }

    private fun formatDate(date: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        return date.format(formatter)
    }

    data class UiState(
        val toolbar: Toolbar,
        val playlists: List<Playlist> = emptyList(),
        val addPlaylistDialog: AddPlaylistDialog? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) {
        data class Toolbar(
            val title: String,
            val backButtonContentDescription: String,
        )

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
                    toolbar = Toolbar(
                        title = "Home",
                        backButtonContentDescription = "Back"
                    ),
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

    }
}