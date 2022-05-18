package com.example.fooddelivery.screens.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.example.fooddelivery.R
import com.example.fooddelivery.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var user: User
    private lateinit var vieww: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = requireArguments().get("user") as User
        vieww = view

        view.findViewById<Button>(R.id.save_password_button).setOnClickListener {
            val progressBar = view.findViewById<ProgressBar>(R.id.password_update_progress)
            val newPassText = view.findViewById<EditText>(R.id.new_password_edit_text)
            val newPass = newPassText.text.toString()
            if(newPass.isBlank()){
                newPassText.error = "Password is empty!"
            }else{
                progressBar.visibility = View.VISIBLE
                firebaseAuth.currentUser!!.updatePassword(newPass).addOnCompleteListener {
                    progressBar.visibility = View.INVISIBLE
                    if(it.isSuccessful){
                        findNavController().navigateUp()
                    }else{
                        newPassText.error = "Password is incorrect!"
                    }
                }
            }
        }

        view.findViewById<ImageView>(R.id.back_button).setOnClickListener {
            findNavController().navigateUp()
        }
    }

}