package com.cnam.magasinenligne.fragments.landing

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
import com.cnam.magasinenligne.userType
import com.cnam.magasinenligne.utils.addTransaction
import com.cnam.magasinenligne.utils.findPreference
import com.cnam.magasinenligne.utils.logDebug
import com.cnam.magasinenligne.utils.show
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

        }
        cl_find_us.setOnClickListener {

        }
        cl_wish_list.setOnClickListener {

        }
        bt_get_orders.setOnClickListener {

        }
        //</editor-fold>

        //<editor-fold desc="layout-merchant">
        cl_news.setOnClickListener {

        }
        cl_customers.setOnClickListener {

        }
        cl_wallet.setOnClickListener {

        }
        bt_get_products.setOnClickListener {

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

    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        logDebug("onBackStack")
        resetClicks()
    }

    private fun resetClicks() {
        allClientsClicked = false
        allMerchantsClicked = false
        allProductsClicked = false
        allOrdersClicked = false
        allItemsClicked = false
        myActivity.lockView(false)
        myActivity.stopLoading()
    }

}