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
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class BreezometerService : Service() {

    private val compositeDisposable = CompositeDisposable()

    lateinit var appComponent: ApplicationComponent

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var gpsRepository: GpsRepository


    private var notification: DistanceNotification? = DistanceNotification()


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        appComponent =
            (application as BaseApplication).appComponent
        appComponent.inject(this)

        notification?.notificate(this)

        getLocationFromSubject()
        getDistanceFromSubject()
        getBreezometerDataFromSubject()
    }

    private fun getBreezometerDataFromSubject() {
        compositeDisposable.add(gpsRepository.breezometerData
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock { baqi ->

            })
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    fun getLocationFromSubject() {
        compositeDisposable.add(gpsRepository.gpsLocationTimer()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock {
                getLocation()
            })
    }

    fun getLocation(){
        compositeDisposable.add(gpsRepository.getLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock {  })
    }

    fun getDistanceFromSubject(){
        compositeDisposable.add(gpsRepository.notificationSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock {
                notification?.updateNotification(this,it)
            })
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        notification = null
        gpsRepository.onDestroy()
    }
}