package com.example.proyectomapasgeo

import android.Manifest
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


class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private var userMarker: Marker? = null // Almacena el marcador del usuario

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

        //Agregar la capa de ubiccion del usuario
        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        mapView.overlays.add(myLocationOverlay)

        // Configurar el botón flotante para centrar el mapa en la ubicación del usuario
        val centerMapFab: FloatingActionButton = findViewById(R.id.center_map_fab)
        centerMapFab.setOnClickListener {
            val currentLocation = myLocationOverlay.myLocation
            if (currentLocation != null) {
                mapController.animateTo(currentLocation)
            }
        }
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

        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        mapView.overlays.add(mapEventsOverlay)
    }
    // (Código existente para onCreate, onResume, onPause, etc.)

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
