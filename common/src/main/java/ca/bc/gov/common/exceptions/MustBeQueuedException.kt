package ca.bc.gov.common.exceptions

class MustBeQueuedException(
    errCode: Int,
    message: String? = null
) : MyHealthException(errCode, message)