package me.sofiiak.sharedplay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.sofiiak.sharedplay.data.PlaylistsRepository
import me.sofiiak.sharedplay.data.datasource.dto.PlaylistResponse
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PlaylistsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = repository
                .getPlaylistsForUser("-Obxqhr22iL8uHRQlaXM")
                .toUiState()

        }
    }

    private fun List<PlaylistResponse>.toUiState() = UiState(
        this.map { playlistResponse ->
            UiState.Playlist(
                id = playlistResponse.id,
                name = playlistResponse.name,
                lastUpdated = playlistResponse.last_updated,
            )
        }
    )

    // TODO: add 3 states: Loading/Error/Result
    data class UiState(
        val playlists: List<Playlist> = emptyList(),
    ) {
        data class Playlist(
            val id: String,
            val name: String,
            val lastUpdated: String,
        )
    }
}