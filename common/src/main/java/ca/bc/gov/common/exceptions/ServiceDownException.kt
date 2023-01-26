package ca.bc.gov.common.exceptions

import ca.bc.gov.common.const.SERVICE_NOT_AVAILABLE

class ServiceDownException : MyHealthException(
    errCode = SERVICE_NOT_AVAILABLE,
)
