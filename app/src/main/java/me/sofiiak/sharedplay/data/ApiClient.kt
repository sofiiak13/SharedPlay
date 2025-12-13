package me.sofiiak.sharedplay.data

import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime


object ApiClient {
    private const val BASE_URL = "https://web-production-568ff.up.railway.app/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(FirebaseAuth.getInstance()))
        .build()

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    val service: Service by lazy {
        retrofit.create(Service::class.java)
    }
}