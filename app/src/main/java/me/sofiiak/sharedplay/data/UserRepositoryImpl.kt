package me.sofiiak.sharedplay.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import me.sofiiak.sharedplay.data.dto.UserRequest
import me.sofiiak.sharedplay.data.dto.UserResponse
import retrofit2.HttpException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val service: Service,
) : UserRepository {
    override suspend fun signIn(): Result<UserResponse> {
        val user = FirebaseAuth.getInstance().currentUser
        user ?: return Result.failure(Exception("User not found"))

        return runCatching {
            service.getUser(user.uid)
        }.recoverCatching { error ->
            when {
                error is HttpException &&
                        error.code() == HTTP_ERROR_USER_NOT_FOUND  ->
                    service.createUser(user.toUserResponse())

                else -> throw error
            }
        }
    }

    private fun FirebaseUser.toUserResponse() = UserRequest(
        id = uid,
        name = displayName ?: DEFAULT_NAME,
        email = email ?: DEFAULT_EMAIL,
    )

    companion object {
        private const val HTTP_ERROR_USER_NOT_FOUND = 404
        private const val DEFAULT_NAME = "No name"
        private const val DEFAULT_EMAIL = "No email"
    }
}

