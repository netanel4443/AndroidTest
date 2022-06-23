package com.e.androidtest.ui.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.e.androidtest.BaseApplication
import com.e.androidtest.data.usecases.GpsRepository
import com.e.androidtest.di.components.ApplicationComponent
import com.e.androidtest.ui.notifications.DistanceNotification
import com.e.androidtest.utils.subscribeBlock
import com.google.android.gms.location.FusedLocationProviderClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class BreezometerService : Service() {

    private val compositeDisposable = CompositeDisposable()

    lateinit var appComponent: ApplicationComponent

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var gpsRepository: GpsRepository


    private val notification: DistanceNotification = DistanceNotification()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        appComponent =
            (application as BaseApplication).appComponent
        appComponent.inject(this)
        notification.notificate(this)

        getLocationFromSubject()
        getDistanceFromSubject()
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    fun getLocationFromSubject() {
        gpsRepository.gpsLocationTimer()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock {
               gpsRepository.getLocation()
            }
    }

    fun getDistanceFromSubject(){
        gpsRepository.distanceSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock {
                notification.updateNotification(this,it)
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}