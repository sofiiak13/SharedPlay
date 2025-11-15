package me.sofiiak.sharedplay.data.dto

data class PlaylistUpdate (
        val name: String? = null,
        val owner: String? = null,
        val editors: List<String>? = null,
)