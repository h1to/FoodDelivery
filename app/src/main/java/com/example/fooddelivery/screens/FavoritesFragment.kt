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
import com.example.fooddelivery.recyclerviews.DishListener
import com.example.fooddelivery.recyclerviews.DishRecyclerAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FavoritesFragment : Fragment(), DishListener {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val adapter = DishRecyclerAdapter(listOf(), this)

    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = requireArguments().get("user") as User
        addUsersListener()
        addDishesListener()
        view.findViewById<RecyclerView>(R.id.fav_dishes_recycler).adapter = adapter
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
        for (e in user.favorites) {
            mutList.add(e)
        }
        if (dish.isFavorite) {
            for (e in user.favorites) {
                if (dish.id == e) {
                    mutList.remove(e)
                    break
                }
            }
        } else {
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
                            break
                        }
                    }
                }

                getFavorites()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        firebaseDatabase.reference.child("user_table").addValueEventListener(valueEventListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getFavorites() {
        val list = mutableListOf<Dish>()
        for (e in adapter.dataSet) {
            if(e.id in user.favorites){
                list.add(e)
            }
        }
        adapter.dataSet = list
        adapter.notifyDataSetChanged()
    }

    private fun addDishesListener() {
        val valueEventListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dishes = mutableListOf<Dish>()
                for (child in dataSnapshot.children) {
                    val dish = child.getValue(Dish::class.java)
                    if (dish != null) {
                        dish.id = child.key.toString()
                        if(dish.id in user.favorites) {
                            dish.isFavorite = true
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