package com.e.androidtest.ui.viewmodel

import com.e.androidtest.data.api.BreezometerApi
import com.e.androidtest.data.usecases.GpsRepository
import com.e.androidtest.di.scopes.ApplicationScope
import com.e.androidtest.ui.viewmodel.state.MainViewState
import com.e.androidtest.ui.utils.MviMutableLiveData
import com.e.androidtest.ui.utils.livedata.MviLiveData
import com.e.androidtest.utils.printErrorIfDbg
import com.e.androidtest.utils.subscribeBlock
import com.e.security.ui.utils.PrevAndCurrentState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@ApplicationScope
class MainViewModel @Inject constructor(
    private val api:BreezometerApi,
    private val gpsRepository: GpsRepository
):BaseViewModel() {

    private val _viewState = MviMutableLiveData(MainViewState())
    val viewState: MviLiveData<PrevAndCurrentState<MainViewState>> get() = _viewState

    init {
        getLocationFromSubject()
    }

    fun getBreezometerData(lat:String,lon:String) {
        api.getBreezometerData(lat,lon)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock{ response ->

                if (response.isSuccessful ){
                    response.body()?.let { body->

                        val baqi=body.data?.indexes?.baqi
                        println("body ${baqi}")
                        baqi?.let { baqi->
                            _viewState.mviValue {
                                it.copy(
                                    aqi = baqi.aqiDisplay!!,
                                    color = baqi.color!!,
                                    category = baqi.category!!
                                )
                            }
                        }
                    }
                }
            }.addDisposable()
    }

    fun getLocation() {
        gpsRepository.getLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock {  }
            .addDisposable()
    }

  private  fun getLocationFromSubject(){
        gpsRepository.locationSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock { location ->

            location?.let {
                getBreezometerData(it.latitude.toString(), it.longitude.toString())
            }

            }.addDisposable()
    }

}