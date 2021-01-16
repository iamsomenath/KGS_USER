package com.app.kgs_user.callBacks

import com.app.kgs_user.model.UpdateItem
import java.util.*

interface CallbackUpdateProfile {
    fun onSuccess(data: ArrayList<UpdateItem>)
    fun onError(message: String)
    fun onNetworkError(message: String)
}
