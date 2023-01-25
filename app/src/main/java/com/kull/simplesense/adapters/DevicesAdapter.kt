package com.kull.simplesense.adapters

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kull.simplesense.HomeActivity
import com.kull.simplesense.R
import com.kull.simplesense.models.Device
import com.kull.simplesense.utils.SharedPref

class DevicesAdapter(val context: Activity, val devices: ArrayList<Device>): RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_devices, parent, false)
        return DevicesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: DevicesViewHolder, position: Int) {
        val device = devices[position]
        holder.txtName.text = device.name
        holder.txtMac.text = "MAC: ${device.mac}"
        holder.txtLastUpdate.text = "Última actualización: ${device.lastupdate}"
        holder.txtBattery.text = "Batería: ${device.battery}%"

        holder.itemView.setOnClickListener {
            goToHome(device)
        }
    }

    private fun goToHome(device: Device) {
        val i = Intent(context, HomeActivity::class.java)
        saveMacInSession(device.mac)
        context.startActivity(i)
    }

    private fun saveMacInSession(mac: String) {
        val sharedPref = SharedPref(context)
        sharedPref.saveString("mac", mac)
    }

    class DevicesViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtName: TextView
        val txtMac: TextView
        val txtLastUpdate: TextView
        val txtBattery: TextView

        init {
            txtName = view.findViewById(R.id.device_name)
            txtMac = view.findViewById(R.id.device_mac)
            txtLastUpdate = view.findViewById(R.id.device_lastupdate)
            txtBattery = view.findViewById(R.id.device_battery)
        }
    }
}