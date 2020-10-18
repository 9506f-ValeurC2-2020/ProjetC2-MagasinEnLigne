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
import com.cnam.magasinenligne.adapters.ClientsAdapter
import com.cnam.magasinenligne.api.ApiCallback
import com.cnam.magasinenligne.api.AppRetrofitClient
import com.cnam.magasinenligne.api.PAGE_INDEX
import com.cnam.magasinenligne.api.RetrofitResponseListener
import com.cnam.magasinenligne.api.models.Client
import com.cnam.magasinenligne.api.models.MultipleClientResponse
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.pagination.PaginationListener
import com.cnam.magasinenligne.pagination.PaginationListener.Companion.PAGE_START
import com.cnam.magasinenligne.utils.hide
import com.cnam.magasinenligne.utils.logDebug
import com.cnam.magasinenligne.utils.show
import com.cnam.magasinenligne.utils.showSnack
import kotlinx.android.synthetic.main.fragment_all_clients.*


class AllClientsFragment : BaseFragment(), RetrofitResponseListener,
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var myActivity: LandingActivity
    private lateinit var clientsAdapter: ClientsAdapter
    private var clients = ArrayList<Client>()
    private var currentPage: Int = PAGE_START
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
        return inflater.inflate(R.layout.fragment_all_clients, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        myActivity.addOnBackStackListener(this)
        getAllClients(currentPage)
        srl_clients.setOnRefreshListener(this)
        srl_clients.setProgressBackgroundColorSchemeResource(R.color.colorBlue)
    }


    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        logDebug("onBackStack")
    }

    private fun getAllClients(index: Int) {
        myActivity.startLoading()
        myActivity.lockView(true)
        val fields = hashMapOf(
            PAGE_INDEX to "$index"
        )
        val getClientsCallback =
            ApiCallback<MultipleClientResponse>(
                from_flag = "from_clients_get",
                listener = this
            )
        AppRetrofitClient.buildService(1).getClients(fields).enqueue(getClientsCallback)
        if (this::clientsAdapter.isInitialized && currentPage != PAGE_START) {
            clientsAdapter.addLoading()
            isLoading = true
        }
    }

    override fun onSuccess(result: Any, from: String) {
        if (this::clientsAdapter.isInitialized && currentPage != PAGE_START) {
            clientsAdapter.removeLoading()
            isLoading = false
        }
        myActivity.lockView(false)
        myActivity.stopLoading()
        srl_clients.isRefreshing = false
        if (result is List<*>) {
            val list = result as List<Client>
            if (!list.isNullOrEmpty()) {
                if (this::clientsAdapter.isInitialized && currentPage != PAGE_START) {
                    clientsAdapter.addItems(list)
                } else {
                    clients.addAll(list)
                    if (clients.isNotEmpty()) {
                        tv_no_items.hide()
                        initializeRecyclerView()
                    } else {
                        tv_no_items.show()
                    }
                }
            } else {
                isLastPage = true
                if (clients.isEmpty()) tv_no_items.show()
            }
        }
    }

    override fun onFailure(error: String) {
        srl_clients.isRefreshing = false
        myActivity.stopLoading()
        myActivity.lockView(false)
        rv_clients.showSnack(error)
        if (this::clientsAdapter.isInitialized && currentPage != PAGE_START) {
            clientsAdapter.removeLoading()
            isLoading = false
        }
    }

    private fun initializeRecyclerView() {
        clientsAdapter = ClientsAdapter(clients)
        rv_clients.adapter = clientsAdapter
        val mLayoutManager = LinearLayoutManager(myActivity)
        rv_clients.layoutManager = mLayoutManager
        rv_clients.addOnScrollListener(object : PaginationListener(mLayoutManager) {
            override fun loadMoreItems() {
                this@AllClientsFragment.isLoading = true
                currentPage++
                getAllClients(currentPage)
            }

            override val isLastPage: Boolean
                get() = this@AllClientsFragment.isLastPage
            override val isLoading: Boolean
                get() = this@AllClientsFragment.isLoading
        })
    }

    override fun onRefresh() {
        currentPage = PAGE_START
        isLastPage = false
        if (this::clientsAdapter.isInitialized)
            clientsAdapter.clear()
        getAllClients(currentPage)
    }
}