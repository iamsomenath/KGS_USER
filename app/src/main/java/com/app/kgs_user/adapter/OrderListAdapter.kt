package com.app.kgs_user.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.kgs_user.R
import com.app.kgs_user.activity.MyOrdersActivity
import com.app.kgs_user.helper.NetworkHelperOther
import com.app.kgs_user.model.PostOrderPojo
import com.app.kgs_user.rest.ApiInterface
import com.app.kgs_user.utils.Constants
import com.app.kgs_user.utils.NetworkChangeReceiver
import com.app.kgs_user.utils.toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.makeramen.roundedimageview.RoundedImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OrderListAdapter(private val activity: Activity) : RecyclerView.Adapter<OrderListAdapter.MyViewHolder>() {

    private val allOrderList: ArrayList<PostOrderPojo>
    private val mContext: Context
    internal var options: RequestOptions
    private val networkChangeReceiver: NetworkChangeReceiver
    private var network: Boolean? = null

    init {
        this.mContext = activity.applicationContext
        allOrderList = ArrayList()
        options = RequestOptions()
            .placeholder(R.drawable.nopreview)
            .error(R.drawable.nopreview)

        networkChangeReceiver = NetworkChangeReceiver(activity)
        network = networkChangeReceiver.isNetworkAvailable
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.modular_order_list, parent, false)

        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var status = ""
        var mode = ""
        if (allOrderList[position].orderStatus == "P") {
            status = "PENDING"
            holder.order_id.text = "ORDER ID : " + allOrderList[position].orderId + " (" + status + ")"
            holder.order_id.setTextColor(Color.RED)
        }else{
            status = "PAID"
            holder.order_id.text = "ORDER ID : " + allOrderList[position].orderId + " (" + status + ")"
            holder.order_id.setTextColor(Color.GREEN)
        }
        holder.tv_Name.text = allOrderList[position].name
        mode = when {
            allOrderList[position].paymentVia == "COD" -> "Cash On Delivery"
            allOrderList[position].paymentVia == "WAL" -> "VIA Wallet"
            else -> "Online Payment"
        }
        holder.mode.text = "Payment Mode : $mode"
        holder.address.text = allOrderList[position].address + ", " + allOrderList[position].landmark +
                ", " + allOrderList[position].pincode
        holder.category.text = "Category : " + allOrderList[position].mode + " (" + allOrderList[position].category_name + ")"
        holder.tv_price.text =  "Total Amount : " + allOrderList[position].quantity + " X ₹" +
                allOrderList[position].price + " = ₹" +
                (allOrderList[position].quantity!!.toInt() * allOrderList[position].price!!.toInt())


        val image_url = allOrderList[position].image
        Glide.with(mContext)
            .load(Constants.ROOT_IMAGE_URL + image_url)
            .apply(options)
            .into(holder.rv_ItemImage)

        if(!allOrderList[position].rating.isNullOrEmpty()){
            holder.myRating.rating = allOrderList[position].rating!!.toFloat()
            holder.review.visibility = View.GONE
        }else{
            holder.review.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return allOrderList.size
    }

    inner class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        internal var rv_ItemImage: RoundedImageView
        internal var order_id: TextView
        internal var tv_Name: TextView
        internal var tv_price: TextView
        internal var mode: TextView
        internal var category: TextView
        internal var review: Button
        internal var address: TextView
        internal var myRating: RatingBar

        init {
            rv_ItemImage = itemview.findViewById<View>(R.id.rv_ItemImage) as RoundedImageView
            order_id = itemview.findViewById<View>(R.id.order_id) as TextView
            tv_Name = itemview.findViewById<View>(R.id.tv_Name) as TextView
            tv_price = itemview.findViewById<View>(R.id.tv_price) as TextView
            mode = itemview.findViewById<View>(R.id.mode) as TextView
            category = itemview.findViewById<View>(R.id.category) as TextView
            review = itemview.findViewById<View>(R.id.review) as Button
            myRating = itemview.findViewById<View>(R.id.MyRating) as RatingBar
            address = itemview.findViewById<View>(R.id.address) as TextView

            review.setOnClickListener {
                postReview(allOrderList[adapterPosition].kitchenId!!, allOrderList[adapterPosition].id!!)
            }
        }
    }

    fun addList(orderList: ArrayList<PostOrderPojo>) {
        this.allOrderList.addAll(orderList)
    }

    fun clearList() {
        this.allOrderList.clear()
    }

    private fun postReview(kitchenId: String, orderId: String) {
        var rateValue = 0.0f
        val li = LayoutInflater.from(activity)
        val promptsView = li.inflate(R.layout.dialog_post_review, null)
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setView(promptsView)
        val remarks = promptsView.findViewById(R.id.remarks) as EditText
        val ratingBar1 = promptsView.findViewById<RatingBar>(R.id.ratingBar1) as RatingBar
        ratingBar1.setOnRatingBarChangeListener { ratingBar, fl, b ->
            rateValue = ratingBar1.rating
            //activity.toast("Rate for Module is $rateValue")
        }
        // set dialog message
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton(
                Html.fromHtml("<font color='#008000'>SAVE")
            ) { dialog, id ->
                if (remarks.text.isEmpty()) {
                    activity.toast("Please Enter Remarks")
                    return@setPositiveButton
                }
                if (rateValue < 0.5) {
                    activity.toast("Please Enter Rating")
                    return@setPositiveButton
                }
                apiSubmitReview(remarks.text.toString(), rateValue.toString(), kitchenId, orderId)
                dialog.dismiss()
            }
            .setNeutralButton(
                Html.fromHtml("<font color='#FF0000'>CANCEL")
            ) { dialog, id -> dialog.cancel() }

        // create alert dialog
        val alertDialog = alertDialogBuilder.create()
        // show it
        alertDialog.show()
    }

    private fun getAPIInterface(): ApiInterface {
        return NetworkHelperOther.client.create(ApiInterface::class.java)
    }

    @SuppressLint("CheckResult")
    private fun apiSubmitReview(remarks: String, rateValue: String, kitchenId: String, orderId: String) {

        val userResponseObservable = getAPIInterface().submit_review(
            (activity as MyOrdersActivity).sessionManager.getId!!, kitchenId, orderId, rateValue, remarks
        )
        userResponseObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ restResponse ->
                if (restResponse.status == 200) {
                    activity.toast(restResponse.message!!)
                    activity.overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    activity.finish()
                } else {
                    //activity.toast(restResponse.message!!)
                    //activity.toast("Your review has been submitted successfully")
                }
            }, { e ->
                activity.toast(Constants.NETWORK_ERROR_MESSAGE)
            })
    }

}



