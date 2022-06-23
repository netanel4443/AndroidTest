package com.e.androidtest.di.components

import android.app.Application
import com.e.androidtest.MainActivity
import com.e.androidtest.di.modules.AppModule
import com.e.androidtest.di.modules.MainVmModule
import com.e.androidtest.di.scopes.ApplicationScope
import com.e.androidtest.ui.service.BreezometerService
import dagger.BindsInstance
import dagger.Component

@Component(modules = [AppModule::class,
MainVmModule::class])
@ApplicationScope
interface ApplicationComponent {


    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): ApplicationComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(breezometerService: BreezometerService)

}