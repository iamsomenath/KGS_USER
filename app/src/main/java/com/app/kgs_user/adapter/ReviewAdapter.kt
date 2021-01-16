package com.app.kgs_user.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.kgs_user.R
import com.app.kgs_user.model.Reviews

/* Review */
class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.MyViewHolder>() {

    private val reviewList: ArrayList<Reviews> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.modular_review_list, parent, false)

        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.name.text = reviewList[position].name
        holder.review.text = reviewList[position].comment
        holder.myRating.rating = reviewList[position].rating!!.toFloat()
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    inner class MyViewHolder

        (itemview: View) : RecyclerView.ViewHolder(itemview) {

        internal var name: TextView
        internal var review: TextView
        internal var myRating: RatingBar

        init {
            name = itemview.findViewById<View>(R.id.tv_name) as TextView
            review = itemview.findViewById<View>(R.id.review) as TextView
            myRating = itemview.findViewById<View>(R.id.rating) as RatingBar
        }
    }

    fun addList(reviewList: ArrayList<Reviews>) {
        this.reviewList.addAll(reviewList)
    }

    fun clearList() {
        this.reviewList.clear()
    }
}