package com.example.f053.screens

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.f053.LocationPermission
import com.example.f053.R
import com.example.f053.adapters.FamousDrinksAdapter
import com.example.f053.adapters.GalleryAdapter
import com.example.f053.adapters.NearbyShopsAdapter
import com.example.f053.db.CoffeeDatabase
import com.example.f053.models.CategoriesEnum
import com.example.f053.models.CoffeeCategory
import com.example.f053.models.GalleryPhoto
import com.example.f053.models.NearbyShop
import com.example.f053.models.Drink
import com.example.f053.screens.AppNavigator.openProducts
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
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
import pub.devrel.easypermissions.EasyPermissions
import java.net.URL
import java.net.URLEncoder
import java.util.Locale

class ExploreActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private val locationPermission by lazy {
        LocationPermission(this)
    }

    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var famousDrinksRecyclerView: RecyclerView
    private lateinit var galleryRecyclerView: RecyclerView
    private lateinit var nearbyShopsRecyclerView: RecyclerView

    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var famousDrinksAdapter: FamousDrinksAdapter
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var nearbyShopsAdapter: NearbyShopsAdapter

    private lateinit var tvCurrentLocation: TextView
    private lateinit var mapCardView: MaterialCardView
    private lateinit var mapView: MapView
    private lateinit var viewFullMapButton: MaterialButton
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentLocation: GeoPoint? = null
    private var nearbyShops = mutableListOf<NearbyShop>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize OSM configuration
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        setContentView(R.layout.activity_explore)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupToolbar()
        setupRecyclerViews()
        setupViews()
        checkLocation()
    }

    private fun setupViews() {
        tvCurrentLocation = findViewById(R.id.tvCurrentLocation)
        mapCardView = findViewById(R.id.mapCardView)
        mapView = findViewById(R.id.mapView)
        viewFullMapButton = findViewById(R.id.viewFullMapButton)
        val btnSeeAllDrinks: TextView = findViewById(R.id.btnSeeAllDrinks)
        val btnSeeAllGallery: TextView = findViewById(R.id.btnSeeAllGallery)

        setMapSectionVisibility(false)

        viewFullMapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        btnSeeAllDrinks.setOnClickListener {
            openProducts(this, CategoriesEnum.All.name)
        }

        btnSeeAllGallery.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerViews() {
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        val exploreCategories = getCategories()
        categoriesAdapter = CategoriesAdapter(exploreCategories) { category: CoffeeCategory ->
            openProducts(this, category.name)
        }
        categoriesRecyclerView.adapter = categoriesAdapter

        famousDrinksRecyclerView = findViewById(R.id.famousDrinksRecyclerView)
        famousDrinksRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        famousDrinksAdapter = FamousDrinksAdapter(CoffeeDatabase.drinks.take(3)) { drink ->
            navigateToProductDetails(drink.id)
        }
        famousDrinksRecyclerView.adapter = famousDrinksAdapter

        galleryRecyclerView = findViewById(R.id.galleryRecyclerView)
        galleryRecyclerView.layoutManager = GridLayoutManager(this, 3)
        galleryAdapter = GalleryAdapter(getGalleryPhotos()) { photo: GalleryPhoto ->
        }
        galleryRecyclerView.adapter = galleryAdapter

        nearbyShopsRecyclerView = findViewById(R.id.nearbyShopsRecyclerView)
        nearbyShopsRecyclerView.layoutManager = LinearLayoutManager(this)
        nearbyShopsAdapter = NearbyShopsAdapter(getNearbyShops()) { shop: NearbyShop ->
        }
        nearbyShopsRecyclerView.adapter = nearbyShopsAdapter
    }

    private fun navigateToProductDetails(drinkId: Int) {
        val intent = Intent(this, ProductDetailsWrapperActivity::class.java).apply {
            putExtra("drinkId", drinkId)
        }
        startActivity(intent)
    }


    private fun setupMap() {
        // Configure map
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.setBuiltInZoomControls(false)
        mapView.minZoomLevel = 12.0
        mapView.maxZoomLevel = 18.0

        // Add my location overlay with proper configuration
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        myLocationOverlay.isDrawAccuracyEnabled = true

        // Add overlay to map
        mapView.overlays.add(myLocationOverlay)

        // Run location update to show current position
        myLocationOverlay.runOnFirstFix {
            runOnUiThread {
                val myLocation = myLocationOverlay.myLocation
                if (myLocation != null) {
                    mapView.controller.animateTo(myLocation)
                    mapView.controller.setZoom(16.0)
                }
            }
        }
    }

    private fun setMapSectionVisibility(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.tvFindNearCoffees).visibility = visibility
        mapCardView.visibility = visibility
        nearbyShopsRecyclerView.visibility = visibility
        viewFullMapButton.visibility = visibility
    }

    private fun getUserLocation() {
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        currentLocation = GeoPoint(it.latitude, it.longitude)
                        updateLocationUI(it)
                        setupMap()
                        updateMapLocation(currentLocation!!)
                        searchNearbyCoffeeShops(currentLocation!!)
                    } ?: run {
                        tvCurrentLocation.text = getString(R.string.not_Available)
                        setMapSectionVisibility(false)
                    }
                }
                .addOnFailureListener {
                    tvCurrentLocation.text = getString(R.string.not_Available)
                    setMapSectionVisibility(false)
                }
        } catch (e: SecurityException) {
            tvCurrentLocation.text = getString(R.string.not_Available)
            setMapSectionVisibility(false)
        }
    }

    private fun updateLocationUI(location: Location) {
        lifecycleScope.launch {
            val address = getAddressFromLocation(location.latitude, location.longitude)
            tvCurrentLocation.text = address
        }
    }

    private suspend fun getAddressFromLocation(latitude: Double, longitude: Double): String =
        withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(this@ExploreActivity, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    address.locality ?: address.subLocality ?: address.subAdminArea ?: "Current Location"
                } else {
                    "Current Location"
                }
            } catch (e: Exception) {
                Log.e("ExploreActivity", "Error getting address", e)
                "Current Location"
            }
        }

    private fun updateMapLocation(location: GeoPoint) {
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(location)
        setMapSectionVisibility(true)
    }

    private fun searchNearbyCoffeeShops(location: GeoPoint) {
        lifecycleScope.launch {
            try {
                val shops = findCoffeeShopsWithOverpass(
                    location.latitude,
                    location.longitude,
                    1500 // 1.5km radius for preview
                )

                nearbyShops.clear()
                nearbyShops.addAll(shops.take(3))

//                nearbyShopsAdapter = NearbyShopsAdapter(nearbyShops) { shop: NearbyShop ->
//
//                }
                nearbyShopsRecyclerView.adapter = nearbyShopsAdapter

                addMarkersToMap(shops.take(5))

            } catch (e: Exception) {
                Log.e("ExploreActivity", "Error searching coffee shops", e)
            }
        }
    }

    private suspend fun findCoffeeShopsWithOverpass(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int
    ): List<NearbyShop> = withContext(Dispatchers.IO) {

        val query = """
            [out:json][timeout:15];
            (
              node["amenity"="cafe"](around:$radiusMeters,$latitude,$longitude);
              way["amenity"="cafe"](around:$radiusMeters,$latitude,$longitude);
            );
            out body;
            >;
            out skel qt;
        """.trimIndent()

        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "https://overpass-api.de/api/interpreter?data=$encodedQuery"

        try {
            val response = URL(url).readText()
            val jsonObject = JSONObject(response)
            val elements = jsonObject.getJSONArray("elements")

            val shops = mutableListOf<NearbyShop>()

            for (i in 0 until elements.length().coerceAtMost(10)) {
                val element = elements.getJSONObject(i)

                if (element.getString("type") == "way" && !element.has("center")) {
                    continue
                }

                val tags = element.optJSONObject("tags") ?: continue

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
                val distance = calculateDistance(latitude, longitude, lat, lon)

                shops.add(NearbyShop(name, formatDistance(distance), lat, lon))
            }

            shops.sortedBy { extractDistance(it.distance) }
        } catch (e: Exception) {
            Log.e("ExploreActivity", "Error fetching coffee shops", e)
            emptyList()
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    private fun formatDistance(meters: Float): String {
        return if (meters < 1000) {
            "${meters.toInt()} m"
        } else {
            String.format("%.1f km", meters / 1000)
        }
    }

    private fun extractDistance(distanceStr: String): Float {
        return try {
            val number = distanceStr.replace(Regex("[^0-9.]"), "")
            val value = number.toFloat()
            if (distanceStr.contains("km")) value * 1000 else value
        } catch (e: Exception) {
            Float.MAX_VALUE
        }
    }

    private fun addMarkersToMap(shops: List<NearbyShop>) {
        shops.forEach { shop ->
            val marker = Marker(mapView)
            marker.position = GeoPoint(shop.lat, shop.lng)
            marker.title = shop.name
            marker.snippet = shop.distance
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    private fun getCategories(): List<CoffeeCategory> {
        return CoffeeDatabase.categories
    }

    private fun getGalleryPhotos(): List<GalleryPhoto> {
        return CoffeeDatabase.drinks.take(6).mapIndexed { index, drink ->
            GalleryPhoto(
                drink.imageRes,
                "user${index + 1}",
                100 + (index * 50)
            )
        }
    }

    private fun getNearbyShops(): List<NearbyShop> {
        return nearbyShops.ifEmpty {
            listOf(
                NearbyShop("The Daily Grind", "0.2 mi", 40.7580, -73.9855),
                NearbyShop("Brew Haven", "0.3 mi", 40.7589, -73.9851),
                NearbyShop("Coffee Corner", "0.5 mi", 40.7570, -73.9860)
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == LocationPermission.LOCATION_PERMISSION_REQUEST_CODE) {
            getUserLocation()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String?>) {
        tvCurrentLocation.text = getString(R.string.not_Available)
        setMapSectionVisibility(false)
    }

    private fun checkLocation() {
        if (locationPermission.hasPermission()) {
            getUserLocation()
        } else {
            tvCurrentLocation.text = getString(R.string.not_Available)
            setMapSectionVisibility(false)
            locationPermission.requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onResume() {
        super.onResume()
        if (::mapView.isInitialized) {
            mapView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::mapView.isInitialized) {
            mapView.onPause()
        }
    }
}

object AppNavigator {
    fun openProducts(
        context: Context,
        categoryName: String
    ) {
        val exploreCategories = CoffeeDatabase.categories.map { it.name }
        val intent = Intent(context, ProductsActivity::class.java).apply {
            putExtra("selected_category", categoryName)
            putStringArrayListExtra("categories", ArrayList(exploreCategories))
        }
        context.startActivity(intent)
    }
}