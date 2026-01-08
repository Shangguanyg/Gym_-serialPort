package com.ganainy.PortApplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ganainy.PortApplication.adapter.DeviceAdapter
import com.ganainy.gymmasterscompose.databinding.ActivitySelectSerialPortBinding
import com.ganainy.serialportlibrary.SerialPortFinder
import com.ganainy.gymmasterscompose.R

class SelectSerialPortActivity : AppCompatActivity(), OnItemClickListener {


    private var mDeviceAdapter: DeviceAdapter? = null
    val DEVICE = "device"
    private lateinit var binding: ActivitySelectSerialPortBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_serial_port)

        val serialPortFinder = SerialPortFinder()
        val devices = serialPortFinder.devices
        if (binding.lvDevices != null) {
            binding.lvDevices.emptyView = binding.tvEmpty
            mDeviceAdapter = DeviceAdapter(applicationContext, devices)
            binding.lvDevices.adapter = mDeviceAdapter
            binding.lvDevices.onItemClickListener = this
        }

    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        val device = mDeviceAdapter!!.getItem(position)
        val intent = Intent(this, PortActivity::class.java)
        intent.putExtra(DEVICE, device)
        startActivity(intent)
    }
}