package com.cnam.magasinenligne.api

import com.cnam.magasinenligne.NO_INTERNET
import com.cnam.magasinenligne.api.models.CommonResponse
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

                }
            }
        }
    }

}

interface RetrofitResponseListener {
    fun onSuccess()
    fun onFailure(error: String)
}