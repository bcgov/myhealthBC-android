package ca.bc.gov.common.exceptions

import java.io.IOException

/**
 * @author: Created by Rashmi Bambhania on 08,June,2022
 */
class NetworkConnectionException(
    val errCode: Int,
    message: String? = null
) : IOException(message)
