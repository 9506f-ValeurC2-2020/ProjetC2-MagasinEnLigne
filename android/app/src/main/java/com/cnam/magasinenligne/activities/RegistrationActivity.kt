package com.cnam.magasinenligne.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.adapters.RegistrationPagerAdapter
import com.cnam.magasinenligne.utils.hide
import com.cnam.magasinenligne.utils.lockView
import com.cnam.magasinenligne.utils.show
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {
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
}
