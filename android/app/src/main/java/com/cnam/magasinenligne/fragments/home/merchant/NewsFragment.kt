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
import com.cnam.magasinenligne.adapters.CustomersAdapter
import com.cnam.magasinenligne.api.*
import com.cnam.magasinenligne.api.models.Client
import com.cnam.magasinenligne.api.models.MultipleOrderResponse
import com.cnam.magasinenligne.api.models.Order
import com.cnam.magasinenligne.api.models.SingleClientResponse
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.utils.*
import kotlinx.android.synthetic.main.fragment_news.*

class NewsFragment : BaseFragment(), RetrofitResponseListener {
    private lateinit var myActivity: LandingActivity
    private lateinit var customersAdapter: CustomersAdapter
    private var clients = ArrayList<Client>()
    private var orders = ArrayList<Order>()
    private var currentIndex = -1
    private var sendClicked = false

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
        return inflater.inflate(R.layout.fragment_news, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        myActivity.hideNavigation()
        getAllOrders()
        initListeners()
    }

    private fun initListeners() {
        cb_all.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                customersAdapter.selectAll()
                cb_all_users.show()
            } else {
                cb_all_users.hide()
                cb_all_users.isChecked = false
                customersAdapter.clearSelection()
            }
        }
        bt_send.setOnClickListener {
            if (!sendClicked) {
                val message = et_broadcast_message.text.toString()
                if (message.isEmpty()) {
                    showErrorDialog(getString(R.string.broadcast_message_missing_error))
                    return@setOnClickListener
                }
                val receivers = ArrayList<String>()
                customersAdapter.getCustomers().forEach { client -> receivers.add(client.id) }
                if (receivers.isEmpty()) {
                    showErrorDialog(getString(R.string.broadcast_receiver_missing_error))
                    return@setOnClickListener
                }
                sendClicked = true
                myActivity.startLoading()
                myActivity.lockView(true)
                val allAppUsers = cb_all_users.isChecked
                val fields = hashMapOf(
                    MERCHANT_ID to MyApplication.merchantProfile.id,
                    BROADCAST_MESSAGE to message
                )
                if (allAppUsers) {
                    fields[ALL_USERS] = "$allAppUsers"
                } else {
                    fields[RECEIVERS] = receivers.toString()
                }
                val sendBroadcastCallback =
                    ApiCallback<SingleClientResponse>(
                        from_flag = "from_send_broadcast",
                        listener = this
                    )
                AppRetrofitClient.buildService(1).sendBroadcast(fields)
                    .enqueue(sendBroadcastCallback)
            }

        }
    }

    private fun showErrorDialog(error: String) {
        myActivity.createDialog(
            getString(R.string.error),
            error
        )
            .setCancelable(true)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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

    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        logDebug("onBack")
    }

    override fun onDestroyView() {
        myActivity.homePaused = false
        super.onDestroyView()
    }

    override fun onSuccess(result: Any, from: String) {
        when (from) {
            "from_orders_get" -> {
                if (result is List<*>) {
                    val list = result as List<Order>
                    if (!list.isNullOrEmpty()) {
                        orders.addAll(list)
                        if (orders.isNotEmpty()) {
                            getCustomers()
                        } else {
                            myActivity.stopLoading()
                            myActivity.lockView(false)
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
            "from_send_broadcast" -> {
                myActivity.stopLoading()
                myActivity.lockView(false)
                bt_send.showSnack(getString(R.string.users_to_be_notified))
                cb_all.isChecked = false
                et_broadcast_message.setText("")
            }
        }
    }

    override fun onFailure(error: String) {
        myActivity.stopLoading()
        myActivity.lockView(false)
        rv_customers.showSnack(error)
    }

    private fun initializeRecyclerView() {
        customersAdapter = CustomersAdapter(clients)
        rv_customers.adapter = customersAdapter
        rv_customers.layoutManager = LinearLayoutManager(myActivity)
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
}