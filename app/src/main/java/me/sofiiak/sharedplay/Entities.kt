package me.sofiiak.sharedplay

data class User(
    val email: String = "",
    val name: String = "",
    val password: String = "",
    val playlists: List<String> = emptyList()       // list of Playlist IDs
)

data class Playlist(
    val name: String = "",
    val owner: String = "",              // user ID
    val editors: List<String> = emptyList(),      // list of user IDs
    val dateCreated: String = "",
    val lastUpdated: String = "",
    val songs: List<String> = emptyList()           // list of Song IDs
)

data class Song(
    val title: String = "",
    val artist: String = "",
    val addedBy: String = "",            // user ID
    val comments: List<String> = emptyList()       // list of Comment IDs
)
data class Comment(
    val text: String = "",
    val author: String = "",             // user ID
    val date: String = "",
    val prev: String = "",
)

data class Reaction(
    val emoji: String = "",
    val author: String = "",             // user ID
    val date: String = ""
)