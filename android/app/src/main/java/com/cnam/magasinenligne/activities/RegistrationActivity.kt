package com.cnam.magasinenligne.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.adapters.RegistrationPagerAdapter
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.isUserLoggedIn
import com.cnam.magasinenligne.userId
import com.cnam.magasinenligne.userType
import com.cnam.magasinenligne.utils.hide
import com.cnam.magasinenligne.utils.lockView
import com.cnam.magasinenligne.utils.putPreference
import com.cnam.magasinenligne.utils.show
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {
    lateinit var location: LatLng
    private lateinit var registrationPagerAdapter: RegistrationPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        initializeViewPager()
    }

    private fun initializeViewPager() {
        registrationPagerAdapter = RegistrationPagerAdapter(supportFragmentManager)
        landing_view_pager.adapter = registrationPagerAdapter
        view_pager_indicator.setViewPager(landing_view_pager)
    }

    fun startLoading() {
        loader.show()
        cl_mask.lockView(true)
    }

    fun stopLoading() {
        loader.hide()
        cl_mask.lockView(false)
    }

    fun login(type: String,id:String) {
        putPreference(isUserLoggedIn, true)
        putPreference(userType, type)
        putPreference(userId, id)
        startActivity(Intent(this, LandingActivity::class.java))
        finish()
    }

    fun isLocationInitialized() = this::location.isInitialized

    fun addOnBackStackListener(baseFragment: BaseFragment) {
        supportFragmentManager.addOnBackStackChangedListener(baseFragment)
    }
}
