package com.cnam.magasinenligne.fragments.landing

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.cnam.magasinenligne.MyApplication
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.LandingActivity
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.fragments.home.admin.*
import com.cnam.magasinenligne.fragments.home.client.FindUsFragment
import com.cnam.magasinenligne.fragments.home.client.MyOrdersFragment
import com.cnam.magasinenligne.fragments.home.client.SalesFragment
import com.cnam.magasinenligne.fragments.home.client.WishListFragment
import com.cnam.magasinenligne.fragments.home.merchant.MyCustomersFragment
import com.cnam.magasinenligne.fragments.home.merchant.MyProductsFragment
import com.cnam.magasinenligne.fragments.home.merchant.NewsFragment
import com.cnam.magasinenligne.fragments.home.merchant.WalletFragment
import com.cnam.magasinenligne.userType
import com.cnam.magasinenligne.utils.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_home_admin.*
import kotlinx.android.synthetic.main.layout_home_client.*
import kotlinx.android.synthetic.main.layout_home_merchant.*

class HomeFragment : BaseFragment() {
    private lateinit var myActivity: LandingActivity


    /**
     * admin
     */
    private var allClientsClicked = false
    private var allMerchantsClicked = false
    private var allProductsClicked = false
    private var allOrdersClicked = false
    private var allItemsClicked = false

    /**
     * client
     */
    private var salesClicked = false
    private var findUsClicked = false
    private var myOrdersClicked = false
    private var wishListClicked = false
    private val locationRequest = 1000
    private val locationPermission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    /**
     * merchant
     */
    private var newsClicked = false
    private var myProductsClicked = false
    private var myCustomersClicked = false
    private var walletClicked = false

    /**
     * OnBackPressedCallback
     */
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                myActivity.finish()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myActivity = activity!! as LandingActivity
        return inflater.inflate(R.layout.fragment_home, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        myActivity.addOnBackStackListener(this)
        val userType = findPreference(userType, "")
        if (userType.isNotEmpty()) {
            when (userType) {
                "admin" -> {
                    layout_admin.show()
                    myActivity.hideNavigation()
                }
                "client" -> {
                    layout_client.show()
                }
                "merchant" -> {
                    layout_merchant.show()
                    myActivity.hideShop()
                }
            }
        }
        if (MyApplication.isClient()) {
            logDebug(MyApplication.clientProfile.toString())
        }
        if (MyApplication.isMerchant()) {
            logDebug(MyApplication.merchantProfile.toString())
        }
        listeners()
    }

