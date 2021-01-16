package com.app.kgs_user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UpdateItem : Serializable {
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("email")
    @Expose
    var email: String? = null
    @SerializedName("contact")
    @Expose
    var contact: String? = null
}