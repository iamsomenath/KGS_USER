package com.app.kgs_user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PostOrderPojo : Serializable {

    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("user_id")
    @Expose
    var userId: String? = null
    @SerializedName("kitchen_id")
    @Expose
    var kitchenId: String? = null
    @SerializedName("order_id")
    @Expose
    var orderId: String? = null
    @SerializedName("menu_id")
    @Expose
    var menuId: String? = null
    @SerializedName("quantity")
    @Expose
    var quantity: String? = null
    @SerializedName("payment_status")
    @Expose
    var paymentStatus: String? = null
    @SerializedName("payment_via")
    @Expose
    var paymentVia: String? = null
    @SerializedName("order_status")
    @Expose
    var orderStatus: String? = null
    @SerializedName("order_date")
    @Expose
    var orderDate: String? = null
    @SerializedName("order_time")
    @Expose
    var orderTime: String? = null
    @SerializedName("extra_note")
    @Expose
    var extraNote: String? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("image")
    @Expose
    var image: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("price")
    @Expose
    var price: String? = null
    @SerializedName("category_name")
    @Expose
    var category_name: String? = null
    @SerializedName("mode")
    @Expose
    var mode: String? = null
    @SerializedName("address")
    @Expose
    var address: String? = null
    @SerializedName("landmark")
    @Expose
    var landmark: String? = null
    @SerializedName("pincode")
    @Expose
    var pincode: String? = null
    @SerializedName("rating")
    @Expose
    var rating: String? = null
}