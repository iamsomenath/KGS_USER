package com.app.kgs_user.otp_mvp

import android.app.Activity


class OTPPresenter internal constructor(private var otpVIew: OtpView?, private val signupInterceptor: OTPInterceptor) :

    OTPInterceptor.OnOtpFinishedListener {

    fun resendOTP(userId: String, activity: Activity) {
        if (otpVIew != null) {
            otpVIew!!.showProgress()
        }
        signupInterceptor.validate(userId, this, activity)
    }

    fun updateUserStatus(userId: String, activity: Activity) {
        if (otpVIew != null) {
            otpVIew!!.showProgress()
        }
        signupInterceptor.update_user_status(userId, this, activity)
    }

    override fun onSuccess(response: String) {
        if (otpVIew != null) {
            otpVIew!!.hideProgress()
            otpVIew!!.getReSendOTP(response)
        }
    }

    override fun onOTPFailure(errResponse: String) {
        if (otpVIew != null) {
            otpVIew!!.hideProgress()
            otpVIew!!.setOTPError(errResponse)
        }
    }

    override fun onSuccessValidateOTP(response: String) {
        if (otpVIew != null) {
            otpVIew!!.hideProgress()
            otpVIew!!.navigateToWelcome(response)
        }
    }

    override fun onOTPValidationFailure(errResponse: String) {
        if (otpVIew != null) {
            otpVIew!!.hideProgress()
            otpVIew!!.setOTPValidateError(errResponse)
        }
    }
}
