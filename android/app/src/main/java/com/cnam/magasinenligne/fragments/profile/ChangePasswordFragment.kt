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
import com.cnam.magasinenligne.utils.logDebug
import com.cnam.magasinenligne.utils.showSnack
import com.cnam.magasinenligne.utils.validatePassword
import kotlinx.android.synthetic.main.fragment_change_password.*

class ChangePasswordFragment : BaseFragment(), RetrofitResponseListener {
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
        return inflater.inflate(R.layout.fragment_change_password, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        myActivity.hideNavigation()
        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myActivity.accountPaused = false
    }

    private fun initListeners() {
        et_old_password.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val text = et_old_password.text.toString()
                if (MyApplication.isClient()) {
                    if (text != MyApplication.clientProfile.password) {
                        et_old_password.error = "Wrong password"
                    }
                } else {
                    if (text != MyApplication.merchantProfile.password) {
                        et_old_password.error = "Wrong password"
                    }
                }
            }
        }
        bt_change.setOnClickListener {
            if (changeClicked) {
                val old = et_old_password.text.toString()
                val new = et_new_password.text.toString()
                val newConfirm = et_confirm_password.text.toString()
                if (MyApplication.isClient()) {
                    if (old != MyApplication.clientProfile.password) {
                        showError(getString(R.string.wrong_old))
                        return@setOnClickListener
                    }
                } else {
                    if (old != MyApplication.merchantProfile.password) {
                        showError(getString(R.string.wrong_old))
                        return@setOnClickListener
                    }
                }
                if (validatePassword(new) != 0) {
                    showError(getString(R.string.password_error))
                    return@setOnClickListener
                }
                if (new != newConfirm) {
                    showError(getString(R.string.not_matching))
                    return@setOnClickListener
                }
                changeClicked = true
                myActivity.startLoading()
                myActivity.lockView(true)
                if (MyApplication.isClient()) {
                    val fields = hashMapOf(
                        ID to MyApplication.clientProfile.id,
                        PASSWORD to new
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
                        PASSWORD to new
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

    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        logDebug("onBackStack")
    }

    override fun onSuccess(result: Any, from: String) {
        myActivity.lockView(false)
        myActivity.stopLoading()
        when (from) {
            "from_client_update" -> {
                MyApplication.clientProfile = result as Client
            }
            "from_merchant_update" -> {
                MyApplication.merchantProfile = result as Vendeur
            }

        }
        bt_change.showSnack(getString(R.string.password_updated))
    }

    override fun onFailure(error: String) {
        myActivity.lockView(false)
        myActivity.stopLoading()
        bt_change.showSnack(error)
    }
}