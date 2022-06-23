package com.e.androidtest.data.pojo

import com.google.gson.annotations.SerializedName

data class ApiData(
    @SerializedName("data")
    var data: BreezometerData? = null,

    @SerializedName("error")
    var error: Any? = null
)

