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
import com.cnam.magasinenligne.adapters.ProductAdapter
import com.cnam.magasinenligne.api.ApiCallback
import com.cnam.magasinenligne.api.AppRetrofitClient
import com.cnam.magasinenligne.api.PAGE_INDEX
import com.cnam.magasinenligne.api.RetrofitResponseListener
import com.cnam.magasinenligne.api.models.MultipleProductResponse
import com.cnam.magasinenligne.api.models.Product
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.pagination.PaginationListener
import com.cnam.magasinenligne.pagination.PaginationListener.Companion.PAGE_START
import com.cnam.magasinenligne.utils.hide
import com.cnam.magasinenligne.utils.logDebug
import com.cnam.magasinenligne.utils.show
import com.cnam.magasinenligne.utils.showSnack
import kotlinx.android.synthetic.main.fragment_all_products.*

class AllProductsFragment : BaseFragment(), RetrofitResponseListener,
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var myActivity: LandingActivity
    private lateinit var productAdapter: ProductAdapter
    private var products = ArrayList<Product>()
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
        return inflater.inflate(R.layout.fragment_all_products, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        getAllProducts(0)
        srl_products.setOnRefreshListener(this)
        srl_products.setProgressBackgroundColorSchemeResource(R.color.colorBlue)
    }


    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        logDebug("onBackStack")
    }

    private fun getAllProducts(index: Int) {
        val fields = hashMapOf(
            PAGE_INDEX to "$index"
        )
        myActivity.startLoading()
        myActivity.lockView(true)
        val getProductsCallback =
            ApiCallback<MultipleProductResponse>(
                from_flag = "from_products_get",
                listener = this
            )
        AppRetrofitClient.buildService(3).getProducts(fields).enqueue(getProductsCallback)
        if (this::productAdapter.isInitialized && currentPage != PAGE_START) {
            productAdapter.addLoading()
            isLoading = true
        }
    }

    private fun initializeRecyclerView() {
        productAdapter = ProductAdapter(products, 2)
        rv_products.adapter = productAdapter
        val mLayoutManager = LinearLayoutManager(myActivity)
        rv_products.layoutManager = mLayoutManager
        rv_products.addOnScrollListener(object : PaginationListener(mLayoutManager) {
            override fun loadMoreItems() {
                this@AllProductsFragment.isLoading = true
                currentPage++
                getAllProducts(currentPage)
            }

            override val isLastPage: Boolean
                get() = this@AllProductsFragment.isLastPage
            override val isLoading: Boolean
                get() = this@AllProductsFragment.isLoading
        })
    }

    override fun onSuccess(result: Any, from: String) {
        if (this::productAdapter.isInitialized && currentPage != PAGE_START) {
            productAdapter.removeLoading()
            isLoading = false
        }
        myActivity.lockView(false)
        myActivity.stopLoading()
        srl_products?.isRefreshing = false
        if (result is List<*>) {
            val list = result as List<Product>
            if (!list.isNullOrEmpty()) {
                if (this::productAdapter.isInitialized && currentPage != PAGE_START) {
                    productAdapter.addItems(list)
                } else {
                    products.addAll(list)
                    if (products.isNotEmpty()) {
                        tv_no_items.hide()
                        initializeRecyclerView()
                    } else {
                        tv_no_items.show()
                    }
                }
            } else {
                isLastPage = true
                if (products.isEmpty()) tv_no_items.show()
            }
        }
    }

    override fun onFailure(error: String) {
        myActivity.stopLoading()
        myActivity.lockView(false)
        srl_products?.isRefreshing = false
        rv_products?.showSnack(error)
        if (this::productAdapter.isInitialized && currentPage != PAGE_START) {
            productAdapter.removeLoading()
            isLoading = false
        }
    }

    override fun onRefresh() {
        currentPage = PAGE_START
        isLastPage = false
        if (this::productAdapter.isInitialized)
            productAdapter.clear()
        getAllProducts(currentPage)
    }
}