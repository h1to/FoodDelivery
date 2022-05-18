package com.example.fooddelivery.recyclerviews

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddelivery.R
import com.example.fooddelivery.data.Dish
import com.example.fooddelivery.databinding.DishItemViewBinding

class DishRecyclerAdapter(var dataSet: List<Dish>, private val dishListener: DishListener) : RecyclerView.Adapter<DishRecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View, private val dishListener: DishListener) : RecyclerView.ViewHolder(view) {
        private val binding = DishItemViewBinding.bind(view)

        fun bind(dish: Dish){
            binding.dishNameTextView.text = dish.name
            binding.dishDescriptionTextView.text = dish.description
            binding.dishPriceTextView.text = "$".plus(dish.price.toString())
            binding.dishImageView.setImageBitmap(convertStringToBitmap(dish.image))
            if(dish.isFavorite){
                binding.favoriteImage.setImageResource(R.drawable.ic_baseline_favorite_24)
            }else binding.favoriteImage.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            binding.addToCartButton.setOnClickListener {
                dishListener.onCLick(dish)
            }
            binding.favoriteImage.setOnClickListener {
                dishListener.onFavoriteClick(dish)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): DishRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.dish_item_view, viewGroup, false)
        return ViewHolder(view, dishListener)
    }

    override fun getItemCount() = dataSet.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    companion object{
        fun convertStringToBitmap(stringImage: String): Bitmap {
            val decodedString: ByteArray = Base64.decode(stringImage, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }
    }
}

interface DishListener{
    fun onCLick(dish: Dish)
    fun onFavoriteClick(dish: Dish)
}