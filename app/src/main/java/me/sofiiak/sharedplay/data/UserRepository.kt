package me.sofiiak.sharedplay.data

import me.sofiiak.sharedplay.data.dto.UserResponse

interface UserRepository {
    suspend fun signIn(): Result<UserResponse>
//    suspend fun isUserSignedId(): Result<Boolean>
}