package me.sofiiak.sharedplay.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val auth: FirebaseAuth
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val user = auth.currentUser
        val original = chain.request()

        // If user not logged in, send request as-is
        if (user == null) {
            return chain.proceed(original)
        }

        val token = runBlocking {
            user.getIdToken(false).await().token
        }

        val request = original.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }
}
