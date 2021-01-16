package com.app.kgs_user.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.kgs_user.R
import com.app.kgs_user.adapter.MenuAdapter
import com.app.kgs_user.callBacks.CallBackMenuList
import com.app.kgs_user.model.MenuData
import com.app.kgs_user.model.Reviews
import com.app.kgs_user.utils.Constants
import com.app.kgs_user.utils.LoadingDialog
import com.app.kgs_user.utils.snackbar
import com.app.kgs_user.utils.toast
import com.app.kgs_user.viewmodel.MenuViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_kitchen_menu.*
import org.json.JSONObject

class KitchenMenuActivity : AppCompatActivity(), CallBackMenuList {

    lateinit var loadingDialog: LoadingDialog
    lateinit var reviewList: ArrayList<Reviews>
    lateinit var allList: ArrayList<MenuData>
    lateinit var kitchenMenuAdapter: MenuAdapter

    lateinit var viewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitchen_menu)

        viewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        viewModel.setCallback(this)

        kname.text = intent.getStringExtra("NAME")
        type.text = "Veg/Non-Veg Lunch/Dinner Available"

        loadingDialog = LoadingDialog(this)
        loadingDialog.showDialog()
        viewModel.getMenuList(intent.getStringExtra("MID")!!)

        iv_back.setOnClickListener {
            overridePendingTransition(
                R.anim.right_in,
                R.anim.left_out
            )
            finish()
        }

        mFab.setOnClickListener {
            if (reviewList.size != 0) {
                val myIntent = Intent(this, ReviewActivity::class.java)
                myIntent.putExtra("VAL", reviewList)
                startActivity(myIntent)
                overridePendingTransition(
                    R.anim.left_enter,
                    R.anim.right_out
                )
            } else {
                mFab.snackbar("Kitchen don't have any reviews.")
            }
        }

        if (intent.getStringExtra("IMAGE")!!.contains("https"))
            Glide.with(this@KitchenMenuActivity)
                .load(R.drawable.nopreview)
                .into(logo_restaurant)
        else
            Glide.with(this)
                .load(Constants.ROOT_IMAGE_URL + intent.getStringExtra("IMAGE")!!)
                .into(logo_restaurant)
        MyRating.rating = intent.getStringExtra("RATING")!!.toFloat()
    }

    override fun onSuccess(getMenus: String) {
        loadingDialog.hideDialog()

        val jobj = JSONObject(getMenus).getJSONObject("data")
        if (jobj.getJSONArray("todays_menu").length() == 0) {
            menuList.visibility = View.GONE
            notAvailable.visibility = View.VISIBLE
            toast("No menu available")
        } else {

            val gson = Gson()
            val token = object : TypeToken<ArrayList<MenuData>>() {}
            allList = gson.fromJson(jobj.getJSONArray("todays_menu").toString(), token.type)
            val token2 = object : TypeToken<ArrayList<Reviews>>() {}
            reviewList = gson.fromJson(jobj.getJSONArray("reviews").toString(), token2.type)

            val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            menuList.layoutManager = layoutManager
            kitchenMenuAdapter = MenuAdapter(this)
            try {
                kitchenMenuAdapter.clearList()
                kitchenMenuAdapter.addList(allList)
                kitchenMenuAdapter.notifyDataSetChanged()
                menuList.adapter = kitchenMenuAdapter
            } catch (e: Exception) {
                Log.d("EXCEPTION", e.message.toString())
            }
            if (allList.size == 0) {
                menuList.visibility = View.GONE
                notAvailable.visibility = View.VISIBLE
            } else {
                menuList.visibility = View.VISIBLE
                notAvailable.visibility = View.GONE
            }
        }
    }

    override fun onError(message: String) {
        loadingDialog.hideDialog()
        menuList.visibility = View.GONE
        notAvailable.visibility = View.VISIBLE
        toast(message)
    }

    override fun onNetworkError(message: String) {
        loadingDialog.hideDialog()
        menuList.visibility = View.GONE
        notAvailable.visibility = View.VISIBLE
        toast(message)
    }

    override fun onBackPressed() {
        overridePendingTransition(
            R.anim.right_in,
            R.anim.left_out
        )
        finish()
    }
}
