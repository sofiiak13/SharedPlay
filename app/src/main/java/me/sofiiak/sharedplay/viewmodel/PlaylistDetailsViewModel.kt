package me.sofiiak.sharedplay.viewmodel

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
    private val songsRepository: SongsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // Load playlist details first
            val playlist = playlistsRepository
                .getPlaylistDetails("-ObVJBFFjKK-oMGxREs1")
                .toPlaylistUiState()

            _state.value = _state.value.copy(playlist = playlist)

            // Then load songs
            val songs = songsRepository
                .getSongsFrom("-ObVJBFFjKK-oMGxREs1")
                .toSongUiState()

            _state.value = _state.value.copy(songs = songs)
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
        val songs: List<Song> = emptyList()
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
