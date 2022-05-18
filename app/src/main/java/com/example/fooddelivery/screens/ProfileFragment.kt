package com.example.fooddelivery.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.fooddelivery.R
import com.example.fooddelivery.data.Restaurant
import com.example.fooddelivery.data.User
import com.example.fooddelivery.databinding.FragmentMainBinding
import com.example.fooddelivery.databinding.FragmentProfileBinding
import com.example.fooddelivery.recyclerviews.RestaurantRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var binding: FragmentProfileBinding
    private var firebaseUser = firebaseAuth.currentUser
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        addUsersListener()

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = requireArguments().get("user") as User
        updateUI()

        binding.exitImage.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.changePasswordLayout.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment, bundleOf("user" to user))
        }
        binding.changePhoneLayout.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changePhoneFragment, bundleOf("user" to user))
        }
        binding.paymentLayout.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_paymentMethodFragment, bundleOf("user" to user))
        }
        binding.addressLayout.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_addressFragment, bundleOf("user" to user))
        }
        binding.logoutText.setOnClickListener {
            firebaseAuth.signOut()
            findNavController().navigate(R.id.action_profileFragment_to_logInFragment)
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
                            updateUI()
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

    private fun updateUI(){
        binding.nameTextView.text = user!!.name
        binding.emailTextView.text = user!!.email
    }
}