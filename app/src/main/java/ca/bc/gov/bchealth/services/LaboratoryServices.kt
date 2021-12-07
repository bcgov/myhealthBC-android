package ca.bc.gov.bchealth.services

import ca.bc.gov.bchealth.model.network.responses.covidtests.ResponseCovidTests
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

/*
* Created by amit_metri on 07,December,2021
*/
interface LaboratoryServices {

    /*
    * API endpoint for fetching covid test results
    * */
    @GET("api/laboratoryservice/v1/api/PublicLaboratory/CovidTests")
    suspend fun getCovidTests(
        @Header("phn") phn: String,
        @Header("dateOfBirth") dateOfBirth: String,
        @Header("collectionDate") collectionDate: String,
    ): Response<ResponseCovidTests>
}
