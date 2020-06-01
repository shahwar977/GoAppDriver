package com.centosquare.devatease.goappdriver.fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

import com.centosquare.devatease.goappdriver.R
import com.centosquare.devatease.goappdriver.utils.Constants
import com.firebase.geofire.GeoFire
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class AccepRideFragment : Fragment() , OnMapReadyCallback {

    private lateinit var fragmentView: View
    private lateinit var mMap: GoogleMap
    private lateinit var acceptBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var firebaseDatabase:FirebaseDatabase
    private lateinit var responseReference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var userId:String
    private lateinit var sharedPreferences: SharedPreferences
    private var latitude:Double? = null
    private var longitude:Double? = null
    private lateinit var cancelUserIdReference: DatabaseReference
    private lateinit var driverRideTypeReference: DatabaseReference
    private lateinit var driverRideTypeListener:ValueEventListener
    private lateinit var rideType:String





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        fragmentView =  inflater.inflate(R.layout.fragment_accep_ride, container, false)

        initViewComponents()
        initObjectsAndReferences()
        firebaseListeners()


        acceptBtn.setOnClickListener {


            responseReference.child(userId).setValue("accept").addOnSuccessListener {



                val fragmentmanger = activity!!.supportFragmentManager
                        fragmentmanger.beginTransaction().replace(R.id.frame_layout,ArrivedFragment()).addToBackStack(null) .commit()
            }

        }

        cancelBtn.setOnClickListener {


            responseReference.child(userId).setValue("cancel").addOnSuccessListener {

            cancelUserIdReference.removeValue().addOnSuccessListener {

                    responseReference.child(userId).removeValue().addOnSuccessListener {

                        val driversAvailableRef =
                            firebaseDatabase.getReference(Constants.driversAvailableReference).child(rideType)
                        val gfDriversAvailable = GeoFire(driversAvailableRef)
                        gfDriversAvailable.removeLocation(userId)

                            val fragmentmanger = activity!!.supportFragmentManager
                            fragmentmanger.beginTransaction().replace(R.id.frame_layout, MainMapFragment())
                                .addToBackStack(null)
                                .commit()
                        }

                }
            }


            driverRideTypeReference.addValueEventListener(driverRideTypeListener)



        }



        return fragmentView
    }

    private fun initViewComponents(){

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment!!.getMapAsync(this)

        acceptBtn = fragmentView.findViewById(R.id.button2)
        cancelBtn = fragmentView.findViewById(R.id.button3)


    }


    override fun onMapReady(googleMap: GoogleMap?) {

        mMap = googleMap!!

        val latitude = sharedPreferences.getFloat("latitude", 0.0f).toDouble()
        val longitude =  sharedPreferences.getFloat("longitude", 0.0f).toDouble()

        val latLng = LatLng(latitude,longitude )
        mMap.addMarker(MarkerOptions().position(latLng).title("Current location"))
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,20f)
        mMap.animateCamera(cameraUpdate)

    }

    fun initObjectsAndReferences(){


        firebaseDatabase = FirebaseDatabase.getInstance()
        responseReference = firebaseDatabase.getReference("response")
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        userId = firebaseUser!!.uid

        cancelUserIdReference = firebaseDatabase.getReference("Users").child("drivers").child(userId)

        driverRideTypeReference = firebaseDatabase.getReference("driverAccounts").child(userId).child("rideType")

        // initialize shared preferences
        sharedPreferences = context!!.getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE)
        latitude = sharedPreferences.getFloat("latitude", 0.0f).toDouble()
        longitude =  sharedPreferences.getFloat("longitude", 0.0f).toDouble()



    }




    fun firebaseListeners() {



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






}
