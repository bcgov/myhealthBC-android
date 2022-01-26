package ca.bc.gov.common.exceptions

/**
 * @author Pinakin Kansara
 */
open class MyHealthException(
    open val errCode: Int,
    message: String? = null
) : Exception(message)
