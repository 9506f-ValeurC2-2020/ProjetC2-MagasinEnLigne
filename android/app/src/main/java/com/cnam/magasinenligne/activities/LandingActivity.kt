package com.cnam.magasinenligne.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.fragments.landing.AccountFragment
import com.cnam.magasinenligne.fragments.landing.HomeFragment
import com.cnam.magasinenligne.fragments.landing.ShopFragment
import com.cnam.magasinenligne.utils.*
import kotlinx.android.synthetic.main.activity_landing.*

class LandingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        supportFragmentManager.makeTransaction(HomeFragment(), HomeFragment::class.java.simpleName)
        setUpBottomNavigation()
    }

    private fun setUpBottomNavigation() {
        bnv_utilities.itemIconTintList = null
        bnv_utilities.setOnNavigationItemSelectedListener { item: MenuItem ->
            val tag = supportFragmentManager.getActiveFragmentTag()
            when (item.itemId) {
                R.id.item_home -> {
                    if (tag.isNotEmpty() && tag != "home") {
                        supportFragmentManager.makeTransaction(
                            HomeFragment(),
                            HomeFragment::class.java.simpleName
                        )
                    }
                }
                R.id.item_shop -> {
                    if (tag.isNotEmpty() && tag != "shop") {
                        supportFragmentManager.makeTransaction(
                            ShopFragment(),
                            ShopFragment::class.java.simpleName
                        )
                    }
                }
                R.id.item_account -> {
                    if (tag.isNotEmpty() && tag != "account") {
                        supportFragmentManager.makeTransaction(
                            AccountFragment(),
                            AccountFragment::class.java.simpleName
                        )
                    }
                }
            }
            true
        }
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