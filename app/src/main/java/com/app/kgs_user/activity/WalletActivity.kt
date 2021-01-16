package com.app.kgs_user.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.app.kgs_user.R
import com.app.kgs_user.callBacks.CallBackPlaceOrder
import com.app.kgs_user.model.CutWalltetPojo
import com.app.kgs_user.model.PostOrderPojo
import com.app.kgs_user.model.WalletPojo
import com.app.kgs_user.utils.*
import com.app.kgs_user.viewmodel.OrderViewModel
import kotlinx.android.synthetic.main.activity_wallet.*


class WalletActivity : AppCompatActivity() , CallBackPlaceOrder {

    lateinit var viewModel: OrderViewModel
    lateinit var networkChangeReceiver: NetworkChangeReceiver
    private var network: Boolean? = null
    private var walletBalance = 0
    private var amountStr = ""

    lateinit var loadingDialog: LoadingDialog
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_wallet)

        viewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        viewModel.setCallback(this)

        sessionManager = SessionManager(this)
        loadingDialog = LoadingDialog(this)

        networkChangeReceiver = NetworkChangeReceiver(this)
        network = networkChangeReceiver.isNetworkAvailable


        iv_back.setOnClickListener {
            overridePendingTransition(
                R.anim.right_in,
                R.anim.left_out
            )
            finish()
        }

        if (network!!) {
            loadingDialog.showDialog()
            viewModel.getWallet(sessionManager.getId!!)
        } else {
            mainContent.snackbar(getString(R.string.error_message))
        }

        btn_submit_wallet_add_money.setOnClickListener {
            if (checkValidation()) {
                if (network!!) {

                } else {
                    mainContent.snackbar(getString(R.string.error_message))
                }
            }
        }

        one.setOnClickListener {
            amount.setText("50")
            amount.setSelection(2)
        }
        two.setOnClickListener {
            amount.setText("100")
            amount.setSelection(3)
        }
        three.setOnClickListener {
            amount.setText("200")
            amount.setSelection(3)
        }
        four.setOnClickListener {
            amount.setText("500")
            amount.setSelection(3)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onWalletSuccess(data: WalletPojo) {
        loadingDialog.hideDialog()
        try {
            walletBalance = data.wallet!!.toInt()
            //toast(walletBalance.toString())
            wallet.text = "WALLET BALANCE : ₹$walletBalance"
        } catch (e: Exception) {
            toast("Something went wrong.......")
        }
    }

    override fun onOrderSuccess(data: PostOrderPojo) {
        //NR
    }

    override fun onCutWalletSuccess(data: CutWalltetPojo) {
        //NR
    }

    override fun onError(message: String) {
        loadingDialog.hideDialog()
        toast(message)
    }

    override fun onNetworkError(message: String) {
        loadingDialog.hideDialog()
        toast(message)
    }

    private fun checkValidation(): Boolean {

        amountStr = amount.text.toString().trim()
        when {
            amountStr.isEmpty() -> {
                mainContent.snackbar("Please Enter Amount")
            }
            Integer.parseInt(amountStr) < 1 -> {
                mainContent.snackbar("Please Enter Valid Amount")
            }
            else -> return true
        }
        return false
    }

    override fun onBackPressed() {
        overridePendingTransition(
            R.anim.right_in,
            R.anim.left_out
        )
        finish()
    }
}
