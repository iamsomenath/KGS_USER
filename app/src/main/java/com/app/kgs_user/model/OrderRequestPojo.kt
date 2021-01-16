package com.app.kgs_user.model

import java.io.Serializable

class OrderRequestPojo : Serializable {

    var user_id : String? = null
    var kitchen_id : String? = null
    var menu_id : String? = null
    var payment_status : String? = null
    var payment_via : String? = null
    var extra_note : String? = null
    var pincode : String? = null
    var landmark : String? = null
    var address : String? = null
    var quantity : String? = null
    var price : String? = null
}