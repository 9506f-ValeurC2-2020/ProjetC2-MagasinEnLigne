package com.cnam.magasinenligne.fragments.home.merchant

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.cnam.magasinenligne.MyApplication
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.LandingActivity
import com.cnam.magasinenligne.api.ApiCallback
import com.cnam.magasinenligne.api.AppRetrofitClient
import com.cnam.magasinenligne.api.MERCHANT_ID
import com.cnam.magasinenligne.api.RetrofitResponseListener
import com.cnam.magasinenligne.api.models.MultipleOrderResponse
import com.cnam.magasinenligne.api.models.Order
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.utils.logDebug
import com.cnam.magasinenligne.utils.showSnack
import com.github.jinatonic.confetti.CommonConfetti
import kotlinx.android.synthetic.main.fragment_my_wallet.*

class WalletFragment : BaseFragment(), RetrofitResponseListener {
    private lateinit var myActivity: LandingActivity
    private var orders = ArrayList<Order>()

    /**
     * OnBackPressedCallback
     */
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity!!.supportFragmentManager.popBackStack()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myActivity = activity!! as LandingActivity
        return inflater.inflate(R.layout.fragment_my_wallet, null)
    }

    override fun onDestroyView() {
        myActivity.homePaused = false
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        myActivity.hideNavigation()
        getAllOrders()

    }

    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        logDebug("onBack")
    }

    private fun animateWithConfetti() {
        val rect = Rect(0, 0, MyApplication.screenWidth, MyApplication.screenHeight)

        CommonConfetti.rainingConfetti(
            cl_main,
            intArrayOf(
                Color.RED,
                Color.GREEN,
                Color.BLACK,
                Color.BLUE,
                Color.YELLOW,
                Color.CYAN,
                Color.LTGRAY,
                Color.MAGENTA
            )
        ).infinite()
            .setAccelerationX(20f)
            .setRotationalVelocity(180f, 180f)
            .setTouchEnabled(true)
            .setBound(rect)
            .disableFadeOut()
            .animate()

    }

    private fun getAllOrders() {
        myActivity.startLoading()
        myActivity.lockView(true)
        val fields = hashMapOf(
            MERCHANT_ID to MyApplication.merchantProfile.id
        )
        val getOrdersCallback =
            ApiCallback<MultipleOrderResponse>(
                from_flag = "from_orders_get",
                listener = this
            )
        AppRetrofitClient.buildService(4).getMerchantOrders(fields).enqueue(getOrdersCallback)

    }

    override fun onSuccess(result: Any, from: String) {
        myActivity.stopLoading()
        myActivity.lockView(false)
        when (from) {
            "from_orders_get" -> {
                if (result is List<*>) {
                    val list = result as List<Order>
                    if (!list.isNullOrEmpty()) {
                        orders.addAll(list)
                        tv_profit.text = if (orders.isNotEmpty()) "0" else "${getSum()}"
                        animateWithConfetti()
                    }
                }
            }
        }
    }

    override fun onFailure(error: String) {
        myActivity.stopLoading()
        myActivity.lockView(false)
        tv_profit.showSnack(error)
    }

    private fun getSum(): Float {
        var res = 0f
        for (o in orders) {
            res += o.cost
        }
        return res
    }
}