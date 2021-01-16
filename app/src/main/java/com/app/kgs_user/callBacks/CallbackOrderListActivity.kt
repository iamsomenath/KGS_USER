package com.app.kgs_user.callBacks

import com.app.kgs_user.model.PostOrderPojo

interface CallbackOrderListActivity {

    fun onSuccess(orderLists: ArrayList<PostOrderPojo>)
    fun onError(message: String)
    fun onNetworkError(message: String)
}
