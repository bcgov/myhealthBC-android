package ca.bc.gov.common.exceptions

import java.io.IOException

class MustBeQueuedException(
    val errCode: Int,
    message: String? = null
) : IOException(message)
