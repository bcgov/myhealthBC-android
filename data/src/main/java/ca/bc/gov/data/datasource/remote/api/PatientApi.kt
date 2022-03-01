package ca.bc.gov.data.datasource.remote.api

import ca.bc.gov.data.datasource.remote.model.response.PatientResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface PatientApi {
    @GET("api/patientservice/v1/api/Patient/{hdid}")
    suspend fun getPatient(@Header("Authorization") token: String, @Path("hdid") hdid: String): Response<PatientResponse>
}
