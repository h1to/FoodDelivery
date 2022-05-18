package com.example.fooddelivery.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddelivery.R
import com.example.fooddelivery.data.Dish
import com.example.fooddelivery.data.Order
import com.example.fooddelivery.data.Restaurant
import com.example.fooddelivery.data.User
import com.example.fooddelivery.databinding.FragmentMainBinding
import com.example.fooddelivery.databinding.FragmentRestaurantBinding
import com.example.fooddelivery.recyclerviews.DishListener
import com.example.fooddelivery.recyclerviews.DishRecyclerAdapter
import com.example.fooddelivery.recyclerviews.RestaurantRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RestaurantFragment : Fragment(), DishListener {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val adapter = DishRecyclerAdapter(listOf(), this)

    private lateinit var user: User
    private lateinit var restaurant: Restaurant

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_restaurant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = requireArguments().get("user") as User
        restaurant = requireArguments().get("restaurant") as Restaurant
        view.findViewById<TextView>(R.id.restaurant_name_text_view).text = restaurant.name
        addDishesListener()
        addUsersListener()
        view.findViewById<RecyclerView>(R.id.dishes_recycler).adapter = adapter
    }

    override fun onCLick(dish: Dish) {
        val order = Order()
        order.userId = user.id
        order.price = dish.price
        order.dishName = dish.name
        order.dishAmount = 1
        order.dishPrice = dish.price

        firebaseDatabase.reference.child("order_table").push().setValue(order)
    }

    override fun onFavoriteClick(dish: Dish) {
        val mutList = mutableListOf<String>()
        for(e in user.favorites){
            mutList.add(e)
        }
        if(dish.isFavorite){
            for(e in user.favorites) {
                if (dish.id == e) {
                    mutList.remove(e)
                    break
                }
            }
        }else{
            mutList.add(dish.id)
        }

        user.favorites = mutList
        firebaseDatabase.reference.child("user_table").child(user.id).setValue(user)
    }

    private fun addUsersListener() {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (child in dataSnapshot.children) {
                    val userFromDb = child.getValue(User::class.java)
                    if (userFromDb != null) {
                        userFromDb.id = child.key.toString()
                        if (userFromDb.email == user.email) {
                            user = userFromDb
                            updateAdapter()
                            break
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        firebaseDatabase.reference.child("user_table").addValueEventListener(valueEventListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapter(){
        for(e in adapter.dataSet){
            e.isFavorite = e.id in user.favorites
        }
        adapter.notifyDataSetChanged()
    }

    private fun addDishesListener() {
        val valueEventListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dishes = mutableListOf<Dish>()
                for (child in dataSnapshot.children) {
                    val dish = child.getValue(Dish::class.java)
                    if(dish != null) {
                        if(dish.restaurantId == restaurant.id) {
                            if(dish.id in user.favorites){
                                dish.isFavorite = true
                            }
                            dish.id = child.key.toString()
                            dishes.add(dish)
                        }
                    }
                }

                adapter.dataSet = dishes
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        firebaseDatabase.reference.child("dish_table").addValueEventListener(valueEventListener)
    }
}