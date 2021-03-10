package com.example.glus

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*

class Country{
    @SerializedName("name")
    @Expose
    var name:String?=null

    @SerializedName("capital")
    @Expose
    var capital:String?=null

    @SerializedName("region")
    @Expose
    var region:String?=null

    @SerializedName("subregion")
    @Expose
    var subregion:String?=null

    @SerializedName("population")
    @Expose
    var population:Int?=null
}

interface RestCountries{
    @GET("/rest/v2/currency/{currency}")
    fun getCountriesByCurrency(@Path("currency") currency: String): Call<List<Country>>

}






























