package com.motuber.motuber_driver

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View

import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000
    private lateinit var map: GoogleMap

    var flagMap = false
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10
    private var mDatabase: DatabaseReference? = null
    private lateinit var auth: FirebaseAuth

    lateinit var latorigen: String
    lateinit var lonorigen: String
    lateinit var txtLat: TextView
    lateinit var txtLong: TextView
    lateinit var txtTime: TextView
    lateinit var txtOrigen: TextView
    lateinit var txtDestino: TextView

    lateinit var buttTomar: Button
    lateinit var buttLLegue: Button
    lateinit var txtuser: TextView

    lateinit var serviciosView: LinearLayout
    lateinit var actualservicioView: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mLocationRequest = LocationRequest()

        createMapFragment()
        serviciosView = findViewById(R.id.servicios_view)
        actualservicioView = findViewById(R.id.encamino_view)
        txtDestino = findViewById(R.id.text_destino)
        txtOrigen = findViewById(R.id.text_origen)

        mDatabase = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        var firebaseDatabase: FirebaseDatabase
        firebaseDatabase = FirebaseDatabase.getInstance();


        buttTomar = findViewById(R.id.butt_tomar_servicio)
        buttLLegue = findViewById(R.id.butt_tomar_servicio1)

        buttLLegue.setOnClickListener {

            if(buttLLegue.text.toString().equals("finalizar viaje")) {

                mDatabase?.child("rides")?.child("Qcu9W0gazyLY2SIrICjUbcNebbA2")?.child("status")
                    ?.setValue("finished")
                buttLLegue.setText("finalizando...")
            }

            if(buttLLegue.text.toString().equals("iniciar viaje")) {

                mDatabase?.child("rides")?.child("Qcu9W0gazyLY2SIrICjUbcNebbA2")?.child("status")
                    ?.setValue("inprogress")
                buttLLegue.setText("finalizar viaje")
            }

            if(buttLLegue.text.toString().equals("he llegado!")) {
                mDatabase?.child("rides")?.child("Qcu9W0gazyLY2SIrICjUbcNebbA2")?.child("status")
                    ?.setValue("initialized")
                buttLLegue.setText("iniciar viaje")
            }

        }

        buttTomar.setOnClickListener {

            val favoritePlace = LatLng(latorigen.toDouble(), lonorigen.toDouble())
            // You can now create a LatLng Object for use with maps
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(favoritePlace, 18f),
                200,
                null
            )
            mDatabase?.child("rides")?.child("Qcu9W0gazyLY2SIrICjUbcNebbA2")?.child("status")?.setValue("reserved")
            mDatabase?.child("rides")?.child("Qcu9W0gazyLY2SIrICjUbcNebbA2")?.child("driver")?.setValue(auth.currentUser.email)



        }

        val reflatitude = firebaseDatabase.getReference("rides")
        reflatitude.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.child("Qcu9W0gazyLY2SIrICjUbcNebbA2")
                        .child("status").value.toString().equals("created")
                ) {
                    serviciosView.visibility = View.VISIBLE
                    latorigen = dataSnapshot.child("Qcu9W0gazyLY2SIrICjUbcNebbA2")
                        .child("origenlat").value.toString()
                    lonorigen = dataSnapshot.child("Qcu9W0gazyLY2SIrICjUbcNebbA2")
                        .child("origenlon").value.toString()
                    txtDestino.setText(
                        dataSnapshot.child("Qcu9W0gazyLY2SIrICjUbcNebbA2")
                            .child("destinoname").value.toString()
                    )
                    txtOrigen.setText(
                        dataSnapshot.child("Qcu9W0gazyLY2SIrICjUbcNebbA2")
                            .child("origenlat").value.toString() + "," + dataSnapshot.child("Qcu9W0gazyLY2SIrICjUbcNebbA2")
                            .child("origenlon").value.toString()
                    )

                } else {
                    serviciosView.visibility = View.GONE
                }

                if (dataSnapshot.child("Qcu9W0gazyLY2SIrICjUbcNebbA2")
                        .child("status").value.toString().equals("reserved")
                ) {
                    serviciosView.visibility = View.GONE
                    actualservicioView.visibility = View.VISIBLE
                }


            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(null, "Failed to read value.", error.toException())
            }
        })


        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }



        if (checkPermissionForLocation(this)) {
            startLocationUpdates()

        } else {
            checkPermissionForLocation(this)
        }


    }

    private fun createMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this@HomeActivity)
    }

    private fun buildAlertMessageNoGps() {

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    , 11
                )
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
                finish()
            }
        val alert: AlertDialog = builder.create()
        alert.show()


    }


    protected fun startLocationUpdates() {

        // Create the location request to start receiving updates

        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {


            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // do work here
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    fun onLocationChanged(location: Location) {
        // New location has now been determined

        mLastLocation = location

        if (flagMap == false) {
            println("LATITUDE : " + mLastLocation.latitude)
            println("LONGITUDE : " + mLastLocation.longitude)
            val favoritePlace = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            // You can now create a LatLng Object for use with maps
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(favoritePlace, 18f),
                200,
                null
            )
            flagMap = true
        }
    }


    private fun stoplocationUpdates() {
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()

            } else {
                Toast.makeText(this@HomeActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // Show the permission request
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        map = p0!!
    }

}
