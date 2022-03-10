package ca.bc.gov.common.exceptions

import java.io.IOException

/**
 * @author: Created by Rashmi Bambhania on 08,March,2022
 */
class ProtectiveWordException(
    val errCode: Int,
    message: String? = null
) : IOException(message)