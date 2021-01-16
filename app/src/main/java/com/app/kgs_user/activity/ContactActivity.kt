package com.app.kgs_user.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.app.kgs_user.R
import com.app.kgs_user.rest.ApiInterface
import com.app.kgs_user.utils.Constants
import com.app.kgs_user.utils.LoadingDialog
import com.app.kgs_user.utils.NetworkChangeReceiver
import kotlinx.android.synthetic.main.activity_contact.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class ContactActivity : AppCompatActivity() {

    var networkChangeReceiver: NetworkChangeReceiver? = null
    var network: Boolean? = false
    var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        loadingDialog = LoadingDialog(this)
        networkChangeReceiver = NetworkChangeReceiver(this)
        network = networkChangeReceiver!!.isNetworkAvailable

       /* if (network!!)
            getData()
        else {
            val snackbar = Snackbar.make(this.findViewById<View>(android.R.id.content),
                    "It seems your device don't have or no internet connection", Snackbar.LENGTH_LONG)
            snackbar.show()
        }*/

        phone.setOnClickListener {
            try {
                if (Build.VERSION.SDK_INT > 22) {
                    if (ActivityCompat.checkSelfPermission(this@ContactActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this@ContactActivity, arrayOf(Manifest.permission.CALL_PHONE), 101)
                        return@setOnClickListener
                    }
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel:" + tv_phone.text.toString().trim())
                    startActivity(callIntent)
                } else {
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel:" + tv_phone.text.toString().trim())
                    startActivity(callIntent)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        email.setOnClickListener {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "message/rfc822"
            i.putExtra(Intent.EXTRA_EMAIL, arrayOf(tv_email.text.toString()))
            i.putExtra(Intent.EXTRA_SUBJECT, "Enter Your Subject.....")
            i.putExtra(Intent.EXTRA_TEXT, "Enter your query......")
            try {
                startActivity(Intent.createChooser(i, "Send mail..."))
            } catch (ex: android.content.ActivityNotFoundException) {
                //Toast.makeText(MyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }

        iv_back.setOnClickListener { super.onBackPressed() }
    }

    private fun getData() {

        loadingDialog!!.showDialog()
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val api = retrofit.create(ApiInterface::class.java)

        val callCheckSubs = api.contactus()
        callCheckSubs.enqueue(object : Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                loadingDialog!!.hideDialog()
                //Log.d("onFailure:", "Error")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.body() != null) {
                    loadingDialog!!.hideDialog()
                    try {
                        assert(response.body() != null)
                        val tmp =  response.body()!!.string()
                        Log.d("!!onResponse_Contactus:", tmp)
                        try {
                            //Toast.makeText(ContactUs.this, temp, Toast.LENGTH_SHORT).show();
                            val jsonObject = JSONObject(tmp)
                            if (jsonObject.getString("result") == "1") {
                                tv_email.text = jsonObject.getJSONObject("cms_detail").getString("email")
                                tv_phone.text = jsonObject.getJSONObject("cms_detail").getString("phone_no")
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }

        })
    }
}
