package com.cnam.magasinenligne.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.cnam.magasinenligne.fragments.registration.AdminRegistrationFragment
import com.cnam.magasinenligne.fragments.registration.ClientRegistrationFragment
import com.cnam.magasinenligne.fragments.registration.MerchantRegistrationFragment

class RegistrationPagerAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ClientRegistrationFragment()
            1 -> MerchantRegistrationFragment()
            else -> AdminRegistrationFragment()
        }
    }

    override fun getCount(): Int = 3

}