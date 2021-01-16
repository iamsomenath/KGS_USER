package com.app.kgs_user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CutWalltetPojo : Serializable {

    @SerializedName("user_id")
    @Expose
    var userId: String? = null
    @SerializedName("amount")
    @Expose
    var amount: String? = null
}