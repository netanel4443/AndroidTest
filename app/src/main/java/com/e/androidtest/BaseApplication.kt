package com.e.androidtest

import android.app.Application
import com.e.androidtest.di.components.DaggerApplicationComponent

class BaseApplication: Application() {
    val appComponent = DaggerApplicationComponent.builder().application(this).build()

}