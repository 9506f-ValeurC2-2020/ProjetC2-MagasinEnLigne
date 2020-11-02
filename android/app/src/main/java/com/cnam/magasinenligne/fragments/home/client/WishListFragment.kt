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
import com.cnam.magasinenligne.api.models.*
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.pagination.PaginationListener
import com.cnam.magasinenligne.utils.*
import kotlinx.android.synthetic.main.fragment_wish_list.*

class WishListFragment : BaseFragment(), RetrofitResponseListener,
    SwipeRefreshLayout.OnRefreshListener,
    ProductAdapter.OnClick {
    private lateinit var myActivity: LandingActivity
    private lateinit var productAdapter: ProductAdapter
    private var products = ArrayList<Product>()
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var clickedPosition = -1

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
        return inflater.inflate(R.layout.fragment_wish_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        getAllProducts(0)
        srl_products.setOnRefreshListener(this)
        srl_products.setProgressBackgroundColorSchemeResource(R.color.colorBlue)
        myActivity.hideNavigation()
    }


    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        logDebug("onBackStack")
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
                from_flag = "from_products_get",
                listener = this
            )
        AppRetrofitClient.buildService(3).getProducts(fields).enqueue(getProductsCallback)
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
                this@WishListFragment.isLoading = true
                currentPage++
                getAllProducts(currentPage)
            }

            override val isLastPage: Boolean
                get() = this@WishListFragment.isLastPage
            override val isLoading: Boolean
                get() = this@WishListFragment.isLoading
        })
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
                srl_products?.isRefreshing = false
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
            "from_order_save" -> {
                rv_products.showSnack(getString(R.string.order_placed))
            }
            "from_remove_wish" -> {
                rv_products.showSnack(getString(R.string.removed_wish))
                MyApplication.clientProfile = result as Client
                products.removeAt(clickedPosition)
                productAdapter.notifyItemRemoved(clickedPosition)
            }
        }

    }

    override fun onFailure(error: String) {
        myActivity.stopLoading()
        myActivity.lockView(false)
        srl_products?.isRefreshing = false
        rv_products?.showSnack(error)
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
        clickedPosition = position
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
            .setNegativeButton(getString(R.string.remove)) { dialog, _ ->
                removeWish(position)
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

    private fun removeWish(position: Int) {
        val product = products[position]
        logDebug(MyApplication.clientProfile.id)
        myActivity.startLoading()
        myActivity.lockView(true)
        val fields = hashMapOf(
            ID to MyApplication.clientProfile.id,
            WISH_ID to product.id
        )
        val apiCallback = ApiCallback<SingleClientResponse>("from_remove_wish", this)
        AppRetrofitClient.buildService(1).removeWish(fields).enqueue(apiCallback)
    }
}