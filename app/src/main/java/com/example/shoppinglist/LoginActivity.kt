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

class LoginActivity : AppCompatActivity() {

    //--------------Declaring the widgets-------------------
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignIn: Button
    private lateinit var tvRegisterNow: TextView

    //-------------Declaring Firebase auth object-----------
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        //------------Initializing Firebase auth object---------------------
        auth = FirebaseAuth.getInstance()

        //---------Initializing the widgets-----------------
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignIn = findViewById(R.id.btnSignIn)
        tvRegisterNow = findViewById(R.id.tvRegisterNow)



        //--------------Setting up button click function------------------
        btnSignIn.setOnClickListener {
            //------------Validating if the given information is correct--------
            if(etEmail.text.toString().isEmpty()) {
                //checks if email is empty or not
                etEmail.error = "Please enter your email"
                etEmail.requestFocus()
                return@setOnClickListener
            } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
                //checks if the entered email is valid
                etEmail.error = "Please enter a valid email address"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (etPassword.text.toString().isEmpty()) {
                //checks if the password field is empty
                etPassword.error = "Please enter the password"
                etPassword.requestFocus()
                return@setOnClickListener
            } else {
                val loadingDialog = LoadingDialog(this, "Logging you in...")
                loadingDialog.show()
                //executes if email and password are non-empty and valid
                auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString()).addOnCompleteListener { task ->
                    loadingDialog.dismiss()
                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "The email or password that you entered is incorrect", Toast.LENGTH_SHORT).show()
                        etPassword.setText("")
                    }
                }
            }
        }

        //-------------------Takes user to the RegistrationActivity------------------
        tvRegisterNow.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }
    }
}
