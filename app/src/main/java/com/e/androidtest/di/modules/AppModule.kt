package com.e.androidtest.di.modules

import android.app.Application
import com.e.androidtest.data.IBreezometerApi
import com.e.androidtest.di.scopes.ApplicationScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
object AppModule {

    @ApplicationScope
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.breezometer.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    @ApplicationScope
    @Provides
    fun provideBreezometerApi(retrofit: Retrofit): IBreezometerApi {
        return retrofit.create(IBreezometerApi::class.java)
    }

    @ApplicationScope
    @Provides
    fun fusedLocationClient(application: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(application)
    }
}