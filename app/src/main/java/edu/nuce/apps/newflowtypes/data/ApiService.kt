package edu.nuce.apps.newflowtypes.data

import okhttp3.ResponseBody
import retrofit2.http.GET

interface ApiService {

    @GET("users")
    @IgnoreAuth
    suspend fun loadUsers(): ResponseBody
}