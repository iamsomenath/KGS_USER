package com.app.kgs_user.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.kgs_user.R
import com.app.kgs_user.activity.KitchenMenuActivity
import com.app.kgs_user.model.KitchenData
import com.app.kgs_user.utils.Constants
import com.app.kgs_user.utils.NetworkChangeReceiver
import com.app.kgs_user.utils.toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.makeramen.roundedimageview.RoundedImageView

class KitchenListAdapter(private val activity: Activity) : RecyclerView.Adapter<KitchenListAdapter.MyViewHolder>() {

    private val allRestaurantsList: ArrayList<KitchenData>
    private val mContext: Context
    internal var options: RequestOptions
    private val networkChangeReceiver: NetworkChangeReceiver
    private var network: Boolean? = null

    init {
        this.mContext = activity.applicationContext
        allRestaurantsList = ArrayList()
        options = RequestOptions()
            .placeholder(R.drawable.nopreview)
            .error(R.drawable.nopreview)

        networkChangeReceiver = NetworkChangeReceiver(activity)
        network = networkChangeReceiver.isNetworkAvailable
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.modular_item_list, parent, false)

        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.name.text = allRestaurantsList[position].name
        holder.review.text = "Total Reviews : " + allRestaurantsList[position].reviewcount
        holder.myRating.rating = allRestaurantsList[position].review!!.toFloat()

        val dist = String.format("%.2f", allRestaurantsList[position].distance!!.toDouble()).toDouble()
        //Log.d("DIST", dist.toString())
        if (dist.toInt() != 0)
            holder.tv_Distance.text = "$dist Km"
        else
            holder.tv_Distance.text = "Within Reach"

        if (allRestaurantsList[position].image!!.contains("https"))
            Glide.with(mContext)
                .load(allRestaurantsList[position].image!!)
                .apply(options)
                .into(holder.image)
        else
            Glide.with(mContext)
                .load(Constants.ROOT_IMAGE_URL + allRestaurantsList[position].image!!)
                .apply(options)
                .into(holder.image)
    }

    override fun getItemCount(): Int {
        return allRestaurantsList.size
    }

    inner class MyViewHolder

        (itemview: View) : RecyclerView.ViewHolder(itemview) {

        internal var image: RoundedImageView
        internal var name: TextView
        internal var review: TextView
        internal var tv_Distance: TextView
        internal var myRating: RatingBar

        init {
            image = itemview.findViewById<View>(R.id.rv_image) as RoundedImageView
            name = itemview.findViewById<View>(R.id.tv_name) as TextView
            review = itemview.findViewById<View>(R.id.review) as TextView
            tv_Distance = itemview.findViewById<View>(R.id.tv_Distance) as TextView
            myRating = itemview.findViewById<View>(R.id.rating) as RatingBar

            itemView.setOnClickListener {
                network = networkChangeReceiver.isNetworkAvailable
                if(!network!!){
                    activity.toast(activity.getString(R.string.error_message))
                }else{
                    val myIntent = Intent(activity, KitchenMenuActivity::class.java)
                    myIntent.putExtra("MID", allRestaurantsList[adapterPosition].id)
                    myIntent.putExtra("NAME", allRestaurantsList[adapterPosition].name)
                    myIntent.putExtra("IMAGE", allRestaurantsList[adapterPosition].image)
                    myIntent.putExtra("RATING", allRestaurantsList[adapterPosition].review)
                    activity.startActivity(myIntent)
                    activity.overridePendingTransition(
                        R.anim.left_in,
                        R.anim.right_out
                    )
                }
            }
        }
    }

    fun addRestaurantList(orderList: ArrayList<KitchenData>) {
        this.allRestaurantsList.addAll(orderList)
    }

    fun clearRestaurantList() {
        this.allRestaurantsList.clear()
    }
}
