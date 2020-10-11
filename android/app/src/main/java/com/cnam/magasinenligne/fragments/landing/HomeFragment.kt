package com.cnam.magasinenligne.fragments.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.LandingActivity
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.userType
import com.cnam.magasinenligne.utils.findPreference
import com.cnam.magasinenligne.utils.show
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment() {
    private lateinit var myActivity: LandingActivity

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
        myActivity = activity!! as LandingActivity
        return inflater.inflate(R.layout.fragment_home, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        val userType = findPreference(userType, "")
        if (userType.isNotEmpty()) {
            when (userType) {
                "admin" -> {
                    layout_admin.show()
                    myActivity.hideNavigation()
                }
                "client" -> {
                    layout_client.show()
                }
                "merchant" -> {
                    layout_merchant.show()
                    myActivity.hideShop()
                }
            }
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