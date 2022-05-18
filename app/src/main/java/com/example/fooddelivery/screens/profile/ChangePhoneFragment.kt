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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.fooddelivery.R
import com.example.fooddelivery.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChangePhoneFragment : Fragment(R.layout.fragment_change_phone) {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var user: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = requireArguments().get("user") as User

        view.findViewById<TextView>(R.id.old_phone_number_text_view).text = user.phone

        view.findViewById<Button>(R.id.save_number_button).setOnClickListener {
            val textView = view.findViewById<EditText>(R.id.new_phone_number_edit_text)
            val newPhone = textView.text.toString()
            if(newPhone.isBlank()){
                textView.error = "Number is empty!"
            }else{
                user.phone = newPhone
                firebaseDatabase.reference.child("user_table").child(user.id).setValue(user)
                findNavController().navigateUp()
            }
        }

        view.findViewById<ImageView>(R.id.back_button).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}