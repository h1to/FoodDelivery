package com.example.fooddelivery.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddelivery.R
import com.example.fooddelivery.data.Delivery
import com.example.fooddelivery.data.Dish
import com.example.fooddelivery.data.Order
import com.example.fooddelivery.recyclerviews.DeliveryListener
import com.example.fooddelivery.recyclerviews.DeliveryRecyclerAdapter
import com.example.fooddelivery.recyclerviews.DishRecyclerAdapter
import com.example.fooddelivery.recyclerviews.OrderRecyclerAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DeliveryFragment : Fragment(), DeliveryListener {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val adapter = DeliveryRecyclerAdapter(listOf(), this)

    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_delivery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = requireArguments().get("userId") as String
        addDeliveryListener()
        view.findViewById<RecyclerView>(R.id.delivery_recycler).adapter = adapter
    }

    private fun addDeliveryListener() {
        val valueEventListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val deliveries = mutableListOf<Delivery>()
                for (child in dataSnapshot.children) {
                    val delivery = child.getValue(Delivery::class.java)
                    if(delivery != null) {
                        if(delivery.userId == userId) {
                            delivery.id = child.key.toString()
                            deliveries.add(delivery)
                        }
                    }
                }

                adapter.dataSet = deliveries
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        firebaseDatabase.reference.child("delivery_table").addValueEventListener(valueEventListener)
    }

    override fun onClick(delivery: Delivery) {
        firebaseDatabase.reference.child("delivery_table").child(delivery.id).setValue(null)
    }
}