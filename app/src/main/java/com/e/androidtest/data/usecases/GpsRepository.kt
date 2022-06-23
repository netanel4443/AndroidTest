package com.e.androidtest.data.usecases

import android.annotation.SuppressLint
import android.location.Location
import com.e.androidtest.di.scopes.ApplicationScope
import com.google.android.gms.location.FusedLocationProviderClient
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@SuppressLint("MissingPermission")
@ApplicationScope
class GpsRepository @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient
) {

    private val _locationSubject: BehaviorSubject<Location> = BehaviorSubject.create()
    val locationSubject get() = _locationSubject

    private val _distanceSubject: BehaviorSubject<String> = BehaviorSubject.create()
    val distanceSubject get() = _distanceSubject

    private var firstLocationPos: Location? = null
    private var currLocationPos: Location? = null


    init {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (firstLocationPos == null) {
                firstLocationPos = location
            }
            currLocationPos = location
            calculateDistance()
            _locationSubject.onNext(location)
        }
    }


    @SuppressLint("MissingPermission")
    fun getLocation(): Completable {
        return Completable.fromAction {
            fusedLocationClient.lastLocation
        }
    }

    fun gpsLocationTimer(): Observable<Long> {
        return Observable.interval(3, TimeUnit.SECONDS)
            .timeInterval()
            .map { it.time() }


    }

    private fun calculateDistance() {
        val distance = firstLocationPos?.distanceTo(currLocationPos)
        if (distance != null) {
            val condition = distance > 800f
            if (condition) {
                firstLocationPos = currLocationPos
                _distanceSubject.onNext("You have passed 800 meters")
            }


        }


    }
}