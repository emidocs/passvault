package com.example.passvault
import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import android.widget.EditText

import androidx.appcompat.app.AppCompatActivity
class HomeScreen : AppCompatActivity() {
    lateinit var resultTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {  super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)  resultTextView = findViewById(R.id.result)
        val sharedPreferences = getSharedPreferences("Login Data",  MODE_PRIVATE)
        val name = sharedPreferences.getString("Name", "")  val password = sharedPreferences.getString("Password", "")
        resultTextView.text = "Name: $name\nPassword: $password"
    }
}
