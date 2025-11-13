package me.sofiiak.sharedplay.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import me.sofiiak.sharedplay.viewmodel.PlaylistDetailsViewModel

private const val TAG = "PlaylistDetails"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetails(
//    playlistId: String,
    navController: NavController,
    viewModel: PlaylistDetailsViewModel = hiltViewModel(),
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

//    LaunchedEffect(playlistId) {
//        viewModel.loadPlaylistDetails(playlistId)
//    }

    PlaylistDetailsContent(
        uiState = uiState,
        onToolbarBackClick = { navController.popBackStack() },
        uiEvent = viewModel::onUiEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistDetailsContent(
    uiState: PlaylistDetailsViewModel.UiState,
    uiEvent: (PlaylistDetailsViewModel.UiEvent) -> Unit,
    onToolbarBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(uiState.playlist?.name ?: "Playlist") // use ui state
                },
                navigationIcon = {
                    IconButton(onClick = onToolbarBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = uiState.toolbar.backButtonContentDescription,
                        )
                    }
                },
                actions = {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                uiEvent(
                                    PlaylistDetailsViewModel.UiEvent.AddSongButtonClick
                                )
                            },
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                    )

                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                    )

                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                    )

                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFFB6C1)) // soft pink background
        ) {
            when {
                uiState.isLoading -> {
                    Text(
                        text = "Wait a second...", // use ui state
                        color = Color.Blue,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null -> {
                    Text(
                        text = uiState.error,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.DarkGray
                    )

//                    Text(
//                        text = "Error: $error",
//                        color = Color.Red,
//                        modifier = Modifier.align(Alignment.Center)
//                    )
                }

//                uiState.songs.isEmpty() -> {
//                    Text(
//                        text = "No songs have been added yet 🎶",  // use ui state
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Medium,
//                        modifier = Modifier.align(Alignment.Center),
//                        color = Color.DarkGray
//                    )
//                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {

                        items(uiState.songs) { song ->
                            SongCard(song)
                        }
                    }
                }
            }
        }
    }

    uiState.addSongDialog?.let { addSongDialog ->
        AddSongDialog(
            uiState = addSongDialog,
            uiEvent = uiEvent,
        )
    }
}

@Composable
private fun SongCard(song: PlaylistDetailsViewModel.UiState.Song) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "by ${song.artist}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun AddSongDialog(
    uiState: PlaylistDetailsViewModel.UiState.AddSongDialog,
    uiEvent: (PlaylistDetailsViewModel.UiEvent) -> Unit,
) {
    var songUrl by remember { mutableStateOf("") }


    AlertDialog(
        onDismissRequest = {
            uiEvent(
                PlaylistDetailsViewModel.UiEvent.AddSongDialogDismiss
            )
        },
        title = { Text(uiState.title) },
        text = {
            TextField(
                value = songUrl,
                onValueChange = { songUrl = it },
                placeholder = { Text(uiState.placeholder) },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    uiEvent(
                        PlaylistDetailsViewModel.UiEvent.AddSongDialogConfirmButtonClick(
                            songUrl = songUrl
                        )
                    )
                }
            ) {
                Text(uiState.buttonConfirm)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    uiEvent(
                        PlaylistDetailsViewModel.UiEvent.AddSongDialogDismiss
                    )
                }
            ) {
                Text(uiState.buttonCancel)
            }
        }
    )
}

@Preview
@Composable
fun PlaylistDetailsPreview() {
    PlaylistDetailsContent(
        uiState = PlaylistDetailsViewModel.UiState.preview(),
        uiEvent = {},
        onToolbarBackClick = {},
    )
}

