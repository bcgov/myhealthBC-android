package ca.bc.gov.data.remote

import ca.bc.gov.data.remote.model.response.MedicationStatementResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

/*
* Created by amit_metri on 08,February,2022
*/
interface MedicationApi {

    @GET("api/medicationservice/v1/api/MedicationStatement/{hdid}")
    suspend fun getMedicationStatement(
        @Path("hdid") hdid: String,
        @Header("Authorization") accessToken: String
    ): Response<MedicationStatementResponse>
}
