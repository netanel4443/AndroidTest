package com.e.androidtest.data.pojo

import com.google.gson.annotations.SerializedName


data class BreezometerData(
    @SerializedName("indexes")
    var indexes: Indexes? = null
)


