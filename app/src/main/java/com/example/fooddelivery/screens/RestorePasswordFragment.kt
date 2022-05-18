package com.example.fooddelivery.screens

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.fooddelivery.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class RestorePasswordFragment : Fragment(R.layout.fragment_restore_password) {
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editText = view.findViewById<EditText>(R.id.email_to_Restore_password_edit_text)
        val progressBar = view.findViewById<ProgressBar>(R.id.restore_password_progress)

        view.findViewById<Button>(R.id.restore_password_button).setOnClickListener {
            progressBar.visibility = View.VISIBLE
            firebaseAuth.sendPasswordResetEmail(editText.text.toString())
                .addOnCompleteListener {
                    progressBar.visibility = View.GONE
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Link has been sent to your email!", Toast.LENGTH_SHORT).show()
                        Handler().postDelayed({
                            findNavController().navigateUp()
                        }, 1000)
                    } else {
                        editText.error = "Account with this email does not exist!"
                    }
                }
        }
    }
}