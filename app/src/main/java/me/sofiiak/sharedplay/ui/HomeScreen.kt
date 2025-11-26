package me.sofiiak.sharedplay.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import me.sofiiak.sharedplay.viewmodel.HomeViewModel


// todo: change hardcoded colours to theme
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    LaunchedEffect(Unit) {
        viewModel.onHomeUiEvent(
            HomeViewModel.UiEvent.LoadPlaylists
        )
    }

    HomeScreenContent(
        uiState = uiState,
        uiEvent = viewModel::onHomeUiEvent,
        navController = navController,
    )

//    val playlistUpdated = navController.currentBackStackEntry
//        ?.savedStateHandle
//        ?.getStateFlow("playlistUpdated", false)
//        ?.collectAsState()
//
//    LaunchedEffect(playlistUpdated?.value) {
//        if (playlistUpdated?.value == true) {
//            viewModel.loadPlaylists()
//
//            navController.currentBackStackEntry
//                ?.savedStateHandle
//                ?.set("playlistUpdated", false)
//        }
//    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    uiState: HomeViewModel.UiState,
    uiEvent: (HomeViewModel.UiEvent) -> Unit,
    navController: NavController,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Home") // use ui state
                },
                actions = {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                uiEvent(
                                    HomeViewModel.UiEvent.AddPlaylistButtonClick
                                )
                            },
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                    )

                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFFB6C1)) // soft pink background
        )

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            item {
                Text(
                    text = "My playlists", // todo: use ui state
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

            items(uiState.playlists) { playlist ->
                PlaylistRow(
                    playlist = playlist,
                    onPlaylistClick = { playlistId ->
                        navController.navigate("playlist_details/$playlistId")
                                      },
//                    onPlaylistClick = onPlaylistClick,
//                    onEditClick = { viewModel.editPlaylist(playlist.id) },
//                    onDeleteClick = { viewModel.deletePlaylist(playlist.id) },
//                    onAddSongClick = { viewModel.addSongToPlaylist(playlist.id) }
                )
            }
        }

        uiState.addPlaylistDialog?.let { addPlaylistDialog ->
            AddPlaylistDialog(
                uiState = addPlaylistDialog,
                uiEvent = uiEvent
            )
        }
    }
}

@Composable
private fun PlaylistRow(
    playlist: HomeViewModel.UiState.Playlist,
    onPlaylistClick: (playlistId: String) -> Unit,
//    onEditClick: (String) -> Unit,
//    onDeleteClick: (String) -> Unit,
//    onAddSongClick: (String) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            // detect both click and long-press
            .combinedClickable(
                onClick = { onPlaylistClick(playlist.id) },
                onLongClick = { menuExpanded = true }
            )
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = playlist.name,
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.Black
        )
        Text(
            text = "Last updated on ${playlist.lastUpdated}",
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray
            ),
            modifier = Modifier.padding(top = 4.dp)
        )

//        DropdownMenu(
//            expanded = menuExpanded,
//            onDismissRequest = { menuExpanded = false }
//        ) {
//            DropdownMenuItem(
//                text = { Text("Edit Playlist") }, //TODO: break into rename and add owners
//                onClick = {
//                    menuExpanded = false
//                    onEditClick(playlist.id)
//                }
//            )
//            DropdownMenuItem(
//                text = { Text("Delete Playlist") },
//                onClick = {
//                    menuExpanded = false
//                    onDeleteClick(playlist.id)
//                }
//            )
//            DropdownMenuItem(
//                text = { Text("Add Song") },
//                onClick = {
//                    menuExpanded = false
//                    onAddSongClick(playlist.id)
//                }
//            )
//        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            thickness = 1.dp,
            color = Color.Black.copy(alpha = 0.1f)
        )
    }
}

@Composable
private fun AddPlaylistDialog(
    uiState: HomeViewModel.UiState.AddPlaylistDialog,
    uiEvent: (HomeViewModel.UiEvent) -> Unit,
) {
    var newName by remember { mutableStateOf("") }


    AlertDialog(
        onDismissRequest = {
            uiEvent(
                HomeViewModel.UiEvent.AddPlaylistDialogDismiss
            )
        },
        title = { Text(uiState.title) },
        text = {
            TextField(
                value = newName,
                onValueChange = { newName = it },
                placeholder = { Text(uiState.placeholder) },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    uiEvent(
                        HomeViewModel.UiEvent.AddPlaylistConfirmButtonClick(
                            newName = newName
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
                        HomeViewModel.UiEvent.AddPlaylistDialogDismiss
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
fun HomePreview() {
    HomeScreenContent(
        uiState = HomeViewModel.UiState.preview(),
        uiEvent = {},
        navController = NavController(LocalContext.current)
    )
}
