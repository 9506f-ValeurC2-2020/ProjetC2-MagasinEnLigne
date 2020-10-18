package com.cnam.magasinenligne.fragments.home.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.LandingActivity
import com.cnam.magasinenligne.adapters.ClientsAdapter
import com.cnam.magasinenligne.api.ApiCallback
import com.cnam.magasinenligne.api.AppRetrofitClient
import com.cnam.magasinenligne.api.RetrofitResponseListener
import com.cnam.magasinenligne.api.models.Client
import com.cnam.magasinenligne.api.models.MultipleClientResponse
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.utils.hide
import com.cnam.magasinenligne.utils.logDebug
import com.cnam.magasinenligne.utils.show
import com.cnam.magasinenligne.utils.showSnack
import kotlinx.android.synthetic.main.fragment_all_clients.*

class AllClientsFragment : BaseFragment(), RetrofitResponseListener {
    private lateinit var myActivity: LandingActivity
    private lateinit var clientsAdapter: ClientsAdapter
    private var clients = ArrayList<Client>()

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
        getAllClients()
    }


    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        logDebug("onBackStack")
    }

    private fun getAllClients() {
        myActivity.startLoading()
        myActivity.lockView(true)
        val getClientsCallback =
            ApiCallback<MultipleClientResponse>(
                from_flag = "from_clients_get",
                listener = this
            )
        AppRetrofitClient.buildService(1).getClients().enqueue(getClientsCallback)
    }

    override fun onSuccess(result: Any, from: String) {
        myActivity.stopLoading()
        myActivity.lockView(false)
        if (result is List<*>) {
            val list = result as List<Client>
            clients = list as ArrayList<Client>
            if (clients.isNotEmpty()) {
                tv_no_items.hide()
                initializeRecyclerView()
            } else {
                tv_no_items.show()
            }
        }
    }

    override fun onFailure(error: String) {
        myActivity.stopLoading()
        myActivity.lockView(false)
        rv_clients.showSnack(error)
    }

    private fun initializeRecyclerView() {
        clientsAdapter = ClientsAdapter(clients)
        rv_clients.adapter = clientsAdapter
        rv_clients.layoutManager = LinearLayoutManager(myActivity)
    }
}