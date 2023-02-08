package ca.bc.gov.common.exceptions

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR

class InvalidResponseException : MyHealthException(
    errCode = SERVER_ERROR,
    message = MESSAGE_INVALID_RESPONSE
)
