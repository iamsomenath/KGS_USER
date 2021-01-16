package com.app.kgs_user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MenuData : Serializable {

    @SerializedName("category_id")
    @Expose
    var category_id: String? = null
    @SerializedName("kitchen_id")
    @Expose
    var kitchen_id: String? = null
    @SerializedName("category_name")
    @Expose
    var category_name: String? = null
    @SerializedName("category_price")
    @Expose
    var category_price: String? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("image")
    @Expose
    var image: String? = null
    @SerializedName("menu_date")
    @Expose
    var menu_date: String? = null
    @SerializedName("menu_time")
    @Expose
    var menu_time: String? = null
    @SerializedName("mode")
    @Expose
    var mode: String? = null
    @SerializedName("timing")
    @Expose
    var timing: String? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
}