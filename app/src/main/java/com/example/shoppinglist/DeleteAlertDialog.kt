package com.example.shoppinglist

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog

class DeleteAlertDialog(context: Context, private val deleteAccount: DeleteAccount): AppCompatDialog(context) {

    //-------------------Declaring Widgets--------------------------
    private lateinit var etConfirmingPass: EditText
    private lateinit var tvPassCancel: TextView
    private lateinit var tvDeleteAcc: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.delete_alert_dialog)

        //--------------Initializing Widgets-----------------------
        etConfirmingPass = findViewById(R.id.etConfirmingPass)!!
        tvPassCancel = findViewById(R.id.tvPassCancel)!!
        tvDeleteAcc = findViewById(R.id.tvDeleteAcc)!!

        //-------------Cancelling the delete account prompt--------
        tvPassCancel.setOnClickListener {
            cancel()
        }

        //-------------Provides the password for deletion----------
        tvDeleteAcc.setOnClickListener {
            if (etConfirmingPass.text.toString().isEmpty()) {
                Toast.makeText(context, "Please enter the password to continue", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                deleteAccount.accountDeleter(etConfirmingPass.text.toString())
            }

            dismiss()
        }
    }
}