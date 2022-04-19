package ca.bc.gov.data.datasource.remote.api

import ca.bc.gov.data.datasource.remote.model.request.CommentRequest
import ca.bc.gov.data.datasource.remote.model.request.UserProfileRequest
import ca.bc.gov.data.datasource.remote.model.response.AuthenticatedCovidTestResponse
import ca.bc.gov.data.datasource.remote.model.response.CommentResponse
import ca.bc.gov.data.datasource.remote.model.response.LabTestPdfResponse
import ca.bc.gov.data.datasource.remote.model.response.LabTestResponse
import ca.bc.gov.data.datasource.remote.model.response.MedicationStatementResponse
import ca.bc.gov.data.datasource.remote.model.response.PatientResponse
import ca.bc.gov.data.datasource.remote.model.response.ProfileValidationResponse
import ca.bc.gov.data.datasource.remote.model.response.TermsOfServiceResponse
import ca.bc.gov.data.datasource.remote.model.response.UserProfileResponse
import ca.bc.gov.data.datasource.remote.model.response.VaccineStatusResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author Pinakin Kansara
 */
interface HealthGatewayPrivateApi {

    companion object {
        private const val HDID = "hdid"
        private const val AUTHORIZATION = "Authorization"
        private const val REPORT_ID = "reportId"
        private const val IS_COVID_19 = "isCovid19"
        private const val BASE_IMMUNIZATION_SERVICE = "api/immunizationservice/v1/api"
        private const val BASE_LABORATORY_SERVICE = "api/laboratoryservice/v1/api"
        private const val BASE_MEDICATION_SERVICE = "api/medicationservice/v1/api"
        private const val BASE_PATIENT_SERVICE = "api/patientservice/v1/api"
        private const val BASE_USER_PROFILE_SERVICE = "v1/api/UserProfile"
    }

    @GET("$BASE_PATIENT_SERVICE/Patient/{hdid}")
    suspend fun getPatient(
        @Header(AUTHORIZATION) token: String,
        @Path(HDID) hdid: String
    ): Response<PatientResponse>

    @GET("$BASE_IMMUNIZATION_SERVICE/AuthenticatedVaccineStatus")
    suspend fun getVaccineStatus(
        @Header(AUTHORIZATION) token: String,
        @Query(HDID) hdid: String
    ): Response<VaccineStatusResponse>

    @GET("$BASE_LABORATORY_SERVICE/Laboratory/Covid19Orders")
    suspend fun getCovidTests(
        @Header(AUTHORIZATION) token: String,
        @Query(HDID) hdid: String
    ): Response<AuthenticatedCovidTestResponse>

    @GET("$BASE_LABORATORY_SERVICE/Laboratory/LaboratoryOrders")
    suspend fun getLabTests(
        @Header(AUTHORIZATION) token: String,
        @Query(HDID) hdid: String
    ): Response<LabTestResponse>

    @GET("$BASE_LABORATORY_SERVICE/Laboratory/{$REPORT_ID}/Report")
    suspend fun getLabTestReportPdf(
        @Header(AUTHORIZATION) token: String,
        @Path(REPORT_ID) reportId: String,
        @Query(HDID) hdid: String,
        @Query(IS_COVID_19) isCovid19: Boolean
    ): Response<LabTestPdfResponse>

    @GET("$BASE_MEDICATION_SERVICE/MedicationStatement/{$HDID}")
    suspend fun getMedicationStatement(
        @Path(HDID) hdid: String,
        @Header(AUTHORIZATION) accessToken: String,
        @Header("protectiveWord") protectiveWord: String?
    ): Response<MedicationStatementResponse>

    @GET("$BASE_USER_PROFILE_SERVICE/{$HDID}/Comment/Entry")
    suspend fun getComments(
        @Path(HDID) hdid: String,
        @Header(AUTHORIZATION) accessToken: String,
        @Query("parentEntryId") parentEntryId: String?
    ): Response<CommentResponse>

    @GET("$BASE_USER_PROFILE_SERVICE/{$HDID}/Validate")
    suspend fun checkAgeLimit(
        @Path(HDID) hdid: String,
        @Header(AUTHORIZATION) accessToken: String
    ): Response<ProfileValidationResponse>

    @GET("$BASE_USER_PROFILE_SERVICE/{$HDID}")
    suspend fun getUserProfile(
        @Path(HDID) hdid: String,
        @Header(AUTHORIZATION) accessToken: String
    ): Response<UserProfileResponse>

    @GET("$BASE_USER_PROFILE_SERVICE/termsofservice")
    suspend fun getTermsOfService(): Response<TermsOfServiceResponse>

    @POST("$BASE_USER_PROFILE_SERVICE/{$HDID}")
    suspend fun updateUserProfile(
        @Path(HDID) hdid: String,
        @Header(AUTHORIZATION) accessToken: String,
        @Body profileRequest: UserProfileRequest
    ): Response<UserProfileResponse>

    @POST("$BASE_USER_PROFILE_SERVICE/{$HDID}/Comment")
    suspend fun addComment(
        @Path(HDID) hdid: String,
        @Header(AUTHORIZATION) accessToken: String,
        @Body commentRequest: CommentRequest
    ): Response<CommentResponse>

}
