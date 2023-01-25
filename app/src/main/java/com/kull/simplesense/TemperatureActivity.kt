package com.kull.simplesense

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.kull.simplesense.models.Client
import com.kull.simplesense.models.Preparation
import com.kull.simplesense.models.Temperature
import com.kull.simplesense.models.User
import com.kull.simplesense.providers.TemperatureProvider
import com.kull.simplesense.utils.DateUtils
import com.kull.simplesense.utils.SessionHandler
import com.minew.beaconplus.sdk.MTCentralManager
import com.minew.beaconplus.sdk.enums.FrameType
import com.minew.beaconplus.sdk.frames.HTFrame
import org.joda.time.LocalDateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import java.util.*

class TemperatureActivity : AppCompatActivity() {
    private val REQUEST_ENABLE_BT = 3
    private val PERMISSION_COARSE_LOCATION = 2
    private val PERMISSION_FINE_LOCATION = 4
    var temperature = "0.0"
    var qrMacCode = ""
    lateinit var txtTemp: TextView
    lateinit var btnSuccess: Button
    lateinit var txtMin: TextView
    lateinit var txtPreparation: TextView

    var temperatureProvider = TemperatureProvider()
    var preparation: Preparation? = null
    var client: Client? = null
    var place: String = ""
    val gson = Gson()
    var userSession: User? = null

    var mtCentralManagerGlob: MTCentralManager = MTCentralManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temperature)
        txtTemp = findViewById<TextView>(R.id.txtCal)
        btnSuccess = findViewById(R.id.btnTempSuccess)
        txtMin = findViewById(R.id.txtTempMin)
        txtPreparation = findViewById(R.id.txtTempPreparation)

        var sessionHandler: SessionHandler = SessionHandler()

        userSession = sessionHandler.getUserFromSession(this)
        client = gson.fromJson(intent.getStringExtra("client"), Client::class.java)
        preparation = gson.fromJson(intent.getStringExtra("preparation"), Preparation::class.java)
        txtPreparation.setText("${preparation?.description}")
        txtMin.setText("entre ${preparation?.min.toString()}°C y ${preparation?.max.toString()}°C")
        place = intent.getStringExtra("place").toString()
        qrMacCode = sessionHandler.getMacFromSession(this).toString()

/*        if (validatePermission("FINE", PERMISSION_FINE_LOCATION)) {
            process(qrMacCode, mtCentralManagerGlob)
        }*/
        getRequiredPermissions()
        btnSuccess.setOnClickListener {
            register(mtCentralManagerGlob)
        }
    }

/*    fun getPermissionOfType(type: String): String {
        var permission: String = ""
        when(type) {
            "COARSE" -> permission = Manifest.permission.ACCESS_COARSE_LOCATION
            "FINE" -> permission = Manifest.permission.ACCESS_FINE_LOCATION
        }
        return permission
    }*/

/*    fun validatePermission(type: String, code: Int): Boolean {
        var permission = getPermissionOfType(type)
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), code)
            return false
        } else {
            return true
        }
    }*/

    override fun onRequestPermissionsResult(code: Int, permissions: Array<String?>,grantResults: IntArray) {
        super.onRequestPermissionsResult(code, permissions, grantResults)
        var result = true
        var permissionWithError = ""
        when (code) {
            PERMISSION_FINE_LOCATION -> {
                var isGrant = true
                var index = 0
                for (grantResult in grantResults) {
                    Log.e("PERMISSION", "${permissions[index].toString()}")
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        isGrant = false
                        permissionWithError = permissions[index].toString()
                        break
                    }
                    index++
                }
                if (isGrant) {
                    process(qrMacCode, mtCentralManagerGlob)
                }
            }
            else -> {
                Toast.makeText(this, "Error en permisos", Toast.LENGTH_SHORT).show()
                Log.e("PERMISSION ERROR", "${permissionWithError}")
            }
        }
