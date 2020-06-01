package com.centosquare.devatease.goappdriver.fragments


import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide

import com.centosquare.devatease.goappdriver.R
import com.centosquare.devatease.goappdriver.utils.Constants
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class MainMapFragment : Fragment(), OnMapReadyCallback, View.OnClickListener {



    private lateinit var fragmentView: View
    private lateinit var driverAvailableBtn:ImageView
    private lateinit var onOrOffTextView:TextView
    private lateinit var mMap: GoogleMap
    private lateinit var sharedPreferences: SharedPreferences
   private  lateinit var gfDriversAvailable: GeoFire
    private var driversAvailableRef: DatabaseReference? = null
    private lateinit var rideRequestReference: DatabaseReference
    private lateinit var driverRideTypeReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var userId:String
    private var latitude:Double? = null
    private var longitude:Double? = null
    private lateinit var rideRequestListener:ValueEventListener
    private lateinit var driverRideTypeListener:ValueEventListener
    private var isAvailable:Boolean = false
    private lateinit var rideType:String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        fragmentView =  inflater.inflate(R.layout.fragment_main_map, container, false)

        initViewComponents()
        initObjectsAndReferences()
        initClickListeners()
        firebaseListeners()


        rideRequestReference.addValueEventListener(rideRequestListener)

        driverRideTypeReference.addValueEventListener(driverRideTypeListener)


        return fragmentView
    }

    private fun initClickListeners() {

        driverAvailableBtn.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {

        when(p0!!.id){

            R.id.driver_available_btn -> {


                if (isAvailable){

                    onOrOffTextView.setText("ON")
                    driversAvailableRef = firebaseDatabase.getReference(Constants.driversAvailableReference).child(rideType)
                    gfDriversAvailable = GeoFire(driversAvailableRef)
                    gfDriversAvailable.removeLocation(userId)

                    isAvailable = false


                }
                else {

                    driversAvailableRef = firebaseDatabase.getReference(Constants.driversAvailableReference).child(rideType)
                    gfDriversAvailable = GeoFire(driversAvailableRef)
                    gfDriversAvailable.setLocation(userId, GeoLocation(latitude!!, longitude!!))
                    onOrOffTextView.setText("OFF")
                    isAvailable = true
                }


            }
        }

    }

    private fun initViewComponents(){

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment!!.getMapAsync(this)

        driverAvailableBtn = fragmentView.findViewById(R.id.driver_available_btn)
        onOrOffTextView = fragmentView.findViewById(R.id.on_or_off_tv)


    }

    override fun onMapReady(googleMap: GoogleMap?) {

        mMap = googleMap!!

        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        enableMyLocationIfPermitted();

        mMap.getUiSettings().setZoomControlsEnabled(true)
        mMap.setMinZoomPreference(11f)

        val latitude = sharedPreferences.getFloat("latitude", 0.0f).toDouble()
        val longitude =  sharedPreferences.getFloat("longitude", 0.0f).toDouble()

        val latLng = LatLng(latitude, longitude)
//        mMap.addMarker(MarkerOptions().position(latLng).title("Current location"))
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,20f)
        mMap.animateCamera(cameraUpdate)



    }
    private fun enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                     arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION),
                    1)
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    fun initObjectsAndReferences(){


         firebaseDatabase = FirebaseDatabase.getInstance()
         firebaseUser = FirebaseAuth.getInstance().currentUser
         userId = firebaseUser!!.uid
         rideRequestReference = firebaseDatabase.getReference("Users").child("drivers").child(userId)
        driverRideTypeReference = firebaseDatabase.getReference("driverAccounts").child(userId).child("rideType")

        // initialize shared preferences
        sharedPreferences = context!!.getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE)
         latitude = sharedPreferences.getFloat("latitude", 0.0f).toDouble()
         longitude =  sharedPreferences.getFloat("longitude", 0.0f).toDouble()



    }

    fun firebaseListeners() {

        rideRequestListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {

                    if(dataSnapshot.value!!.equals("request")){

                        val fragmentmanger = activity!!.supportFragmentManager
                        fragmentmanger.beginTransaction().replace(R.id.frame_layout,AccepRideFragment()).addToBackStack(null) .commit()
                    }


                }




            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        driverRideTypeListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {



                    rideType = dataSnapshot.value.toString()

                }




            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

    }



      private  val onMyLocationButtonClickListener:GoogleMap.OnMyLocationButtonClickListener =
                     GoogleMap.OnMyLocationButtonClickListener {

                         mMap.setMinZoomPreference(15f)
                         return@OnMyLocationButtonClickListener false

                     }


    private  val onMyLocationClickListener:GoogleMap.OnMyLocationClickListener =
        GoogleMap.OnMyLocationClickListener {

          mMap.setMinZoomPreference(12f);

                     val circleOptions =  CircleOptions()
                    circleOptions.center( LatLng(it.getLatitude(),
                            it.getLongitude()))

                    circleOptions.radius(200.00)
                    circleOptions.fillColor(Color.RED)
                    circleOptions.strokeWidth(6f)

                    mMap.addCircle(circleOptions)
        }





}
