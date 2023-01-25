package com.kull.simplesense

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.kull.simplesense.models.Calibration
import com.kull.simplesense.models.Client
import com.kull.simplesense.models.Preparation
import com.kull.simplesense.models.User
import com.kull.simplesense.providers.CalibrationProvider
import com.kull.simplesense.providers.ClientProvider
import com.kull.simplesense.utils.DateUtils
import com.kull.simplesense.utils.SessionHandler
import com.kull.simplesense.utils.SharedPref
import com.minew.beaconplus.sdk.MTCentralManager
import com.minew.beaconplus.sdk.enums.FrameType
import com.minew.beaconplus.sdk.frames.HTFrame
import org.joda.time.LocalDateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class CalibrationActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private val PERMISSION_COARSE_LOCATION = 2
    private val PERMISSION_FINE_LOCATION = 4
    var temperature = "0.0"
    var sessionHandler: SessionHandler = SessionHandler()
    var qrMacCode = ""
    var userSession: User? = null

    lateinit var txtCal: TextView
    lateinit var btnSuccess: Button
    lateinit var btnCancel: Button
    lateinit var autocompleteClient: AutoCompleteTextView

    var calibrationProvider = CalibrationProvider()
    val gson = Gson()

    var clients: ArrayList<Client> = ArrayList()
    var positionSelected = 0
    var clientProvider = ClientProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibration)
        txtCal = findViewById<TextView>(R.id.txtCal)
        btnSuccess = findViewById(R.id.btnCalSuccess)
        btnCancel = findViewById(R.id.btnCalCancel)
        autocompleteClient = findViewById(R.id.idautocompleteclientcal)

        qrMacCode = sessionHandler.getMacFromSession(this).toString()
        userSession = sessionHandler.getUserFromSession(this)

        getClients()

        if (validatePermission("FINE", PERMISSION_FINE_LOCATION)) {
            process(qrMacCode)
        }
        btnSuccess.setOnClickListener {
            register()
        }
        btnCancel.setOnClickListener {
            val i = Intent(this, HomeActivity::class.java)
            startActivity(i)
        }
    }

    fun getPermissionOfType(type: String): String {
        var permission: String = ""
        when(type) {
            "COARSE" -> permission = Manifest.permission.ACCESS_COARSE_LOCATION
            "FINE" -> permission = Manifest.permission.ACCESS_FINE_LOCATION
        }
        return permission
    }

    fun validatePermission(type: String, code: Int): Boolean {
        var permission = getPermissionOfType(type)
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), code)
            return false
        } else {
            return true
        }
    }

    override fun onRequestPermissionsResult(code: Int, permissions: Array<String?>,grantResults: IntArray) {
        super.onRequestPermissionsResult(code, permissions, grantResults)
        var result = true
        Log.e("Solicitados:", "permisos")
        if (code == PERMISSION_COARSE_LOCATION) {
            Log.e("COARSE", "$code")
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                validatePermission("FINE", PERMISSION_FINE_LOCATION)
                return
            }
        } else if (code == PERMISSION_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                process(qrMacCode)
                return
            }
        } else {
            Toast.makeText(this, "Error en permisos", Toast.LENGTH_SHORT).show()
            Log.e("Error:", "error en permisos")
            return
        }
    }

    private fun process(qrMacCode: String) {
        Log.e("process", "process")
        try {
            var mtCentralManager: MTCentralManager = MTCentralManager.getInstance(this)
            mtCentralManager.startService()
            mtCentralManager.setBluetoothChangedListener {}
            mtCentralManager.startScan()
            mtCentralManager.setMTCentralManagerListener { peripherals ->
                Log.e("peripheral", "Device found")
                Log.e("mac", "${qrMacCode}")
                for (mtPeripheral in peripherals) {
                    val mtFrameHandler = mtPeripheral.mMTFrameHandler
                    val mac = mtFrameHandler.mac.replace(":","")
                    val name = mtFrameHandler.name
                    val advFrames = mtFrameHandler.advFrames
                    if (mac == qrMacCode) {
                        for (minewFrame in advFrames) {
                            when (minewFrame.frameType) {
                                FrameType.FrameHTSensor -> {
                                    val htFrame = minewFrame as HTFrame
                                    temperature = String.format("%.1f", htFrame.temperature)
                                    if (htFrame.temperature > 3.0 ||
                                        htFrame.temperature < -3.0) {
                                        findViewById<ImageView>(R.id.imgCalc).setImageResource(R.drawable.failed)
                                        txtCal.setTextColor(resources.getColor(R.color.failedColor))
                                    } else {
                                        findViewById<ImageView>(R.id.imgCalc).setImageResource(R.drawable.calibration)
                                        txtCal.setTextColor(resources.getColor(R.color.colorPrimary))
                                    }
                                    txtCal.setText("$temperature°C")
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            // txtData.setText("Error: ${e.message}")
        }
    }

    fun register() {
        var dateUtils = DateUtils()
        val formatted = dateUtils.getCurrentUTCDateTimeInISOFormat()
        val local = LocalDateTime()
        val temperatureTS =  Timestamp(local.toDateTime().millis);
        var objClient = clients[positionSelected]
        val objCalibration = Calibration(value = "${temperature}", date = "${temperatureTS}", iduser = "${userSession?.id}", idclient = "${objClient.id}")
        btnSuccess.isEnabled = false
        btnCancel.isEnabled = false
        calibrationProvider.register(objCalibration)?.enqueue(object: Callback<Calibration> {
            override fun onResponse(call: Call<Calibration>, response: Response<Calibration>) {
                Toast.makeText(this@CalibrationActivity, "Se registró la calibración correctamente.", Toast.LENGTH_LONG).show()
                Log.e("response: ", "${response}")
                Log.e("response body: ", "${response.body()}")
                val i = Intent(this@CalibrationActivity, HomeActivity::class.java)
                startActivity(i)
            }

            override fun onFailure(call: Call<Calibration>, t: Throwable) {
                Log.e("On Failure:", "Se produjo un error ${t.message}")
                Toast.makeText(this@CalibrationActivity, "Se produjo un error ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getClients() {
        clientProvider.getClientsByUser(userSession?.id.toString())?.enqueue(object: Callback<ArrayList<Client>> {
            override fun onResponse(
                call: Call<ArrayList<Client>>,
                response: Response<ArrayList<Client>>
            ) {
                if (response.body() != null) {
                    clients = response.body()!!

                    val clientOptions = Array<String?>(clients.size){null}
                    for (i in 0 until clients.size) {
                        clientOptions[i] = clients[i].name
                    }
                    val adapter = ArrayAdapter(this@CalibrationActivity,R.layout.list_item,clientOptions)

                    with(autocompleteClient){
                        setAdapter(adapter)
                        onItemClickListener = this@CalibrationActivity
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Client>>, t: Throwable) {
                Toast.makeText(this@CalibrationActivity, t.message, Toast.LENGTH_LONG).show()
                Log.e("Error", "${t.message}")
            }
        })
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position).toString()
        positionSelected = position
        Toast.makeText(this@CalibrationActivity, item, Toast.LENGTH_LONG).show()
    }
}