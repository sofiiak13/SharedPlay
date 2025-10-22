package me.sofiiak.sharedplay.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://web-production-568ff.up.railway.app/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val playlistService: PlaylistService by lazy {
        retrofit.create(PlaylistService::class.java)
    }

//    val songService: SongService by lazy {
//        retrofit.create(SongService::class.java)
//    }
//
//    val userService: UserService by lazy {
//        retrofit.create(UserService::class.java)
//    }
}