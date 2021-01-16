package com.app.kgs_user.otp_mvp

interface OtpView {
    fun showProgress()
    fun hideProgress()
    fun setOTPError(errResponse: String)
    fun getReSendOTP(response: String)
    fun setOTPValidateError(errResponse: String)
    fun navigateToWelcome(response: String)
}