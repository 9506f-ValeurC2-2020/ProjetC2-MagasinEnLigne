package com.cnam.magasinenligne.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.cnam.magasinenligne.MyApplication
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.LandingActivity
import com.cnam.magasinenligne.api.*
import com.cnam.magasinenligne.api.models.Client
import com.cnam.magasinenligne.api.models.SingleClientResponse
import com.cnam.magasinenligne.api.models.SingleVendeurResponse
import com.cnam.magasinenligne.api.models.Vendeur
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.utils.createDialog
import com.cnam.magasinenligne.utils.isValidEmail
import com.cnam.magasinenligne.utils.logDebug
import com.cnam.magasinenligne.utils.showSnack
import kotlinx.android.synthetic.main.fragment_change_email.*

class ChangeEmailFragment : BaseFragment(), RetrofitResponseListener {
    private lateinit var myActivity: LandingActivity
    private var changeClicked = false

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
        return inflater.inflate(R.layout.fragment_change_email, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        loadView()
        initListeners()
        myActivity.hideNavigation()
    }

    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        logDebug("onBackStack")
    }

    private fun loadView() {
        if (MyApplication.isClient()) {
            if (!MyApplication.clientProfile.email.isNullOrEmpty()) {
                et_email.setText(MyApplication.clientProfile.email)
            }
        } else {
            if (!MyApplication.merchantProfile.email.isNullOrEmpty()) {
                et_email.setText(MyApplication.merchantProfile.email)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myActivity.accountPaused = false
    }

    private fun initListeners() {
        bt_change.setOnClickListener {
            if (!changeClicked) {
                val email = et_email.text.toString()
                if (MyApplication.isClient()) {
                    if (!MyApplication.clientProfile.email.isNullOrEmpty()) {
                        if (email == MyApplication.clientProfile.email) {
                            showError(getString(R.string.same_email))
                            return@setOnClickListener
                        }
                    }
                } else {
                    if (!MyApplication.merchantProfile.email.isNullOrEmpty()) {
                        showError(getString(R.string.same_email))
                        return@setOnClickListener
                    }
                }
                if (!isValidEmail(email)) {
                    showError(getString(R.string.email_not_valid))
                    return@setOnClickListener
                }
                changeClicked = true
                myActivity.startLoading()
                myActivity.lockView(true)
                if (MyApplication.isClient()) {
                    val fields = hashMapOf(
                        ID to MyApplication.clientProfile.id,
                        EMAIL to email
                    )
                    val updateCallback =
                        ApiCallback<SingleClientResponse>(
                            from_flag = "from_client_update",
                            listener = this
                        )
                    AppRetrofitClient.buildService(1).updateClient(fields)
                        .enqueue(updateCallback)
                } else {
                    val fields = hashMapOf(
                        ID to MyApplication.merchantProfile.id,
                        EMAIL to email
                    )
                    val updateCallback =
                        ApiCallback<SingleVendeurResponse>(
                            from_flag = "from_merchant_update",
                            listener = this
                        )
                    AppRetrofitClient.buildService(2).updateVendeur(fields)
                        .enqueue(updateCallback)
                }

            }

        }
    }

    private fun showError(message: String) {
        myActivity.createDialog(getString(R.string.error), message)
            .setCancelable(true)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onSuccess(result: Any, from: String) {
        myActivity.lockView(false)
        myActivity.stopLoading()
        when (from) {
            "from_client_update" -> {
                MyApplication.clientProfile = result as Client
                et_email.setText(MyApplication.clientProfile.email)
            }
            "from_merchant_update" -> {
                MyApplication.merchantProfile = result as Vendeur
                et_email.setText(MyApplication.merchantProfile.email)
            }

        }
        bt_change.showSnack(getString(R.string.email_updated))
    }

    override fun onFailure(error: String) {
        myActivity.lockView(false)
        myActivity.stopLoading()
        bt_change.showSnack(error)
    }
}
