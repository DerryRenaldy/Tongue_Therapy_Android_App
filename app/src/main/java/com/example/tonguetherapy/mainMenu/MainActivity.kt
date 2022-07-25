package com.example.tonguetherapy.mainMenu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tonguetherapy.personalData.PersonalData
import com.example.tonguetherapy.R
import com.example.tonguetherapy.bluetooth.MainActivityBt
import com.example.tonguetherapy.bluetooth2.MainActivityBt2
import com.example.tonguetherapy.common.FormLogin
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //tanggal
        val mTvTanggalMainMenu = findViewById<TextView>(R.id.tvTanggalMainMenu)
        val currentTime = Calendar.getInstance().time
        val formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime)
        mTvTanggalMainMenu.setText(formattedDate)

        val b = findViewById<Button>(R.id.btnAccount)
        b.setOnClickListener {
            startActivity(Intent(this, FormLogin::class.java))
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        val c = findViewById<Button>(R.id.btnPersonalData2)
        c.setOnClickListener {
            startActivity(Intent(this, PersonalData::class.java))
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        buttonBt.setOnClickListener {
            startActivity(Intent(this, MainActivityBt::class.java))
        }

        buttonBt2.setOnClickListener {
            startActivity(Intent(this, MainActivityBt2::class.java))
        }
    }
}