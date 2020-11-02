package com.cnam.magasinenligne.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.fragments.home.merchant.AddProductFragment
import com.cnam.magasinenligne.fragments.landing.AccountFragment
import com.cnam.magasinenligne.fragments.landing.HomeFragment
import com.cnam.magasinenligne.fragments.landing.ShopFragment
import com.cnam.magasinenligne.utils.*
import kotlinx.android.synthetic.main.activity_landing.*

class LandingActivity : AppCompatActivity() {
    var homePaused = false
    var accountPaused = false
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

    fun startLoading() = loader.show()

    fun stopLoading() = loader.hide()


    fun lockView(lock: Boolean) = cl_mask.lockView(lock)

    fun addOnBackStackListener(baseFragment: BaseFragment) {
        supportFragmentManager.addOnBackStackChangedListener(baseFragment)
    }

    fun hideNavigation() = bnv_utilities.hide()

    fun showNavigation() = bnv_utilities.show()

    fun hideShop(): MenuItem = bnv_utilities.menu.getItem(1).setVisible(false)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (val fragment = supportFragmentManager.findFragmentById(R.id.fl_container)) {
            is AddProductFragment -> fragment.onActivityResult(
                requestCode,
                resultCode,
                data
            )
            is AccountFragment -> fragment.onActivityResult(
                requestCode,
                resultCode,
                data
            )
            else -> logDebug("fragment is null")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (val fragment = supportFragmentManager.findFragmentById(R.id.fl_container)) {
            is AddProductFragment -> fragment.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
            is HomeFragment -> fragment.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
            is AccountFragment -> fragment.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
            else -> logDebug("fragment is null")
        }
    }

}