package com.app.kgs_user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MenuResponse : Serializable {

    @SerializedName("status")
    @Expose
    var status: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: ArrayList<MenuData>? = null
}