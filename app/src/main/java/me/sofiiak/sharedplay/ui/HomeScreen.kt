package me.sofiiak.sharedplay.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import me.sofiiak.sharedplay.viewmodel.HomeViewModel


// todo: change hardcoded colours to theme
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.onHomeUiEvent(
                HomeViewModel.UiEvent.InitScreen
            )
        }
    }

    HomeScreenContent(
        uiState = uiState,
        uiEvent = viewModel::onHomeUiEvent,
        navController = navController,
    )

    uiState.inviteMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.clearInviteMessage() },
            title = { Text("Invitation") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearInviteMessage() }) {
                    Text("OK")
                }
            }
        )
    }

    if (uiState.needSignIn) {
        viewModel.onHomeUiEvent(HomeViewModel.UiEvent.NavigatedToSignIn)
        navController.navigate("sign-in")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    uiState: HomeViewModel.UiState,
    uiEvent: (HomeViewModel.UiEvent) -> Unit,
    navController: NavController,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Home") // use ui state
                },
                actions = {
                    IconButton(onClick = { uiEvent(HomeViewModel.UiEvent.AddPlaylistButtonClick) }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Playlist",
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Sign Out") },
                icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                onClick = {
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        AuthUI.getInstance()
                            .signOut(context)
                            .addOnCompleteListener {
                                Log.d("MainActivity", "Signed out successfully")

                                navController.navigate("sign-in") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFFB6C1)) // soft pink background
        ) {
            // Check for initial loading (loading is true AND the list is empty)
            if (uiState.error != null) {
                Text(
                    text = uiState.error,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 32.dp)
                )
            } else if (uiState.playlists.isEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text(
                    text = "Wait a second...", // use ui state
                    color = Color.Blue,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else { // handle loading or refreshing
                if (uiState.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                PullToRefreshBox(
                    isRefreshing = false,
                    onRefresh = {
                        uiEvent(
                            HomeViewModel.UiEvent.LoadPlaylists
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
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

                        items(uiState.playlists) { playlist ->
                            PlaylistRow(
                                playlist = playlist,
                                onPlaylistClick = { playlistId ->
                                    navController.navigate("playlist_details/$playlistId")
                                },
                            )
                        }
                    }
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
}

@Composable
private fun PlaylistRow(
    playlist: HomeViewModel.UiState.Playlist,
    onPlaylistClick: (playlistId: String) -> Unit,
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
