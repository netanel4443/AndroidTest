package com.e.androidtest.data

import com.e.androidtest.data.pojo.ApiData
import com.e.androidtest.di.scopes.ApplicationScope
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IBreezometerApi {

    //@GET("air-quality/v2/current-conditions?lat={latitude}&lon={longitude}&key=b02281b23ca0469c917010e6135cbc0e")
    @GET("air-quality/v2/current-conditions")
    fun getData(
        @Query("lat") lat: String, @Query("lon") lon: String, @Query("key") key:String="b02281b23ca0469c917010e6135cbc0e"
    ): Single<Response<ApiData>>
}