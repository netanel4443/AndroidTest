package com.e.androidtest.data.usecases

import android.annotation.SuppressLint
import android.location.Location
import com.e.androidtest.data.api.BreezometerApi
import com.e.androidtest.data.pojo.Baqi
import com.e.androidtest.di.scopes.ApplicationScope
import com.e.androidtest.utils.subscribeBlock
import com.google.android.gms.location.FusedLocationProviderClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import okhttp3.internal.wait
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@SuppressLint("MissingPermission")
@ApplicationScope
class GpsRepository @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val api: BreezometerApi
) {
    private val compositeDisposable = CompositeDisposable()

    private val _locationSubject: BehaviorSubject<Location> = BehaviorSubject.create()
    val locationSubject get() = _locationSubject

    private val _notificationSubject: BehaviorSubject<String> = BehaviorSubject.create()
    val notificationSubject get() = _notificationSubject

    private val _breezometerData: BehaviorSubject<Baqi> = BehaviorSubject.create()
    val breezometerData get() = _breezometerData

    private var firstLocationPos: Location? = null
    private var currLocationPos: Location? = null

    private var firstAirQuality: String? = null

    @SuppressLint("MissingPermission")
    fun getLocation(): Completable {
        return Completable.fromAction {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    if (firstLocationPos == null) {
                        firstLocationPos = location
                    }
                    currLocationPos = location
                    calculateDistance()
                    _locationSubject.onNext(location)
                    getBreezometerData(it.latitude.toString(), it.latitude.toString())
                }
            }
        }
    }

    fun gpsLocationTimer(): Observable<Long> {
        // the time is 10 sec just for this project
        return Observable.interval(10, TimeUnit.SECONDS)
            .timeInterval()
            .map { it.time() }
    }

    private fun calculateDistance() {
        val distance = firstLocationPos?.distanceTo(currLocationPos)
        if (distance != null) {
            val condition = distance > 800f
            if (condition) {
                firstLocationPos = currLocationPos
                _notificationSubject.onNext("You have passed 800 meters")
            }
        }
    }

    fun getBreezometerData(lat: String, lon: String) {
        compositeDisposable.add(api.getBreezometerData(lat, lon)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock { response ->
                if (response.isSuccessful) {
                    response.body()?.let { body ->

                        val baqi = body.data?.indexes?.baqi

                        baqi?.let { baqi ->
                            if (firstAirQuality == null){ firstAirQuality=baqi.aqiDisplay }
                            _breezometerData.onNext(baqi)
                            checkAirQuality(baqi)
                        }
                    }
                }
            })
    }

    private fun checkAirQuality(baqi: Baqi) {
        val quality = baqi.aqiDisplay!!.toDouble()
        if (quality < 80) {
            _notificationSubject.onNext("quality is less than 80")
        }
        val qualityPercent = (firstAirQuality!!.toInt() * 0.1)
        if (firstAirQuality!!.toDouble()+ qualityPercent < quality){
            _notificationSubject.onNext("Quality has increased by more than 10% ")
            firstAirQuality = baqi.aqiDisplay // new value
        }
        if (firstAirQuality!!.toDouble() - qualityPercent > quality){
            _notificationSubject.onNext("Quality has decreased by more than 10%")
            firstAirQuality = baqi.aqiDisplay // new value
        }
    }

    fun onDestroy() {
        compositeDisposable.clear()
    }
}