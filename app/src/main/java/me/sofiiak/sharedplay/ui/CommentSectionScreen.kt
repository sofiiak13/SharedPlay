package me.sofiiak.sharedplay.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import me.sofiiak.sharedplay.viewmodel.CommentSectionViewModel
import me.sofiiak.sharedplay.viewmodel.CommentSectionViewModel.UiEvent

@Composable
fun CommentSection(
    navController: NavController,
    viewModel: CommentSectionViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    CommentSectionContent(
        uiState = uiState,
        navController = navController,
        uiEvent = viewModel::onUiEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentSectionContent(
    uiState: CommentSectionViewModel.UiState,
    navController: NavController,
    uiEvent: (UiEvent) -> Unit,
) {
    uiState.deleteCommentDialog?.let { dialog ->
        DeleteCommentDialog(
            uiState = dialog,
            uiEvent = uiEvent
        )
    }

    uiState.editCommentDialog?.let { editCommentDialog ->
        EditCommentDialog(
            uiState = editCommentDialog,
            uiEvent = uiEvent
        )
    }

    uiState.writeCommentDialog?.let { writeCommentDialog ->
        WriteCommentDialog(
            uiState = writeCommentDialog,
            uiEvent = uiEvent
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comments") },
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
                                    UiEvent.WriteNewComment
                                )
                            },
                        imageVector = Icons.Filled.Add,
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
                .background(Color(0xFFFFB6C1))
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
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        items(uiState.comments) { comment ->
                            CommentItem(
                                comment = comment,
                                onEditClick = {
                                    uiEvent(
                                        UiEvent.DropdownMenu.EditComment(comment.id)
                                    )
                                },
                                onDeleteClick = {
                                    uiEvent(
                                        UiEvent.DropdownMenu.DeleteComment(comment.id)
                                    )
                                },
                                onReplyClick = {
                                    uiEvent(
                                        UiEvent.DropdownMenu.ReplyToComment(comment.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: CommentSectionViewModel.UiState.Comment,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onReplyClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (comment.depth * 16).dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                CommentMenu(
                    onEdit = { onEditClick(comment.id) },
                    onDelete = { onDeleteClick(comment.id) },
                    onReply = { onReplyClick(comment.id) }
                )
            }
            Text(
                text = comment.author,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
            Text(
                text = comment.date,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.End),
                color = Color.DarkGray,
            )
        }
    }
}

@Composable
fun CommentMenu(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onReply: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.size(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert, // three-dot icon
                contentDescription = "More options"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    expanded = false
                    onEdit()
                })
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    expanded = false
                    onDelete()
                })
            DropdownMenuItem(
                text = { Text("Reply") },
                onClick = {
                    expanded = false
                    onReply()
                })
        }
    }
}

@Composable
fun WriteCommentDialog(
    uiState: CommentSectionViewModel.UiState.WriteCommentDialog,
    uiEvent: (UiEvent) -> Unit,
) {

    var newText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            uiEvent(
                UiEvent.WriteAlert.DismissButton
            )
        },
        title = {
            Text(uiState.title)
        },
        text = {
            TextField(
                value = newText,
                onValueChange = { newText = it }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    uiEvent(
                        UiEvent.WriteAlert.ConfirmButton(
                            prevCommentId = uiState.prevCommentId,
                            text = newText,
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
                        UiEvent.WriteAlert.DismissButton
                    )
                }
            ) {
                Text(uiState.buttonCancel)
            }
        }
    )
}

@Composable
fun EditCommentDialog(
    uiState: CommentSectionViewModel.UiState.EditCommentDialog,
    uiEvent: (UiEvent) -> Unit,
) {
    var newText by remember { mutableStateOf(uiState.comment.text) }

    AlertDialog(
        onDismissRequest = {
            uiEvent(
                UiEvent.EditAlert.DismissButton
            )
        },
        title = {
            Text(uiState.title)
        },
        text = {
            TextField(
                value = newText,
                onValueChange = { newText = it }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    uiEvent(
                        UiEvent.EditAlert.ConfirmButton(
                            commentId = uiState.comment.id,
                            newText = newText,
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
                        UiEvent.EditAlert.DismissButton
                    )
                }
            ) {
                Text(uiState.buttonCancel)
            }
        }
    )
}

@Composable
fun DeleteCommentDialog(
    uiState: CommentSectionViewModel.UiState.DeleteCommentDialog,
    uiEvent: (UiEvent) -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            uiEvent(
                UiEvent.DeleteAlert.DismissButton
            )
        },
        title = { Text(uiState.title) },
        confirmButton = {
            TextButton(
                onClick = {
                    uiEvent(
                        UiEvent.DeleteAlert.ConfirmButton(
                            commentId = uiState.commentId
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
                        UiEvent.DeleteAlert.DismissButton
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
fun CommentSectionPreview() {
    CommentSectionContent(
        uiState = CommentSectionViewModel.UiState.preview(),
        navController = NavController(LocalContext.current),
        uiEvent = {}
    )
}
