package com.example.todo

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.HeaderMap
import retrofit2.http.POST

class ToDo(
    val id : Int,
    val content : String,
    val is_complete : Boolean,
    val created : String
)

interface RetrofitService {
    @POST("to-do/")
    @FormUrlEncoded
    fun makeToDo(
        @HeaderMap headers: Map<String, String>,
        @FieldMap params: HashMap<String, Any>
    ): Call<Any>

}