    private fun listeners() {
        //<editor-fold desc="layout-client">
        cl_sales.setOnClickListener {
            if (!salesClicked) {
                myActivity.homePaused = true
                salesClicked = true
                myActivity.lockView(true)
                myActivity.supportFragmentManager.addTransaction(
                    SalesFragment(),
                    SalesFragment::class.java.simpleName,
                    R.anim.enter_from_right,
                    R.anim.exit_to_right
                )
            }

        }
        cl_find_us.setOnClickListener {
            if (!findUsClicked) {
                myActivity.homePaused = true
                findUsClicked = true
                myActivity.lockView(true)
                val granted = checkPermissions(myActivity, locationPermission)
                if (granted) {
                    handlePermissionResult(1)
                } else {
                    myActivity.createDialog(
                        getString(R.string.permission_needed),
                        getString(R.string.permission_importance_message)
                    ).setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                        findUsClicked = true
                        requestPermissions(locationPermission, locationRequest)
                        dialog.dismiss()
                    }.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        handlePermissionResult(0)
                        dialog.dismiss()
                    }
                        .create()
                        .show()

                }
            }
        }
        cl_wish_list.setOnClickListener {
            if (!wishListClicked) {
                myActivity.homePaused = true
                wishListClicked = true
                myActivity.lockView(true)
                myActivity.supportFragmentManager.addTransaction(
                    WishListFragment(),
                    WishListFragment::class.java.simpleName,
                    R.anim.enter_from_right,
                    R.anim.exit_to_right
                )
            }
        }
        bt_get_orders.setOnClickListener {
            if (!myOrdersClicked) {
                myActivity.homePaused = true
                myOrdersClicked = true
                myActivity.lockView(true)
                myActivity.supportFragmentManager.addTransaction(
                    MyOrdersFragment(),
                    MyOrdersFragment::class.java.simpleName,
                    R.anim.enter_from_right,
                    R.anim.exit_to_right
                )
            }
        }
        //</editor-fold>

        //<editor-fold desc="layout-merchant">
        cl_news.setOnClickListener {
            if (!newsClicked) {
                myActivity.homePaused = true
                newsClicked = true
                myActivity.lockView(true)
                myActivity.supportFragmentManager.addTransaction(
                    NewsFragment(),
                    NewsFragment::class.java.simpleName,
                    R.anim.enter_from_right,
                    R.anim.exit_to_right
                )
            }
        }
        cl_customers.setOnClickListener {
            if (!myCustomersClicked) {
                myActivity.homePaused = true
                myCustomersClicked = true
                myActivity.lockView(true)
                myActivity.supportFragmentManager.addTransaction(
                    MyCustomersFragment(),
                    MyCustomersFragment::class.java.simpleName,
                    R.anim.enter_from_right,
                    R.anim.exit_to_right
                )
            }
        }
        cl_wallet.setOnClickListener {
            if (!walletClicked) {
                myActivity.homePaused = true
                walletClicked = true
                myActivity.lockView(true)
                myActivity.supportFragmentManager.addTransaction(
                    WalletFragment(),
                    WalletFragment::class.java.simpleName,
                    R.anim.enter_from_right,
                    R.anim.exit_to_right
                )
            }
        }
        bt_get_products.setOnClickListener {
            if (!myProductsClicked) {
                myActivity.homePaused = true
                myProductsClicked = true
                myActivity.lockView(true)
                myActivity.supportFragmentManager.addTransaction(
                    MyProductsFragment(),
                    MyProductsFragment::class.java.simpleName,
                    R.anim.enter_from_right,
                    R.anim.exit_to_right
                )
            }
        }
        //</editor-fold>

        //<editor-fold desc="layout-admin">
        bt_get_all_clients.setOnClickListener {
            if (!allClientsClicked) {
                allClientsClicked = true
                myActivity.lockView(true)
                myActivity.supportFragmentManager.addTransaction(
                    AllClientsFragment(),
                    AllClientsFragment::class.java.simpleName,
                    R.anim.enter_from_right,
                    R.anim.exit_to_right
                )
            }
        }
        bt_get_all_merchants.setOnClickListener {
            if (!allMerchantsClicked) {
                allMerchantsClicked = true
                myActivity.lockView(true)
                myActivity.supportFragmentManager.addTransaction(
                    AllMerchantFragment(),
                    AllMerchantFragment::class.java.simpleName,
                    R.anim.enter_from_right,
                    R.anim.exit_to_right
                )
            }
        }
        bt_get_all_products.setOnClickListener {
            if (!allProductsClicked) {
                allProductsClicked = true
                myActivity.lockView(true)
                myActivity.supportFragmentManager.addTransaction(
                    AllProductsFragment(),
                    AllProductsFragment::class.java.simpleName,
                    R.anim.enter_from_right,
                    R.anim.exit_to_right
                )
            }
        }
        bt_get_all_orders.setOnClickListener {
            if (!allOrdersClicked) {
                allOrdersClicked = true
                myActivity.lockView(true)
                myActivity.supportFragmentManager.addTransaction(
                    AllOrdersFragment(),
                    AllOrdersFragment::class.java.simpleName,
                    R.anim.enter_from_right,
                    R.anim.exit_to_right
                )
            }
        }
        bt_get_all_delivery_items.setOnClickListener {
            if (!allItemsClicked) {
                allItemsClicked = true
                myActivity.lockView(true)
                myActivity.supportFragmentManager.addTransaction(
                    AllDeliveryItemsFragment(),
                    AllDeliveryItemsFragment::class.java.simpleName,
                    R.anim.enter_from_right,
                    R.anim.exit_to_right
                )
            }
        }
        //</editor-fold>
    }

    private fun handlePermissionResult(successOrFail: Int) {
        if (successOrFail == 1) {
            myActivity.homePaused = true
            myActivity.supportFragmentManager.addTransaction(
                FindUsFragment(),
                FindUsFragment::class.java.simpleName,
                R.anim.enter_from_right,
                R.anim.exit_to_right
            )
        } else {
            myActivity.createDialog(
                getString(R.string.alert),
                getString(R.string.location_is_necessary)
            )
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    findUsClicked = false
                    dialog.dismiss()
                }
                .create()
                .show()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        logDebug("$requestCode is my request code")
        when (requestCode) {
            locationRequest -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    logDebug("granted")
                    handlePermissionResult(1)
                } else {
                    handlePermissionResult(0)
                }
            }
        }
    }

    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        logDebug("onBackStack ${myActivity.homePaused}")
        if (!myActivity.homePaused) {
            val fragment = myActivity.supportFragmentManager.findFragmentById(R.id.fl_container)
            if (fragment != null && fragment is HomeFragment)
                resetClicks()
        }

    }

    private fun resetClicks() {
        allClientsClicked = false
        allMerchantsClicked = false
        allProductsClicked = false
        allOrdersClicked = false
        allItemsClicked = false
        salesClicked = false
        findUsClicked = false
        myOrdersClicked = false
        wishListClicked = false
        newsClicked = false
        myProductsClicked = false
        myCustomersClicked = false
        walletClicked = false
        myActivity.lockView(false)
        myActivity.stopLoading()
        myActivity.showNavigation()
    }

}