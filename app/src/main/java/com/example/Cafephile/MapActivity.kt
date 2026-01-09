package com.example.f053.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.f053.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.net.URL

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: GeoPoint? = null
    private val coffeeShopMarkers = mutableListOf<Marker>()
    private val coffeeShops = mutableListOf<CoffeeShop>()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                updateUserLocation(location)
            }
        }
    }

    private lateinit var fabSearch: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
    private lateinit var fabMyLocation: com.google.android.material.floatingactionbutton.FloatingActionButton
    private lateinit var tvShopCount: android.widget.TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize OSM configuration
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        setContentView(R.layout.activity_map)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupMap()
        setupUI()
        checkLocationPermission()
    }

    private fun setupUI() {
        fabSearch = findViewById(R.id.fabSearch)
        fabMyLocation = findViewById(R.id.fabMyLocation)
        tvShopCount = findViewById(R.id.tvShopCount)

        fabSearch.setOnClickListener {
            currentLocation?.let { location ->
                searchNearbyCoffeeShops(location, 2000)
            } ?: run {
                Toast.makeText(this, getString(R.string.location_not_available), Toast.LENGTH_SHORT).show()
            }
        }

        fabMyLocation.setOnClickListener {
            currentLocation?.let { location ->
                mapView.controller.animateTo(location)
                mapView.controller.setZoom(16.0)
            } ?: run {
                Toast.makeText(this, getString(R.string.location_not_available), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMap() {
        mapView = findViewById(R.id.mapView)

        // Configure map
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.setBuiltInZoomControls(false)
        mapView.minZoomLevel = 10.0
        mapView.maxZoomLevel = 20.0

        // Set initial position (Fes, Morocco)
        val startPoint = GeoPoint(34.0331, -5.0003)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(startPoint)

        // Add my location overlay
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        mapView.overlays.add(myLocationOverlay)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Toast.makeText(this, getString(R.string.location_permission_required), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        // Get last known location first
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                updateUserLocation(it)
            }
        }

        // Request continuous location updates
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000 // Update every 10 seconds
        ).setMinUpdateIntervalMillis(5000).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )
    }

    private fun updateUserLocation(location: Location) {
        val newLocation = GeoPoint(location.latitude, location.longitude)
        currentLocation = newLocation

        Log.d("MapActivity", "Location updated: ${location.latitude}, ${location.longitude}")

        // Move map to user location
        mapView.controller.animateTo(newLocation)

        // Update the location overlay
        myLocationOverlay.onLocationChanged(location, null)

        // Search for nearby coffee shops
        searchNearbyCoffeeShops(newLocation)
    }

    private fun searchNearbyCoffeeShops(location: GeoPoint, radiusMeters: Int = 2000) {
        tvShopCount.text = "Searching..."

        lifecycleScope.launch {
            try {
                val shops = findCoffeeShopsWithOverpass(
                    location.latitude,
                    location.longitude,
                    radiusMeters
                )

                if (shops.isNotEmpty()) {
                    coffeeShops.clear()
                    coffeeShops.addAll(shops)
                    updateMapMarkers()
                    tvShopCount.text = "Found ${shops.size} coffee shops"
                } else {
                    tvShopCount.text = "No coffee shops found"
                }
            } catch (e: Exception) {
                Log.e("MapActivity", "Error searching coffee shops", e)
                Toast.makeText(
                    this@MapActivity,
                    "Error searching: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private suspend fun findCoffeeShopsWithOverpass(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int
    ): List<CoffeeShop> = withContext(Dispatchers.IO) {

        val query = """
            [out:json][timeout:25];
            (
              node["amenity"="cafe"](around:$radiusMeters,$latitude,$longitude);
              way["amenity"="cafe"](around:$radiusMeters,$latitude,$longitude);
              node["amenity"="restaurant"]["cuisine"="coffee_shop"](around:$radiusMeters,$latitude,$longitude);
            );
            out body;
            >;
            out skel qt;
        """.trimIndent()

        val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
        val url = "https://overpass-api.de/api/interpreter?data=$encodedQuery"

        val response = URL(url).readText()
        val jsonObject = JSONObject(response)
        val elements = jsonObject.getJSONArray("elements")

        val shops = mutableListOf<CoffeeShop>()

        for (i in 0 until elements.length()) {
            val element = elements.getJSONObject(i)

            // Skip way elements without center
            if (element.getString("type") == "way" && !element.has("center")) {
                continue
            }

            val tags = element.optJSONObject("tags") ?: continue

            // Get coordinates
            val lat: Double
            val lon: Double

            if (element.getString("type") == "way" && element.has("center")) {
                val center = element.getJSONObject("center")
                lat = center.getDouble("lat")
                lon = center.getDouble("lon")
            } else {
                lat = element.optDouble("lat", 0.0)
                lon = element.optDouble("lon", 0.0)
            }

            if (lat == 0.0 || lon == 0.0) continue

            val name = tags.optString("name", "Coffee Shop")
            val address = tags.optString("addr:street", "")

            // Calculate distance from user location
            val distance = calculateDistance(latitude, longitude, lat, lon)

            shops.add(
                CoffeeShop(
                    name = name,
                    lat = lat,
                    lng = lon,
                    address = address,
                    distanceMeters = distance
                )
            )
        }

        // Sort by distance
        shops.sortedBy { it.distanceMeters }
    }

    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    private fun updateMapMarkers() {
        // Remove old markers
        coffeeShopMarkers.forEach { mapView.overlays.remove(it) }
        coffeeShopMarkers.clear()

        // Add new markers
        coffeeShops.forEach { shop ->
            val marker = Marker(mapView)
            marker.position = GeoPoint(shop.lat, shop.lng)
            marker.title = shop.name

            // Format distance
            val distanceText = if (shop.distanceMeters < 1000) {
                "${shop.distanceMeters.toInt()} m"
            } else {
                String.format("%.1f km", shop.distanceMeters / 1000)
            }

            marker.snippet = "$distanceText${if (shop.address.isNotEmpty()) " • ${shop.address}" else ""}"
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

            // Set marker click listener
            marker.setOnMarkerClickListener { clickedMarker, _ ->
                clickedMarker.showInfoWindow()
                mapView.controller.animateTo(clickedMarker.position)
                true
            }

            mapView.overlays.add(marker)
            coffeeShopMarkers.add(marker)
        }

        mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        myLocationOverlay.disableMyLocation()
    }

    companion object {
        private const val LOCATION_PERMISSION_CODE = 1001
    }
}

data class CoffeeShop(
    val name: String,
    val lat: Double,
    val lng: Double,
    val address: String = "",
    val distanceMeters: Float = 0f
)
