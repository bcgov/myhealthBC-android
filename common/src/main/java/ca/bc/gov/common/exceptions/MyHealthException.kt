package ca.bc.gov.common.exceptions

/**
 * @author Pinakin Kansara
 */
open class MyHealthException(
    errCode: Int,
    message: String? = null
) : Exception(message)
