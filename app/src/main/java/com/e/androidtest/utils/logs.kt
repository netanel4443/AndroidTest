package com.e.androidtest.utils

import android.util.Log

const val DBG=true

fun printErrorIfDbg(e:Throwable){
    if (DBG){
        Log.e(e.javaClass.name,e.message.toString())
    }
}


