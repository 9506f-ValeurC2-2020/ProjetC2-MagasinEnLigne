package com.cnam.magasinenligne.api

import com.cnam.magasinenligne.FAILED
import com.cnam.magasinenligne.NO_INTERNET
import com.cnam.magasinenligne.SUCCESS
import com.cnam.magasinenligne.api.models.CommonResponse
import com.cnam.magasinenligne.api.models.SingleClientResponse
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
                    "from_client_register" -> {
                        val clientResponse = body as SingleClientResponse
                        result = if (body.status == SUCCESS) {
                            clientResponse.client
                        } else FAILED
                    }
                    "from_client_login" -> {
                        val clientResponse = body as SingleClientResponse
                        result = if (body.status == SUCCESS) {
                            clientResponse.client
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