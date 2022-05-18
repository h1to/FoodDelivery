package com.example.fooddelivery.screens.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.fooddelivery.R
import com.example.fooddelivery.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class AddressFragment : Fragment(R.layout.fragment_address) {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var user: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = requireArguments().get("user") as User

        if (user.address.isNotBlank()) {
            view.findViewById<TextView>(R.id.address_text).text = user.address
        }

        view.findViewById<Button>(R.id.save_address_button).setOnClickListener {
            val textView = view.findViewById<EditText>(R.id.address_edit_text)
            val newAddress = textView.text.toString()
            if (newAddress.isBlank()) {
                textView.error = "Address is empty!"
            } else {
                user.address = newAddress
                firebaseDatabase.reference.child("user_table").child(user.id).setValue(user)
                findNavController().navigateUp()
            }
        }

        view.findViewById<ImageView>(R.id.back_button).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}