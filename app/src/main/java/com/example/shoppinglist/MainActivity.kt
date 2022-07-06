package com.example.shoppinglist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    //------------Firebase Database Object Declaration-------------------
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    //------------Firebase Auth Object Declaration--------
    private lateinit var auth: FirebaseAuth

    //----------------------Declaring the widgets--------------------------------
    private lateinit var fab: FloatingActionButton
    private lateinit var listView: ListView
    private lateinit var shoppingBags: ImageView

    //----------------------Requirement for ListView---------------------------
    var shoppingList = ArrayList<ShoppingList>()
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //------------------Initializing the auth object-----------------------------
        auth = FirebaseAuth.getInstance()

        //------------------Initializing reference to Firebase Database--------------
        val referenceShoppingList: DatabaseReference =
            database.reference.child("Shopping List").child(auth.currentUser?.uid.toString())

        //------------------Initializing the widgets-----------------------------
        fab = findViewById(R.id.fab)
        listView = findViewById(R.id.listView)
        shoppingBags = findViewById(R.id.shoppingBags)

        //------------------Button click action---------------------------------
        fab.setOnClickListener {
            val alertDialog = ShoppingItemDialog(this, object : AddShoppingItem {
                override fun setData(name: String, amount: String) {

                    //---------------Insert data into firebase-------------------
                    val token: String = referenceShoppingList.push().key.toString()
                    id = token
                    val insertShoppingItem = ShoppingList(id, name, amount)

                    referenceShoppingList.child(token).setValue(insertShoppingItem)
                        .addOnCompleteListener { status ->
                            if (!status.isSuccessful) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Record insertion failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            })
            alertDialog.show()
        }

        //------------------Retrieve data from firebase----------------------------
        referenceShoppingList.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                shoppingList.clear()
                for (eachShoppingItem in snapshot.children) {
                    val getShoppingItem = eachShoppingItem.getValue(ShoppingList::class.java)
                    if (getShoppingItem != null) {
                        if (getShoppingItem.name.isNotEmpty() || getShoppingItem.amount.isNotEmpty()) {
                            shoppingList.add(getShoppingItem)
                        }
                    }
                }
                //------------Storing the item in ListView-------------------------
                listView.adapter =
                    ShoppingListAdapter(this@MainActivity, R.layout.shopping_item, shoppingList)
                shoppingBags.isVisible = shoppingList.isEmpty()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error in retrieving data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    //---------------Creating the options menu------------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    //--------------Adding functionality to options menu---------------
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_item -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }

            R.id.log_out_item -> {
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        return true
    }
}