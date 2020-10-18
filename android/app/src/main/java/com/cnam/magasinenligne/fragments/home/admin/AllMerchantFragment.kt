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
import com.cnam.magasinenligne.adapters.MerchantAdapter
import com.cnam.magasinenligne.api.ApiCallback
import com.cnam.magasinenligne.api.AppRetrofitClient
import com.cnam.magasinenligne.api.PAGE_INDEX
import com.cnam.magasinenligne.api.RetrofitResponseListener
import com.cnam.magasinenligne.api.models.MultipleVendeurResponse
import com.cnam.magasinenligne.api.models.Vendeur
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.pagination.PaginationListener
import com.cnam.magasinenligne.pagination.PaginationListener.Companion.PAGE_START
import com.cnam.magasinenligne.utils.hide
import com.cnam.magasinenligne.utils.logDebug
import com.cnam.magasinenligne.utils.show
import com.cnam.magasinenligne.utils.showSnack
import kotlinx.android.synthetic.main.fragment_all_merchants.*

class AllMerchantFragment : BaseFragment(), RetrofitResponseListener,
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var myActivity: LandingActivity
    private lateinit var merchantAdapter: MerchantAdapter
    private var merchants = ArrayList<Vendeur>()
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
        return inflater.inflate(R.layout.fragment_all_merchants, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        getAllMerchants(currentPage)
        srl_merchants.setOnRefreshListener(this)
        srl_merchants.setProgressBackgroundColorSchemeResource(R.color.colorBlue)
    }


    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        logDebug("onBackStack")
    }

    private fun getAllMerchants(index: Int) {
        myActivity.startLoading()
        myActivity.lockView(true)
        val fields = hashMapOf(
            PAGE_INDEX to "$index"
        )
        val getMerchantsCallback =
            ApiCallback<MultipleVendeurResponse>(
                from_flag = "from_merchants_get",
                listener = this
            )
        AppRetrofitClient.buildService(2).getVendeurs(fields).enqueue(getMerchantsCallback)
        if (this::merchantAdapter.isInitialized && currentPage != PAGE_START) {
            merchantAdapter.addLoading()
            isLoading = true
        }
    }

    private fun initializeRecyclerView() {
        merchantAdapter = MerchantAdapter(merchants)
        rv_merchants.adapter = merchantAdapter
        val mLayoutManager = LinearLayoutManager(myActivity)
        rv_merchants.layoutManager = mLayoutManager
        rv_merchants.addOnScrollListener(object : PaginationListener(mLayoutManager) {
            override fun loadMoreItems() {
                this@AllMerchantFragment.isLoading = true
                currentPage++
                getAllMerchants(currentPage)
            }

            override val isLastPage: Boolean
                get() = this@AllMerchantFragment.isLastPage
            override val isLoading: Boolean
                get() = this@AllMerchantFragment.isLoading
        })
    }

    override fun onSuccess(result: Any, from: String) {
        if (this::merchantAdapter.isInitialized && currentPage != PAGE_START) {
            merchantAdapter.removeLoading()
            isLoading = false
        }
        myActivity.lockView(false)
        myActivity.stopLoading()
        srl_merchants.isRefreshing = false
        if (result is List<*>) {
            val list = result as List<Vendeur>
            if (!list.isNullOrEmpty()) {
                if (this::merchantAdapter.isInitialized && currentPage != PAGE_START) {
                    merchantAdapter.addItems(list)
                } else {
                    merchants.addAll(list)
                    if (merchants.isNotEmpty()) {
                        tv_no_items.hide()
                        initializeRecyclerView()
                    } else {
                        tv_no_items.show()
                    }
                }
            } else {
                isLastPage = true
                if (merchants.isEmpty()) tv_no_items.show()
            }
        }
    }

    override fun onFailure(error: String) {
        srl_merchants.isRefreshing = false
        myActivity.stopLoading()
        myActivity.lockView(false)
        rv_merchants.showSnack(error)
        if (this::merchantAdapter.isInitialized && currentPage != PAGE_START) {
            merchantAdapter.removeLoading()
            isLoading = false
        }
    }

    override fun onRefresh() {
        currentPage = PAGE_START
        isLastPage = false
        if (this::merchantAdapter.isInitialized)
            merchantAdapter.clear()
        getAllMerchants(currentPage)
    }
}