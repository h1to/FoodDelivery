package com.example.fooddelivery.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddelivery.R
import com.example.fooddelivery.data.Order
import com.example.fooddelivery.databinding.OrderItemViewBinding

class OrderRecyclerAdapter(var dataSet: List<Order>, private val orderListener: OrderListener) : RecyclerView.Adapter<OrderRecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View, private val orderListener: OrderListener) : RecyclerView.ViewHolder(view) {
        private val binding = OrderItemViewBinding.bind(view)

        fun bind(order: Order){
            binding.orderNameTextView.text = order.dishName
            binding.orderCountTextView.text = order.dishAmount.toString()
            binding.orderPriceText.text = "$".plus(order.price.toString())
            binding.increaseOrderCountImage.setOnClickListener {
                orderListener.onIncreaseClick(order)
            }
            binding.decreaseOrderCountImage.setOnClickListener {
                orderListener.onDecreaseClick(order)
            }
            binding.deleteImage.setOnClickListener {
                orderListener.onDeleteClick(order)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.order_item_view, viewGroup, false)
        return ViewHolder(view, orderListener)
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }
}

interface OrderListener{
    fun onIncreaseClick(order: Order)
    fun onDecreaseClick(order: Order)
    fun onDeleteClick(order: Order)
}