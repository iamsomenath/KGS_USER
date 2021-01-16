package com.app.kgs_user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class ItemDetailsResponse : Serializable {

    @SerializedName("product_name")
    @Expose
    var productName: String? = null
    @SerializedName("qty")
    @Expose
    var qty: String? = null
    @SerializedName("total_price")
    @Expose
    var totalPrice: String? = null
    @SerializedName("price")
    @Expose
    var price: String? = null
    @SerializedName("product_desc")
    @Expose
    var productDesc: String? = null
    @SerializedName("cat_id")
    @Expose
    var catId: String? = null
    @SerializedName("rest_grocery")
    @Expose
    var restGrocery: String? = null
}
