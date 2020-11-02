package com.cnam.magasinenligne.fragments.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.cnam.magasinenligne.utils.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_change_phone.*
import kotlinx.android.synthetic.main.fragment_change_phone.et_phone
import kotlinx.android.synthetic.main.fragment_change_phone.verify_popup
import kotlinx.android.synthetic.main.fragment_client_registration.*
import kotlinx.android.synthetic.main.popup_verification.*
import java.util.concurrent.TimeUnit

class ChangePhoneFragment : BaseFragment(), RetrofitResponseListener {
    private lateinit var myActivity: LandingActivity
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var credential: PhoneAuthCredential
    private var changeClicked = false
    private var resendClicked = false

    /**
     * OnBackPressedCallback
     */
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (verify_popup.isVisible()) {
                    verify_popup.hide()
                } else {
                    myActivity.supportFragmentManager.popBackStack()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myActivity = activity!! as LandingActivity
        return inflater.inflate(R.layout.fragment_change_phone, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        myActivity.hideNavigation()
        loadView()
        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myActivity.accountPaused = false
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(firebaseException: FirebaseException) {
            this@ChangePhoneFragment.logDebug(firebaseException.message.toString())
            bt_change.showSnack("Fail: ${firebaseException.message}")
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            storedVerificationId = verificationId
            resendToken = token
        }

        override fun onCodeAutoRetrievalTimeOut(p0: String) {
            bt_change.showSnack("Auto verification failed")
            tv_resend.show()
        }

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
            et_phone.setText(MyApplication.clientProfile.phoneNumber)
        } else {
            et_phone.setText(MyApplication.merchantProfile.phoneNumber)
        }
    }

    private fun initListeners() {
        bt_change.setOnClickListener {
            if (!changeClicked) {
                val phone = et_phone.text.toString()
                if (!isValidPhone("+961$phone")) {
                    showError(getString(R.string.phone_not_valid))
                    return@setOnClickListener
                }
                if (MyApplication.isClient()) {
                    if (phone == MyApplication.clientProfile.phoneNumber) {
                        showError(getString(R.string.unable_to_change))
                        return@setOnClickListener
                    }
                } else {
                    if (phone == MyApplication.merchantProfile.phoneNumber) {
                        showError(getString(R.string.unable_to_change))
                        return@setOnClickListener
                    }
                }
                changeClicked = true
                myActivity.startLoading()
                myActivity.lockView(true)
                requestVerificationCode(phone)
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

    private fun requestVerificationCode(phoneNumber: String) {
        verify_popup.show()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+961$phoneNumber", // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            myActivity, // Activity (for callback binding)
            callbacks
        )
        tv_resend.setOnClickListener {
            if (!resendClicked) {
                resendClicked = true
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+961$phoneNumber",
                    60,
                    TimeUnit.SECONDS,
                    myActivity,
                    callbacks,
                    resendToken
                )
            }
        }
        et_code.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 6) {
                    credential = PhoneAuthProvider.getCredential(storedVerificationId, s.toString())
                    signInWithPhoneAuthCredential(credential)
                }
            }
        })
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(myActivity) { task ->
                if (task.isSuccessful) {
                    myActivity.startLoading()
                    changeClicked = true
                    if (MyApplication.isClient()) {
                        val fields = hashMapOf(
                            ID to MyApplication.clientProfile.id,
                            PHONE_NUMBER to et_phone.text.toString()
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
                            PHONE_NUMBER to et_phone.text.toString()
                        )
                        val updateCallback =
                            ApiCallback<SingleVendeurResponse>(
                                from_flag = "from_merchant_update",
                                listener = this
                            )
                        AppRetrofitClient.buildService(2).updateVendeur(fields)
                            .enqueue(updateCallback)
                    }
                    verify_popup.hide()
                } else {
                    // Sign in failed, display a message and update the UI
                    logDebug("signInWithCredential:failure ${task.exception}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        bt_register.showSnack("Invalid Code")
                    }
                }
            }
    }

    override fun onSuccess(result: Any, from: String) {
        myActivity.lockView(false)
        myActivity.stopLoading()
        when (from) {
            "from_client_update" -> {
                MyApplication.clientProfile = result as Client
                et_phone.setText(MyApplication.clientProfile.phoneNumber)
            }
            "from_merchant_update" -> {
                MyApplication.merchantProfile = result as Vendeur
                et_phone.setText(MyApplication.merchantProfile.phoneNumber)
            }

        }
        bt_change.showSnack(getString(R.string.phone_updated))
    }

    override fun onFailure(error: String) {
        myActivity.lockView(false)
        myActivity.stopLoading()
        bt_change.showSnack(error)
    }
}