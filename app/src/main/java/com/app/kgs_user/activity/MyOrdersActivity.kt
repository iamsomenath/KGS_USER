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
import com.app.kgs_user.adapter.OrderListAdapter
import com.app.kgs_user.callBacks.CallbackOrderListActivity
import com.app.kgs_user.model.PostOrderPojo
import com.app.kgs_user.utils.*
import com.app.kgs_user.viewmodel.PostOrdersViewModel
import kotlinx.android.synthetic.main.activity_myorder.*

class MyOrdersActivity : AppCompatActivity(), CallbackOrderListActivity {

    lateinit var loadingDialog: LoadingDialog
    lateinit var sessionManager: SessionManager
    lateinit var viewModel: PostOrdersViewModel
    lateinit var networkChangeReceiver: NetworkChangeReceiver
    private var network: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myorder)

        viewModel = ViewModelProviders.of(this).get(PostOrdersViewModel::class.java)
        viewModel.setCallback(this)

        sessionManager = SessionManager(this)
        loadingDialog = LoadingDialog(this)

        networkChangeReceiver = NetworkChangeReceiver(this)
        network = networkChangeReceiver.isNetworkAvailable

        iv_back!!.setOnClickListener {
            if (intent.hasExtra("FROM")) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                overridePendingTransition(
                    R.anim.right_in,
                    R.anim.left_out
                )
                finish()
            } else {
                overridePendingTransition(
                    R.anim.right_in,
                    R.anim.left_out
                )
                finish()
            }
        }

        if (network!!) {
            loadingDialog.showDialog()
            viewModel.getOrderList(sessionManager.getId!!)
        } else {
            mainContent.snackbar(getString(R.string.error_message))
        }
    }

    override fun onSuccess(orderLists: ArrayList<PostOrderPojo>) {
        loadingDialog.hideDialog()
        val orderListAdpter = OrderListAdapter(this)
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv_orderlistRecyclerView!!.layoutManager = layoutManager

        try {
            orderListAdpter.clearList()
            orderListAdpter.addList(orderLists)
            orderListAdpter.notifyDataSetChanged()
            rv_orderlistRecyclerView.adapter = orderListAdpter
        } catch (e: Exception) {
            Log.d("EXCEPTION", e.message.toString())
        }
        if (orderLists.size == 0) {
            rv_orderlistRecyclerView.visibility = View.GONE
            layout_orderlist_notFound.visibility = View.VISIBLE
        } else {
            rv_orderlistRecyclerView.visibility = View.VISIBLE
            layout_orderlist_notFound.visibility = View.GONE
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

    override fun onBackPressed() {
        if (intent.hasExtra("FROM")) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            overridePendingTransition(
                R.anim.right_in,
                R.anim.left_out
            )
            finish()
        } else {
            overridePendingTransition(
                R.anim.right_in,
                R.anim.left_out
            )
            finish()
        }
    }
}
