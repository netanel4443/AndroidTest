package com.e.androidtest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.e.androidtest.databinding.ActivityMainBinding
import com.e.androidtest.di.components.ApplicationComponent
import com.e.androidtest.ui.activities.BaseActivity
import com.e.androidtest.ui.service.BreezometerService
import com.e.androidtest.ui.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import javax.inject.Inject


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by lazy(this::getViewModel)
    lateinit var appComponent: ApplicationComponent
    private var launcher: ActivityResultLauncher<Array<String>> = registerForActivityResult()

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private val REQUIRED_PERMISSIONS =
        mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).toTypedArray()


    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent =
            (application as BaseApplication).appComponent
        appComponent.inject(this)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        launcher.launch(REQUIRED_PERMISSIONS)

        initUi()
        initStateObserver()

        if (allPermissionsGranted()){
            startService()
        }


    }

    private fun initUi() {
        binding.getDataButton.setOnClickListener {
            if (allPermissionsGranted())
                viewModel.getLocation()
            else {
                launcher.launch(REQUIRED_PERMISSIONS)
            }
        }
        initSpeedometer()
    }

    private fun initSpeedometer() {

        binding.speedometer.maxSpeed = 300.0
        binding.speedometer.majorTickStep = 30.0
        binding.speedometer.minorTicks = 2

        binding.speedometer.addColoredRange(30.0, 140.0, Color.GREEN)
        binding.speedometer.addColoredRange(140.0, 180.0, Color.YELLOW)
        binding.speedometer.addColoredRange(180.0, 400.0, Color.RED)


    }

    private fun initStateObserver() {
        viewModel.viewState.observe(this) { state ->
            val currentState = state.currentState
            val prevState = state.prevState

            if (currentState != prevState) {
                binding.airQuality.text = currentState.category
                binding.airQuality.setBackgroundColor(currentState.color.toColorInt())
                binding.aqi.text = currentState.aqi

            }
        }
    }


    private fun registerForActivityResult(): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val granted = allPermissionsGranted()
            if (granted) {
                startService()
                viewModel.getLocation()
            } else {
                Toast.makeText(
                    this,
                    "need to grant access in order to use this feature",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startService(){
        startService( Intent(this,BreezometerService::class.java))
    }


}