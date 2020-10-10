package com.cnam.magasinenligne.fragments.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.RegistrationActivity
import com.cnam.magasinenligne.api.*
import com.cnam.magasinenligne.api.models.SingleClientResponse
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.utils.hide
import com.cnam.magasinenligne.utils.isValidPhone
import com.cnam.magasinenligne.utils.show
import com.cnam.magasinenligne.utils.validatePassword
import kotlinx.android.synthetic.main.fragment_merchant_registration.bt_register
import kotlinx.android.synthetic.main.fragment_merchant_registration.cb_already_have_account
import kotlinx.android.synthetic.main.fragment_merchant_registration.et_confirm_password
import kotlinx.android.synthetic.main.fragment_merchant_registration.et_name
import kotlinx.android.synthetic.main.fragment_merchant_registration.et_password
import kotlinx.android.synthetic.main.fragment_merchant_registration.et_phone
import kotlinx.android.synthetic.main.fragment_merchant_registration.group_login
import kotlinx.android.synthetic.main.fragment_merchant_registration.iv_confirm_password_ok
import kotlinx.android.synthetic.main.fragment_merchant_registration.iv_name_ok
import kotlinx.android.synthetic.main.fragment_merchant_registration.iv_password_ok
import kotlinx.android.synthetic.main.fragment_merchant_registration.iv_phone_ok

class MerchantRegistrationFragment : BaseFragment(), RetrofitResponseListener {
    private var registerClicked = false
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
        myActivity = activity!! as RegistrationActivity
        return inflater.inflate(R.layout.fragment_merchant_registration, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)

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
                    register(name, phoneNumber, password, confirmPassword)
                }
            }
        }
    }

    private fun register(
        name: String,
        phoneNumber: String,
        password: String,
        confirmPassword: String
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

        // all checks are good.. sending request to api
        myActivity.startLoading()
        registerClicked = true
        val fields = hashMapOf(
            FULL_NAME to name,
            PHONE_NUMBER to phoneNumber,
            PASSWORD to password
        )
        val registerCallback =
            ApiCallback<SingleClientResponse>(from_flag = "from_merchant_register", listener = this)
        AppRetrofitClient.buildService(1).saveClient(fields).enqueue(registerCallback)
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
            ApiCallback<SingleClientResponse>(from_flag = "from_merchant_login", listener = this)
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

    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        TODO("Not yet implemented")
    }

    override fun onSuccess(result: Any, from: String) {
        TODO("Not yet implemented")
    }

    override fun onFailure(error: String) {
        TODO("Not yet implemented")
    }
}