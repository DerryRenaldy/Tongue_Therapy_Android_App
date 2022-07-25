package com.example.tonguetherapy.common

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tonguetherapy.R
import com.example.tonguetherapy.databinding.ActivityFormRegistrasiBinding
import com.google.firebase.auth.FirebaseAuth


class FormRegistrasi : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityFormRegistrasiBinding

    //ActionBar
    //private lateinit var actionBar: ActionBar

    //ProgressDialog
    private lateinit var progressDialog : ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormRegistrasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure actionBar
        //actionBar = supportActionBar!!
        //actionBar.title = "Sign Up"
        //actionBar.setDisplayHomeAsUpEnabled(true)
        //actionBar.setDisplayShowHomeEnabled(true)

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Creating Account In...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //handle click, begin sign up
        binding.buttonRegister.setOnClickListener {
            //validate data
            validateData()
        }

        val a = findViewById<Button>(R.id.btnLogRegister)
        a.setOnClickListener{
            onBackPressed()
        }
    }

    private fun validateData() {
        //get data
        email = binding.email.text.toString().trim()
        password = binding.password.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email format
            binding.email.error = "Invalid email format"
        }

        else if (TextUtils.isEmpty(password)){
            //no Password Enter
            binding.password.error = "Please enter password"
        }
        else if (password.length <6){
            //password length <6
            binding.password.error = "Password must atleast 6 character long"
        }
        else{
            //data is validated, begin login
            firebaseLogin()
        }
    }

    private fun firebaseLogin() {
        //show progress
        progressDialog.show()

        //create account
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //login success
                progressDialog.dismiss()
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this,"Account created with email $email", Toast.LENGTH_SHORT).show()

                //open profile
                startActivity(Intent(this, ActivityProfile::class.java))
                finish()
            }
            .addOnFailureListener { e->
                //login failed
                progressDialog.dismiss()
                Toast.makeText(this,"Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when button of actionbar clicked
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
    }
}