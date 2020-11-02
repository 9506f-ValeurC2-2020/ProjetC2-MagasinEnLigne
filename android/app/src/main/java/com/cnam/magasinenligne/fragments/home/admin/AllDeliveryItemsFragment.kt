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
import com.cnam.magasinenligne.adapters.DeliveryItemsAdapter
import com.cnam.magasinenligne.api.ApiCallback
import com.cnam.magasinenligne.api.AppRetrofitClient
import com.cnam.magasinenligne.api.PAGE_INDEX
import com.cnam.magasinenligne.api.RetrofitResponseListener
import com.cnam.magasinenligne.api.models.Item
import com.cnam.magasinenligne.api.models.MultipleItemResponse
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.pagination.PaginationListener
import com.cnam.magasinenligne.pagination.PaginationListener.Companion.PAGE_START
import com.cnam.magasinenligne.utils.hide
import com.cnam.magasinenligne.utils.logDebug
import com.cnam.magasinenligne.utils.show
import com.cnam.magasinenligne.utils.showSnack
import kotlinx.android.synthetic.main.fragment_all_delivery_items.*

class AllDeliveryItemsFragment : BaseFragment(), RetrofitResponseListener,
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var myActivity: LandingActivity
    private lateinit var itemsAdapter: DeliveryItemsAdapter
    private var items = ArrayList<Item>()
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
        return inflater.inflate(R.layout.fragment_all_delivery_items, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        getAllDeliveryItems(currentPage)
        srl_items.setOnRefreshListener(this)
        srl_items.setProgressBackgroundColorSchemeResource(R.color.colorBlue)
    }


    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        logDebug("onBackStack")
    }

    private fun getAllDeliveryItems(index: Int) {
        myActivity.startLoading()
        myActivity.lockView(true)
        val fields = hashMapOf(
            PAGE_INDEX to "$index"
        )
        val getItemsCallback =
            ApiCallback<MultipleItemResponse>(
                from_flag = "from_items_get",
                listener = this
            )
        AppRetrofitClient.buildService(5).getItems(fields).enqueue(getItemsCallback)
        if (this::itemsAdapter.isInitialized && currentPage != PAGE_START) {
            itemsAdapter.addLoading()
            isLoading = true
        }
    }

    private fun initializeRecyclerView() {
        itemsAdapter = DeliveryItemsAdapter(items)
        rv_delivery_items.adapter = itemsAdapter
        val mLayoutManager = LinearLayoutManager(myActivity)
        rv_delivery_items.layoutManager = mLayoutManager
        rv_delivery_items.addOnScrollListener(object : PaginationListener(mLayoutManager) {
            override fun loadMoreItems() {
                this@AllDeliveryItemsFragment.isLoading = true
                currentPage++
                getAllDeliveryItems(currentPage)
            }

            override val isLastPage: Boolean
                get() = this@AllDeliveryItemsFragment.isLastPage
            override val isLoading: Boolean
                get() = this@AllDeliveryItemsFragment.isLoading
        })
    }

    override fun onSuccess(result: Any, from: String) {
        if (this::itemsAdapter.isInitialized && currentPage != PAGE_START) {
            itemsAdapter.removeLoading()
            isLoading = false
        }
        myActivity.lockView(false)
        myActivity.stopLoading()
        srl_items?.isRefreshing = false
        if (result is List<*>) {
            val list = result as List<Item>
            if (!list.isNullOrEmpty()) {
                if (this::itemsAdapter.isInitialized && currentPage != PAGE_START) {
                    itemsAdapter.addItems(list)
                } else {
                    items.addAll(list)
                    if (items.isNotEmpty()) {
                        tv_no_items.hide()
                        initializeRecyclerView()
                    } else {
                        tv_no_items.show()
                    }
                }
            } else {
                isLastPage = true
                if (items.isEmpty()) tv_no_items.show()
            }
        }
    }

    override fun onFailure(error: String) {
        myActivity.stopLoading()
        myActivity.lockView(false)
        srl_items?.isRefreshing = false
        rv_delivery_items?.showSnack(error)
        if (this::itemsAdapter.isInitialized && currentPage != PAGE_START) {
            itemsAdapter.removeLoading()
            isLoading = false
        }
    }

    override fun onRefresh() {
        currentPage = PAGE_START
        isLastPage = false
        if (this::itemsAdapter.isInitialized)
            itemsAdapter.clear()
        getAllDeliveryItems(currentPage)
    }
}