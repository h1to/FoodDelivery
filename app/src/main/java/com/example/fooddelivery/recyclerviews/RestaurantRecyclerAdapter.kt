package com.example.fooddelivery.recyclerviews

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddelivery.R
import com.example.fooddelivery.data.Restaurant
import com.example.fooddelivery.databinding.RestaurantItemViewBinding
import kotlin.math.roundToInt

class RestaurantRecyclerAdapter(var dataSet: List<Restaurant>, private val restaurantListener: RestaurantListener) : RecyclerView.Adapter<RestaurantRecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View, private val restaurantListener: RestaurantListener) : RecyclerView.ViewHolder(view) {
        private val binding = RestaurantItemViewBinding.bind(view)

        fun bind(restaurant: Restaurant){
            binding.restaurantNameTextView.text = restaurant.name
            binding.ratingTextView.text = (restaurant.rating * 10).roundToInt().toDouble().div(10.0).toString()
            binding.voicesTextView.text = "(".plus(restaurant.voices.toString()).plus(")")
            binding.restaurantImageView.setImageBitmap(convertStringToBitmap(restaurant.image))
            binding.root.setOnClickListener {
                restaurantListener.onCLick(restaurant)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.restaurant_item_view, viewGroup, false)
        return ViewHolder(view, restaurantListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

    companion object {
        fun convertStringToBitmap(stringImage: String): Bitmap {
            val decodedString: ByteArray = Base64.decode(stringImage, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }
    }
}

interface RestaurantListener{
    fun onCLick(restaurant: Restaurant)
}
