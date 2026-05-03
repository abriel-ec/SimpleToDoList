package com.example.simpletodolist.data.remote

import com.example.simpletodolist.data.model.Notebook
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

/*
 * Interfaz Retrofit para la entidad Notebook.
 *
 * Cubre el CRUD completo contra el endpoint /notebooks de JSON Server:
 *  - getAllNotebooks(): obtiene todos los cuadernos del servidor. El
 *    filtrado por usuario se realiza en la capa Repository, ya que las
 *    versiones beta de JSON Server v1 no aplican correctamente los
 *    filtros por query param.
 *  - createNotebook(): crea un cuaderno nuevo.
 *  - updateNotebook(): renombra un cuaderno (PATCH solo modifica los
 *    campos enviados, en lugar de reemplazar el recurso entero).
 *  - deleteNotebook(): elimina un cuaderno por su id.
 */
interface NotebookApi {

    @GET("notebooks")
    suspend fun getAllNotebooks(): List<Notebook>

    @POST("notebooks")
    suspend fun createNotebook(@Body notebook: Notebook): Notebook

    @PATCH("notebooks/{id}")
    suspend fun updateNotebook(
        @Path("id") id: String,
        @Body fields: Map<String, String>
    ): Notebook

    @DELETE("notebooks/{id}")
    suspend fun deleteNotebook(@Path("id") id: String)
}
