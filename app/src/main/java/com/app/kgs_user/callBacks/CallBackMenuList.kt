package com.app.kgs_user.callBacks

import com.app.kgs_user.model.MenuData

interface CallBackMenuList {

    fun onSuccess(getMenus: String)
    fun onError(message: String)
    fun onNetworkError(message: String)
}