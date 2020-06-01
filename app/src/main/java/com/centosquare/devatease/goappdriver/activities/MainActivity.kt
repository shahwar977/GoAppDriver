package com.centosquare.devatease.goappdriver.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.centosquare.devatease.goappdriver.R
import com.centosquare.devatease.goappdriver.fragments.*
import com.centosquare.devatease.goappdriver.models.DriverProfileModel
import com.centosquare.devatease.gooapp.fragments.PastTripFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var  drawerLayout: DrawerLayout
    private lateinit var draweBtn:CircleImageView
    private lateinit var imgvProfile: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var userRatingTextView:TextView
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var profileInfoReference: DatabaseReference
    private  var firebaseUser: FirebaseUser? = null
    private lateinit var userId:String
    private lateinit var profileListener: ValueEventListener


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_main)

        viewComponents()
        initObjectsAndReferences()
        firebaseListeners()



       val firebaseUser = FirebaseAuth.getInstance().currentUser
        val userId = firebaseUser!!.uid
        val firebaseDatabase = FirebaseDatabase.getInstance()

       /* val tempModel: DriverProfileModel = DriverProfileModel("shoaib","03248249031","Car")
         firebaseDatabase.getReference("driverAccounts").child(userId).setValue(tempModel)*/

        draweBtn.setOnClickListener {

            drawerLayout.openDrawer(GravityCompat.START)



        }

        profileInfoReference.addListenerForSingleValueEvent(profileListener)

        val fragmentmanger = supportFragmentManager
        fragmentmanger.beginTransaction().replace(R.id.frame_layout,MainMapFragment()).addToBackStack(null) .commit()



    }

    private fun viewComponents() {

        drawerLayout = findViewById(R.id.drawer_layout)
        draweBtn = findViewById(R.id.navigation_drawer_img)

        val navView: NavigationView =  findViewById(R.id.nav_view)

        var header: View = navView.getHeaderView(0)
        imgvProfile =  header.findViewById(R.id.nav_profile_img)
        userNameTextView = header.findViewById(R.id.nav_tv_profile_name)
        userRatingTextView = header.findViewById(R.id.nav_tv_rating)

        navView.setNavigationItemSelectedListener(this)

    }


    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        // Handle navigation view item clicks here.

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        when (p0.itemId) {
            R.id.nav_your_trip -> {
                // Handle the camera action

               supportFragmentManager.beginTransaction().add(R.id.frame_layout,PastTripFragment()).addToBackStack(null).commit()

                drawerLayout.closeDrawer(GravityCompat.START)

            }
            R.id.nav_notification -> {
                // Handle the camera action

                supportFragmentManager.beginTransaction().replace(R.id.frame_layout,NotificationFragment()).addToBackStack(null).commit()

                //drawerLayout.closeDrawer(GravityCompat.START)

            }

            R.id.nav_help -> {


                logout()

            }


        }
//        drawerLayout.closeDrawer(GravityCompat.START)
        return true



    }

    private fun initObjectsAndReferences() {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser!!.uid
        firebaseDatabase = FirebaseDatabase.getInstance()
        profileInfoReference = firebaseDatabase.getReference("driverAccounts").child(userId)
    }


    private fun firebaseListeners() {

        profileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                val name:String = dataSnapshot.child("name").value.toString()

                userNameTextView.setText(name)

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
    }

    private fun logout(){


        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this@MainActivity,LoginActivity::class.java))
        finish()
    }

}
