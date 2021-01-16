package com.app.kgs_user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class KitchenData : Serializable {

    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("contact")
    @Expose
    var contact: String? = null
    @SerializedName("email")
    @Expose
    var email: String? = null
    @SerializedName("address")
    @Expose
    var address: String? = null
    @SerializedName("image")
    @Expose
    var image: String? = null
    @SerializedName("distance")
    @Expose
    var distance: String? = null
    @SerializedName("review")
    @Expose
    var review: String? = null
    @SerializedName("review_count")
    @Expose
    var reviewcount: String? = null
}
