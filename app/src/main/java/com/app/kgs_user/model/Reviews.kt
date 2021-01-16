package com.app.kgs_user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Reviews : Serializable {

    @SerializedName("comment")
    @Expose
    var comment: String? = null

    @SerializedName("rating")
    @Expose
    var rating: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null
}