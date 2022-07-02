package com.example.shoppinglist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SettingsActivity : AppCompatActivity() {

    //-------------Declaring Firebase auth object-----------
    private lateinit var auth: FirebaseAuth

    //------------Firebase Database Object Declaration-------------------
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    //--------------Declaring the widgets-------------------
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var btnDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //------------Initializing Firebase auth object------
        auth = FirebaseAuth.getInstance()

        //---------Initializing the widgets------------------
        tvUserName = findViewById(R.id.tvUserName)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        btnDelete = findViewById(R.id.btnDelete)

        auth.currentUser?.let {
            for (profile in it.providerData) {
                tvUserName.text = profile.displayName
                tvUserEmail.text = profile.email
            }
        }

        btnDelete.setOnClickListener {
            //creates deleteAlertDialog to ask user for password
            val deleteAlertDialog = DeleteAlertDialog(this, object : DeleteAccount{

                //Re-authenticates user and deletes account and associated shopping list
                override fun accountDeleter(password: String) {

                    //stores the UID of the current user
                    val uid = auth.currentUser?.uid.toString()

                    //gets user details
                    val credential = EmailAuthProvider.getCredential(tvUserEmail.text.toString(), password)

                    //Initializes the loading dialog
                    val loadingDialog = LoadingDialog(this@SettingsActivity, "Please wait")
                    loadingDialog.show()

                    //Re-authenticates user
                    auth.currentUser?.reauthenticate(credential)?.addOnCompleteListener { task ->
                        if (task.isSuccessful){

                            //deletes the account
                            auth.currentUser!!.delete().addOnCompleteListener { status ->
                                if (status.isSuccessful) {

                                    //deletes associated database
                                    val referenceShoppingList: DatabaseReference = database.reference.child("Shopping List")
                                    referenceShoppingList.child(uid).removeValue().addOnCompleteListener { deletion ->

                                        //dismisses the loading dialog
                                        loadingDialog.dismiss()

                                        if (!deletion.isSuccessful) {
                                            Toast.makeText(this@SettingsActivity, "Unknown Error", Toast.LENGTH_SHORT).show()
                                        }

                                        startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
                                        finish()
                                    }
                                } else {
                                    Toast.makeText(this@SettingsActivity, "Unknown Error: Could not delete account", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this@SettingsActivity, "Incorrect Password", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            })
            //makes the deleteAlertDialog visible
            deleteAlertDialog.show()
        }
    }
}