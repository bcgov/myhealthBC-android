package ca.bc.gov.bchealth.services

import ca.bc.gov.bchealth.model.network.responses.vaccinestatus.VaxStatusResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ImmunizationServices {

    /*
    * API endpoint for VaccineStatus
    * */
    @GET("api/immunizationservice/v1/api/PublicVaccineStatus")
    suspend fun getVaccineStatus(
        @Header("phn") phn: String,
        @Header("dateOfBirth") dateOfBirth: String,
        @Header("dateOfVaccine") dateOfVaccine: String,
    ): Response<VaxStatusResponse>
}
