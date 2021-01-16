package com.app.kgs_user.signup_mvp

interface SignupVIew {
    fun showProgress()
    fun hideProgress()
    fun setSignUpError(errResponse: String)
    fun navigateToOTP(response: String)
}