package com.app.kgs_user.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.kgs_user.activity.ConfirmOrderActivity
import com.app.kgs_user.R
import com.app.kgs_user.model.MenuData
import com.app.kgs_user.utils.Constants
import com.app.kgs_user.utils.NetworkChangeReceiver
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.makeramen.roundedimageview.RoundedImageView
import java.io.Serializable


class MenuAdapter(private val activity: Activity) : RecyclerView.Adapter<MenuAdapter.MyViewHolder>() {

    private val allMenuList: ArrayList<MenuData>
    private val mContext: Context
    internal var options: RequestOptions
    private val networkChangeReceiver: NetworkChangeReceiver
    private var network: Boolean? = null

    init {
        this.mContext = activity.applicationContext
        allMenuList = ArrayList()
        options = RequestOptions()
            .placeholder(R.drawable.nopreview)
            .error(R.drawable.nopreview)

        networkChangeReceiver = NetworkChangeReceiver(activity)
        network = networkChangeReceiver.isNetworkAvailable
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.modular_menu_list, parent, false)

        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tv_ItemName.text = allMenuList[position].category_name + " (" + allMenuList[position].mode + ")"
        holder.tv_ItemDesc.text = allMenuList[position].description
        holder.tv_price.text = " Price : ₹" + allMenuList[position].category_price
        holder.tv_Time.text = "\uD83D\uDD51 Order within " + allMenuList[position].timing + " O'Clock"

        val image_url = allMenuList[position].image
        Glide.with(mContext)
            .load(Constants.ROOT_IMAGE_URL + image_url)
            .apply(options)
            .into(holder.rv_ItemImage)
    }

    override fun getItemCount(): Int {
        return allMenuList.size
    }

    inner class MyViewHolder (itemview: View) : RecyclerView.ViewHolder(itemview) {

        internal var rv_ItemImage: RoundedImageView
        internal var tv_ItemName: TextView
        internal var tv_ItemDesc: TextView
        internal var tv_price: TextView
        internal var tv_Time: TextView
        internal var card: CardView

        init {
            rv_ItemImage = itemview.findViewById<View>(R.id.rv_ItemImage) as RoundedImageView
            tv_ItemName = itemview.findViewById<View>(R.id.tv_ItemName) as TextView
            tv_ItemDesc = itemview.findViewById<View>(R.id.tv_ItemDesc) as TextView
            tv_price = itemview.findViewById<View>(R.id.tv_price) as TextView
            tv_Time = itemview.findViewById<View>(R.id.tv_Time) as TextView
            card = itemview.findViewById<View>(R.id.card) as CardView

            card.setOnClickListener {
                val myIntent = Intent(activity, ConfirmOrderActivity::class.java)
                myIntent.putExtra("DATA", allMenuList[adapterPosition] as Serializable)
                activity.startActivity(myIntent)
                activity.overridePendingTransition(
                    R.anim.left_in,
                    R.anim.right_out
                )
            }
        }
    }

    fun addList(orderList: ArrayList<MenuData>) {
        this.allMenuList.addAll(orderList)
    }

    fun clearList() {
        this.allMenuList.clear()
    }
}
