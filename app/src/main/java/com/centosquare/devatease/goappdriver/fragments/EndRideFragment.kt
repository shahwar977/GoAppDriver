package com.centosquare.devatease.goappdriver.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.centosquare.devatease.goappdriver.R
import com.centosquare.devatease.goappdriver.interfaces.TaskLoadedCallback
import com.centosquare.devatease.goappdriver.interfaces.TaskLoadedDurationCallback
import com.centosquare.devatease.goappdriver.utils.Constants
import com.centosquare.devatease.goappdriver.utils.FetchURL
import com.firebase.geofire.GeoFire
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class EndRideFragment : Fragment() , OnMapReadyCallback,TaskLoadedCallback, TaskLoadedDurationCallback {

    private lateinit var fragmentView: View
    private lateinit var mMap: GoogleMap
    private lateinit var endRideBtn: Button
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var responseReference: DatabaseReference
    private lateinit var workingDriverReference: DatabaseReference
    private  lateinit var gfDriversWorking: GeoFire
    private lateinit var rideReference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var userId:String
    private lateinit var customerId:String
    private lateinit var getRideReference:DatabaseReference
    private lateinit var getRideInfoListener:ValueEventListener
    private lateinit var getCustomerNameListener:ValueEventListener
    private lateinit var getCustomerNameReference:DatabaseReference

    private lateinit var customerName:String
    private lateinit var pickupLocation:String
    private lateinit var dropOffLocation:String
    private lateinit var rideFare:String

    private  var pickupLat:Double? = null
    private  var pickupLong:Double? =null
    private  var destinationLat:Double? = null
    private  var destinationLong:Double? =null


    private var currentPolyline: Polyline? = null
    private lateinit var pickupLocationMarker:MarkerOptions
    private lateinit var destinationLocationMarker:MarkerOptions
    private lateinit var pickupLatLng:LatLng
    private lateinit var destinationLatLng:LatLng




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        fragmentView =  inflater.inflate(R.layout.fragment_end_ride, container, false)

        customerId = this.arguments!!.getString("CUSTOMERID")!!



        initViewComponents()
        initObjectsAndReferences()
        firebaseListeners()


        endRideBtn.setOnClickListener {

            responseReference.child(userId).setValue("complete").addOnSuccessListener {

                gfDriversWorking = GeoFire(workingDriverReference)
                gfDriversWorking.removeLocation(userId)

                responseReference.child(userId).removeValue()

                getRideReference.removeValue().addOnSuccessListener{



                    val transaction =  activity!!.supportFragmentManager.beginTransaction()
                    val bundle = Bundle()
                    bundle.putString("CUSTOMERNAME", customerName)
                    bundle.putString("PICKUPLOC", pickupLocation)
                    bundle.putString("DROPOFFLOC", dropOffLocation)
                    bundle.putString("RIDEFARE", rideFare)
                    val fragInfo = RideRecieptFragment()
                    fragInfo.arguments = bundle
                    transaction.replace(R.id.frame_layout, fragInfo).addToBackStack(null)
                    transaction.commit()

                }



            }

        }

        getRideReference.addListenerForSingleValueEvent(getRideInfoListener)
        getCustomerNameReference.addListenerForSingleValueEvent(getCustomerNameListener)



        return fragmentView
    }


    private fun initViewComponents() {

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment!!.getMapAsync(this)

        endRideBtn = fragmentView.findViewById(R.id.end_ride_btn)


    }

    override fun onMapReady(googleMap: GoogleMap?) {

        mMap = googleMap!!

        val latLng = LatLng(24.68, 67.67)
        mMap.addMarker(MarkerOptions().position(latLng).title("Current location"))
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,20f)
        mMap.animateCamera(cameraUpdate)

    }

    fun initObjectsAndReferences(){


        firebaseDatabase = FirebaseDatabase.getInstance()
        responseReference = firebaseDatabase.getReference("response")
        workingDriverReference = firebaseDatabase.getReference("driversworking")
        rideReference = firebaseDatabase.getReference("response")
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        userId = firebaseUser!!.uid
        getRideReference = firebaseDatabase.getReference("Ride").child(customerId)
        getCustomerNameReference = firebaseDatabase.getReference("profile").child(customerId)

    }


    fun firebaseListeners() {

        getRideInfoListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {


                    pickupLocation =  dataSnapshot.child("Current").child("address").value.toString()
                    dropOffLocation =  dataSnapshot.child("Destination").child("address").value.toString()
                    rideFare =  dataSnapshot.child("ridePrice").value.toString()

                    pickupLat = dataSnapshot.child("Current").child("latitude").value as Double
                    pickupLong = dataSnapshot.child("Current").child("longitude").value as Double
                    destinationLat = dataSnapshot.child("Destination").child("latitude").value as Double
                    destinationLong = dataSnapshot.child("Destination").child("longitude").value as Double


                    drawRouteFromPickupToDestination()
                    drawMarker()


                }




            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }


        getCustomerNameListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {


                    customerName = dataSnapshot.child("name").value.toString()



                }




            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }








    }




    private fun drawRouteFromPickupToDestination(){

        pickupLocationMarker = MarkerOptions()
        destinationLocationMarker = MarkerOptions()
        pickupLatLng = LatLng(pickupLat!!,pickupLong!!)
        destinationLatLng = LatLng(destinationLat!!,destinationLong!!)
        pickupLocationMarker.position(pickupLatLng)
        destinationLocationMarker!!.position(destinationLatLng)


        FetchURL(this,this
        ).execute(
            getUrl(
                pickupLocationMarker.getPosition(),
                destinationLocationMarker!!.getPosition(),
                "driving"
            ), "driving"
        )


    }

    //setting url for polyline direction
    private fun getUrl(origin: LatLng, dest: LatLng, directionMode: String): String {
        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        // Mode
        val mode = "mode=$directionMode"
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$mode"
        // Output format
        val output = "json"
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_mapsdirection_key)
    }

    override fun onTaskDone(vararg values: Any) {

        if (currentPolyline != null)
            currentPolyline!!.remove()
        currentPolyline = mMap.addPolyline(values[0] as PolylineOptions)
        val builderBounds = LatLngBounds.Builder()
        val  padding = 50



        builderBounds.include(pickupLatLng)
        builderBounds.include(destinationLatLng)
        val bounds = builderBounds.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,padding)

        mMap.animateCamera(cameraUpdate)




    }

    override fun onTimeDone(vararg value: String) {
    }

    override fun onDuration(value: String) {


    }

    private fun drawMarker(){

        var icon:BitmapDescriptor? = null

        icon = BitmapDescriptorFactory.fromResource(R.drawable.car)

        // setting marker on map for all available drivers

        val markerOptions =  MarkerOptions()
        // setting marker on availabe drivers with their ridetype icon
        markerOptions.position(LatLng(pickupLat!!,pickupLong!!)).title("My Location").icon(icon)
        mMap.addMarker(markerOptions)
    }



}
