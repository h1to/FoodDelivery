package com.example.fooddelivery.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.fooddelivery.R
import com.example.fooddelivery.databinding.FragmentLogInBinding
import com.google.firebase.auth.FirebaseAuth

class LogInFragment : Fragment() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: FragmentLogInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(firebaseAuth.currentUser != null){
            findNavController().navigate(R.id.action_logInFragment_to_mainFragment)
        }
        binding = FragmentLogInBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.forgotPasswordTextView.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_restorePasswordFragment)
        }

        binding.noAccountTextView.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_registerFragment)
        }

        binding.logInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if(email.isBlank()){
                binding.emailEditText.error = "Email is empty!"
            }else if(password.isBlank()){
                binding.passwordEditText.error = "Password is empty!"
            }else{
                binding.logInProgress.visibility = View.VISIBLE
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    binding.logInProgress.visibility = View.GONE
                    if(it.isSuccessful){
                        findNavController().navigate(R.id.action_logInFragment_to_mainFragment)
                    }else{
                        binding.emailEditText.error = "Email or password is not correct!"
                    }
                }
            }
        }
    }
}