package com.kull.simplesense

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kull.simplesense.adapters.DevicesAdapter
import com.kull.simplesense.models.Device
import com.kull.simplesense.models.User
import com.kull.simplesense.utils.SharedPref
import com.minew.beaconplus.sdk.MTCentralManager
import com.minew.beaconplus.sdk.MTPeripheral
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class DeviceActivity : AppCompatActivity() {
    private val PERMISSION_COARSE_LOCATION = 2
    private val PERMISSION_FINE_LOCATION = 4
    val qrMacCode = ""
    lateinit var btnSearch: Button
    lateinit var btnBack: ImageButton
    lateinit var btnHome: ImageButton
    lateinit var btnLogout: ImageButton
    var recyclerDevices: RecyclerView? = null
    var adapter: DevicesAdapter? = null
    var devices = arrayListOf<Device>()
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)
        btnSearch = findViewById(R.id.btnDevSearch)
        btnBack = findViewById(R.id.btnDevBack)
        btnHome = findViewById(R.id.btnDevHome)
        btnLogout = findViewById(R.id.btnDevLogout)
        recyclerDevices = findViewById(R.id.reciclerDevices)
        recyclerDevices?.layoutManager = LinearLayoutManager(this)
        progressBar = findViewById(R.id.progressBar)

        if (validatePermission("FINE", PERMISSION_FINE_LOCATION)) {
            process()
        }

        btnSearch.setOnClickListener {
            process()
        }

        btnBack.setOnClickListener {
            val i = Intent(this, com.kull.simplesense.MainActivity::class.java)
            startActivity(i)
        }
        btnHome.setOnClickListener {
            /*
            val i = Intent(this, com.kull.simplesense.HomeActivity::class.java)
            startActivity(i)
             */
        }
        btnLogout.setOnClickListener {
            val i = Intent(this, com.kull.simplesense.MainActivity::class.java)
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
                process()
                return
            }
        } else {
            Toast.makeText(this, "Error en permisos", Toast.LENGTH_SHORT).show()
            Log.e("Error:", "error en permisos")
            return
        }
    }

    private fun process() {
        Log.e("process", "process")
        devices.clear()
        recyclerDevices?.adapter = null
        progressBar.visibility = View.VISIBLE
        try {
            var mtCentralManager: MTCentralManager = MTCentralManager.getInstance(this)
            mtCentralManager.startService()
            mtCentralManager.setBluetoothChangedListener {}
            mtCentralManager.startScan()
            var detectedPeripherals = ArrayList<MTPeripheral>()
            mtCentralManager.setMTCentralManagerListener { peripherals ->
                Log.e("peripheral", "Device found ${detectedPeripherals.size}")
                detectedPeripherals.add(peripherals[0])
            }
            Timer().schedule(TimeUnit.SECONDS.toMillis(20)) {
                mtCentralManager.stopScan()
                setAdapterDevices(detectedPeripherals)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            // txtData.setText("Error: ${e.message}")
        }
    }

    fun setAdapterDevices(detectedPeripherals: ArrayList<MTPeripheral>) {
        runOnUiThread {
            if (detectedPeripherals.size > 0) {
                progressBar.visibility = View.GONE
                val dateFormat = SimpleDateFormat("yyyy/MM/dd")
                for (mtPeripheral in detectedPeripherals.distinct()) {
                    val mtFrameHandler = mtPeripheral.mMTFrameHandler
                    val formatted = dateFormat.format(Date(mtFrameHandler.lastUpdate))
                    var objDevice = Device(name = "${mtFrameHandler.name}",
                        mac = mtFrameHandler.mac,
                        lastupdate = "${formatted}",
                        battery = "${mtFrameHandler.battery}")
                    devices.add(objDevice)
                    Log.e("SAVE-MAC", "${objDevice.mac}")
                }
                adapter = DevicesAdapter(this@DeviceActivity, devices!!)
                recyclerDevices?.adapter = adapter
            } else {
                Toast.makeText(this@DeviceActivity, "No se encontraron dispositivos", Toast.LENGTH_LONG).show()
            }
        }
    }
}