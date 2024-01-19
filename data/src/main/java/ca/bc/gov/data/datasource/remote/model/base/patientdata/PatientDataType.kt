package ca.bc.gov.data.datasource.remote.model.base.patientdata

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
enum class PatientDataType(val value: String) {
    @SerializedName("DiagnosticImagingExam")
    DIAGNOSTIC_IMAGING_EXAM("DiagnosticImagingExam"),

    @SerializedName("OrganDonorRegistration")
    ORGAN_DONOR_REGISTRATION("OrganDonorRegistration"),

    @SerializedName("BcCancerScreening")
    BC_CANCER_SCREENING("BcCancerScreening")
}
