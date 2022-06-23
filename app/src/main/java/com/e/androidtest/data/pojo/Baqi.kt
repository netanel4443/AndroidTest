package com.e.androidtest.data.pojo

import com.google.gson.annotations.SerializedName

data class Baqi(

    @SerializedName("aqi_display")
    var aqiDisplay: String? = null,

    @SerializedName("color")
    var color: String? = null,

    @SerializedName("category")
    var category: String? = null,

//    @SerializedName("dominant_pollutant")
//    var dominantPollutant: String? = null
)




