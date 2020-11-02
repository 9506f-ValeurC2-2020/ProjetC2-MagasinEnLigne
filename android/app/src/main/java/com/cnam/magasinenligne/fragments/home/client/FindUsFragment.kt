package com.cnam.magasinenligne.fragments.home.client

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.activities.LandingActivity
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.utils.createDialog
import com.cnam.magasinenligne.utils.hide
import com.cnam.magasinenligne.utils.logDebug
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map.*

class FindUsFragment : BaseFragment() {
    private lateinit var googleMap: GoogleMap
    private lateinit var myLocation: LatLng
    private var latitude = 33.8938
    private var longitude = 35.5018
    private lateinit var myLocation1: LatLng
    private var latitude1 = 33.8038
    private var longitude1 = 35.6018
    private lateinit var myLocation2: LatLng
    private var latitude2 = 33.56
    private var longitude2 = 35.57
    private lateinit var myLocation3: LatLng
    private var latitude3 = 33.9
    private var longitude3 = 35.57
    private lateinit var myActivity: LandingActivity
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener


    /**
     * OnBackPressedCallback
     */
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                myActivity.supportFragmentManager.popBackStack()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myActivity = activity!! as LandingActivity
        return inflater.inflate(R.layout.fragment_map, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)
        locationManager = myActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = MyLocationListener()
        mv_location?.onCreate(savedInstanceState)
        initMap()
        listeners()
        myActivity.hideNavigation()

    }

    private fun showStaticLocationAlert() {
        myActivity.createDialog(getString(R.string.alert), getString(R.string.static_alert))
            .setCancelable(true)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun initMap() {
        MapsInitializer.initialize(activity!!.applicationContext)
        mv_location?.getMapAsync { map ->
            googleMap = map
            if (ActivityCompat.checkSelfPermission(
                    myActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    myActivity,
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
            googleMap.isMyLocationEnabled = true

            initStoreLocations()
            logDebug("Entering async")
            googleMap.addMarker(
                MarkerOptions()
                    .position(myLocation)
                    .title("Main Shop")
                    .icon(
                        BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                    )
            )
            googleMap.addMarker(
                MarkerOptions()
                    .position(myLocation1)
                    .title("Shop 1")
                    .icon(
                        BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
            )
            googleMap.addMarker(
                MarkerOptions()
                    .position(myLocation2)
                    .title("Shop 2")
                    .icon(
                        BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    )
            )
            googleMap.addMarker(
                MarkerOptions()
                    .position(myLocation3)
                    .title("Shop 3")
                    .icon(
                        BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                    )
            )
            val cameraPosition =
                CameraPosition.Builder().target(myLocation).zoom(10f).build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            showStaticLocationAlert()
        }


    }

    private fun initStoreLocations() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            myLocation = LatLng(latitude, longitude)
            myLocation1 = LatLng(latitude1, longitude1)
            myLocation2 = LatLng(latitude2, longitude2)
            myLocation3 = LatLng(latitude3, longitude3)
        } else {
            registerLocationUpdates()
        }
    }

    private fun registerLocationUpdates() {
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.powerRequirement = Criteria.POWER_LOW
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false
        val provider = locationManager.getBestProvider(criteria, true) ?: return

        val lastKnownLocation = locationManager.getLastKnownLocation(provider)
        if (lastKnownLocation != null) {
            myLocation = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
            logDebug("My location is => $myLocation")
        }

        // Cant get a hold of provider

        if (ActivityCompat.checkSelfPermission(
                myActivity.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                myActivity.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0f,
            locationListener
        )
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            locationListener
        )

    }

    private fun listeners() {
        bt_get_location.hide()
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

    override fun onDestroyView() {
        myActivity.homePaused = false
        super.onDestroyView()
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
        logDebug("onBack")
    }

    private inner class MyLocationListener : LocationListener {

        override fun onLocationChanged(location: Location) {


            if (ActivityCompat.checkSelfPermission(
                    myActivity.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    myActivity.applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            locationManager.removeUpdates(this)

            longitude = location.longitude
            latitude = location.latitude

            myLocation = LatLng(latitude, longitude)
            myLocation1 = LatLng(latitude1, longitude1)
            myLocation2 = LatLng(latitude2, longitude2)
            myLocation3 = LatLng(latitude3, longitude3)
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}

        override fun onProviderEnabled(s: String) {}

        override fun onProviderDisabled(s: String) {}
    }

}