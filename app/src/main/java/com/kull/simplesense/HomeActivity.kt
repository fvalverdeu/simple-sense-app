package com.kull.simplesense

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.kull.simplesense.models.Preparation

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // mac = intent.getStringExtra("mac").toString()

        val linearLayoutTemperature: LinearLayout = findViewById<LinearLayout>(R.id.btnHomeTemperature)
        linearLayoutTemperature.setOnClickListener {
            val i = Intent(this, PreparationActivity::class.java)
            // i.putExtra("mac", mac)
            startActivity(i)
        }

        val linearLayoutCalibration: LinearLayout = findViewById<LinearLayout>(R.id.btnHomeCalibration)
        linearLayoutCalibration.setOnClickListener {
            val i = Intent(this, CalibrationActivity::class.java)
            startActivity(i)
        }
    }
}