package com.centosquare.devatease.goappdriver.location

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.centosquare.devatease.goappdriver.R
import com.centosquare.devatease.goappdriver.activities.MainActivity
import com.centosquare.devatease.goappdriver.utils.SharedPrefsManager
import com.google.android.gms.location.*

class LocationActivity : AppCompatActivity() {

    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var isGPS: Boolean = false
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        progressBar = findViewById(R.id.progress_bar)
        // for getting current location
        turnOnGps()

        //for getting fused location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this@LocationActivity)
        mLocationRequest = LocationRequest()
        mLocationRequest.setInterval(1000) // two minute interval
        mLocationRequest.setFastestInterval(1000)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                //LocationLatLng Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())

            } else {
                //Request LocationLatLng Permission
                checkLocationPermission()
            }
        } else {

            // add location call backs for gps
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())


        }


    }


    fun turnOnGps() {
        GpsUtils(this@LocationActivity).turnGPSOn(object :GpsUtils.onGpsListener{
            override fun gpsStatus(isGPSEnable: Boolean) {

                isGPS = isGPSEnable
            }


        })



    }

    override fun onPause() {
        super.onPause()
        //stop location updates when Activity is no longer active
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }
    internal var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                //The last location in the list is the newest
                val location = locationList[locationList.size - 1]
                // store the deal in shared pref

                if (isGPS) {
                    if (location.latitude != 0.0 && location.longitude != 0.0) {

                        progressBar.setVisibility(View.GONE)

                        val intentLocation = Intent(applicationContext, MainActivity::class.java)
                        val sharedManager = SharedPrefsManager(applicationContext)
                        sharedManager.setFloatValues("latitude", location.latitude.toFloat())
                        sharedManager.setFloatValues("longitude", location.longitude.toFloat())
                        startActivity(intentLocation)
                        finish()

                        Log.d("MapsActivity", "LocationLatLng: " + location.latitude + " " + location.longitude)
                    }
                }


            }
        }
    }
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                // Show an explanation to the user asynchronously -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("LocationLatLng Permission Must Needed")
                    .setMessage("This app needs the LocationLatLng permission, please accept in order to see the restaurants")
                    .setPositiveButton("OK") { dialogInterface, i ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@LocationActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .setCancelable(false)
                    .create()
                    .show()


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // TODO: Consider calling
                                //    Activity#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for Activity#requestPermissions for more details.
                                return
                            }
                        }
                        mFusedLocationClient.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                        )


                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    showSettingsDialog()
                    //   Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();

                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 100) {

                // user turn on the gps location
                isGPS = true// flag maintain before get location
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        //LocationLatLng Permission already granted
                        mFusedLocationClient.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                        )

                    } else {
                        //Request LocationLatLng Permission
                        checkLocationPermission()
                    }
                } else {

                    // request location callbacks for gps
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
                }


            }
        } else if (resultCode == Activity.RESULT_CANCELED) {

            // user deny to gps turn on it open again gps dialog
            turnOnGps()
        }
    }

    // navigating settings app
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", applicationContext.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
        finish()
    }

    /**
     * Showing Alert Dialog with Settings option in case of deny any permission
     */
    private fun showSettingsDialog() {

        val builder = android.app.AlertDialog.Builder(this@LocationActivity)
        builder.setTitle(getString(R.string.message_need_permission))
        builder.setMessage(getString(R.string.message_permission))
        builder.setPositiveButton(getString(R.string.title_go_to_setting)) { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.show()
        Toast.makeText(this@LocationActivity, "permission denied", Toast.LENGTH_SHORT).show()

    }
}
