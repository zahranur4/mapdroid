package com.zahranur.mapdroid

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zahranur.mapdroid.databinding.ActivityMainBinding
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mapLibreMap: MapLibreMap? = null
    private var activeMarker: Marker? = null

    // Default style and available styles
    private val mapStyles = arrayOf(
        "OpenFreeMap Liberty" to "https://tiles.openfreemap.org/styles/liberty",
        "OpenFreeMap Positron (Light)" to "https://tiles.openfreemap.org/styles/positron",
        "OpenFreeMap Bright" to "https://tiles.openfreemap.org/styles/bright",
        "MapLibre Demotiles" to "https://demotiles.maplibre.org/style.json"
    )
    private var selectedStyleIndex = 0

    // Default coordinates: Monumen Nasional (Monas), Jakarta
    private val defaultCenter = LatLng(-6.175392, 106.827153)

    // Permission request launcher for location
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            enableLocationComponent()
        } else {
            Toast.makeText(
                this,
                "Izin lokasi tidak diberikan. Menampilkan titik pusat default.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize MapLibre before layout inflation
        MapLibre.getInstance(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize MapView
        binding.mapView.onCreate(savedInstanceState)

        setupMap()
        setupControls()
    }

    private fun setupMap() {
        binding.mapView.getMapAsync { map ->
            mapLibreMap = map

            // Load initial map style
            loadMapStyle(mapStyles[selectedStyleIndex].second)

            // Setup camera listener to display live coordinates & zoom
            map.addOnCameraMoveListener {
                val position = map.cameraPosition
                val lat = String.format("%.4f", position.target.latitude)
                val lng = String.format("%.4f", position.target.longitude)
                val zoom = String.format("%.1f", position.zoom)
                binding.tvCoordinates.text = "Lat: $lat | Lng: $lng | Zoom: $zoom"
            }

            // Long-click to drop a custom pin marker
            map.addOnMapLongClickListener { point ->
                activeMarker?.let { map.removeMarker(it) }
                activeMarker = map.addMarker(
                    MarkerOptions()
                        .position(point)
                        .title("Pin Terpilih")
                        .snippet(
                            String.format("Lat: %.5f, Lng: %.5f", point.latitude, point.longitude)
                        )
                )
                Toast.makeText(this, "Marker ditambahkan di titik pilihan!", Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    private fun loadMapStyle(styleUrl: String) {
        mapLibreMap?.setStyle(styleUrl) { style ->
            // Move camera to initial position
            mapLibreMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(defaultCenter, 12.0)
            )

            // Add default marker on Monas Jakarta
            activeMarker?.let { mapLibreMap?.removeMarker(it) }
            activeMarker = mapLibreMap?.addMarker(
                MarkerOptions()
                    .position(defaultCenter)
                    .title("Monas Jakarta")
                    .snippet("Pusat Kota Jakarta, Indonesia")
            )

            // Check location permission
            checkAndEnableLocation(style)
        }
    }

    private fun setupControls() {
        // Zoom in button
        binding.btnZoomIn.setOnClickListener {
            mapLibreMap?.animateCamera(CameraUpdateFactory.zoomIn())
        }

        // Zoom out button
        binding.btnZoomOut.setOnClickListener {
            mapLibreMap?.animateCamera(CameraUpdateFactory.zoomOut())
        }

        // Compass / Reset Bearing to North
        binding.btnCompass.setOnClickListener {
            mapLibreMap?.animateCamera(CameraUpdateFactory.bearingTo(0.0))
            Toast.makeText(this, "Arah peta diatur ke Utara", Toast.LENGTH_SHORT).show()
        }

        // My Location button
        binding.btnLocation.setOnClickListener {
            checkAndEnableLocation(mapLibreMap?.style)
        }

        // Layer / Style Switcher Dialog
        binding.btnLayers.setOnClickListener {
            showStyleSelectorDialog()
        }
    }

    private fun showStyleSelectorDialog() {
        val styleNames = mapStyles.map { it.first }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Pilih Gaya Peta (Map Style)")
            .setSingleChoiceItems(styleNames, selectedStyleIndex) { dialog, which ->
                selectedStyleIndex = which
                val chosenStyleUrl = mapStyles[which].second
                loadMapStyle(chosenStyleUrl)
                Toast.makeText(this, "Memuat gaya: ${mapStyles[which].first}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun checkAndEnableLocation(style: Style?) {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED ||
            coarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {
            enableLocationComponent(style)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(style: Style? = mapLibreMap?.style) {
        val map = mapLibreMap ?: return
        val currentStyle = style ?: map.style ?: return

        try {
            val locationComponent = map.locationComponent
            val activationOptions = LocationComponentActivationOptions.builder(this, currentStyle)
                .useDefaultLocationEngine(true)
                .build()

            locationComponent.activateLocationComponent(activationOptions)
            locationComponent.isLocationComponentEnabled = true
            locationComponent.cameraMode = CameraMode.TRACKING
            locationComponent.renderMode = RenderMode.COMPASS

            val lastLocation = locationComponent.lastKnownLocation
            if (lastLocation != null) {
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(lastLocation.latitude, lastLocation.longitude),
                        14.0
                    )
                )
            } else {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultCenter, 13.0))
            }
        } catch (e: Exception) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultCenter, 13.0))
        }
    }

    // --- Lifecycle Callbacks forwarded to MapView ---

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}
