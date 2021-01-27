package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import timber.log.Timber

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    override val viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private var poiMarker: Marker? = null
    private var pointOfInterest: PointOfInterest? = null
    private var requestingLocationUpdates = false

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionGranted ->
        Timber.d(permissionGranted.toString())

        if (permissionGranted) {
            enableMyLocation()
        } else {
            viewModel.showSnackBar.value = getString(R.string.permission_denied_explanation)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_select_location,
            container,
            false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.buttonSaveLocation.isEnabled = false

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.googleMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.buttonSaveLocation.setOnClickListener {
            onLocationSelected()
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setPoiClick(map)
        enableMyLocation()
        startMapGestureListener()
        setMapStyle(map)

        map.setOnMyLocationButtonClickListener {
            requestingLocationUpdates = true
            false
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            poiMarker?.remove()

            poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )

            poiMarker?.showInfoWindow()
            pointOfInterest = poi
            binding.buttonSaveLocation.isEnabled = poiMarker != null
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val fusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(requireContext())

        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (requestingLocationUpdates == false) return

                for (location in locationResult.locations) {
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            16f
                        )
                    )
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun startMapGestureListener() {
        if (!::map.isInitialized) return

        map.setOnCameraMoveStartedListener { reason ->
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                requestingLocationUpdates = false
            }
        }
    }

    private fun onLocationSelected() {
        pointOfInterest?.let { pointOfInterest ->
            viewModel.reminderSelectedLocationStr.value = pointOfInterest.name
            viewModel.selectedPOI.value = pointOfInterest
            viewModel.latitude.value = pointOfInterest.latLng.latitude
            viewModel.longitude.value = pointOfInterest.latLng.longitude
            viewModel.navigationCommand.postValue(NavigationCommand.Back)
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )

            if (!success) {
                Timber.e("Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Timber.e("Can't find style. Error: $e")
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (!::map.isInitialized) return

        if (getPermissionStates().containsValue(false)) {
            requestPermissions()
        } else {
            map.isMyLocationEnabled = true
            startLocationUpdates()
            requestingLocationUpdates = true
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun getPermissionStates(): MutableMap<String, Boolean> {
        val permissionStates = mutableMapOf<String, Boolean>()

        permissionStates[Manifest.permission.ACCESS_FINE_LOCATION] =
            PackageManager.PERMISSION_GRANTED == checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )

        permissionStates[Manifest.permission.ACCESS_BACKGROUND_LOCATION] = when {
            runningQOrLater -> {
                PackageManager.PERMISSION_GRANTED == checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
            else -> true
        }

        return permissionStates
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun requestPermissions() {
        val permissionStates = getPermissionStates()

        if (permissionStates[Manifest.permission.ACCESS_FINE_LOCATION] == false) {
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else if (permissionStates[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == false) {
            requestPermission.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    companion object {
        private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

}
