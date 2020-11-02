package com.cnam.magasinenligne.fragments.landing

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import kotlinx.android.synthetic.main.fragment_shop.*

class ShopFragment : BaseFragment(), RetrofitResponseListener,
    SwipeRefreshLayout.OnRefreshListener,
    ProductAdapter.OnClick {
    private lateinit var myActivity: LandingActivity
    private lateinit var productAdapter: ProductAdapter
    private var products = ArrayList<Product>()
    private var searchResult = ArrayList<Product>()
    private var listBeforeSort = ArrayList<Product>()
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var shouldTriggerListener = true

    /**
     * OnBackPressedCallback
     */
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity!!.finish()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myActivity = activity!! as LandingActivity
        return inflater.inflate(R.layout.fragment_shop, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        getAllProducts(0)
        srl_products.setOnRefreshListener(this)
        srl_products.setProgressBackgroundColorSchemeResource(R.color.colorBlue)
        initListeners()
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
        if (searchResult.isEmpty()) {
            rv_products.addOnScrollListener(object : PaginationListener(mLayoutManager) {
                override fun loadMoreItems() {
                    this@ShopFragment.isLoading = true
                    currentPage++
                    getAllProducts(currentPage)
                }

                override val isLastPage: Boolean
                    get() = this@ShopFragment.isLastPage
                override val isLoading: Boolean
                    get() = this@ShopFragment.isLoading
            })
        }

    }

    private fun initListeners() {
        sp_filter_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 1) {
                    hideShowAgeSexFilters(false)
                } else {
                    hideShowAgeSexFilters(true)
                }
                filterProducts(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        sp_filter_age.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                filterProducts(1)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        sp_filter_sex.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                filterProducts(2)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        sp_filter_price.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                filterProducts(3)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    findProduct(s.toString())
                } else {
                    onRefresh()
                }
            }
        })
    }

    private fun findProduct(name: String) {
        for (p in products) {
            if (p.name.contains(name, true)) {
                searchResult.add(p)
            }
        }
        if (this::productAdapter.isInitialized) {
            products.clear()
            if (searchResult.isEmpty()) {
                productAdapter.notifyDataSetChanged()
                tv_no_items.show()
            } else {
                products.addAll(searchResult)
                productAdapter.notifyDataSetChanged()
                tv_no_items.hide()
            }
        }

    }

    private fun filterProducts(code: Int) {
        if (listBeforeSort.isEmpty()) {
            if (searchResult.isNotEmpty()) listBeforeSort.addAll(searchResult) else listBeforeSort.addAll(
                products
            )
        }
        if (code == 3) {
            if (sp_filter_price.selectedItem.toString() == "all") {
                searchResult.clear()
                searchResult.addAll(listBeforeSort)
            } else if (sp_filter_price.selectedItem.toString() == "highest") {
                if (searchResult.isEmpty()) {
                    products.sortByDescending { product -> product.price }
                } else {
                    searchResult.sortByDescending { product -> product.price }
                }
            } else {
                if (searchResult.isEmpty()) {
                    products.sortBy { product -> product.price }
                } else {
                    searchResult.sortBy { product -> product.price }
                }
            }
        } else {
            for (p in products) {
                when (code) {
                    0 -> {
                        if (p.category == sp_filter_category.selectedItem.toString()) {
                            searchResult.add(p)
                        }
                    }
                    1 -> {
                        if (p.ageCategory == sp_filter_age.selectedItem.toString()) {
                            searchResult.add(p)
                        }
                    }
                    2 -> {
                        if (p.sex == sp_filter_category.selectedItem.toString().toIntSex()) {
                            searchResult.add(p)
                        }
                    }

                }
            }
        }
        if (this::productAdapter.isInitialized) {
            if (searchResult.isNotEmpty()) {
                products.clear()
                products.addAll(searchResult)
            }
            productAdapter.notifyDataSetChanged()
        }
    }

    private fun hideShowAgeSexFilters(hide: Boolean) {
        if (hide) {
            sp_filter_age.hide()
            tv_dummy_age.hide()
            sp_filter_sex.hide()
            tv_dummy_sex.hide()
        } else {
            sp_filter_age.show()
            tv_dummy_age.show()
            sp_filter_sex.show()
            tv_dummy_sex.show()
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
            "from_add_to_wish" -> {
                rv_products.showSnack(getString(R.string.added_to_wish))
                MyApplication.clientProfile = result as Client
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
        if (shouldTriggerListener) {
            shouldTriggerListener = false
            et_search.setText("")
            return
        }
        searchResult.clear()
        listBeforeSort.clear()
        currentPage = PaginationListener.PAGE_START
        isLastPage = false
        if (this::productAdapter.isInitialized)
            productAdapter.clear()
        getAllProducts(currentPage)
        sp_filter_category.setSelection(0)
        sp_filter_price.setSelection(0)
        shouldTriggerListener = true
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