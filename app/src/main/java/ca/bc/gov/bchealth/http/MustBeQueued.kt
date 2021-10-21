package ca.bc.gov.bchealth.http

import java.io.IOException

class MustBeQueued constructor(s: String?) : IOException() {
    private val value: String? = s

    fun getValue(): String {
        return value.toString()
    }
}
