package com.app.kgs_user.callBacks

import com.app.kgs_user.model.DataItemResetPass

interface CallbackResetPassword {
    fun onResetPasswordSuccess(data: ArrayList<DataItemResetPass>)
    fun onResetPasswordError(message: String)
    fun onNetworkError(message: String)
}