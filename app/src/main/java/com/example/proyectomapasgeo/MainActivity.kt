package com.example.proyectomapasgeo

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.graphics.Color
import android.view.View
import android.widget.TextView
import android.widget.Toast
import org.osmdroid.views.overlay.Polyline
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private var myLocationOverlay: MyLocationNewOverlay? = null
    private var userMarker: Marker? = null // Almacena el marcador del usuario
    private var currentRoute: Polyline? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissionsIfNecessary()
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))

        mapView = findViewById(R.id.map_view)

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)

        val mapController = mapView.controller
        mapController.setZoom(15.0)


        /*
        Establecer una ubicación inicial del mapa
        val startPoint = GeoPoint(20.138733, -101.150324) // Ubicacion del ITSUR
        mapController.setCenter(startPoint)

        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        myLocationOverlay.enableMyLocation()
        mapView.overlays.add(myLocationOverlay)
         */

        /*
        //Agregar la capa de ubiccion del usuario
        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        mapView.overlays.add(myLocationOverlay)

         */

        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        myLocationOverlay?.enableMyLocation()
        myLocationOverlay?.enableFollowLocation()
        mapView.overlays.add(myLocationOverlay)


        // Configurar el botón flotante para centrar el mapa en la ubicación del usuario
        val centerMapFab: FloatingActionButton = findViewById(R.id.center_map_fab)
        centerMapFab.setOnClickListener {
            val currentLocation = myLocationOverlay!!.myLocation
            if (currentLocation != null) {
                mapController.animateTo(currentLocation)
            }
        }
        /*
        // Agregar MapEventsOverlay para manejar eventos de toque en el mapa
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(geoPoint: GeoPoint): Boolean {
                addUserMarker(geoPoint.latitude, geoPoint.longitude)
                return true
            }

            override fun longPressHelper(geoPoint: GeoPoint): Boolean {
                return false
            }
        }

         */

        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(geoPoint: GeoPoint): Boolean {
                addUserMarker(geoPoint.latitude, geoPoint.longitude)
                drawRouteToMarker(geoPoint)
                showMarkerInfo(geoPoint)
                return true
            }

            override fun longPressHelper(geoPoint: GeoPoint): Boolean {
                return false
            }
        }

        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        mapView.overlays.add(mapEventsOverlay)

    }
    private fun showMarkerInfo(geoPoint: GeoPoint) {
        val infoTextView = findViewById<TextView>(R.id.infoTextView)
        val infoText = "Latitud: ${geoPoint.latitude}, Longitud: ${geoPoint.longitude}"
        infoTextView.text = infoText
        infoTextView.visibility = View.VISIBLE
    }


    private fun addMarker(latitude: Double, longitude: Double, label: String? = null, icon: Drawable? = null) {
        // (Código existente para crear y agregar un marcador)
    }

    private fun addUserMarker(latitude: Double, longitude: Double) {
        userMarker?.let {
            mapView.overlays.remove(it)
        }

        userMarker = Marker(mapView).apply {
            position = GeoPoint(latitude, longitude)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = resources.getDrawable(R.drawable.marker_icon) // Reemplaza 'marker_icon' con el nombre de tu recurso de imagen personalizado
        }

        mapView.overlays.add(userMarker!!)
        mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    private fun createOpenRouteServiceApi(): OpenRouteServiceApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(OpenRouteServiceApi::class.java)
    }

    private fun drawRouteToMarker(geoPoint: GeoPoint) {
        val currentLocation = myLocationOverlay?.myLocation ?: return
        val start = "${currentLocation.longitude},${currentLocation.latitude}"
        val end = "${geoPoint.longitude},${geoPoint.latitude}"

        val openRouteServiceApi = createOpenRouteServiceApi()
        openRouteServiceApi.getRoute(start, end).enqueue(object : Callback<RouteResponse> {
            override fun onResponse(call: Call<RouteResponse>, response: Response<RouteResponse>) {
                if (response.isSuccessful) {
                    val routeResponse = response.body() // Aquí se asigna la respuesta a la variable routeResponse
                    if (routeResponse != null) {
                        // Elimina la ruta anterior del mapa
                        currentRoute?.let {
                            mapView.overlays.remove(it)
                        }

                        // Dibuja la nueva ruta en el mapa
                        val coordinates = routeResponse.features[0].geometry.coordinates
                        val routePoints = coordinates.map { GeoPoint(it[1], it[0]) }

                        currentRoute = Polyline(mapView).apply {
                            color = Color.CYAN
                            width = 10f
                            setPoints(routePoints)
                        }

                        mapView.overlays.add(currentRoute!!)
                        mapView.invalidate()
                    } else {
                        Toast.makeText(this@MainActivity, "No se pudo obtener la ruta", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error en la respuesta de la API", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error al obtener la ruta", Toast.LENGTH_SHORT).show()
            }
        })
    }




    private fun requestPermissionsIfNecessary() {
        if (Build.VERSION.SDK_INT >= 23) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE
            )

            val missingPermissions: MutableList<String> = ArrayList()
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    missingPermissions.add(permission)
                }
            }

            if (missingPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), REQUEST_PERMISSIONS_REQUEST_CODE)
            }
        }
    }

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    }


}
