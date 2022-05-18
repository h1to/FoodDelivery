package com.example.fooddelivery.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.fooddelivery.R
import com.example.fooddelivery.data.Dish
import com.example.fooddelivery.data.Restaurant
import com.example.fooddelivery.data.User
import com.example.fooddelivery.databinding.FragmentMainBinding
import com.example.fooddelivery.databinding.FragmentRegisterBinding
import com.example.fooddelivery.recyclerviews.RestaurantListener
import com.example.fooddelivery.recyclerviews.RestaurantRecyclerAdapter
import com.example.fooddelivery.services.DeliveryService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*

class MainFragment : Fragment(), RestaurantListener {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var binding: FragmentMainBinding
    private var firebaseUser = firebaseAuth.currentUser
    private var user: User? = null
    private val adapter = RestaurantRecyclerAdapter(listOf(), this)
    private var restaurantsList = listOf<Restaurant>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addUsersListener()
        addRestaurantsListener()

        binding.cartImage.setOnClickListener {
            if (user == null) {
                Toast.makeText(
                    context,
                    "Something went wrong, please try later!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                findNavController().navigate(
                    R.id.action_mainFragment_to_shoppingCartFragment,
                    bundleOf("user" to user)
                )
            }
        }

        binding.favoritesImage.setOnClickListener {
            if (user == null) {
                Toast.makeText(
                    context,
                    "Something went wrong, please try later!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                findNavController().navigate(
                    R.id.action_mainFragment_to_favoritesFragment,
                    bundleOf("user" to user)
                )
            }
        }

        binding.mopedImage.setOnClickListener {
            if (user == null) {
                Toast.makeText(
                    context,
                    "Something went wrong, please try later!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                findNavController().navigate(
                    R.id.action_mainFragment_to_deliveryFragment,
                    bundleOf("userId" to user!!.id)
                )
            }
        }

        binding.profileImage.setOnClickListener {
            if (user == null) {
                Toast.makeText(
                    context,
                    "Something went wrong, please try later!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                findNavController().navigate(
                    R.id.action_mainFragment_to_profileFragment,
                    bundleOf("user" to user)
                )
            }
        }

        binding.restaurantsRecycler.adapter = adapter

        binding.searchView.addTextChangedListener {
            if (it.toString().isNotBlank()) {
                val newList = mutableListOf<Restaurant>()
                for (e in restaurantsList) {
                    if (e.name.lowercase(Locale.getDefault())
                            .contains(it.toString().lowercase(Locale.getDefault()))
                    ) {
                        newList.add(e)
                    }
                }
                adapter.dataSet = newList
                adapter.notifyDataSetChanged()
            } else {
                adapter.dataSet = restaurantsList
                adapter.notifyDataSetChanged()
            }
        }

    }

    private fun addUsersListener() {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (child in dataSnapshot.children) {
                    val userFromDb = child.getValue(User::class.java)
                    if (userFromDb != null) {
                        userFromDb.id = child.key.toString()
                        if (userFromDb.email == firebaseUser!!.email) {
                            user = userFromDb
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


    private fun addRestaurantsListener() {
        val valueEventListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val restaurants = mutableListOf<Restaurant>()
                for (child in dataSnapshot.children) {
                    val restaurant = child.getValue(Restaurant::class.java)
                    if(restaurant != null) {
                        restaurant.id = child.key.toString()
                        restaurants.add(restaurant)
                    }
                }
                restaurantsList = restaurants
                adapter.dataSet = restaurants
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        firebaseDatabase.reference.child("restaurant_table").addValueEventListener(valueEventListener)
    }

    override fun onCLick(restaurant: Restaurant) {
        val bundle = bundleOf("restaurant" to restaurant, "user" to user)
        findNavController().navigate(R.id.action_mainFragment_to_restaurantFragment, bundle)
    }
}