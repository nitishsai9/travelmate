package com.example.komma.travelmate

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest
import kotlin.reflect.KMutableProperty0

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var locationManager: LocationManager? = null
    var locationListener: LocationListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(myListner)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onProviderDisabled(provider: String?) {
            }

            override fun onProviderEnabled(provider: String?) {

            }

            override fun onLocationChanged(p0: Location?) {
                if(p0!=null)
                    {
                        var userLocation = LatLng(p0!!.latitude,p0!!.longitude)
                        mMap.addMarker(MarkerOptions().position(userLocation).title("your location"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,17f))
                    }

            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }
        }
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }
        else
        {
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)
            val intent = intent
            val info  = intent.getStringExtra("info")
            if(info.equals("new"))
            {
              mMap.clear()
                val lastlocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                var lastuserLocation = LatLng(lastlocation.latitude,lastlocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastuserLocation,17f))
            }
            else
            {
               mMap.clear()
                val latitude = intent.getDoubleExtra("latitude",0.0)
                val longitude = intent.getDoubleExtra("longitude",0.0)
                val name = intent.getStringExtra("nsme")
                val location = LatLng(latitude,longitude)
                mMap.addMarker(MarkerOptions().position(location).title(name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,17f))
            }
        }
    }
    val myListner = object :GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng?) {
           val geocoder = Geocoder(applicationContext, Locale.getDefault())
            var addrress = ""
            try {
                val addressList = geocoder.getFromLocation(p0!!.latitude,p0!!.longitude,1)
                if (addressList!=null && addressList.size>0)
                {
                    if (addressList[0].thoroughfare!=null)
                    {
                        addrress+=addressList[0].thoroughfare
                        if (addressList[0].subThoroughfare!=null)
                        {
                            addrress+=addressList[0].subThoroughfare
                        }

                    }
                }else
                {
                    addrress="new place"
                }
            }catch (e: Exception){e.printStackTrace()}

            mMap.addMarker(MarkerOptions().position(p0!!).title(addrress))
            Toast.makeText(applicationContext,"new Place added",Toast.LENGTH_LONG).show()

            try {
                val latitude =p0.latitude.toString()
                val longitutde=p0.longitude.toString()
                val database =openOrCreateDatabase("place",Context.MODE_PRIVATE,null)
                database.execSQL("CREATE TABLE IF NOT EXISTS places(name VARCHAR,  latitude VARCHAR, longitude VARCHAR) ")
                 val toCompile = "INSERT INTO places (name,latitude,longitude)VALUES(?,?,?)"
                val sqLiteStatement =database.compileStatement(toCompile)
                sqLiteStatement.bindString(1 , addrress )
                sqLiteStatement.bindString(2,latitude)
                sqLiteStatement.bindString(3,longitutde)
                sqLiteStatement.execute()
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }











        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.size>0)
        {
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}

