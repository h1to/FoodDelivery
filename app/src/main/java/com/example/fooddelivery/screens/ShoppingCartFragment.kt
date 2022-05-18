package com.example.fooddelivery.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.format.Time
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddelivery.R
import com.example.fooddelivery.data.Delivery
import com.example.fooddelivery.data.Dish
import com.example.fooddelivery.data.Order
import com.example.fooddelivery.data.User
import com.example.fooddelivery.databinding.OrderItemViewBinding
import com.example.fooddelivery.recyclerviews.OrderListener
import com.example.fooddelivery.recyclerviews.OrderRecyclerAdapter
import com.example.fooddelivery.services.DeliveryService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text

class ShoppingCartFragment : Fragment(), OrderListener {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val adapter = OrderRecyclerAdapter(listOf(), this)
    private lateinit var vieww: View

    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = requireArguments().get("user") as User
        return inflater.inflate(R.layout.fragment_shopping_cart, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vieww = view
        addOrdersListener()
        view.findViewById<RecyclerView>(R.id.shopping_recycler).adapter = adapter
        view.findViewById<Button>(R.id.checkout_button).setOnClickListener {
            if(user.address == ""){
                Toast.makeText(context, "You have not added address!", Toast.LENGTH_SHORT).show()
            }
            else {
                var description = ""
                for (i in adapter.dataSet) {
                    if (description.isBlank()) {
                        description = description.plus(i.dishName).plus(" x").plus(i.dishAmount)
                    } else description =
                        description.plus(", ").plus(i.dishName).plus(" x").plus(i.dishAmount)
                }

                if (description.isNotBlank()) {
                    val delivery = Delivery()
                    delivery.description = System.currentTimeMillis().toString().plus('\n').plus(description)
                    delivery.userId = user.id

                    firebaseDatabase.reference.child("delivery_table").push().setValue(delivery)
                    val intent = Intent(requireActivity(), DeliveryService::class.java).putExtra("deliveryName", delivery.description)
                    requireActivity().startService(intent)

                    for (i in adapter.dataSet) {
                        firebaseDatabase.reference.child("order_table").child(i.id).setValue(null)
                    }
                }
                else{
                    Toast.makeText(context, "Cart is empty!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addOrdersListener() {
        val valueEventListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val orders = mutableListOf<Order>()
                for (child in dataSnapshot.children) {
                    val order = child.getValue(Order::class.java)
                    if(order != null) {
                        if(order.userId == user.id) {
                            order.id = child.key.toString()
                            orders.add(order)
                        }
                    }
                }
                adapter.dataSet = orders
                adapter.notifyDataSetChanged()
                updateUI()
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        firebaseDatabase.reference.child("order_table").addValueEventListener(valueEventListener)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(){
        var sum = 0
        for(i in adapter.dataSet){
            sum += i.price
        }
        vieww.findViewById<TextView>(R.id.total_price_text).text = " $".plus(sum)
    }

    override fun onIncreaseClick(order: Order) {
        order.dishAmount++
        order.price += order.dishPrice
        firebaseDatabase.reference.child("order_table").child(order.id).setValue(order)
    }

    override fun onDecreaseClick(order: Order) {
        if(order.dishAmount > 1){
            order.dishAmount--
            order.price -= order.dishPrice
            firebaseDatabase.reference.child("order_table").child(order.id).setValue(order)
        }
    }

    override fun onDeleteClick(order: Order) {
        firebaseDatabase.reference.child("order_table").child(order.id).setValue(null)
    }
}