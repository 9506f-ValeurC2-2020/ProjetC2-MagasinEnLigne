package com.cnam.magasinenligne.fragments.home.merchant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.cnam.magasinenligne.MyApplication
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.LandingActivity
import com.cnam.magasinenligne.adapters.ClientsAdapter
import com.cnam.magasinenligne.api.*
import com.cnam.magasinenligne.api.models.Client
import com.cnam.magasinenligne.api.models.MultipleOrderResponse
import com.cnam.magasinenligne.api.models.Order
import com.cnam.magasinenligne.api.models.SingleClientResponse
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.utils.hide
import com.cnam.magasinenligne.utils.logDebug
import com.cnam.magasinenligne.utils.show
import com.cnam.magasinenligne.utils.showSnack
import kotlinx.android.synthetic.main.fragment_my_customers.*

class MyCustomersFragment : BaseFragment(), RetrofitResponseListener {
    private lateinit var myActivity: LandingActivity
    private var orders = ArrayList<Order>()
    private lateinit var clientsAdapter: ClientsAdapter
    private var clients = ArrayList<Client>()
    private var currentIndex = -1

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
        return inflater.inflate(R.layout.fragment_my_customers, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        getAllOrders()
        myActivity.hideNavigation()
    }

    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        logDebug("onBack")
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
        when (from) {
            "from_orders_get" -> {
                if (result is List<*>) {
                    val list = result as List<Order>
                    if (!list.isNullOrEmpty()) {
                        orders.addAll(list)
                        if (orders.isNotEmpty()) {
                            tv_no_items.hide()
                            getCustomers()
                        } else {
                            myActivity.stopLoading()
                            myActivity.lockView(false)
                            tv_no_items.show()
                        }
                    }
                }
            }
            "from_client_by_id" -> {
                clients.add(result as Client)
                if (currentIndex == orders.lastIndex) {
                    myActivity.stopLoading()
                    myActivity.lockView(false)
                    initializeRecyclerView()
                }
            }
        }
    }

    override fun onFailure(error: String) {
        myActivity.stopLoading()
        myActivity.lockView(false)
        rv_clients.showSnack(error)
    }

    private fun initializeRecyclerView() {
        clientsAdapter = ClientsAdapter(clients)
        rv_clients.adapter = clientsAdapter
        rv_clients.layoutManager = LinearLayoutManager(myActivity)
    }

    private fun getCustomers() {
        orders.forEachIndexed { index, order ->
            currentIndex = index
            getEachCustomer(order.fromClient)
        }
    }

    private fun getEachCustomer(id: String) {
        val fields = hashMapOf(
            CLIENT_ID to id
        )
        val getClientCallback =
            ApiCallback<SingleClientResponse>(
                from_flag = "from_client_by_id",
                listener = this
            )
        AppRetrofitClient.buildService(1).findClientById(fields).enqueue(getClientCallback)
    }

    override fun onDestroyView() {
        myActivity.homePaused = false
        super.onDestroyView()
    }
}