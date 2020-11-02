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
import com.cnam.magasinenligne.adapters.ProductAdapter
import com.cnam.magasinenligne.api.*
import com.cnam.magasinenligne.api.models.MultipleProductResponse
import com.cnam.magasinenligne.api.models.Product
import com.cnam.magasinenligne.api.models.SingleClientResponse
import com.cnam.magasinenligne.api.models.SingleOrderResponse
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.pagination.PaginationListener
import com.cnam.magasinenligne.utils.*
import kotlinx.android.synthetic.main.fragment_sales.*

class SalesFragment : BaseFragment(), RetrofitResponseListener,
    SwipeRefreshLayout.OnRefreshListener,
    ProductAdapter.OnClick {
    private lateinit var myActivity: LandingActivity
    private lateinit var productAdapter: ProductAdapter
    private var products = ArrayList<Product>()
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var addClicked = false

    /**
     * OnBackPressedCallback
     */
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                myActivity.supportFragmentManager.popBackStack()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myActivity = activity!! as LandingActivity
        return inflater.inflate(R.layout.fragment_sales, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        getAllProducts(0)
        srl_products.setOnRefreshListener(this)
        srl_products.setProgressBackgroundColorSchemeResource(R.color.colorBlue)
        myActivity.addOnBackStackListener(this)

    }


    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
    }

    override fun onDestroyView() {
        myActivity.homePaused = false
        super.onDestroyView()
    }

    private fun getAllProducts(index: Int) {
        val fields = hashMapOf(
            PAGE_INDEX to "$index"
        )
        myActivity.startLoading()
        myActivity.lockView(true)
        val getProductsCallback =
            ApiCallback<MultipleProductResponse>(
                from_flag = "from_products_sales",
                listener = this
            )
        AppRetrofitClient.buildService(3).findSales(fields).enqueue(getProductsCallback)
        if (this::productAdapter.isInitialized && currentPage != PaginationListener.PAGE_START) {
            productAdapter.addLoading()
            isLoading = true
        }
    }

    private fun initializeRecyclerView() {
        productAdapter = ProductAdapter(products)
        productAdapter.click(this)
        rv_products.adapter = productAdapter
        val mLayoutManager = LinearLayoutManager(myActivity)
        rv_products.layoutManager = mLayoutManager
        rv_products.addOnScrollListener(object : PaginationListener(mLayoutManager) {
            override fun loadMoreItems() {
                this@SalesFragment.isLoading = true
                currentPage++
                getAllProducts(currentPage)
            }

            override val isLastPage: Boolean
                get() = this@SalesFragment.isLastPage
            override val isLoading: Boolean
                get() = this@SalesFragment.isLoading
        })
    }

    override fun onSuccess(result: Any, from: String) {
        if (this::productAdapter.isInitialized && currentPage != PaginationListener.PAGE_START) {
            productAdapter.removeLoading()
            isLoading = false
        }
        myActivity.lockView(false)
        myActivity.stopLoading()
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

    override fun onClick(position: Int) {
        val product = products[position]

        myActivity.createDialog(
            getString(R.string.get_product, product.name),
            getString(R.string.confirm_op)
        )
            .setCancelable(true)
            .setPositiveButton(getString(R.string.buy)) { dialog, _ ->
                buyProduct(position)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.add_to_wish)) { dialog, _ ->
                addToWishList(position)
                dialog.dismiss()
            }
            .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun buyProduct(position: Int) {
        val product = products[position]
        val price = if (product.onSale) "${product.salePrice}" else "${product.price}"
        myActivity.startLoading()
        myActivity.lockView(true)
        val fields = hashMapOf(
            DESCRIPTION to product.name,
            TIME to getNowTimeStamp(),
            COST to price,
            FROM_CLIENT to MyApplication.clientProfile.id,
            TO_VENDEUR to product.providedBy
        )
        val apiCallback = ApiCallback<SingleOrderResponse>("from_order_save", this)
        AppRetrofitClient.buildService(4).saveOrder(fields).enqueue(apiCallback)
    }

    private fun addToWishList(position: Int) {
        val product = products[position]
        logDebug(MyApplication.clientProfile.id)
        myActivity.startLoading()
        myActivity.lockView(true)
        val fields = hashMapOf(
            ID to MyApplication.clientProfile.id,
            WISH_ID to product.id
        )
        val apiCallback = ApiCallback<SingleClientResponse>("from_add_to_wish", this)
        AppRetrofitClient.buildService(1).addToWish(fields).enqueue(apiCallback)
    }
}