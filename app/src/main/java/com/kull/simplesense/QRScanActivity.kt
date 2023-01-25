package com.kull.simplesense

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.kull.simplesense.models.Device
import com.kull.simplesense.utils.SharedPref

class QRScanActivity : AppCompatActivity() {
    lateinit var btnSearch: Button
    var qrMac: String = ""
    var sharedPref: SharedPref? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscan)
        sharedPref = SharedPref(this)
        btnSearch = findViewById(R.id.btnQRScan)

        btnSearch.setOnClickListener {
            setUpQRCode()
        }
    }

    private fun setUpQRCode() {
        if (!sharedPref?.getData("mac").isNullOrBlank()) {
            sharedPref?.remove("mac")
        }
        IntentIntegrator(this)
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            .setTorchEnabled(false)
            .setBeepEnabled(true)
            .setPrompt("Para escanear, coloque un código QR dentro del rectángulo")
            .initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents !== null) {
                qrMac = result.contents
                goToHome(qrMac)
            } else {
                Toast.makeText(this, "No se pudo obtener información.", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun goToHome(mac: String) {
        val i = Intent(this, HomeActivity::class.java)
        saveMacInSession(mac)
        this.startActivity(i)
    }

    private fun saveMacInSession(mac: String) {
        // val sharedPref = SharedPref(this)
        sharedPref?.saveString("mac", mac)
    }
}