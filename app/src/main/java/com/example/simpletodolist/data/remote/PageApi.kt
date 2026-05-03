package com.example.simpletodolist.data.remote

import com.example.simpletodolist.data.model.Page
import retrofit2.http.*

/*
 * Interfaz Retrofit para el CRUD de páginas.
 * Opera contra el endpoint /pages de JSON Server.
 */
interface PageApi {

    @GET("pages")
    suspend fun getAllPages(): List<Page>

    @POST("pages")
    suspend fun createPage(@Body page: Page): Page

    @PATCH("pages/{id}")
    suspend fun updatePage(
        @Path("id") id: String,
        @Body fields: Map<String, String>
    ): Page

    @DELETE("pages/{id}")
    suspend fun deletePage(@Path("id") id: String)
}