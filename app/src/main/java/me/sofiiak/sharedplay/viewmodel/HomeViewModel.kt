package me.sofiiak.sharedplay.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.sofiiak.sharedplay.data.PlaylistsRepository
import me.sofiiak.sharedplay.data.dto.PlaylistResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "HomeViewModel"
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PlaylistsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        loadPlaylists()
    }

    private fun List<PlaylistResponse>.toUiState() = UiState(
        this.map { playlistResponse ->
            UiState.Playlist(
                id = playlistResponse.id,
                name = playlistResponse.name,
                lastUpdated = formatDate(playlistResponse.last_updated),
            )
        }
    )

    fun loadPlaylists() {
        viewModelScope.launch {
            val result = repository.getPlaylistsForUser(userId = "-Odv6YSev5ZNYnDdis9d")
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
            repository.deletePlaylist(id)
                .onSuccess {
                    loadPlaylists() // refresh list
                }
                .onFailure {
                    // show error message
                }
        }
    }

    fun addSongToPlaylist(id: String) {
        // navigate or open UI for adding song
    }

    fun editPlaylist(id: String) {
        // trigger navigation to edit screen
    }


    private fun formatDate(date: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        return date.format(formatter)
    }

    // TODO: add 3 states: Loading/Error/Result
    data class UiState(
        val playlists: List<Playlist> = emptyList(),
        val error: String? = null
    ) {
        data class Playlist(
            val id: String,
            val name: String,
            val lastUpdated: String,
        )
    }
}