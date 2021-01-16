package com.app.kgs_user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResetResponse : Serializable {
    @SerializedName("status")
    @Expose
    var status: Int = 0
    @SerializedName("server_message")
    @Expose
    var serverMessage: String? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: ArrayList<DataItemResetPass>? = null
}