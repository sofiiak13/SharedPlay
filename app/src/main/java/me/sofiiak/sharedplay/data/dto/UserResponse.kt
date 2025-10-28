package me.sofiiak.sharedplay.data.dto

data class UserResponse(
    val id: String ,
    val email: String ,
    val name: String ,
    val password: String,
    val date_joined: String ,
    val friends: List<String>,
)
