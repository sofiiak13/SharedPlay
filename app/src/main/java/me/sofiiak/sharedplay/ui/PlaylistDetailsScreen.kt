package me.sofiiak.sharedplay.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import me.sofiiak.sharedplay.viewmodel.PlaylistDetailsViewModel

@Composable
fun PlaylistDetails(
    navController: NavController,
    viewModel: PlaylistDetailsViewModel = hiltViewModel(),
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    val uiEvent = viewModel::onUiEvent

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            uiEvent(
                PlaylistDetailsViewModel.UiEvent.LoadSongs
            )
        }
    }

    PlaylistDetailsContent(
        uiState = uiState,
        navController = navController,
        uiEvent = uiEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistDetailsContent(
    uiState: PlaylistDetailsViewModel.UiState,
    navController: NavController,
    uiEvent: (PlaylistDetailsViewModel.UiEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(uiState.playlist?.name ?: "Playlist") // use ui state
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                        contentDescription = "Button to add new song to current playlist.",
                    )

                    Icon(
                        modifier = Modifier
                            .clickable {
                                uiEvent(
                                    PlaylistDetailsViewModel.UiEvent.DeletePlaylistButtonClick
                                )
                            },
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Button to delete current playlist.",
                    )

                    Icon(
                        modifier = Modifier
                            .clickable {
                                uiEvent(
                                    PlaylistDetailsViewModel.UiEvent.EditPlaylistNameButtonClick
                                )
                            },
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Button to rename current playlist.",
                    )

                    Icon(
                        modifier = Modifier
                            .clickable {
                                uiEvent(
                                    PlaylistDetailsViewModel.UiEvent.SharePlaylistButtonClick
                                )
                            },
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Button to share current playlist.",
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
            if (uiState.error != null) {
                Text(
                    text = uiState.error,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.DarkGray
                )

            } else { // handle loading or refreshing
                if (uiState.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                PullToRefreshBox(
                    isRefreshing = false,
                    onRefresh = {
                        uiEvent(
                            PlaylistDetailsViewModel.UiEvent.LoadSongs
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {

                        items(uiState.songs) { song ->
                            SongCard(song, navController)
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

    uiState.deletePlaylistDialog?.let { deletePlaylistDialog ->
        DeletePlaylistDialog(
            uiState = deletePlaylistDialog,
            uiEvent = uiEvent,
        )
    }

    uiState.editPlaylistNameDialog?.let { editPlaylistNameDialog ->
        EditPlaylistNameDialog(
            uiState = editPlaylistNameDialog,
            uiEvent = uiEvent,
        )
    }

    uiState.sharePlaylistDialog?.let { sharePlaylistDialog ->
        SharePlaylistDialog(
            uiState = sharePlaylistDialog,
            uiEvent = uiEvent,
        )
    }

}

@Composable
private fun SongCard(song: PlaylistDetailsViewModel.UiState.Song, navController: NavController) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                navController.navigate(
                    "playlist_details/${song.playlistId}/song/${song.id}"
                )
            },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "by ${song.artist}", // todo: use song class for this
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                if (song.lastComment != null) {
                    val comment = song.lastComment

                    Text(
                        text = "${comment.author}:",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = comment.text,
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (song.totalComments > 1) {
                        Text(
                            text = "+ ${song.totalComments - 1} comments",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
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

@Composable
private fun DeletePlaylistDialog(
    uiState: PlaylistDetailsViewModel.UiState.DeletePlaylistDialog,
    uiEvent: (PlaylistDetailsViewModel.UiEvent) -> Unit,
) {


    AlertDialog(
        onDismissRequest = {
            uiEvent(
                PlaylistDetailsViewModel.UiEvent.DeletePlaylistDialogDismiss
            )
        },
        title = { Text(uiState.title) },
        confirmButton = {
            TextButton(
                onClick = {
                    uiEvent(
                        PlaylistDetailsViewModel.UiEvent.DeletePlaylistConfirmButtonClick
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
                        PlaylistDetailsViewModel.UiEvent.DeletePlaylistDialogDismiss
                    )
                }
            ) {
                Text(uiState.buttonCancel)
            }
        }
    )
}


@Composable
private fun EditPlaylistNameDialog(
    uiState: PlaylistDetailsViewModel.UiState.EditPlaylistNameDialog,
    uiEvent: (PlaylistDetailsViewModel.UiEvent) -> Unit,
) {
    var newName by remember { mutableStateOf("") }


    AlertDialog(
        onDismissRequest = {
            uiEvent(
                PlaylistDetailsViewModel.UiEvent.EditPlaylistNameDismiss
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
                        PlaylistDetailsViewModel.UiEvent.EditPlaylistNameConfirmButtonClick(
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
                        PlaylistDetailsViewModel.UiEvent.EditPlaylistNameDismiss
                    )
                }
            ) {
                Text(uiState.buttonCancel)
            }
        }
    )
}

@Composable
private fun SharePlaylistDialog(
    uiState: PlaylistDetailsViewModel.UiState.SharePlaylistDialog,
    uiEvent: (PlaylistDetailsViewModel.UiEvent) -> Unit,
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = {
            uiEvent(
                PlaylistDetailsViewModel.UiEvent.SharePlaylistDismiss
            )
        },
        title = { Text(uiState.title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Read-only text field showing the link
                OutlinedTextField(
                    value = uiState.link,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    clipboard.setText(AnnotatedString(uiState.link))
                    Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text("Copy")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    uiEvent(PlaylistDetailsViewModel.UiEvent.SharePlaylistDismiss)
                }
            ) {
                Text("Cancel")
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
        navController = NavController(LocalContext.current)
    )
}

