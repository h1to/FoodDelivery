package com.example.fooddelivery.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.fooddelivery.R
import com.example.fooddelivery.data.User
import com.example.fooddelivery.databinding.FragmentLogInBinding
import com.example.fooddelivery.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class RegisterFragment : Fragment() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val  firebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val phone = binding.phoneEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if(email.isBlank()){
                binding.emailEditText.error = "Email is empty!"
            } else if(name.isBlank()){
                binding.nameEditText.error = "Name is empty!"
            } else if(phone.isBlank()){
                binding.phoneEditText.error = "Phone is empty!"
            } else if(password.isBlank()){
                binding.emailEditText.error = "Password is empty!"
            }else{
                binding.registerProgress.visibility = View.VISIBLE
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    binding.registerProgress.visibility = View.GONE
                    if(it.isSuccessful){
                        val user = User()
                        user.email = email
                        user.name = name
                        user.phone = phone

                        firebaseDatabase.reference.child("user_table").push().setValue(user)
                        findNavController().navigateUp()
                    }else{
                        binding.emailEditText.error = "Email and password is wrong or email already exists!"
                    }
                }
            }
        }
    }
}