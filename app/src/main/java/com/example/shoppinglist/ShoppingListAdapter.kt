package com.example.shoppinglist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ShoppingListAdapter(
    private var location: Context,
    private var shoppingItemLayout: Int,
    private var shoppingListArray: ArrayList<ShoppingList>
) : ArrayAdapter<ShoppingList>(location, shoppingItemLayout, shoppingListArray) {

    //------------Firebase  Database Declaration-------------------
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    //------------Firebase Auth Object Initialization--------
    private val auth = FirebaseAuth.getInstance()


    private val referenceShoppingList: DatabaseReference =
        database.reference.child("Shopping List").child(auth.currentUser?.uid.toString())

    //------------------Inflate view-----------------------------------
    private var layoutInflater: LayoutInflater = LayoutInflater.from(location)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val shoppingViewHolder: ShoppingViewHolder

        if (convertView == null) {
            view = layoutInflater.inflate(shoppingItemLayout, null)
            shoppingViewHolder = ShoppingViewHolder(view)
            view.tag = shoppingViewHolder
        } else {
            view = convertView
            shoppingViewHolder = view.tag as ShoppingViewHolder
        }

        //----------------------Initializing the widgets----------------------
        shoppingViewHolder.tvName.text = shoppingListArray[position].name
        shoppingViewHolder.tvAmount.text = shoppingListArray[position].amount

        //-------------------Removes the shopping item----------------------
        shoppingViewHolder.ivDelete.setOnClickListener {
            referenceShoppingList.child(shoppingListArray[position].id).removeValue()
                .addOnCompleteListener { status ->
                    if (!status.isSuccessful) {
                        Toast.makeText(location, "Deletion failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        //-----------------Decreases the amount of item----------------------
        shoppingViewHolder.ivMinus.setOnClickListener {
            var amountDecrease = shoppingListArray[position].amount.toInt()
            if (amountDecrease > 0) {
                amountDecrease--
                shoppingListArray[position].amount = amountDecrease.toString()

                shoppingListUpdater(
                    shoppingListArray[position].id,
                    shoppingListArray[position].amount
                )
            }
        }

        //---------------Increases the amount of item-------------------------
        shoppingViewHolder.ivPlus.setOnClickListener {
            var amountIncrease = shoppingListArray[position].amount.toInt()
            amountIncrease++
            shoppingListArray[position].amount = amountIncrease.toString()

            shoppingListUpdater(shoppingListArray[position].id, shoppingListArray[position].amount)
        }

        return view
    }

    //--------------------Increases or decreases the amount of a Shopping Item--------------------
    private fun shoppingListUpdater(id: String, amount: String) {
        val shoppingListUpdate = mutableMapOf<String, Any>()
        shoppingListUpdate["amount"] = amount
        referenceShoppingList.child(id).updateChildren(shoppingListUpdate)
            .addOnCompleteListener { status ->
                if (!status.isSuccessful) {
                    Toast.makeText(location, "Record update failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //-----------------Inner class to remember the view-------------------------------
    inner class ShoppingViewHolder(shoppingView: View) {
        var tvName: TextView = shoppingView.findViewById(R.id.tvName)
        var tvAmount: TextView = shoppingView.findViewById(R.id.tvAmount)
        var ivDelete: ImageView = shoppingView.findViewById(R.id.ivDelete)
        var ivMinus: ImageView = shoppingView.findViewById(R.id.ivMinus)
        var ivPlus: ImageView = shoppingView.findViewById(R.id.ivPlus)
    }
}
