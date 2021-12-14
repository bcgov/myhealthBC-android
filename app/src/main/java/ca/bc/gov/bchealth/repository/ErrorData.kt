package ca.bc.gov.bchealth.repository

/*
* Created by amit_metri on 26,October,2021
*/
enum class ErrorData(var errorTitle: String?, var errorMessage: String?) {

    INVALID_QR(
        "Invalid QR Code!",
        "Please use an official BC Vaccine Card QR Code."
    ),
}
