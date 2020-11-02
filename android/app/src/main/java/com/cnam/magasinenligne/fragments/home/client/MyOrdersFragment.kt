package com.cnam.magasinenligne.fragments.home.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cnam.magasinenligne.MyApplication
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.LandingActivity
import com.cnam.magasinenligne.adapters.OrdersAdapter
import com.cnam.magasinenligne.api.*
import com.cnam.magasinenligne.api.models.MultipleOrderResponse
import com.cnam.magasinenligne.api.models.Order
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.pagination.PaginationListener
import com.cnam.magasinenligne.utils.hide
import com.cnam.magasinenligne.utils.logDebug
import com.cnam.magasinenligne.utils.show
import com.cnam.magasinenligne.utils.showSnack
import kotlinx.android.synthetic.main.fragment_my_orders.*

class MyOrdersFragment : BaseFragment(), RetrofitResponseListener,
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var myActivity: LandingActivity
    private lateinit var ordersAdapter: OrdersAdapter
    private var orders = ArrayList<Order>()
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false

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
        return inflater.inflate(R.layout.fragment_my_orders, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        getAllOrders(currentPage)
        srl_orders.setOnRefreshListener(this)
        srl_orders.setProgressBackgroundColorSchemeResource(R.color.colorBlue)
        myActivity.addOnBackStackListener(this)
        logDebug("OrdersCreated")
        myActivity.hideNavigation()
    }


    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onDestroyView() {
        myActivity.homePaused = false
        super.onDestroyView()
    }

    override fun onBackStackChanged() {
        logDebug("onBack")
    }

    private fun getAllOrders(index: Int) {
        myActivity.startLoading()
        myActivity.lockView(true)
        val fields = hashMapOf(
            CLIENT_ID to MyApplication.clientProfile.id,
            PAGE_INDEX to "$index"
        )
        val getOrdersCallback =
            ApiCallback<MultipleOrderResponse>(
                from_flag = "from_orders_get",
                listener = this
            )
        AppRetrofitClient.buildService(4).getClientOrders(fields).enqueue(getOrdersCallback)
        if (this::ordersAdapter.isInitialized && currentPage != PaginationListener.PAGE_START) {
            ordersAdapter.addLoading()
            isLoading = true
        }
    }

    private fun initializeRecyclerView() {
        ordersAdapter = OrdersAdapter(orders)
        rv_orders.adapter = ordersAdapter
        val mLayoutManager = LinearLayoutManager(myActivity)
        rv_orders.layoutManager = mLayoutManager
        rv_orders.addOnScrollListener(object : PaginationListener(mLayoutManager) {
            override fun loadMoreItems() {
                this@MyOrdersFragment.isLoading = true
                currentPage++
                getAllOrders(currentPage)
            }

            override val isLastPage: Boolean
                get() = this@MyOrdersFragment.isLastPage
            override val isLoading: Boolean
                get() = this@MyOrdersFragment.isLoading
        })
    }

    override fun onSuccess(result: Any, from: String) {
        if (this::ordersAdapter.isInitialized && currentPage != PaginationListener.PAGE_START) {
            ordersAdapter.removeLoading()
            isLoading = false
        }
        myActivity.lockView(false)
        myActivity.stopLoading()
        srl_orders?.isRefreshing = false
        if (result is List<*>) {
            val list = result as List<Order>
            if (!list.isNullOrEmpty()) {
                if (this::ordersAdapter.isInitialized && currentPage != PaginationListener.PAGE_START) {
                    ordersAdapter.addItems(list)
                } else {
                    orders.addAll(list)
                    if (orders.isNotEmpty()) {
                        tv_no_items.hide()
                        initializeRecyclerView()
                    } else {
                        tv_no_items.show()
                    }
                }
            } else {
                isLastPage = true
                if (orders.isEmpty()) tv_no_items.show()
            }
        }
    }

    override fun onFailure(error: String) {
        myActivity.stopLoading()
        myActivity.lockView(false)
        srl_orders?.isRefreshing = false
        rv_orders?.showSnack(error)
        if (this::ordersAdapter.isInitialized && currentPage != PaginationListener.PAGE_START) {
            ordersAdapter.removeLoading()
            isLoading = false
        }
    }

    override fun onRefresh() {
        currentPage = PaginationListener.PAGE_START
        isLastPage = false
        if (this::ordersAdapter.isInitialized)
            ordersAdapter.clear()
        getAllOrders(currentPage)
    }
}