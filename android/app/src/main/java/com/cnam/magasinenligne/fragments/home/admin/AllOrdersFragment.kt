package com.cnam.magasinenligne.fragments.home.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.LandingActivity
import com.cnam.magasinenligne.adapters.OrdersAdapter
import com.cnam.magasinenligne.api.ApiCallback
import com.cnam.magasinenligne.api.AppRetrofitClient
import com.cnam.magasinenligne.api.PAGE_INDEX
import com.cnam.magasinenligne.api.RetrofitResponseListener
import com.cnam.magasinenligne.api.models.MultipleOrderResponse
import com.cnam.magasinenligne.api.models.Order
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.pagination.PaginationListener
import com.cnam.magasinenligne.pagination.PaginationListener.Companion.PAGE_START
import com.cnam.magasinenligne.utils.hide
import com.cnam.magasinenligne.utils.logDebug
import com.cnam.magasinenligne.utils.show
import com.cnam.magasinenligne.utils.showSnack
import kotlinx.android.synthetic.main.fragment_all_orders.*

class AllOrdersFragment : BaseFragment(), RetrofitResponseListener,
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var myActivity: LandingActivity
    private lateinit var ordersAdapter: OrdersAdapter
    private var orders = ArrayList<Order>()
    private var currentPage = PAGE_START
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
        return inflater.inflate(R.layout.fragment_all_orders, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        getAllOrders(currentPage)
        srl_orders.setOnRefreshListener(this)
        srl_orders.setProgressBackgroundColorSchemeResource(R.color.colorBlue)
    }


    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        logDebug("onBackStack")
    }

    private fun getAllOrders(index: Int) {
        myActivity.startLoading()
        myActivity.lockView(true)
        val fields = hashMapOf(
            PAGE_INDEX to "$index"
        )
        val getOrdersCallback =
            ApiCallback<MultipleOrderResponse>(
                from_flag = "from_orders_get",
                listener = this
            )
        AppRetrofitClient.buildService(4).getOrders(fields).enqueue(getOrdersCallback)
        if (this::ordersAdapter.isInitialized && currentPage != PAGE_START) {
            ordersAdapter.addLoading()
            isLoading = true
        }
    }

    private fun initializeRecyclerView() {
        ordersAdapter = OrdersAdapter(orders, true)
        rv_orders.adapter = ordersAdapter
        val mLayoutManager = LinearLayoutManager(myActivity)
        rv_orders.layoutManager = mLayoutManager
        rv_orders.addOnScrollListener(object : PaginationListener(mLayoutManager) {
            override fun loadMoreItems() {
                this@AllOrdersFragment.isLoading = true
                currentPage++
                getAllOrders(currentPage)
            }

            override val isLastPage: Boolean
                get() = this@AllOrdersFragment.isLastPage
            override val isLoading: Boolean
                get() = this@AllOrdersFragment.isLoading
        })
    }

    override fun onSuccess(result: Any, from: String) {
        if (this::ordersAdapter.isInitialized && currentPage != PAGE_START) {
            ordersAdapter.removeLoading()
            isLoading = false
        }
        myActivity.lockView(false)
        myActivity.stopLoading()
        srl_orders?.isRefreshing = false
        if (result is List<*>) {
            val list = result as List<Order>
            if (!list.isNullOrEmpty()) {
                if (this::ordersAdapter.isInitialized && currentPage != PAGE_START) {
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
        if (this::ordersAdapter.isInitialized && currentPage != PAGE_START) {
            ordersAdapter.removeLoading()
            isLoading = false
        }
    }

    override fun onRefresh() {
        currentPage = PAGE_START
        isLastPage = false
        if (this::ordersAdapter.isInitialized)
            ordersAdapter.clear()
        getAllOrders(currentPage)
    }
}