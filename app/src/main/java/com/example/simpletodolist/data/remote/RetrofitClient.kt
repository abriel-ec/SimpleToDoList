package com.example.simpletodolist.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
 * Singleton que provee las instancias de Retrofit para cada API.
 * La dirección 10.0.2.2 permite al emulador acceder al localhost del PC.
 */
object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:3000/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }

    val taskListApi: TaskListApi by lazy {
        retrofit.create(TaskListApi::class.java)
    }

    val taskApi: TaskApi by lazy {
        retrofit.create(TaskApi::class.java)
    }
}