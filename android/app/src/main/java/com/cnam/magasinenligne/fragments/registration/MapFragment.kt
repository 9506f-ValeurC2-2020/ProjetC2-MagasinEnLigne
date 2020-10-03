package com.cnam.magasinenligne.fragments.registration

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : BaseFragment() {


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
        return inflater.inflate(R.layout.fragment_map, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        mv_location?.onCreate(savedInstanceState)
        initMap()
    }

    private fun initMap() {
        mv_location?.getMapAsync { map ->
            if (ActivityCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            map.isMyLocationEnabled = true
        }

    }

    /**
     * we must add the mapView lifecycle to the fragment's lifecycle methods
     */
    override fun onStart() {
        super.onStart()
        mv_location?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mv_location?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mv_location?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mv_location?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mv_location?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mv_location?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mv_location?.onSaveInstanceState(outState)
    }

    override fun addOnBackPressedCallback(onBackPressedCallback: OnBackPressedCallback?) {
        if (activity == null) return
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    override fun onBackStackChanged() {
        TODO("Not yet implemented")
    }
}