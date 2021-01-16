package com.app.kgs_user.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.kgs_user.R
import com.app.kgs_user.adapter.ReviewAdapter
import com.app.kgs_user.model.Reviews
import kotlinx.android.synthetic.main.activity_review.*

class ReviewActivity : AppCompatActivity() {

    lateinit var reviewArrayList: ArrayList<Reviews>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        reviewArrayList = intent.getSerializableExtra("VAL") as ArrayList<Reviews>

        val reviewAdapter = ReviewAdapter()
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        reviewList!!.layoutManager = layoutManager

        try {
            reviewAdapter.clearList()
            reviewAdapter.addList(reviewArrayList)
            reviewAdapter.notifyDataSetChanged()
            reviewList.adapter = reviewAdapter
        } catch (e: Exception) {
            Log.d("EXCEPTION", e.message.toString())
        }

        iv_back.setOnClickListener {
            overridePendingTransition(
                R.anim.right_in,
                R.anim.left_out
            )
            finish()
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
