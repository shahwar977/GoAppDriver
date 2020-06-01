package com.centosquare.devatease.goappdriver.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import com.centosquare.devatease.goappdriver.R
import com.centosquare.devatease.goappdriver.models.PastRidesModel
import com.firebase.geofire.GeoFire
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class RideRecieptFragment : Fragment() {

    private lateinit var fragmentView:View
    private lateinit var toolbar: Toolbar
    private lateinit var fareCollectedBtn: Button
    private lateinit var customerNameTexView: TextView
    private lateinit var pickupLocTexView:TextView
    private lateinit var dropOffLocTexView:TextView
    private lateinit var rideFareTexView:TextView

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var pastRideReference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var userId:String


    private lateinit var customerName:String
    private lateinit var pickupLocation:String
    private lateinit var dropOffLocation:String
    private lateinit var rideFare:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_ride_reciept, container, false)


        //for toolbar
        toolbar = fragmentView.findViewById(R.id.toolbar)
        var activityContext = activity!! as AppCompatActivity
        activityContext.setSupportActionBar(toolbar)
        toolbar.title = resources.getString(R.string.title_fragment_ride_reciept)

        initViewComponents()
        initObjectsAndReferences()

        customerName = this.arguments!!.getString("CUSTOMERNAME")!!
        pickupLocation = this.arguments!!.getString("PICKUPLOC")!!
        dropOffLocation = this.arguments!!.getString("DROPOFFLOC")!!
        rideFare = this.arguments!!.getString("RIDEFARE")!!


        customerNameTexView.setText(customerName)
        pickupLocTexView.setText(pickupLocation)
        dropOffLocTexView.setText(dropOffLocation)
        rideFareTexView.setText(rideFare)





        fareCollectedBtn.setOnClickListener {


            val pastRidesModel= PastRidesModel(customerName,pickupLocation,dropOffLocation,rideFare)

            pastRideReference.setValue(pastRidesModel).addOnSuccessListener {

                val fragmentmanger = activity!!.supportFragmentManager
                fragmentmanger.beginTransaction().replace(R.id.frame_layout,MainMapFragment()).addToBackStack(null) .commit()

            }



        }






        return fragmentView
    }


    private fun initViewComponents(){

        fareCollectedBtn = fragmentView.findViewById(R.id.fare_collected_btn)
        customerNameTexView = fragmentView.findViewById(R.id.customer_name_tv)
        pickupLocTexView = fragmentView.findViewById(R.id.pickup_loc_tv)
        dropOffLocTexView = fragmentView.findViewById(R.id.dropoff_loc_tv)
        rideFareTexView = fragmentView.findViewById(R.id.ride_fare_tv)
    }


    fun initObjectsAndReferences(){


        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        userId = firebaseUser!!.uid
        pastRideReference = firebaseDatabase.getReference("pastRides").child(userId).child(UUID.randomUUID().toString())


    }


}
