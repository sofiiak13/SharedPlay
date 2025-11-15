package me.sofiiak.sharedplay.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.sofiiak.sharedplay.data.PlaylistsRepository
import me.sofiiak.sharedplay.data.SongsRepository
import me.sofiiak.sharedplay.data.dto.PlaylistResponse
import me.sofiiak.sharedplay.data.dto.PlaylistUpdate
import me.sofiiak.sharedplay.data.dto.SongResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor(
    private val playlistsRepository: PlaylistsRepository,
    private val songsRepository: SongsRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val playlistId: String = savedStateHandle["playlistId"] ?: ""

    private val _state = MutableStateFlow(
        PlaylistDetailsViewModel.UiState(
            toolbar = PlaylistDetailsViewModel.UiState.Toolbar(
                title = "Playlist Details", // TODO: replace with string resources
                backButtonContentDescription = "Back" // TODO: replace with string resources
            )
        )
    )

    val state = _state.asStateFlow()


    init {
        if (validatePlaylistId()) {
            loadPlaylistDetails(playlistId)
        }
    }

    private fun validatePlaylistId(): Boolean {
        val isValid = playlistId.isNotEmpty()
        if (isValid.not()) {
            _state.update {
                it.copy(error = "Invalid playlist id")
            }
        }
        return isValid
    }

    fun onUiEvent(event: UiEvent) {
        when (event) {
            is UiEvent.AddSongDialogConfirmButtonClick ->
                viewModelScope.launch {

                    songsRepository.addSong(
                        url = event.songUrl,
                        playlistId = playlistId,
                        userId = "-Odv6YSev5ZNYnDdis9d", // todo: modify this hardcoded value
                    )
                    hideAddSongDialog()
                    _state.update {
                        it.copy(isLoading = true)
                    }
                    loadPlaylistDetails(playlistId)
                }

            UiEvent.AddSongDialogDismiss -> hideAddSongDialog()

            UiEvent.AddSongButtonClick -> _state.update {
                it.copy(
                    addSongDialog = getAddSongDialog()
                )
            }

            is UiEvent.DeletePlaylistConfirmButtonClick -> viewModelScope.launch {

                playlistsRepository.deletePlaylist(
                    playlistId = playlistId,
                )
                hideDeletePlaylistDialog()
                _state.update {
                    it.copy(isLoading = true)
                }
            }

            UiEvent.DeletePlaylistDialogDismiss -> hideDeletePlaylistDialog()

            UiEvent.DeletePlaylistButtonClick -> _state.update {
                it.copy(
                    deletePlaylistDialog = getDeletePlaylistDialog()
                )
            }

            is UiEvent.EditPlaylistNameConfirmButtonClick ->  viewModelScope.launch {

                playlistsRepository.editPlaylist(
                    playlistId = playlistId,
                    playlist = PlaylistUpdate(
                        name = event.newName,
                    )
                )
                hideEditPlaylistNameDialog()
                _state.update {
                    it.copy(isLoading = true)
                }
                loadPlaylistDetails(playlistId)
            }


            UiEvent.EditPlaylistNameButtonClick ->  _state.update {
                it.copy(
                    editPlaylistNameDialog = getEditPlaylistNameDialog()
                )
            }

            UiEvent.EditPlaylistNameDismiss -> hideEditPlaylistNameDialog()
        }
    }

    private fun hideAddSongDialog() {
        _state.update {
            it.copy(addSongDialog = null)
        }
    }

    private fun getAddSongDialog() = UiState.AddSongDialog(
        title = "Add Song",
        placeholder = "Enter song URL",
        buttonConfirm = "Add",
        buttonCancel = "Cancel",
    )

    private fun hideDeletePlaylistDialog() {
        _state.update {
            it.copy(deletePlaylistDialog = null)
        }
    }

    private fun getDeletePlaylistDialog() = UiState.DeletePlaylistDialog(
        title = "Are you sure you want to delete this playlist?",
        buttonConfirm = "Delete",
        buttonCancel = "Cancel",
    )


    private fun hideEditPlaylistNameDialog() {
        _state.update {
            it.copy(editPlaylistNameDialog = null)
        }
    }

    private fun getEditPlaylistNameDialog() = UiState.EditPlaylistNameDialog(
        title = "Rename Playlist",
        placeholder = "Enter new playlist name",
        buttonConfirm = "Rename",
        buttonCancel = "Cancel",
    )

    /**
     * Public: load playlist details and songs for the given id.
     * Safe to call multiple times (will update state).
     */
    fun loadPlaylistDetails(playlistId: String) {
        viewModelScope.launch {
            // set loading
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                // Load playlist details
                val playlist = playlistsRepository
                    .getPlaylistDetails(playlistId)
                    .getOrNull()        // to unwrap Result response
                    ?.toPlaylistUiState()

                _state.value = _state.value.copy(playlist = playlist)

                // Load songs
                val songs = songsRepository
                    .getSongsFrom(playlistId)
                    .getOrNull()
                    ?.toSongUiState()
                    ?: emptyList()

                if (songs.isNotEmpty()) {
                    _state.value = _state.value.copy(songs = songs, isLoading = false)
                    _state.update {
                      it.copy(songs = songs, isLoading = false)
                    }
                } else {
                    _state.update {
                        it.copy(error = "No songs have been added yet 🎶", isLoading = false)
                    }
                }
            } catch (t: Throwable) {
                // handle error
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = t.message ?: "Unknown error"
                )
            }
        }
    }

    private fun PlaylistResponse.toPlaylistUiState() =
        UiState.Playlist(
            id = id,
            name = name,
            lastUpdated = formatDate(last_updated)
        )

    private fun formatDate(date: LocalDateTime): String {
        // TODO:  move to a separate DateTimeUtility
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        return date.format(formatter)
    }

    private fun List<SongResponse>.toSongUiState() =
        this.map { songResponse ->
            UiState.Song(
                id = songResponse.id,
                title = songResponse.title,
                artist = songResponse.artist,
            )
        }

    data class UiState(
        val toolbar: Toolbar,
        val playlist: Playlist? = null,
        val songs: List<Song> = emptyList(),
        val addSongDialog: AddSongDialog? = null,
        val deletePlaylistDialog: DeletePlaylistDialog? = null,
        val editPlaylistNameDialog: EditPlaylistNameDialog? = null,
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

        data class Song(
            val id: String,
            val title: String,
            val artist: String,
        )

        data class AddSongDialog(
            val title: String,
            val placeholder: String,
            val buttonConfirm: String,
            val buttonCancel: String,
        )

        data class DeletePlaylistDialog(
            val title: String,
            val buttonConfirm: String,
            val buttonCancel: String,
        )

        data class EditPlaylistNameDialog(
            val title: String,
            val placeholder: String,
            val buttonConfirm: String,
            val buttonCancel: String,
        )

        companion object {
            fun preview(): UiState =
                UiState(
                    toolbar = Toolbar(
                        title = "Playlist Details",
                        backButtonContentDescription = "Back"
                    ),
                    songs = listOf(
                        Song(
                            id = "1",
                            title = "Song 1",
                            artist = "Artist 1"
                        ),
                        Song(
                            id = "2",
                            title = "Song 2",
                            artist = "Artist 2"
                        )
                    )
                )
        }
    }

    sealed interface UiEvent {
        data object AddSongButtonClick : UiEvent
        data object AddSongDialogDismiss : UiEvent
        data class AddSongDialogConfirmButtonClick(
            val songUrl: String,
        ) : UiEvent

        data object DeletePlaylistButtonClick : UiEvent
        data object DeletePlaylistDialogDismiss : UiEvent
        data object DeletePlaylistConfirmButtonClick : UiEvent

        data object EditPlaylistNameButtonClick : UiEvent
        data object EditPlaylistNameDismiss : UiEvent
        data class EditPlaylistNameConfirmButtonClick(
            val newName: String,
        ) : UiEvent

    }
}
