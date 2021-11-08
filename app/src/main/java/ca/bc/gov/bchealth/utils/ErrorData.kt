package ca.bc.gov.bchealth.utils

/*
* Created by amit_metri on 26,October,2021
*/
enum class ErrorData(var errorTitle: String?, var errorMessage: String?) {

    GENERIC_ERROR(
        "Error!",
        "Something went wrong. Please retry."
    ),

    INVALID_QR(
        "Invalid QR Code!",
        "Please use an official BC Vaccine Card QR Code."
    ),

    EXISTING_QR(
        "Duplicate!",
        "This health pass is already added."
    ),

    FULLY_VACCINATED_QR_EXISTS(
        "Duplicate!",
        "Fully vaccinated health pass is already added."
    ),

    NETWORK_ERROR(
        "Error!",
        "Something went wrong. Please retry."
    ),
}
