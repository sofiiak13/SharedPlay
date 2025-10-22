package me.sofiiak.sharedplay.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.sofiiak.sharedplay.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFFFFB6C1),
        modifier = Modifier
            .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.systemBars)

    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            items(uiState.value.playlists) { playlist: HomeViewModel.UiState.Playlist ->
                Column {
                    Text(
                        text = playlist.name,
                        style = TextStyle(fontSize = 24.sp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = playlist.lastUpdated)
                }
            }
        }
    }
}