package com.app.kgs_user.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import com.app.kgs_user.callBacks.CallBackPlaceOrder
import com.app.kgs_user.model.OrderRequestPojo
import com.app.kgs_user.rest.ApiInterface
import com.app.kgs_user.utils.Constants
import com.app.kgs_user.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OrderViewModel : BaseViewModel() {

    var apiInterface: ApiInterface = getAPIInterface()
    private lateinit var callback: CallBackPlaceOrder

    fun setCallback(callback: CallBackPlaceOrder) {
        this.callback = callback
    }

    fun placeOrder(orderDetails: OrderRequestPojo) {
        apiCallPlaceOrder(orderDetails)
    }

    fun cutFomWallet(userId: String, amount : String) {
        apiCutFomWallet(userId, amount)
    }

    fun getWallet(userId: String) {
        apiCallGetWallet(userId)
    }

    @SuppressLint("CheckResult")
    private fun apiCallGetWallet(userId: String) {

        val userResponseObservable = apiInterface.getWallet(userId)
        userResponseObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ restResponse ->
                Log.d("RESPONSE", restResponse.toString())
                if (restResponse.status == 200) {
                    callback.onWalletSuccess(restResponse.data!!)
                } else {
                    callback.onError(restResponse.message!!)
                }
            }, {
                    e -> callback.onNetworkError(Constants.NETWORK_ERROR_MESSAGE)
            })
    }

    @SuppressLint("CheckResult")
    private fun apiCutFomWallet(userId: String, amount : String) {

        val userResponseObservable = apiInterface.cutFomWallet(userId, amount)
        userResponseObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ restResponse ->
                Log.d("RESPONSE", restResponse.toString())
                if (restResponse.status == 200) {
                    callback.onCutWalletSuccess(restResponse.data!!)
                } else {
                    callback.onError(restResponse.message!!)
                }
            }, {
                    e -> callback.onNetworkError(Constants.NETWORK_ERROR_MESSAGE)
            })
    }

    @SuppressLint("CheckResult")
    private fun apiCallPlaceOrder(orderDetails: OrderRequestPojo) {

        val userResponseObservable = apiInterface.placeOrder(
            orderDetails.user_id!!,
            orderDetails.kitchen_id!!,
            orderDetails.menu_id!!,
            orderDetails.payment_status!!,
            orderDetails.payment_via!!,
            orderDetails.extra_note!!,
            orderDetails.pincode!!,
            orderDetails.landmark!!,
            orderDetails.address!!,
            orderDetails.quantity!!,
            orderDetails.price!!
        )
        userResponseObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ restResponse ->
                //Log.d("RESPONSE", restResponse.toString())
                if (restResponse.status == 200) {
                    callback.onOrderSuccess(restResponse.data!!)
                } else {
                    callback.onError(restResponse.message!!)
                }
            }, { e -> callback.onNetworkError(Constants.NETWORK_ERROR_MESSAGE) })
    }
}