package com.example.tonguetherapy.common

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tonguetherapy.R
import com.example.tonguetherapy.bluetooth2.MonitoringScreen2
import com.example.tonguetherapy.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profile.*

class ActivityProfile : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityProfileBinding

    //ActionBar
    //private lateinit var actionBar: ActionBar

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure actionBar
        //actionBar = supportActionBar!!
        //actionBar.title = "profile"

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        //sendUser()
        //handle click, logout
        binding.buttonLogout.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }

    }

    private fun checkUser() {
        //check user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
            //user not null, user is logged in, get user info
            val email = firebaseUser.email
            //set to text view
            binding.emailTv.text = email
        }
        else{
            //user is null, user is not logged in, go to login activity
            startActivity(Intent(this,FormLogin::class.java))
            finish()
        }
    }
}