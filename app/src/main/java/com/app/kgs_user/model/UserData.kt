package com.app.kgs_user.model

class UserData {

    var userMobile: String? = null
    var userPhone: String? = null
    var name: String? = null
    var dev_key: String? = null
    var otp: String? = null
    var user_nm: String? = null
    var user_eml: String? = null
    var user_id: String? = null
    var rest_id: String? = null
    var food_catid: String? = null
    var catId: String? = null
    var cartId: String? = null
    private var pId: String? = null
    var quantity: String? = null
    var orderType: String? = null
    var price: Int? = null

    fun getpId(): String? {
        return pId
    }

    fun setpId(pId: String) {
        this.pId = pId
    }
}
