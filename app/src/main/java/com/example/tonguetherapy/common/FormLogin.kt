package com.example.tonguetherapy.common

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import com.example.tonguetherapy.R
import com.example.tonguetherapy.databinding.ActivityFormLoginBinding
import com.google.firebase.auth.FirebaseAuth

class FormLogin : AppCompatActivity() {

    //viewBinding
    private lateinit var binding: ActivityFormLoginBinding

    //ActionBar
    //private lateinit var actionBar: ActionBar

    //progressDialog
    private lateinit var progressDialog : ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth : FirebaseAuth
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configureActionbar
       //actionBar = supportActionBar!!
        // actionBar.title = "Login"

        //configureProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Logging In...")
        progressDialog.setCanceledOnTouchOutside(false)

        //initFirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle click, open form registrasi
        binding.noAccount.setOnClickListener {
            startActivity(Intent(this,FormRegistrasi::class.java))
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        //handle click, begin login
        binding.buttonLogin.setOnClickListener {
            //before logging in, Validate data
            validateData()
        }

        //back button
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.forgotPass.setOnClickListener {
            startActivity(Intent(this,ForgotPassword::class.java))
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        val b = findViewById<Button>(R.id.btnRegLogin)
        b.setOnClickListener {
            startActivity(Intent(this, FormRegistrasi::class.java))
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }
    }

    private fun validateData() {
        //get data
        email = binding.email.text.toString().trim()
        password = binding.password.text.toString().trim()

        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email format
            binding.email.error = "Invalid email format"
        }

        else if (TextUtils.isEmpty(password)){
            //no Password Enter
            binding.password.error = "Please enter password"
        }

        else{
            //data is validated, begin login
            firebaseLogin()
        }

    }

    private fun firebaseLogin() {
        //show proggress
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                //login success
                progressDialog.dismiss()
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this,"Logged in as $email",Toast.LENGTH_SHORT).show()

                //open profile
                startActivity(Intent(this, ActivityProfile::class.java))
                finish()
            }
            .addOnFailureListener { e->
                //login failed
                progressDialog.dismiss()
                Toast.makeText(this,"Login failed due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        //if user is already logged in go to profile activity
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            //user is already legged in
            startActivity(Intent(this, ActivityProfile::class.java))
            finish()
        }
    }
}