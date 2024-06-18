package com.example.sisvita.data
import com.example.sisvita.data.models.AnswerFormModel
import com.example.sisvita.data.models.AuthModel
import com.example.sisvita.data.models.AuthResponseModel
import com.example.sisvita.data.models.ContentFormModel
import com.example.sisvita.data.models.FormularioEnvioModel
import com.example.sisvita.data.models.FormularioModel
import com.example.sisvita.data.models.FormularioResponseModel
import com.example.sisvita.data.models.PreguntasFormularioModel
import com.example.sisvita.data.models.RegisterModel
import com.example.sisvita.data.models.RegisterUserResponse
import com.example.sisvita.data.models.ScoreFormModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Path

interface RetrofitService {
    @POST("api/v2/login")
    suspend fun loginAccount(
        @Body authModel : AuthModel
    ) : AuthResponseModel

    @GET("api/v2/questions/{id}")
    suspend fun getQuestions(
        @Path("id") formularioId: Int
    ): List<PreguntasFormularioModel>
    @GET("api/v2/forms")
    suspend fun getForms(
    ): List<FormularioModel>
    @POST("api/v2/llenarFormulario")
    suspend fun submitForm(
        @Body formularioEnvioModel: FormularioEnvioModel
    ) : FormularioResponseModel
    @POST("api/v2/register")
    suspend fun registerUser(
        @Body registerModel: RegisterModel
    ) : RegisterUserResponse

    @GET("api/v2/ContentForm/{id}")
    suspend fun getContentForm(
        @Path("id") formularioId: Int
    ): List<ContentFormModel>

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