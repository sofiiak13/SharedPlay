package me.sofiiak.sharedplay.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.sofiiak.sharedplay.data.PlaylistsRepository
import me.sofiiak.sharedplay.data.SongsRepository
import me.sofiiak.sharedplay.data.dto.PlaylistResponse
import me.sofiiak.sharedplay.data.dto.SongResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor (
    private val playlistsRepository: PlaylistsRepository,
    private val songsRepository: SongsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

//    // key used by navigation route / arguments (must match the nav arg name)
//    companion object {
//        const val NAV_ARG_PLAYLIST_ID = "playlistId"
//    }

//    init {
//        // If navigation provided a playlistId, load automatically
//        savedStateHandle.get<String>(NAV_ARG_PLAYLIST_ID)?.let { id ->
//            loadPlaylistDetails(id)
//        }
//    }

    /**
     * Public: load playlist details and songs for the given id.
     * Safe to call multiple times (will update state).
     */
    fun loadPlaylistDetails(playlistId: String) {
        viewModelScope.launch {
            // set loading
            _state.value = _state.value.copy(isLoading= true, error = null)

            try {
                // Load playlist details
                val playlist = playlistsRepository
                    .getPlaylistDetails(playlistId)
                    .toPlaylistUiState()

                _state.value = _state.value.copy(playlist = playlist)

                // Load songs
                val songs = songsRepository
                    .getSongsFrom(playlistId)
                    .toSongUiState()

                _state.value = _state.value.copy(songs = songs, isLoading = false)
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
    // TODO: add 3 states: Loading/Error/Result
    data class UiState(
        val playlist: Playlist? = null,
        val songs: List<Song> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) {
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
    }
}
