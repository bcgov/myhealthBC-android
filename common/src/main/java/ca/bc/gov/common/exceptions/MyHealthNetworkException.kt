package ca.bc.gov.common.exceptions

class MyHealthNetworkException(
    override val errCode: Int,
    message: String? = null
) : MyHealthException(errCode, message) {
}