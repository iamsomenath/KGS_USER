package com.app.kgs_user.otp_mvp

import android.app.Activity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.app.kgs_user.utils.Constants
import com.app.kgs_user.utils.RequestQueueSingleton
import java.util.*


internal class OTPInterceptor {

    internal interface OnOtpFinishedListener {
        fun onSuccess(response: String)
        fun onOTPFailure(errResponse: String)

        fun onSuccessValidateOTP(response: String)
        fun onOTPValidationFailure(errResponse: String)
    }

    fun validate(
        userId: String,
        otpPresenter: OTPPresenter,
        activity: Activity
    ) {

        val requestQueue: RequestQueue = RequestQueueSingleton.getInstance(activity).requestQueue
        val REQ_TAG = "OTP"

        val url = Constants.ROOT_URL + "resend_otp"
        val jsonObjectRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->

            otpPresenter.onSuccess(response.toString())

        }, Response.ErrorListener { error ->
            var errorCode = 0
            when (error) {
                is TimeoutError -> errorCode = -7
                is NoConnectionError -> errorCode = -1
                is AuthFailureError -> errorCode = -6
                is ServerError -> errorCode = -5
                is NetworkError -> errorCode = -2
                is ParseError -> errorCode = -8
            }
            otpPresenter.onOTPFailure(errorCode.toString() + "")
        }) {
            /*@Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }*/
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = userId
                return params
            }
        }

        jsonObjectRequest.tag = REQ_TAG
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            30000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(jsonObjectRequest)
    }

    fun update_user_status(
        user_id: String,
        otpPresenter: OTPPresenter,
        activity: Activity
    ) {

        val requestQueue: RequestQueue = RequestQueueSingleton.getInstance(activity).requestQueue
        val REQ_TAG = "UPDATE_STATUS"

        val url = Constants.ROOT_URL + "update_user"
        val jsonObjectRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->

            otpPresenter.onSuccessValidateOTP(response.toString())

        }, Response.ErrorListener { error ->
            var errorCode = 0
            when (error) {
                is TimeoutError -> errorCode = -7
                is NoConnectionError -> errorCode = -1
                is AuthFailureError -> errorCode = -6
                is ServerError -> errorCode = -5
                is NetworkError -> errorCode = -2
                is ParseError -> errorCode = -8
            }
            otpPresenter.onOTPValidationFailure(errorCode.toString() + "")
        }) {
            /*@Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }*/

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = user_id
                return params
            }
        }

        jsonObjectRequest.tag = REQ_TAG
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            30000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(jsonObjectRequest)
    }
}