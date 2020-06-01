package com.centosquare.devatease.goappdriver.fragments



import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.centosquare.devatease.goappdriver.R
import com.centosquare.devatease.goappdriver.activities.Notification
import com.centosquare.devatease.goappdriver.activities.NotificationAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A simple [Fragment] subclass.
 */
class NotificationFragment : Fragment() {


    private lateinit var toolbar: Toolbar
    private lateinit var fragmentView: View
    private lateinit var list:ArrayList<Notification>
    private lateinit var recyclerview: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_notification, container, false)
        recyclerview = fragmentView.findViewById(R.id.recycler_noti)
        recyclerview.layoutManager = LinearLayoutManager(context)
        viewComponents()
        fetchdata()

        list = ArrayList()
        val activityContext = activity!! as AppCompatActivity
        activityContext.setSupportActionBar(toolbar)
        toolbar.title = resources.getString(R.string.title_fragment_notifications)

        // for back button
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).getSupportActionBar()!!.setDisplayShowHomeEnabled(true)


        // for back button transaction
        toolbar.setNavigationOnClickListener {

            activity!!.supportFragmentManager.beginTransaction().remove(this).commit()

        }

        return fragmentView
    }
    fun viewComponents() {


        toolbar = fragmentView.findViewById(R.id.toolbar)

    }
    private fun fetchdata() {
        val databasereference = FirebaseDatabase.getInstance().getReference("Notification")

        databasereference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for(snapshot in dataSnapshot.children){
                        val notification = snapshot.getValue(Notification::class.java)


                        list.add(notification!!)
                    }
                    notificationAdapter = NotificationAdapter(context!!, list)
                    recyclerview.adapter = notificationAdapter

                }else{
                    Toast.makeText(context,"No data found", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
}

