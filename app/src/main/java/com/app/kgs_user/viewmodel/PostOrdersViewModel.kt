package com.app.kgs_user.viewmodel

import android.annotation.SuppressLint
import com.app.kgs_user.callBacks.CallBackMenuList
import com.app.kgs_user.callBacks.CallbackOrderListActivity
import com.app.kgs_user.rest.ApiInterface
import com.app.kgs_user.utils.Constants
import com.app.kgs_user.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PostOrdersViewModel : BaseViewModel() {

    var apiInterface: ApiInterface = getAPIInterface()
    private lateinit var callback: CallbackOrderListActivity

    fun setCallback(callback: CallbackOrderListActivity) {
        this.callback = callback
    }

    fun getOrderList(user_id: String) {
        apiCallOrderList(user_id)
    }

    @SuppressLint("CheckResult")
    private fun apiCallOrderList(user_id: String) {

        val userResponseObservable = apiInterface.todaysOrder(user_id)
        userResponseObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ restResponse ->
                //Log.d("RESPONSE", restResponse.toString())
                if (restResponse.status == 200) {
                    callback.onSuccess(restResponse.data!!)
                } else {
                    callback.onError(restResponse.message!!)
                }
            }, { e -> callback.onNetworkError(Constants.NETWORK_ERROR_MESSAGE) })
    }
}