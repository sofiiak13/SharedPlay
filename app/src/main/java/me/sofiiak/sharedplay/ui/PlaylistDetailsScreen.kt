package me.sofiiak.sharedplay.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import me.sofiiak.sharedplay.viewmodel.HomeViewModel
import me.sofiiak.sharedplay.viewmodel.PlaylistDetailsViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetails(
    playlistId: String,
    viewModel: PlaylistDetailsViewModel = hiltViewModel(),
    navController: NavController,
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(playlistId) {
        viewModel.loadPlaylistDetails(playlistId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Playlists") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = Color(0xFFFFB6C1), // TODO: replace hardcoded color with Theme color
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
        ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    Text("Loading…")
                }

                uiState.error != null -> {
                    Text("Error: ${uiState.error}")
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        item {
                            Text(
                                text = "${uiState.playlist?.name}",
                                style = TextStyle(
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 20.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        items(uiState.songs) { song ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            ) {
                                Text(
                                    text = song.title,
                                    style = TextStyle(
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.Black
                                )
                                Text(
                                    // TODO: move the UI logic to viewmodel
                                    text = "by ${song.artist}",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    ),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                thickness = 1.dp,
                                color = Color.Black.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
