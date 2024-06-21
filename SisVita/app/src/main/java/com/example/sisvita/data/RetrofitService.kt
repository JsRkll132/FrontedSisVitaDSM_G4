package com.example.sisvita.data
import com.example.sisvita.data.models.AuthModel
import com.example.sisvita.data.models.AuthResponseModel
import com.example.sisvita.data.models.ContentFormModel
import com.example.sisvita.data.models.DiagnosticModel
import com.example.sisvita.data.models.DiagnosticResponseModel
import com.example.sisvita.data.models.FormularioCompletadoModelResponse
import com.example.sisvita.data.models.FormularioEnvioModel
import com.example.sisvita.data.models.FormularioModel
import com.example.sisvita.data.models.FormularioResponseModel
import com.example.sisvita.data.models.PreguntasFormularioModel
import com.example.sisvita.data.models.RegisterModel
import com.example.sisvita.data.models.RegisterUserResponseModel
import com.example.sisvita.data.models.RespFormCompletPacient
import com.example.sisvita.data.models.UserByIdResponseModel
import retrofit2.http.GET
import retrofit2.http.POST
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
    ) : RegisterUserResponseModel

    @GET("api/v2/ContentForm/{id}")
    suspend fun getContentForm(
        @Path("id") formularioId: Int
    ): List<ContentFormModel>

    @GET("api/v2/getuserbyid/{usuario_id}")
    suspend fun getUserById(
        @Path("usuario_id") usuario_id : Int
    ):UserByIdResponseModel
    @POST("api/v2/diagnosticar")
    suspend fun DiagnosticarService(
        @Body diagnosticModel: DiagnosticModel
    ) : DiagnosticResponseModel

    @GET("api/v2/obtener/respuestas/formularioCompletado/paciente/{paciente_id}/formulario_completado/{completado_formulario_id}")
    suspend fun obtener_respuestasService(
        @Path("paciente_id") paciente_id : Int,
        @Path("completado_formulario_id") completado_formulario_id:Int
    ) : List<RespFormCompletPacient>

    @GET("api/v2/obtener/formularioCompletado/paciente/{paciente_id}")
    suspend fun obtener_puntuacionesService(
        @Path("paciente_id") paciente_id : Int
    ) : List<FormularioCompletadoModelResponse>

    @GET("api/v2/obtener/formularioCompletado/all")
    suspend fun obtener_puntuacionesAllService() :
            List<FormularioCompletadoModelResponse>
    @GET("api/v2/obtener/formularioCompletado/formulario/{completado_formulario_id}")
    suspend fun obtener_puntuaciones_paciente_formulario(
        @Path("completado_formulario_id") completado_formulario_id : Int
    ) :FormularioCompletadoModelResponse
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