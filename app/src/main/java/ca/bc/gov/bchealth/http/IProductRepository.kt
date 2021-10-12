package ca.bc.gov.bchealth.http

import ca.bc.gov.bchealth.model.network.responses.VaxStatusResponse
import ca.bc.gov.bchealth.services.Product
import java.io.IOException

interface IProductRepository {

    @Throws(IOException::class, MustBeQueued::class)
    fun getVaccineStatus(): VaxStatusResponse?

    @Throws(IOException::class, MustBeQueued::class)
    fun getProduct(): Product?

    fun addQueueToken(queueItToken: String?)
}
