package com.centosquare.devatease.goappdriver.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.centosquare.devatease.goappdriver.R
import com.centosquare.devatease.goappdriver.location.LocationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var splashImgv: ImageView
    private  var firebaseUser: FirebaseUser? = null
    private  var userId:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        splashImgv = findViewById(R.id.imgv_splash)

        Glide.with(this).load(R.drawable.ic_splash_logo).centerInside().into(splashImgv)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        userId = firebaseUser?.uid

        val secondsDelayed = 1

        Handler().postDelayed({

            if (firebaseUser == null){

                startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
            finish()

        }
            else {

                startActivity(Intent(this@SplashScreenActivity, LocationActivity::class.java))
                finish()

        }

        }, (secondsDelayed * 3000).toLong())


    }
}
