package com.example.fooddelivery.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddelivery.R
import com.example.fooddelivery.data.Delivery
import com.example.fooddelivery.data.Order
import com.example.fooddelivery.databinding.DeliveryItemViewBinding
import com.example.fooddelivery.databinding.OrderItemViewBinding

class DeliveryRecyclerAdapter(var dataSet: List<Delivery>, private val deliveryListener: DeliveryListener) : RecyclerView.Adapter<DeliveryRecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View, private val deliveryListener: DeliveryListener) : RecyclerView.ViewHolder(view) {
        private val binding = DeliveryItemViewBinding.bind(view)

        fun bind(delivery: Delivery){
            binding.deliveryDescriptionText.text = delivery.description
            binding.deliveryProgress.progress = delivery.progress
            if(delivery.completed){
                binding.getDeliveryButton.isClickable = true
                binding.getDeliveryButton.setOnClickListener {
                    deliveryListener.onClick(delivery)
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.delivery_item_view, viewGroup, false)
        return ViewHolder(view, deliveryListener)
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }
}

interface DeliveryListener{
    fun onClick(delivery: Delivery)
}