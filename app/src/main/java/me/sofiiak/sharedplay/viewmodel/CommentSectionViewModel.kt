package me.sofiiak.sharedplay.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.sofiiak.sharedplay.data.CommentSectionRepository
import me.sofiiak.sharedplay.data.dto.CommentResponse
import me.sofiiak.sharedplay.data.formatDate
import kotlin.collections.isNotEmpty
import kotlin.text.isNotEmpty

@HiltViewModel
class CommentSectionViewModel @Inject constructor(
    private val commentRepository: CommentSectionRepository,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val songId: String = savedStateHandle["songId"] ?: ""
    private val _state = MutableStateFlow(UiState(
        toolbar = UiState.Toolbar(
            title = "Comments",
            backButtonContentDescription = "Back"
        )
    ))

    val state = _state.asStateFlow()

    private fun List<CommentResponse>.toUiState() = UiState (
        toolbar = UiState.Toolbar(
            title = "Comments",
            backButtonContentDescription = "Back"
        ),
        comments = this.map { commentResponse ->
            UiState.Comment(
                id = commentResponse.id,
                text = commentResponse.text,
                author = commentResponse.author,
                date = formatDate(commentResponse.date),
            )
        }
    )

    init {
        if (validateSongId()) {
            loadComments(songId)
        }
    }

    private fun validateSongId(): Boolean {
        val isValid = songId.isNotEmpty()
        if (isValid.not()) {
            _state.update {
                it.copy(error = "Invalid song id")
            }
        }
        return isValid
    }

    fun loadComments(songId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val comments = commentRepository
                    .getAllComments(songId)
                    .getOrNull()
                    ?.toCommentUiState()
                    ?: emptyList()

                if (comments.isNotEmpty()) {
                    _state.value = _state.value.copy(comments = comments, isLoading = false)
                    _state.update {
                        it.copy(comments = comments, isLoading = false)
                    }
                } else {
                    _state.update {
                        it.copy(error="No one has commented on this song yet!", isLoading = false)
                    }
                }
            } catch (t: Throwable) {
                // handle error
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = t.message ?: "Unknown error"
                )
            }
        }
    }

    private fun List<CommentResponse>.toCommentUiState() =
        this.map { commentResponse ->
            UiState.Comment(
                id = commentResponse.id,
                text = commentResponse.text,
                author = commentResponse.author,
                date = formatDate(commentResponse.date),
                depth = commentResponse.depth
            )
        }

    fun onUiEvent(uiEvent: UiEvent) {
        when (uiEvent) {
            is UiEvent.DropdownMenu -> onUiEvent(uiEvent)

            is UiEvent.WriteAlert -> onUiEvent(uiEvent = uiEvent)

            is UiEvent.EditAlert -> onUiEvent(uiEvent = uiEvent)

            is UiEvent.DeleteAlert -> onUiEvent(uiEvent = uiEvent)

            UiEvent.WriteNewComment -> _state.update {
                // new comment has no previous comment
                it.copy(writeCommentDialog = getWriteCommentDialog(null))
            }
        }
    }

    private fun onUiEvent(uiEvent: UiEvent.DropdownMenu) {
        when (uiEvent) {
            is UiEvent.DropdownMenu.ReplyToComment -> _state.update {
                it.copy(
                    writeCommentDialog = getWriteCommentDialog(uiEvent.prevCommentId)
                )
            }

            is UiEvent.DropdownMenu.EditComment -> {
                state.value.comments.find(commentId = uiEvent.commentId)?.let { comment ->
                    _state.update {
                        it.copy(
                            editCommentDialog = getEditCommentDialog(comment)
                        )
                    }
                }
            }

            is UiEvent.DropdownMenu.DeleteComment -> _state.update {
                it.copy(
                    deleteCommentDialog = getDeleteCommentDialog(uiEvent.commentId)
                )
            }
        }
    }

    private fun List<UiState.Comment>.find(commentId: String) =
        find { comment -> comment.id == commentId }

    private fun onUiEvent(uiEvent: UiEvent.EditAlert) {
        when (uiEvent) {
            is UiEvent.EditAlert.ConfirmButton ->
                editComment(uiEvent)

            UiEvent.EditAlert.DismissButton -> hideEditCommentDialog()
        }
    }

    private fun onUiEvent(uiEvent: UiEvent.WriteAlert) {
        when (uiEvent) {
            is UiEvent.WriteAlert.ConfirmButton ->
                viewModelScope.launch {
                    commentRepository.writeComment(
                        CommentResponse(
                            id = "", //placeholder
                            text = uiEvent.text,
                            prev = uiEvent.prevCommentId,
                            author = "Name 1",
                            author_id = "1",
                            song_id = songId,
                        )
                    )
                    hideWriteCommentDialog()
                    _state.update {
                        it.copy(isLoading = true)
                    }
                    loadComments(songId)
                }

            UiEvent.WriteAlert.DismissButton -> hideWriteCommentDialog()
        }
    }

    private fun editComment(uiEvent: UiEvent.EditAlert.ConfirmButton) {
        viewModelScope.launch {
            commentRepository.editComment(
                commentId = uiEvent.commentId,
                newText = uiEvent.newText
            )
            hideEditCommentDialog()
            _state.update {
                it.copy(isLoading = true)
            }
            loadComments(songId)
        }
    }

    private fun onUiEvent(uiEvent: UiEvent.DeleteAlert) {
        when (uiEvent) {
            is UiEvent.DeleteAlert.ConfirmButton -> deleteComment(uiEvent)

            UiEvent.DeleteAlert.DismissButton -> hideDeleteCommentDialog()
        }
    }

    private fun deleteComment(uiEvent: UiEvent.DeleteAlert.ConfirmButton) {
        viewModelScope.launch {
            commentRepository.deleteComment(commentId = uiEvent.commentId)
            hideDeleteCommentDialog()
            _state.update {
                it.copy(isLoading = true)
            }
            loadComments(songId)
        }
    }

    private fun getWriteCommentDialog(prevCommentId: String?) = UiState.WriteCommentDialog(
        title = "Writing a comment...",
        buttonConfirm = "Post",
        buttonCancel = "Cancel",
        prevCommentId = prevCommentId
    )

    private fun hideWriteCommentDialog() {
        _state.update {
            it.copy(writeCommentDialog = null)
        }
    }

    private fun getDeleteCommentDialog(commentId: String) = UiState.DeleteCommentDialog(
        title = "Delete this comment?",
        buttonConfirm = "Delete",
        buttonCancel = "Cancel",
        commentId = commentId
    )

    private fun hideDeleteCommentDialog() {
        _state.update {
            it.copy(deleteCommentDialog = null)
        }
    }

    private fun getEditCommentDialog(comment: UiState.Comment) = UiState.EditCommentDialog(
        title = "Edit Comment",
        buttonConfirm = "Edit",
        buttonCancel = "Cancel",
        comment = comment,
    )

    private fun hideEditCommentDialog() {
        _state.update {
            it.copy(editCommentDialog = null)
        }
    }

    data class UiState(
        val toolbar: Toolbar,
        val comments: List<Comment> = emptyList(),
        val writeCommentDialog: WriteCommentDialog? = null,
        val deleteCommentDialog: DeleteCommentDialog? = null,
        val editCommentDialog: EditCommentDialog? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) {
        data class Toolbar(
            val title: String,
            val backButtonContentDescription: String = ""
        )

        data class Comment(
            val id: String,
            val text: String,
            val author: String,
            val date: String,
            val depth: Int = 0
        )

        data class WriteCommentDialog(
            val title: String,
            val buttonConfirm: String,
            val buttonCancel: String,
            val prevCommentId: String? = null
        )

        data class DeleteCommentDialog(
            val title: String,
            val buttonConfirm: String,
            val buttonCancel: String,
            val commentId: String
        )

        data class EditCommentDialog(
            val title: String,
            val buttonConfirm: String,
            val buttonCancel: String,
            val comment: Comment,
        )


        data class Reaction(
            val id: String,
            val emoji: String,
            val author: String
        )

        companion object {
            fun preview(): CommentSectionViewModel.UiState =
                UiState(
                    toolbar = Toolbar(
                        title = "CommentSection",
                        backButtonContentDescription = "Back"
                    ),
                    comments = listOf(
                        Comment(
                            id = "1",
                            text = "Comment 1",
                            author = "Lily",
                            date = "Apr 11, 2025"
                        ),
                        Comment(
                            id = "4",
                            text = "Reply 1",
                            author = "Harry",
                            date = "Apr 11, 2025",
                            depth = 1
                        ),
                        Comment(
                            id = "3",
                            text = "Reply 2",
                            author = "lily",
                            date = "Apr 12, 2025",
                            depth = 2
                        ),
                        Comment(
                            id = "2",
                            text = "Comment 2",
                            author = "James",
                            date = "Sep 30, 2025"
                        )
                    )
                )
        }
    }

    sealed interface UiEvent {

        sealed interface DropdownMenu : UiEvent {
            data class ReplyToComment(
                val prevCommentId: String,
            ) : DropdownMenu

            data class DeleteComment(
                val commentId: String,
            ) : DropdownMenu

            data class EditComment(
                val commentId: String,
            ) : DropdownMenu
        }

        data object WriteNewComment : UiEvent

        sealed interface WriteAlert : UiEvent {
            data class ConfirmButton(
                val prevCommentId: String? = null,
                val text: String,
            ) : WriteAlert

            data object DismissButton: WriteAlert
        }

        sealed interface DeleteAlert : UiEvent {
            data class ConfirmButton(
                val commentId: String,
            ) : DeleteAlert
            data object DismissButton : DeleteAlert
        }

        sealed interface EditAlert : UiEvent {
            data class ConfirmButton(
                val commentId: String,
                val newText: String,
            ): EditAlert

            data object DismissButton : EditAlert

        }
    }
}