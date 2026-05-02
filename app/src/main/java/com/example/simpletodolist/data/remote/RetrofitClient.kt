package com.example.simpletodolist.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
Aquí se configura y proporciona una instancia de Retrofit para comunicarse
con API REST, el objeto a continuación define la URL del servidor de backend
la dirección 10.0.2.2 es una dirección especial que permite que el emulador de Android
acceda al servidor en el localhost

Posteriormente se instancia la interfaz TaskApi que contiene las peticiones HTTP
construyendo el cliente Retrofit y agregando el convertidor de JSON a clases Kotlin

.addConverterFactory(GsonConverterFactory.create())

 */

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:3000/"

    val api: TaskApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TaskApi::class.java)
    }
}