package ca.bc.gov.bchealth.repository

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
        "This record is already added."
    ),

    MISMATCH_ERROR(
        "Data mismatch",
        "The information you entered does not match our records. " +
            "Please check and try again."
    ),

    INVALID_PHN(
        "Error!",
        "There was an error with your Personal Health Number. " +
            "Please check that it is correct and try again."
    ),
}
