package me.sofiiak.sharedplay.data

import me.sofiiak.sharedplay.data.dto.InviteResponse
import me.sofiiak.sharedplay.data.dto.PlaylistResponse
import me.sofiiak.sharedplay.data.dto.PlaylistUpdate

interface PlaylistsRepository {
    suspend fun getPlaylistsForUser(userId: String): Result<List<PlaylistResponse>>
    suspend fun getPlaylistDetails(playlistId: String): Result<PlaylistResponse>
    suspend fun createPlaylist(playlist: PlaylistResponse): Result<PlaylistResponse>
    suspend fun editPlaylist(playlistId: String, playlist: PlaylistUpdate): Result<PlaylistResponse>
    suspend fun deletePlaylist(playlistId: String): Result<String>

    suspend fun createInvite(playlistId: String, userId: String): Result<InviteResponse>
    suspend fun validateInvite(inviteId: String): Result<InviteResponse>
    suspend fun addEditor(playlistId: String, userId: String): Result<String>
}


