package com.example.fooddelivery.screens.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import androidx.navigation.fragment.findNavController
import com.example.fooddelivery.R
import com.example.fooddelivery.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PaymentMethodFragment : Fragment(R.layout.fragment_payment_method) {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var user: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = requireArguments().get("user") as User

        val radioButtonCash = view.findViewById<RadioButton>(R.id.cash_radio_button)
        val radioButtonCard = view.findViewById<RadioButton>(R.id.card_radio_button)
        val textView = view.findViewById<EditText>(R.id.card_number_edit_text)

        if(user.paymentMethod.isBlank()){
            radioButtonCash.isChecked = true
            radioButtonCard.isChecked = false
        }else{
            radioButtonCard.isChecked = true
            radioButtonCash.isChecked = false
        }

        view.findViewById<Button>(R.id.save_payment_button).setOnClickListener {
            if(radioButtonCash.isChecked){
                user.paymentMethod = ""
                firebaseDatabase.reference.child("user_table").child(user.id).setValue(user)
            }else{
                if(textView.text.toString().isBlank() || textView.text.toString().length != 16){
                    textView.error = "INCORRECT"
                }else{
                    user.paymentMethod = textView.text.toString()
                    firebaseDatabase.reference.child("user_table").child(user.id).setValue(user)
                    findNavController().navigateUp()
                }
            }
        }

        view.findViewById<ImageView>(R.id.back_button).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}