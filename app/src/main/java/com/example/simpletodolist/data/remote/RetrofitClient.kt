package com.example.simpletodolist.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
 * Cliente HTTP centralizado de la aplicación.
 *
 * Construye una única instancia de Retrofit y expone las distintas APIs
 * (UserApi, y en el futuro NotebookApi, SectionApi, PageApi).
 *
 * BASE_URL apunta a 10.0.2.2:3000 porque esa es la dirección que usa el
 * emulador de Android para acceder al "localhost" de la máquina donde
 * corre JSON Server.
 *
 * Se utiliza "by lazy" para que la creación se realice solo cuando se
 * necesita por primera vez.
 */
object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:3000/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }
    val notebookApi: NotebookApi by lazy { retrofit.create(NotebookApi::class.java) }
}
