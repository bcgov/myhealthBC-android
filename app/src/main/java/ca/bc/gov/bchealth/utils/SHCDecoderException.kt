package ca.bc.gov.bchealth.utils

/**
 * [SHCDecoderException]
 *
 * @auther Pinakin Kansara
 */
class SHCDecoderException(
    val errCode: Int,
    message: String? = null
) : Exception(message)
