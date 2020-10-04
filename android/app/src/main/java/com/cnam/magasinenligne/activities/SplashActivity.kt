package com.cnam.magasinenligne.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.isUserLoggedIn
import com.cnam.magasinenligne.utils.findPreference

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            checkLogin()
        }, 2500)
    }

    private fun checkLogin() {
        val isUserLoggedIn = findPreference(isUserLoggedIn, false)
        if (isUserLoggedIn) {
            // user has logged in previously to the app.. going to landing activity
            startActivity(Intent(this, LandingActivity::class.java))
        } else {
            // user not logged in.. registration
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}