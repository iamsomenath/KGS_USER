package com.app.kgs_user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class KitchenRestResponse : Serializable {

    @SerializedName("data")
    @Expose
    val data: ArrayList<KitchenData>? = null
    @SerializedName("status")
    @Expose
    val status: Int? = null
    @SerializedName("message")
    @Expose
    val message: String? = null
}
