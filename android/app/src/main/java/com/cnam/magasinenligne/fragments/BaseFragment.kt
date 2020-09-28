package com.cnam.magasinenligne.fragments

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

abstract class BaseFragment : Fragment(), FragmentManager.OnBackStackChangedListener {
    abstract fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?)
}