/*        if (code == PERMISSION_COARSE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                validatePermission("FINE", PERMISSION_FINE_LOCATION)
                return
            }
        } else if (code == PERMISSION_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                process(qrMacCode, mtCentralManagerGlob)
                return
            }
        } else {
            Toast.makeText(this, "Error en permisos", Toast.LENGTH_SHORT).show()
            return
        }*/
    }

    private fun process(qrMacCode: String, mtCentralManager: MTCentralManager) {
        try {

            mtCentralManager.startService()
            // findViewById<EditText>(R.id.txtObservation).setText("startService")
            Log.e("START SERVICE", "START SERVICE OK")
            mtCentralManager.setBluetoothChangedListener {}
            mtCentralManager.startScan()
            Log.e("START SCAN", "START SCAN OK")
            // findViewById<EditText>(R.id.txtObservation).setText("startScan")
            mtCentralManager.setMTCentralManagerListener { peripherals ->
                Log.e("LISTENER MTC", "PERIPHERALS")
                for (mtPeripheral in peripherals) {
                    val mtFrameHandler = mtPeripheral.mMTFrameHandler
                    val mac = mtFrameHandler.mac.replace(":","")
                    // findViewById<EditText>(R.id.txtObservation).setText("${mac} - ${qrMacCode}")
                    val name = mtFrameHandler.name
                    val advFrames = mtFrameHandler.advFrames
                    Log.e("COMPARISION", "${mac.toString()} VS ${qrMacCode}")
                    if (mac == qrMacCode) {
                        for (minewFrame in advFrames) {
                            when (minewFrame.frameType) {
                                FrameType.FrameHTSensor -> {
                                    val htFrame = minewFrame as HTFrame
                                    temperature = String.format("%.1f", htFrame.temperature)
                                    if (htFrame.temperature > preparation?.max!!.toDouble() ||
                                            htFrame.temperature < preparation?.min!!.toDouble()) {
                                        findViewById<ImageView>(R.id.imgCalc).setImageResource(R.drawable.failed)
                                        txtTemp.setTextColor(resources.getColor(R.color.failedColor))
                                    } else {
                                        findViewById<ImageView>(R.id.imgCalc).setImageResource(R.drawable.success)
                                        txtTemp.setTextColor(resources.getColor(R.color.successColor))
                                    }
                                    txtTemp.setText("$temperature°C")
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("ERROR!!", "Error: ${e.message}")
        }
    }

    fun register(mtCentralManager: MTCentralManager) {
        var dateUtils: DateUtils = DateUtils()
        val formatted = dateUtils.getCurrentUTCDateTimeInISOFormat()
        val local = LocalDateTime()
        val temperatureTS =  Timestamp(local.toDateTime().millis);

        val observation = findViewById<EditText>(R.id.txtObservation).text.toString()
        val objTemperature = Temperature(value = "${temperature}", date = "${temperatureTS}",
            location = "${place}", observation = "${observation}", idpreparation = "${preparation?.id}",
            iduser = "${userSession?.id}", idclient = "${client?.id}")
        btnSuccess.isEnabled = false

        temperatureProvider.register(objTemperature)?.enqueue(object: Callback<Temperature> {
            override fun onResponse(call: Call<Temperature>, response: Response<Temperature>) {
                Toast.makeText(this@TemperatureActivity, "Se registró la temperatura correctamente.", Toast.LENGTH_LONG).show()
                Log.e("response: ", "${response}")
                mtCentralManagerGlob.stopScan()
                val i = Intent(this@TemperatureActivity, HomeActivity::class.java)
                startActivity(i)
            }
            override fun onFailure(call: Call<Temperature>, t: Throwable) {
                Log.e("On Failure:", "Se produjo un error ${t.message}")
                Toast.makeText(this@TemperatureActivity, "Se produjo un error ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getRequiredPermissions() {
        val requestPermissions: Array<String>
        requestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        ActivityCompat.requestPermissions(
            this,
            requestPermissions, PERMISSION_FINE_LOCATION
        )
    }
}