package com.cnam.magasinenligne.fragments.home.merchant

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
import com.cnam.magasinenligne.adapters.ProductAdapter
import com.cnam.magasinenligne.api.*
import com.cnam.magasinenligne.api.models.MultipleProductResponse
import com.cnam.magasinenligne.api.models.Product
import com.cnam.magasinenligne.api.models.SingleProductResponse
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.pagination.PaginationListener
import com.cnam.magasinenligne.utils.*
import kotlinx.android.synthetic.main.fragment_my_products.*
import kotlinx.android.synthetic.main.popup_sale.*

class MyProductsFragment : BaseFragment(), RetrofitResponseListener,
    SwipeRefreshLayout.OnRefreshListener,
    ProductAdapter.OnSaleClick {
    private lateinit var myActivity: LandingActivity
    private lateinit var productAdapter: ProductAdapter
    private var products = ArrayList<Product>()
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var addClicked = false
    private var okClicked = false
    private var cancelClicked = false
    private var currentPosition = -1

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
        return inflater.inflate(R.layout.fragment_my_products, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        getAllProducts(0)
        srl_products.setOnRefreshListener(this)
        srl_products.setProgressBackgroundColorSchemeResource(R.color.colorBlue)
        listeners()
        myActivity.hideNavigation()
    }


    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        if (isActiveIn(myActivity)) {
            addClicked = false
            logDebug("Active")
        }
    }

    override fun onDestroyView() {
        myActivity.homePaused = false
        super.onDestroyView()
    }

    private fun getAllProducts(index: Int) {
        val fields = hashMapOf(
            MERCHANT_ID to MyApplication.merchantProfile.id,
            PAGE_INDEX to "$index"
        )
        myActivity.startLoading()
        myActivity.lockView(true)
        val getProductsCallback =
            ApiCallback<MultipleProductResponse>(
                from_flag = "from_products_get",
                listener = this
            )
        AppRetrofitClient.buildService(3).getMerchantProducts(fields).enqueue(getProductsCallback)
        if (this::productAdapter.isInitialized && currentPage != PaginationListener.PAGE_START) {
            productAdapter.addLoading()
            isLoading = true
        }
    }

    private fun initializeRecyclerView() {
        productAdapter = ProductAdapter(products, 1)
        productAdapter.saleClick(this)
        rv_products.adapter = productAdapter
        val mLayoutManager = LinearLayoutManager(myActivity)
        rv_products.layoutManager = mLayoutManager
        rv_products.addOnScrollListener(object : PaginationListener(mLayoutManager) {
            override fun loadMoreItems() {
                this@MyProductsFragment.isLoading = true
                currentPage++
                getAllProducts(currentPage)
            }

            override val isLastPage: Boolean
                get() = this@MyProductsFragment.isLastPage
            override val isLoading: Boolean
                get() = this@MyProductsFragment.isLoading
        })
    }

    private fun listeners() {
        fab_add_product.setOnClickListener {
            if (!addClicked) {
                addClicked = true
                myActivity.lockView(true)
                myActivity.supportFragmentManager.addTransaction(
                    AddProductFragment(),
                    AddProductFragment::class.java.simpleName,
                    R.anim.slide_in_up,
                    R.anim.slide_out_down
                )
            }
        }

        bt_ok.setOnClickListener {
            if (!okClicked) {
                val product = products[currentPosition]
                val salePrice = et_sale_price.text.toString().toDouble()
                if (product.price < salePrice) {
                    myActivity.createDialog(
                        getString(R.string.error),
                        getString(R.string.sale_price_error)
                    )
                    return@setOnClickListener
                }
                okClicked = true
                myActivity.startLoading()
                myActivity.lockView(true)
                val fields = hashMapOf(
                    ID to product.id,
                    SALE_PRICE to "$salePrice"
                )
                val apiCallback =
                    ApiCallback<SingleProductResponse>("from_product_put_on_sale", this)
                AppRetrofitClient.buildService(3).putOnSale(fields).enqueue(apiCallback)

            }
        }
        bt_cancel.setOnClickListener {
            et_sale_price.setText("")
            sale_popup.hide()
        }
    }

    override fun onSuccess(result: Any, from: String) {
        myActivity.lockView(false)
        myActivity.stopLoading()
        when (from) {
            "from_products_get" -> {
                if (this::productAdapter.isInitialized && currentPage != PaginationListener.PAGE_START) {
                    productAdapter.removeLoading()
                    isLoading = false
                }
                srl_products.isRefreshing = false
                if (result is List<*>) {
                    val list = result as List<Product>
                    if (!list.isNullOrEmpty()) {
                        if (this::productAdapter.isInitialized && currentPage != PaginationListener.PAGE_START) {
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
            "from_product_put_on_sale" -> {
                val updatedItem = result as Product
                products[currentPosition] = updatedItem
                productAdapter.notifyItemChanged(currentPosition)
                rv_products.showSnack(getString(R.string.product_on_sale))
                sale_popup.hide()
                okClicked = false
            }
        }

    }

    override fun onFailure(error: String) {
        srl_products.isRefreshing = false
        myActivity.stopLoading()
        myActivity.lockView(false)
        rv_products.showSnack(error)
        if (this::productAdapter.isInitialized && currentPage != PaginationListener.PAGE_START) {
            productAdapter.removeLoading()
            isLoading = false
        }
    }

    override fun onRefresh() {
        currentPage = PaginationListener.PAGE_START
        isLastPage = false
        if (this::productAdapter.isInitialized)
            productAdapter.clear()
        getAllProducts(currentPage)
    }

    override fun onSaleClick(position: Int) {
        currentPosition = position
        sale_popup.show()
    }
}