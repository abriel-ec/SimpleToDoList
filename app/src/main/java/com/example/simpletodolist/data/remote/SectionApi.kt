package com.example.simpletodolist.data.remote

import com.example.simpletodolist.data.model.Section
import retrofit2.http.*

/*
 * Interfaz Retrofit para el CRUD de secciones.
 * Opera contra el endpoint /sections de JSON Server.
 */
interface SectionApi {

    @GET("sections")
    suspend fun getAllSections(): List<Section>

    @POST("sections")
    suspend fun createSection(@Body section: Section): Section

    @PATCH("sections/{id}")
    suspend fun updateSection(
        @Path("id") id: String,
        @Body fields: Map<String, String>
    ): Section

    @DELETE("sections/{id}")
    suspend fun deleteSection(@Path("id") id: String)
}