package com.cnam.magasinenligne.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.adapters.RegistrationPagerAdapter
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
}
