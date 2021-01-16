package com.app.kgs_user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.app.kgs_user.R
import com.makeramen.roundedimageview.RoundedImageView
import com.smarteist.autoimageslider.SliderViewAdapter

class ImageSliderAdaper(private val context: Context) : SliderViewAdapter<ImageSliderAdaper.SliderAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        //   viewHolder.textViewDescription.setText("This is slider item " + position);

        when (position) {
            0 -> {
                viewHolder.imageViewBackground.setBackgroundResource(R.drawable.slider1)
                viewHolder.tv_sliderText1.text = "Choose what you want to eat today!"
                viewHolder.tv_sliderText2.text =
                    "We Deliver to your office or home. Choose your food from thousands of restaurants nearby"
            }
            1 -> {
                viewHolder.imageViewBackground.setBackgroundResource(R.drawable.slider2)
                viewHolder.tv_sliderText1.text = "Order food"
                viewHolder.tv_sliderText2.text = "Place an order and our courier will reach the location on time"
            }
            else -> {
                viewHolder.imageViewBackground.setBackgroundResource(R.drawable.slider3)
                viewHolder.tv_sliderText1.text = "Enjoy!"
                viewHolder.tv_sliderText2.text = "Leave a review and get a coupons to use with the new order"
            }
        }
    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return 3
    }

    inner class SliderAdapterVH(var itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        var imageViewBackground: RoundedImageView
        var tv_sliderText1: TextView
        var tv_sliderText2: TextView

        init {
            imageViewBackground = itemView.findViewById(R.id.iv_silderImage)
            tv_sliderText1 = itemView.findViewById(R.id.tv_sliderText1)
            tv_sliderText2 = itemView.findViewById(R.id.tv_sliderText2)
        }
    }
}

