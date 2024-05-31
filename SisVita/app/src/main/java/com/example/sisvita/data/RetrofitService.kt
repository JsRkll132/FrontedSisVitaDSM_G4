package com.example.sisvita.data
import com.example.sisvita.data.models.AuthModel
import com.example.sisvita.data.models.AuthResponseModel
import com.example.sisvita.data.models.PreguntasFormularioModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface RetrofitService {
    @POST("api/v2/login")
    suspend fun loginAccount(
        @Body authModel : AuthModel
    ) : AuthResponseModel
    @GET("api/v2/questions")
    suspend fun getQuestions(): List<PreguntasFormularioModel>

}


object RetrofitServiceFactory{
    fun makeRetrofitService() : RetrofitService {
        return Retrofit.Builder()
            .baseUrl("https://backendsisvitadsm-g4.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)

    }
}