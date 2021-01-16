package com.app.kgs_user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import cn.pedant.SweetAlert.SweetAlertDialog
import com.app.kgs_user.R
import com.app.kgs_user.callBacks.CallBackPlaceOrder
import com.app.kgs_user.model.*
import com.app.kgs_user.utils.*
import com.app.kgs_user.viewmodel.OrderViewModel
import kotlinx.android.synthetic.main.activity_confirm_order.*
import kotlinx.android.synthetic.main.layout_address_toolbar.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ConfirmOrderActivity : AppCompatActivity(), CallBackPlaceOrder {

    lateinit var loadingDialog: LoadingDialog
    lateinit var sessionManager: SessionManager
    lateinit var menuData: MenuData
    internal var spQuantityStr = "1"
    private var countList = Array(10) { i -> (i + 1).toString() }
    private var total = 0
    private var payment = ""
    private var paymentStatus = ""
    private var walletBalance = 0
    lateinit var orderPojo : OrderRequestPojo

    lateinit var viewModel: OrderViewModel
    lateinit var networkChangeReceiver: NetworkChangeReceiver
    private var network: Boolean? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_order)

        viewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        viewModel.setCallback(this)


        networkChangeReceiver = NetworkChangeReceiver(this)
        network = networkChangeReceiver.isNetworkAvailable

        menuData = intent.getSerializableExtra("DATA") as MenuData
        //Log.d("TEST", menuData.category_id!!)
        sessionManager = SessionManager(this)
        loadingDialog = LoadingDialog(this)

        tv_address.text = sessionManager.address
        itemName.text = menuData.category_name + " (" + menuData.mode + ")"
        tv_Price.text = " Price : ₹" + menuData.category_price
        total = spQuantityStr.toInt() * menuData.category_price!!.toInt()
        tv_totalPrice.text =
            "Total Payable Amount : " + spQuantityStr + " X ₹" + menuData.category_price + " = ₹" + total

        if (sessionManager.getLocalAddress!!.isNotEmpty())
            details_address.text =
                sessionManager.getLocalAddress + ", " + sessionManager.getLocation + ", " + sessionManager.getPincode

        iv_back.setOnClickListener {
            overridePendingTransition(
                R.anim.right_in,
                R.anim.left_out
            )
            finish()
        }

        val adap = ArrayAdapter(this, R.layout.new_spinner_item, countList)
        spQuantity.adapter = adap
        spQuantity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                spQuantityStr = spQuantity.selectedItem.toString()
                total = spQuantityStr.toInt() * menuData.category_price!!.toInt()
                tv_totalPrice.text =
                    "Total Payable Amount : " + spQuantityStr + " X ₹" + menuData.category_price + " = ₹" + total
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        getAddress.setOnClickListener {
            getLocalAddress()
        }

        tvBtn_ConfirmOrder.setOnClickListener {
            gotoPayment()
        }

        if (network!!) {
            loadingDialog.showDialog()
            viewModel.getWallet(sessionManager.getId!!)
        } else {
            mainContent.snackbar(getString(R.string.error_message))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getLocalAddress() {
        val li = LayoutInflater.from(this)
        val promptsView = li.inflate(R.layout.dialog_set_location, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(promptsView)
        val address = promptsView.findViewById(R.id.address) as EditText
        val location = promptsView.findViewById(R.id.location) as EditText
        val pincode = promptsView.findViewById(R.id.pincode) as EditText
        // set dialog message
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton(
                "SAVE"
            ) { dialog, id ->
                if (address.text.isEmpty()) {
                    toast("Please Enter Address")
                    return@setPositiveButton
                }
                if (location.text.isEmpty()) {
                    toast("Please Enter Nearest Landmark")
                    return@setPositiveButton
                }
                if (pincode.text.isEmpty()) {
                    toast("Please Enter Pin Code")
                    return@setPositiveButton
                }
                if (pincode.text.length != 6) {
                    toast("Please Valid Pin Code")
                    return@setPositiveButton
                }
                sessionManager.setLocalAddress(
                    address.text.toString(),
                    location.text.toString(),
                    pincode.text.toString()
                )
                details_address.text =
                    sessionManager.getLocalAddress + ", " + sessionManager.getLocation + " , " + sessionManager.getPincode
                dialog.dismiss()
            }
            .setNeutralButton(
                "CANCEL"
            ) { dialog, id -> dialog.cancel() }

        // create alert dialog
        val alertDialog = alertDialogBuilder.create()
        // show it
        alertDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun gotoPayment() {
        val selectedId = rg_payment.checkedRadioButtonId
        payment = when (selectedId) {
            R.id.rb_payment_option_op -> "ONL"
            R.id.rb_payment_option_cod -> "COD"
            R.id.rb_payment_option_wallet -> "WAL"
            else -> ""
        }
        paymentStatus = when (selectedId) {
            R.id.rb_payment_option_op -> "C"
            R.id.rb_payment_option_cod -> "P"
            R.id.rb_payment_option_wallet -> "C"
            else -> ""
        }

        val sdf = SimpleDateFormat("dd/mm/yyyy HH:mm")
        val currentDate = sdf.format(Date())
        //System.out.println("CDATE is  $currentDate")
        //Log.d("TIME", currentDate.split(" ")[1] + "#" + menuData.timing!!)
        if (!checkTimings(currentDate.split(" ")[1], menuData.timing!!)) {
            mainContent.snackbar(menuData.mode + " Time has passed")
            return
        }
        if (sessionManager.getLocalAddress!!.isEmpty()) {
            mainContent.snackbar("Please enter details delivery address")
            return
        }
        if (payment.isEmpty()) {
            scrollView.scrollTo(0, scrollView.bottom)
            mainContent.snackbar("Please select payment option")
            return
        }

        orderPojo = OrderRequestPojo()
        orderPojo.user_id = sessionManager.getId
        orderPojo.kitchen_id = menuData.kitchen_id // kitchen_id
        orderPojo.menu_id = menuData.id // id
        orderPojo.payment_status = paymentStatus
        orderPojo.payment_via = payment
        orderPojo.extra_note = et_ConfirmOrder_remark.text.toString()
        orderPojo.address = sessionManager.getLocalAddress
        orderPojo.pincode = sessionManager.getPincode
        orderPojo.landmark = sessionManager.getLocation
        orderPojo.quantity = spQuantityStr
        orderPojo.price = menuData.category_price
        //val gson = Gson()
        //val json = gson.toJson(orderPojo)

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Confirm")
        alertDialog.setMessage("Do you want to place order?")
        alertDialog.setIcon(R.drawable.confirm)
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton(
            Html.fromHtml("<font color='#009494'>Yes")
        ) { dialog, which ->
            dialog.dismiss()
            when (payment) {
                "COD" -> {
                    viewModel.placeOrder(orderPojo)
                }
                "WAL" -> {
                    if (walletBalance >= total) {
                        loadingDialog.showDialog()
                        viewModel.cutFomWallet(orderPojo.user_id!!, total.toString())
                    } else {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(
                            "⚠ Your wallet balance (₹" + walletBalance + ") is too low to place this order." +
                                    "Please add amount to wallet or select different payment method."
                        )
                        builder.setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        builder.setCancelable(false)
                        builder.show()
                    }
                }
                else -> {
                    toast("Please select different payment method")
                }
            }
        }
        alertDialog.setNegativeButton(
            Html.fromHtml("<font color='#00585e'>No")
        ) { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkTimings(time: String, endtime: String): Boolean {

        val pattern = "HH:mm"
        val sdf = SimpleDateFormat(pattern)
        try {
            val date1 = sdf.parse(time)
            val date2 = sdf.parse(endtime)

            return date1!!.before(date2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return false
    }

    override fun onOrderSuccess(data: PostOrderPojo) {
        loadingDialog.hideDialog()
        try {
            if (data.orderId!!.isNotEmpty()){
                val alert = SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Order have been placed successfully")
                    .setContentText("Your order id : " + data.orderId!!)
                    .setConfirmText("DONE")
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                        val myIntent = Intent(this, MyOrdersActivity::class.java)
                        myIntent.putExtra("FROM", "CONFIRM")
                        startActivity(myIntent)
                        overridePendingTransition(
                            R.anim.right_in,
                            R.anim.left_out
                        )
                        finishAffinity()
                    }
                alert.setCancelable(false)
                alert.show()
            }
        } catch (e: Exception) {
            toast("Something went wrong.......")
        }
    }

    override fun onWalletSuccess(data: WalletPojo) {
        loadingDialog.hideDialog()
        try {
            walletBalance = data.wallet!!.toInt()
            //toast(walletBalance.toString())
        } catch (e: Exception) {
            toast("Something went wrong.......")
        }
    }

    override fun onError(message: String) {
        loadingDialog.hideDialog()
        toast(message)
    }

    override fun onNetworkError(message: String) {
        loadingDialog.hideDialog()
        toast(message)
    }

    override fun onCutWalletSuccess(data: CutWalltetPojo) {
        loadingDialog.hideDialog()
        try {
            if (data.amount!!.toInt() > 0){
                loadingDialog.showDialog()
                viewModel.placeOrder(orderPojo)
            }
        } catch (e: Exception) {
            toast("Something went wrong.......")
        }
    }

    override fun onBackPressed() {
        overridePendingTransition(
            R.anim.right_in,
            R.anim.left_out
        )
        finish()
    }
}
