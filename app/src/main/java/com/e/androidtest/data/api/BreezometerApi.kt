package com.e.androidtest.data.api

import com.e.androidtest.data.IBreezometerApi
import com.e.androidtest.data.pojo.ApiData
import com.e.androidtest.di.scopes.ApplicationScope
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import javax.inject.Inject

@ApplicationScope
class BreezometerApi @Inject constructor(
    private val iBreezometerApi: IBreezometerApi
) {
    fun getBreezometerData(lat:String, lon:String): Single<Response<ApiData>> {

        return iBreezometerApi.getData(lat,lon)
    }

}