package com.example.shoppinglist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegistrationActivity : AppCompatActivity() {

    //------------------Declaring the widgets------------------------------
    private lateinit var etRegUserName: EditText
    private lateinit var etRegEmail: EditText
    private lateinit var etRegPassword: EditText
    private lateinit var etRegConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvSignInNow: TextView

    //------------------Declaring Firebase Auth Object----------------------
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.hide()

        //------------Initializing Firebase auth object---------------------
        auth = FirebaseAuth.getInstance()

        //-------------Initializing the widgets-----------------------------
        etRegUserName = findViewById(R.id.etRegUserName)
        etRegEmail = findViewById(R.id.etRegEmail)
        etRegPassword = findViewById(R.id.etRegPassword)
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvSignInNow = findViewById(R.id.tvSignInNow)

        //----------Setting up the button click function--------------------
        btnSignUp.setOnClickListener {
            //------------Validating if the given information is correct--------
            if (etRegUserName.text.toString().isEmpty()) {
                //checks if the name is empty or not
                etRegUserName.error = "Please enter your name"
                etRegUserName.requestFocus()
                return@setOnClickListener
            }

            if(etRegEmail.text.toString().isEmpty()) {
                //checks if email is empty or not
                etRegEmail.error = "Please enter your email"
                etRegEmail.requestFocus()
                return@setOnClickListener
            } else if (!Patterns.EMAIL_ADDRESS.matcher(etRegEmail.text.toString()).matches()) {
                //checks if the entered email is valid
                etRegEmail.error = "Please enter a valid email address"
                etRegEmail.requestFocus()
                return@setOnClickListener
            }

            if (etRegPassword.text.toString().isEmpty()) {
                //checks if password is empty or not
                etRegPassword.error = "Password cannot be empty"
                etRegPassword.requestFocus()
                return@setOnClickListener
            }

            if (etRegConfirmPassword.text.toString().isEmpty()) {
                etRegConfirmPassword.error = "This field cannot be left empty"
                etRegConfirmPassword.requestFocus()
                return@setOnClickListener
            } else if (etRegPassword.text.toString() != etRegConfirmPassword.text.toString()) {
                etRegConfirmPassword.error = "Password does not match"
                etRegConfirmPassword.requestFocus()
                return@setOnClickListener
            } else {
                val loadingDialog = LoadingDialog(this, "Loading...!!")
                loadingDialog.show()
                //Registers user
                auth.createUserWithEmailAndPassword(etRegEmail.text.toString(), etRegPassword.text.toString()).addOnCompleteListener {task ->
                    loadingDialog.dismiss()
                    if (task.isSuccessful) {
                        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(etRegUserName.text.toString()).build()
                        auth.currentUser?.updateProfile(profileUpdates)
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        //-------------------Takes user to the LoginActivity------------------
        tvSignInNow.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}