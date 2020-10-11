package com.cnam.magasinenligne.fragments.registration

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.RegistrationActivity
import com.cnam.magasinenligne.api.*
import com.cnam.magasinenligne.api.models.Client
import com.cnam.magasinenligne.api.models.SingleClientResponse
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.utils.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_client_registration.*
import kotlinx.android.synthetic.main.popup_verification.*
import java.util.concurrent.TimeUnit

class ClientRegistrationFragment : BaseFragment(), RetrofitResponseListener {
    private var mapClicked = false
    private var registerClicked = false
    private val locationRequest = 1000
    private val locationPermission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private lateinit var myActivity: RegistrationActivity
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var credential: PhoneAuthCredential

    /**
     * OnBackPressedCallback
     */
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (verify_popup.isVisible()) {
                    verify_popup.hide()
                } else {
                    myActivity.finish()
                }
            }
        }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(firebaseException: FirebaseException) {
            this@ClientRegistrationFragment.logDebug(firebaseException.message.toString())
            bt_register.showSnack("Fail: ${firebaseException.message}")
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            storedVerificationId = verificationId
            resendToken = token

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myActivity = (activity!! as RegistrationActivity)
        return inflater.inflate(R.layout.fragment_client_registration, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        myActivity.addOnBackStackListener(this)
        listeners()
        auth = FirebaseAuth.getInstance()
    }

    private fun listeners() {
        et_name.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (et_name.text.toString().isEmpty()) {
                    et_name.error = getString(R.string.required_field_error)
                    iv_name_ok.hide()
                } else {
                    iv_name_ok.show()
                }
            }
        }
        et_phone.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val phone = et_phone.text.toString()
                if (phone.isEmpty()) {
                    et_phone.error = getString(R.string.required_field_error)
                    iv_phone_ok.hide()
                } else {
                    val lebanonPhone = "+961$phone"
                    if (isValidPhone(lebanonPhone)) {
                        iv_phone_ok.show()
                    } else {
                        iv_phone_ok.hide()
                        et_phone.error = getString(R.string.phone_not_valid_error)
                    }
                }
            }
        }
        et_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                iv_password_ok.hide()
            }
        })
        et_confirm_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                iv_confirm_password_ok.hide()
            }
        })
        et_password.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val password = et_password.text.toString()
                if (password.isEmpty()) {
                    et_password.error = getString(R.string.required_field_error)
                    iv_password_ok.hide()
                } else {
                    val code = validatePassword(password)
                    if (code != 0) {
                        et_password.error = getErrorMessage(code)
                        iv_password_ok.hide()
                    } else {
                        iv_password_ok.show()
                        if (et_confirm_password.text.toString() == password) {
                            et_confirm_password.error = null
                            iv_confirm_password_ok.show()
                        } else {
                            et_confirm_password.error = getString(R.string.password_not_match_error)
                            iv_confirm_password_ok.hide()
                        }
                    }
                }
            }
        }
        et_confirm_password.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val confirmedPassword = et_confirm_password.text.toString()
                if (confirmedPassword.isEmpty()) {
                    et_confirm_password.error = getString(R.string.required_field_error)
                    iv_confirm_password_ok.hide()
                } else {
                    if (confirmedPassword != et_password.text.toString()) {
                        et_confirm_password.error = getString(R.string.password_not_match_error)
                        iv_confirm_password_ok.hide()
                    } else {
                        iv_confirm_password_ok.show()
                    }
                }
            }
        }
        cl_address.setOnClickListener {
            logDebug("clicking address")
            if (!mapClicked) {
                val granted = checkPermissions(activity!!, locationRequest, locationPermission)
                if (granted) {

                    handlePermissionResult(1)
                } else {
                    myActivity.createDialog(
                        getString(R.string.permission_needed),
                        getString(R.string.permission_importance_message)
                    ).setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                        mapClicked = true
                        requestPermissions(locationPermission, locationRequest)
                        dialog.dismiss()
                    }.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        handlePermissionResult(0)
                        dialog.dismiss()
                    }
                        .create()
                        .show()

                }
            }

        }

        cb_already_have_account.setOnCheckedChangeListener { _, isChecked ->
            group_login.visibility = if (isChecked) {
                bt_register.text = getString(R.string.login)
                View.INVISIBLE
            } else {
                bt_register.text = getString(R.string.register)
                View.VISIBLE
            }

        }
        bt_register.setOnClickListener {
            if (!registerClicked) {
                if (cb_already_have_account.isChecked) {
                    val phoneNumber = et_phone.text.toString()
                    val password = et_password.text.toString()
                    login(phoneNumber, password)
                } else {
                    val name = et_name.text.toString()
                    val phoneNumber = et_phone.text.toString()
                    val password = et_password.text.toString()
                    val confirmPassword = et_confirm_password.text.toString()
                    val address = tv_address.text.toString()
                    register(name, phoneNumber, password, confirmPassword, address)
                }
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(myActivity) { task ->
                if (task.isSuccessful) {
                    myActivity.startLoading()
                    registerClicked = true
                    val fields = hashMapOf(
                        FULL_NAME to et_name.text.toString(),
                        PHONE_NUMBER to et_phone.text.toString(),
                        PASSWORD to et_password.text.toString(),
                        ADDRESS to tv_address.text.toString()
                    )
                    val registerCallback =
                        ApiCallback<SingleClientResponse>(
                            from_flag = "from_client_register",
                            listener = this
                        )
                    AppRetrofitClient.buildService(1).saveClient(fields).enqueue(registerCallback)
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

    private fun handlePermissionPopup() {
        requestPermissions(locationPermission, locationRequest)
        val granted = verifyPermissions(myActivity, locationRequest, locationPermission)
        if (granted) {
            handlePermissionResult(1)
        }
    }

    private fun register(
        name: String,
        phoneNumber: String,
        password: String,
        confirmPassword: String,
        address: String
    ) {
        if (name.isEmpty()) {
            et_name.error = getString(R.string.required_field_error)
            return
        } else {
            et_name.error = null
        }
        if (phoneNumber.isEmpty()) {
            et_phone.error = getString(R.string.required_field_error)
            return
        } else {
            et_phone.error = null
        }
        if (password.isEmpty()) {
            et_password.error = getString(R.string.required_field_error)
            return
        } else {
            et_password.error = null
        }
        if (confirmPassword.isEmpty()) {
            et_confirm_password.error = getString(R.string.required_field_error)
            return
        } else {
            et_confirm_password.error = null
        }
        if (address.isEmpty()) {
            tv_address.error = getString(R.string.required_field_error)
            return
        } else {
            tv_address.error = null
        }
        if (!isValidPhone("+961$phoneNumber")) {
            et_phone.error = getString(R.string.phone_not_valid_error)
            return
        } else {
            et_phone.error = null
        }
        val code = validatePassword(password)
        if (code != 0) {
            et_password.error = getErrorMessage(code)
            return
        } else {
            et_password.error = null
        }
        if (password != confirmPassword) {
            et_confirm_password.error = getString(R.string.password_not_match_error)
            return
        } else {
            et_confirm_password.error = null
        }
        // all checks are good.. verifying phone with firebase
        requestVerificationCode(phoneNumber)
    }

    private fun login(phoneNumber: String, password: String) {
        if (phoneNumber.isEmpty()) {
            et_phone.error = getString(R.string.required_field_error)
            return
        } else {
            et_phone.error = null
        }
        if (password.isEmpty()) {
            et_password.error = getString(R.string.required_field_error)
            return
        } else {
            et_password.error = null
        }
        if (!isValidPhone("+961$phoneNumber")) {
            et_phone.error = getString(R.string.phone_not_valid_error)
            return
        } else {
            et_phone.error = null
        }

        myActivity.startLoading()
        registerClicked = true
        val fields = hashMapOf(
            PHONE_NUMBER to phoneNumber,
            PASSWORD to password
        )
        val registerCallback =
            ApiCallback<SingleClientResponse>(from_flag = "from_client_login", listener = this)
        AppRetrofitClient.buildService(1).loginClient(fields).enqueue(registerCallback)
    }

    private fun getErrorMessage(code: Int): String? {
        return when (code) {
            -1 -> getString(R.string.password_length_error)
            -2 -> getString(R.string.password_upper_case_error)
            -3 -> getString(R.string.password_lower_case_error)
            -4 -> getString(R.string.password_digit_error)
            else -> getString(R.string.password_special_character_error)
        }

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        logDebug("$requestCode is my request code")
        when (requestCode) {
            locationRequest -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    logDebug("granted")
                    handlePermissionResult(1)
                } else {
                    handlePermissionResult(0)
                }
            }
        }
    }

    private fun handlePermissionResult(successOrFail: Int) {
        if (successOrFail == 1) {
            myActivity.supportFragmentManager.addTransaction(
                MapFragment(),
                MapFragment::class.java.simpleName,
                R.anim.enter_from_right,
                R.anim.exit_to_right
            )
        } else {
            myActivity.createDialog(
                getString(R.string.alert),
                getString(R.string.location_is_necessary)
            )
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    mapClicked = false
                    dialog.dismiss()
                }
                .create()
                .show()
        }

    }

    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        mapClicked = false
        val locationInitialized = myActivity.isLocationInitialized()
        logDebug("Initialized => $locationInitialized")
        if (locationInitialized) {
            val text = "${myActivity.location.latitude},${myActivity.location.longitude}"
            tv_address.text = text
            iv_address_ok.show()
            tv_address.error = null
        } else {
            iv_address_ok.hide()
            tv_address.hint = getString(R.string.address)
            tv_address.error = getString(R.string.required_field_error)
        }
    }

    override fun onSuccess(result: Any, from: String) {
        myActivity.stopLoading()
        registerClicked = false
        val client = result as Client
        val message =
            if (from == "from_client_register") "Successful Register" else "Successful Login"
        bt_register.showSnack(message)
        myActivity.login("client", client.id)
    }


    override fun onFailure(error: String) {
        registerClicked = false
        myActivity.stopLoading()
        bt_register.showSnack(error)
    }
}