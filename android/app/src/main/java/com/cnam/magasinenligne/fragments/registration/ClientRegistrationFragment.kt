package com.cnam.magasinenligne.fragments.registration

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.RegistrationActivity
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.utils.*
import kotlinx.android.synthetic.main.fragment_client_registration.*

class ClientRegistrationFragment : BaseFragment() {
    private var mapClicked = false
    private var registerClicked = false
    private val locationRequest = 1000
    private val locationPermission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private lateinit var myActivity: RegistrationActivity

    /**
     * OnBackPressedCallback
     */
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                myActivity.finish()
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
                mapClicked = true
                val granted = verifyPermissions(myActivity, locationRequest, locationPermission)
                if (granted) {
                    handlePermissionResult(1)
                }
            }

        }

        bt_register.setOnClickListener {
            if (!registerClicked) {
                registerClicked = true
                val name = et_name.text.toString()
                val phoneNumber = "+961${et_phone.text}"
                val password = et_password.text.toString()
                val confirmPassword = et_confirm_password.text.toString()
                val address = tv_address.text.toString()
                register(name, phoneNumber, password, confirmPassword, address)
            }
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
        }
        if (phoneNumber.isEmpty()) {
            et_phone.error = getString(R.string.required_field_error)
            return
        }
        if (password.isEmpty()) {
            et_password.error = getString(R.string.required_field_error)
            return
        }
        if (confirmPassword.isEmpty()) {
            et_confirm_password.error = getString(R.string.required_field_error)
            return
        }
        if (address.isEmpty()) {
            tv_address.error = getString(R.string.required_field_error)
            return
        }
        if (!isValidPhone(phoneNumber)) {
            et_phone.error = getString(R.string.phone_not_valid_error)
        }
        val code = validatePassword(password)
        if (code != 0) {
            et_password.error = getErrorMessage(code)
            return
        }
        if (password != confirmPassword) {
            et_confirm_password.error = getString(R.string.password_not_match_error)
            return
        }

        // all checks are good.. sending request to api

        myActivity.login("client")


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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            locationRequest -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        } else {
            iv_address_ok.hide()
            tv_address.text = getString(R.string.address)
        }
    }
}