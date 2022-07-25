package com.example.tonguetherapy.common

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.tonguetherapy.databinding.ActivityForgotPasswordBinding
import com.example.tonguetherapy.mainMenu.MainActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    //viewBinding
    private lateinit var binding: ActivityForgotPasswordBinding

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //back button
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        //init/setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click, begin password recovery process
        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        //get data
        email = binding.emailEt.text.toString().trim()

        //validateData
        if(email.isEmpty()) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
        }
        else {
            recoverPassword()
        }
    }

    private fun recoverPassword() {
        //Show Progress
        progressDialog.setMessage("Sending password reset Instructions to $email")
        progressDialog.show()

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                //sent
                progressDialog.dismiss()
                Toast.makeText(this,"Instructions sent to \n$email",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                //failed
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to send due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }
}