package ca.bc.gov.bchealth.services

import retrofit2.Call
import retrofit2.http.GET

// TODO: 11/10/21 Temporarily placed for reference
interface ProductService {

    @GET("safeaction?queue-event1-nodomain=t")
    fun getProduct(): Call<Product>
}
