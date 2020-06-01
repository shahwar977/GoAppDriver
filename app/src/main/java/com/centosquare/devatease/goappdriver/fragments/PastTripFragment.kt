package com.centosquare.devatease.gooapp.fragments

import PastTripAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.centosquare.devatease.goappdriver.R
import com.centosquare.devatease.goappdriver.models.PastRidesModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class PastTripFragment : Fragment()  {

    private lateinit var fragemntView: View
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerview:RecyclerView
    private lateinit var pastTripAdapter:PastTripAdapter

    private lateinit var firebaseDatabase: FirebaseDatabase
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var pastRidetReference: DatabaseReference
    private lateinit var userId:String
    private lateinit var pastRidesModel: PastRidesModel
    private lateinit var pastRidesListener: ValueEventListener



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragemntView = inflater.inflate(R.layout.fragment_past_trips, container, false)
        // initialize all view components
        viewComponents()
        initObjectsAndReferences()
        firebaseListeners()
        // for toolbar name

        var activityContext = activity!! as AppCompatActivity
        activityContext.setSupportActionBar(toolbar)
        toolbar.title = resources.getString(R.string.title_fragment_past_trip)

        // for back button
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayShowHomeEnabled(true)


        // for back button transaction
        toolbar.setNavigationOnClickListener {

            activity!!.supportFragmentManager.beginTransaction().remove(this).commit()

        }


        pastRidetReference.addValueEventListener(pastRidesListener)

        return fragemntView
    }

    fun viewComponents(){

        toolbar = fragemntView.findViewById(R.id.toolbar)
        recyclerview = fragemntView.findViewById(R.id.recyclerview_past_trip)
        val layoutManager = LinearLayoutManager(context)
        recyclerview.setLayoutManager(layoutManager)


    }





    fun initObjectsAndReferences(){

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser!!.uid
        firebaseDatabase = FirebaseDatabase.getInstance()
        pastRidetReference = firebaseDatabase.getReference("pastRides").child(userId)
    }


    fun firebaseListeners() {

        pastRidesListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {



                var list:ArrayList<PastRidesModel> = ArrayList()
                for(snapshot in dataSnapshot.children){


                    var  model:PastRidesModel? = snapshot.getValue(PastRidesModel::class.java)

                    list.add(model!!)

                }



                pastTripAdapter = PastTripAdapter(activity!!,list)
                recyclerview.setAdapter(pastTripAdapter)





            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }







    }



}