package com.example.shoppinglist

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog

class ShoppingItemDialog(context: Context, private var addShoppingItem: AddShoppingItem): AppCompatDialog(context) {

    //-------------------Declaration----------------------------
    private lateinit var tvAdd: TextView
    private lateinit var tvCancel: TextView
    private lateinit var etName: EditText
    private lateinit var etAmount: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shopping_item_dialog)

        //-------------------Initialization-----------------------
        tvAdd = findViewById(R.id.tvAdd)!!
        tvCancel = findViewById(R.id.tvCancel)!!
        etName = findViewById(R.id.etName)!!
        etAmount = findViewById(R.id.etAmount)!!

        //------------------Adds Item-----------------------------
        tvAdd.setOnClickListener{
            val name = etName.text.toString()
            val amount = etAmount.text.toString()

            if(name.isEmpty() || amount.isEmpty()) {
                Toast.makeText(context, "Please enter all the information", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                addShoppingItem.setData(name, amount)
            }


            dismiss()
        }

        //--------------Cancels Add Item Prompt---------------------
        tvCancel.setOnClickListener {
            cancel()
        }
    }
}