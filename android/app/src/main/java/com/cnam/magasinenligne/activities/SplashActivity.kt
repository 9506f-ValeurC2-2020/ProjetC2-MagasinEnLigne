package com.cnam.magasinenligne.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.cnam.magasinenligne.*
import com.cnam.magasinenligne.api.ApiCallback
import com.cnam.magasinenligne.api.AppRetrofitClient
import com.cnam.magasinenligne.api.ID
import com.cnam.magasinenligne.api.RetrofitResponseListener
import com.cnam.magasinenligne.api.models.Client
import com.cnam.magasinenligne.api.models.SingleClientResponse
import com.cnam.magasinenligne.api.models.SingleVendeurResponse
import com.cnam.magasinenligne.api.models.Vendeur
import com.cnam.magasinenligne.utils.findPreference
import com.cnam.magasinenligne.utils.showSnack
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity(), RetrofitResponseListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({}, 2000)
    }

    private fun checkLogin() {
        val isUserLoggedIn = findPreference(isUserLoggedIn, false)
        if (isUserLoggedIn) {
            // user has logged in previously to the app.. checking client, merchant or admin userType
            val userType = findPreference(userType, "")
            if (userType.isNotEmpty()) {
                handleUserType(userType)
            }
        } else {
            // user not logged in.. registration
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }
    }

    private fun handleUserType(type: String) {
        when (type) {
            "admin" -> {
                startActivity(Intent(this, LandingActivity::class.java))
                finish()
            }
            "client" -> checkIn(0)
            "merchant" -> checkIn(1)
        }
    }

    private fun checkIn(type: Int) {
        val fields = hashMapOf(
            ID to findPreference(userId, "")
        )
        if (type == 0) {
            val checkInCallback =
                ApiCallback<SingleClientResponse>(
                    from_flag = "from_client_check_in",
                    listener = this
                )
            AppRetrofitClient.buildService(1).checkInClient(fields).enqueue(checkInCallback)
        } else {
            val checkInCallback =
                ApiCallback<SingleVendeurResponse>(
                    from_flag = "from_merchant_check_in",
                    listener = this
                )
            AppRetrofitClient.buildService(2).checkInVendeur(fields).enqueue(checkInCallback)
        }
    }

    override fun onSuccess(result: Any, from: String) {
        when (from) {
            "from_client_check_in" -> {
                MyApplication.clientProfile = result as Client
            }
            "from_merchant_check_in" -> {
                MyApplication.merchantProfile = result as Vendeur
            }
        }
    }

    override fun onFailure(error: String) {
        lottie_loader.showSnack(error)
    }
}