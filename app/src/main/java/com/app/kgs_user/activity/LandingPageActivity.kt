package com.app.kgs_user.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.kgs_user.adapter.ImageSliderAdaper
import com.app.kgs_user.R
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_landing_page.*

class LandingPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)
        initImageSlider()

        login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.left_enter, R.anim.right_out)
            finish()
        }
    }

    private fun initImageSlider() {
        imageSlider.sliderAdapter = ImageSliderAdaper(this)
        //imageSlider.setIndicatorAnimation(IndicatorAnimations.SWAP)
        //set indicator animation by using 	  SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        imageSlider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        imageSlider.scrollTimeInSec = 10 //set scroll delay in seconds :
        imageSlider.startAutoCycle()
    }
}
