package com.cnam.magasinenligne.api

import com.cnam.magasinenligne.FAILED
import com.cnam.magasinenligne.NO_INTERNET
import com.cnam.magasinenligne.SUCCESS
import com.cnam.magasinenligne.api.models.*
import com.cnam.magasinenligne.utils.logDebug
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ApiCallback<T : CommonResponse>(
    private val from_flag: String = "",
    private val listener: RetrofitResponseListener
) : Callback<T> {
    var result: Any? = null
    override fun onFailure(call: Call<T>, t: Throwable) {
        this@ApiCallback.logDebug("Exception error is ${t.message}")
        if (t is IOException) {
            listener.onFailure(NO_INTERNET)
        } else {
            listener.onFailure("${t.message}")
        }
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            if (response.code() == 200) {
                val body = response.body()
                when (from_flag) {
                    "from_client_register", "from_client_login", "from_client_check_in", "from_client_update" -> {
                        val clientResponse = body as SingleClientResponse
                        result = if (body.status == SUCCESS) {
                            clientResponse.client
                        } else FAILED
                    }
                    "from_client_get", "from_client_find" -> {
                        val clientResponse = body as MultipleClientResponse
                        result = if (body.status == SUCCESS) {
                            clientResponse.clients
                        } else FAILED
                    }
                    "from_merchant_register", "from_merchant_login", "from_merchant_check_in", "from_merchant_update" -> {
                        val vendeurResponse = body as SingleVendeurResponse
                        result = if (body.status == SUCCESS) {
                            vendeurResponse.vendeur
                        } else FAILED
                    }
                    "from_merchant_get", "from_merchant_find" -> {
                        val vendeurResponse = body as MultipleVendeurResponse
                        result = if (body.status == SUCCESS) {
                            vendeurResponse.vendeurs
                        } else FAILED
                    }
                    "from_order_save", "from_order_update" -> {
                        val orderResponse = body as SingleOrderResponse
                        result = if (body.status == SUCCESS) {
                            orderResponse.order
                        } else FAILED
                    }
                    "from_order_get", "from_order_find" -> {
                        val orderResponse = body as MultipleOrderResponse
                        result = if (body.status == SUCCESS) {
                            orderResponse.orders
                        } else FAILED
                    }
                    "from_product_save", "from_product_update" -> {
                        val productResponse = body as SingleProductResponse
                        result = if (body.status == SUCCESS) {
                            productResponse.product
                        } else FAILED
                    }
                    "from_product_get", "from_product_find" -> {
                        val productResponse = body as MultipleProductResponse
                        result = if (body.status == SUCCESS) {
                            productResponse.products
                        } else FAILED
                    }
                    "from_item_save", "from_item_update" -> {
                        val itemResponse = body as SingleItemResponse
                        result = if (body.status == SUCCESS) {
                            itemResponse.item
                        } else FAILED
                    }
                    "from_item_get", "from_item_find" -> {
                        val itemResponse = body as MultipleItemResponse
                        result = if (body.status == SUCCESS) {
                            itemResponse.items
                        } else FAILED
                    }
                    else -> {
                        val cR = body as CommonResponse
                        result = if (body.status == SUCCESS) {
                            cR.message
                        } else FAILED
                    }
                }
                if (result != FAILED) {
                    listener.onSuccess(result!!, from_flag)
                } else {
                    listener.onFailure("${body.status} with message: ${body.message}")
                }
            }
        }
    }

}

interface RetrofitResponseListener {
    fun onSuccess(result: Any, from: String)
    fun onFailure(error: String)
}