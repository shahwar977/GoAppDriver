package com.centosquare.devatease.goappdriver.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.centosquare.devatease.goappdriver.R
import com.centosquare.devatease.goappdriver.location.LocationActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBtn:Button
    private lateinit var userNameEditText:EditText
    private lateinit var passwordEditText:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViewComponents()

        loginBtn.setOnClickListener {

            if (userNameEditText.text.toString().trim().isNotEmpty() && passwordEditText.text.toString().trim().isNotEmpty() ) {


                performLogin(userNameEditText.text.toString().trim(),passwordEditText.text.toString().trim())

            }

            else{

                Toast.makeText(this,"username or password cannot be empty", Toast.LENGTH_SHORT).show()
            }

          //  startActivity(Intent(this@LoginActivity,LocationActivity::class.java))
        }






    }

    private fun performLogin(userName: String, password: String) {


        FirebaseAuth.getInstance().signInWithEmailAndPassword(userName,password)
            .addOnSuccessListener {

                startActivity(Intent(this@LoginActivity,LocationActivity::class.java))

                }

            .addOnFailureListener {

                Toast.makeText(this@LoginActivity,"you are not registered", Toast.LENGTH_SHORT).show()



            }
            }





    private fun initViewComponents() {

        userNameEditText = findViewById(R.id.editText_phon_no_login)
        passwordEditText = findViewById(R.id.user_password_et)
        loginBtn = findViewById(R.id.btn_login)

    }


}
