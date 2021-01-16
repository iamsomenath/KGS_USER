package com.app.kgs_user.callBacks

import com.app.kgs_user.model.CutWalltetPojo
import com.app.kgs_user.model.PostOrderPojo
import com.app.kgs_user.model.WalletPojo

interface CallBackPlaceOrder {

    fun onOrderSuccess(data: PostOrderPojo)
    fun onWalletSuccess(data: WalletPojo)
    fun onCutWalletSuccess(data: CutWalltetPojo)
    fun onError(message: String)
    fun onNetworkError(message: String)
}