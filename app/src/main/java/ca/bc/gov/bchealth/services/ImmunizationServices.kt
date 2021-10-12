package ca.bc.gov.bchealth.services

import ca.bc.gov.bchealth.model.network.responses.VaxStatusResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ImmunizationServices {

    /*
    * API endpoint for VaccineStatus
    * */
    @GET("api/immunizationservice/v1/api/VaccineStatus")
    fun getVaccineStatus(
        @Header("phn") phn: String,
        @Header("dateOfBirth") dateOfBirth: String,
        @Header("dateOfVaccine") dateOfVaccine: String,
    ): Call<VaxStatusResponse>
}
