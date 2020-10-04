package com.cnam.magasinenligne.fragments.registration

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.LandingActivity
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.isUserLoggedIn
import com.cnam.magasinenligne.userType
import com.cnam.magasinenligne.utils.createDialog
import com.cnam.magasinenligne.utils.putPreference
import kotlinx.android.synthetic.main.fragment_admin_registration.*

class AdminRegistrationFragment : BaseFragment() {

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
        return inflater.inflate(R.layout.fragment_admin_registration, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        listeners()
    }

    private fun listeners() {
        pv_secret.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 5) {
                    val code = s.toString()
                    if (code == "30184") { // this is an admin
                        putPreference(isUserLoggedIn, true)
                        putPreference(userType, "admin")
                        startActivity(Intent(activity!!, LandingActivity::class.java))
                        activity!!.finish()
                    } else {
                        activity!!.createDialog(
                            "Error",
                            "The pin you entered is incorrect, please make sure you insert the correct pin to login as an admin"
                        )
                            .setPositiveButton("Ok") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }

                }
            }
        })
    }

    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        TODO("Not yet implemented")
    }
}