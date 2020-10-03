package com.cnam.magasinenligne.fragments.registration

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.utils.addTransaction
import com.cnam.magasinenligne.utils.createDialog
import com.cnam.magasinenligne.utils.verifyPermissions
import kotlinx.android.synthetic.main.fragment_client_registration.*

class ClientRegistrationFragment : BaseFragment() {
    private var mapClicked = false
    private val locationRequest = 1000
    private val locationPermission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

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
        return inflater.inflate(R.layout.fragment_client_registration, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        listeners()
    }

    private fun listeners() {
        iv_choose_from_map.setOnClickListener {
            if (!mapClicked) {
                mapClicked = true
                val granted = verifyPermissions(activity!!, locationRequest, locationPermission)
                if (granted) {
                    handlePermissionResult(1)
                } else {
                    handlePermissionResult(0)
                }
            }

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
            activity!!.supportFragmentManager.addTransaction(
                MapFragment(),
                MapFragment::class.java.simpleName,
                R.anim.enter_from_right,
                R.anim.exit_to_right
            )
        } else {
            activity!!.createDialog(
                "Alert",
                "Location permission is necessary for better service"
            )
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    mapClicked = false
                    dialog.dismiss()
                }
                .show()
        }

    }

    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        TODO("Not yet implemented")
    }
}