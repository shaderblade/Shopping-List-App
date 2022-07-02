package com.example.shoppinglist

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog

class LoadingDialog(context: Context, private val loadingMessage: String): AppCompatDialog(context) {
    private lateinit var tvLoginMessage: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_item)

        this.setCancelable(false)

        tvLoginMessage = findViewById(R.id.tvLoginMessage)!!
        tvLoginMessage.text = loadingMessage
    }
}