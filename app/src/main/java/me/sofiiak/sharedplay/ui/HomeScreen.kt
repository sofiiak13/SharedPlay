package me.sofiiak.sharedplay.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.sofiiak.sharedplay.viewmodel.HomeViewModel


// todo: change hardcoded colours to theme
@Composable
fun HomeScreen(
    onPlaylistClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle()
//    var showAddDialog by remember { mutableStateOf(false) } // todo: question is there a more elegant way to do this?
//    var newPlaylistName by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color(0xFFFFB6C1),
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            item {
                Text(
                    text = "My playlists",
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

            items(uiState.value.playlists) { playlist ->
                PlaylistRow(
                    playlist = playlist,
                    onPlaylistClick = onPlaylistClick,
                    onEditClick = { viewModel.editPlaylist(playlist.id) },
                    onDeleteClick = { viewModel.deletePlaylist(playlist.id) },
                    onAddSongClick = { viewModel.addSongToPlaylist(playlist.id) }
                )
            }

            item {
                Text(
                    text = "+", // use ui state
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 20.dp),
//                        .clickable { showAddDialog = true },
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // ➕ Add Playlist Dialog
//    if (showAddDialog) {
//        AlertDialog(
//            onDismissRequest = { showAddDialog = false },
//            title = { Text("Create New Playlist") },
//            text = {
//                TextField(
//                    value = newPlaylistName,
//                    onValueChange = { newPlaylistName = it },
//                    placeholder = { Text("Enter playlist name") },
//                    singleLine = true,
//                )
//            },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        if (newPlaylistName.isNotBlank()) {
////                            viewModel.createPlaylist(newPlaylistName)
//                            newPlaylistName = ""
//                            showAddDialog = false
//                        }
//                    }
//                ) {
//                    Text("Create")
//                }
//            },
//            dismissButton = {
//                TextButton(
//                    onClick = {
//                        showAddDialog = false
//                        newPlaylistName = ""
//                    }
//                ) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
}

@Composable
private fun PlaylistRow(
    playlist: HomeViewModel.UiState.Playlist,
    onPlaylistClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onAddSongClick: (String) -> Unit,
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

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit Playlist") }, //TODO: rename playlist basically?
                onClick = {
                    menuExpanded = false
                    onEditClick(playlist.id)
                }
            )
            DropdownMenuItem(
                text = { Text("Delete Playlist") },
                onClick = {
                    menuExpanded = false
                    onDeleteClick(playlist.id)
                }
            )
            DropdownMenuItem(
                text = { Text("Add Song") },
                onClick = {
                    menuExpanded = false
                    onAddSongClick(playlist.id)
                }
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            thickness = 1.dp,
            color = Color.Black.copy(alpha = 0.1f)
        )
    }
}
