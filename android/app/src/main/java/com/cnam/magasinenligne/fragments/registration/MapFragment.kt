package com.cnam.magasinenligne.fragments.registration

import android.Manifest
import android.annotation.SuppressLint
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
import com.cnam.magasinenligne.activities.RegistrationActivity
import com.cnam.magasinenligne.fragments.BaseFragment
import com.cnam.magasinenligne.utils.logDebug
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_map.*


class MapFragment : BaseFragment() {
    private lateinit var googleMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var myLocation: LatLng
    private var latitude = 33.8938
    private var longitude = 35.5018
    private lateinit var myMarker: Marker
    private var locationClicked = false
    private lateinit var myActivity: RegistrationActivity

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
        myActivity = activity!! as RegistrationActivity
        return inflater.inflate(R.layout.fragment_map, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback(onBackPressedCallback)

        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = MyLocationListener()

        mv_location?.onCreate(savedInstanceState)
        initMap()
        listeners()
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
            logDebug("Entering async")

            googleMap.setOnMapClickListener {
                myMarker.remove()
                myLocation = LatLng(it.latitude, it.longitude)
                myMarker = googleMap.addMarker(
                    MarkerOptions()
                        .position(myLocation)
                        .title(getString(R.string.my_current_location))
                        .icon(
                            BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        )
                )
            }
            googleMap.setOnMyLocationButtonClickListener {
                try {
                    myLocation = LatLng(
                        googleMap.myLocation.latitude,
                        googleMap.myLocation.longitude
                    )
                    myMarker.remove()
                    myMarker = googleMap.addMarker(
                        MarkerOptions()
                            .position(myLocation)
                            .title(getString(R.string.my_current_location))
                            .icon(
                                BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                            )
                    )
                    val cameraPosition =
                        CameraPosition.Builder().target(myLocation).zoom(11f).build()
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                } catch (e: Exception) {
                    logDebug("Exception error is => ${e.message}")
                }
                false
            }
            getMyLocation()
        }


    }

    private fun listeners() {
        bt_get_location.setOnClickListener {
            if (!locationClicked) {
                locationClicked = true
                myActivity.location = myLocation
                myActivity.supportFragmentManager.popBackStack()
            }
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

    private fun getMyLocation() {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            myLocation = LatLng(latitude, longitude)
            //getLastLocation()
            if (myActivity.isLocationInitialized()) {
                myLocation = myActivity.location
            }
            // For zooming automatically to the location of the marker
            try {
                val cameraPosition = CameraPosition.Builder().target(myLocation).zoom(11f).build()
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                if (this@MapFragment::myMarker.isInitialized) {
                    myMarker.remove()
                }
                myMarker = googleMap.addMarker(
                    MarkerOptions()
                        .position(myLocation)
                        .title(getString(R.string.my_current_location))
                        .icon(
                            BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            registerLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
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

            if (myActivity.isLocationInitialized()) {
                myLocation = myActivity.location
            }

            // For zooming automatically to the location of the marker
            try {
                val cameraPosition =
                    CameraPosition.Builder().target(myLocation).zoom(11f).build()
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                if (this@MapFragment::myMarker.isInitialized) {
                    myMarker.remove()
                }
                myMarker = googleMap.addMarker(
                    MarkerOptions()
                        .position(myLocation)
                        .title(getString(R.string.my_current_location))
                        .icon(
                            BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}

        override fun onProviderEnabled(s: String) {}

        override fun onProviderDisabled(s: String) {}
    }
}