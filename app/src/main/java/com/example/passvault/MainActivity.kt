package com.example.passvault

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var editTextName: EditText
    lateinit var editTextPassword: EditText
    lateinit var btnLogin: Button
    lateinit var btnRegister: Button
    lateinit var dbHelper: VaultDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextName = findViewById(R.id.eTName)
        editTextPassword = findViewById(R.id.eTPassword)
        btnLogin = findViewById(R.id.btnSave)
        btnRegister = findViewById(R.id.btnRegister)
        btnRegister.paintFlags = btnRegister.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        dbHelper = VaultDatabaseHelper(this)

        btnLogin.setOnClickListener {
            val n = editTextName.text.toString()
            val p = editTextPassword.text.toString()

            val userId = dbHelper.authenticateUser(n, p)
            if (userId != -1) {
                Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show()
                openHomeScreen(userId, n)
            } else {
                Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            val n = editTextName.text.toString()
            val p = editTextPassword.text.toString()

            if (n.isNotEmpty() && p.isNotEmpty()) {
                val newUserId = dbHelper.registerUser(n, p)
                if (newUserId != -1L) {
                    Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show()
                    openHomeScreen(newUserId.toInt(), n)
                } else {
                    Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields first!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openHomeScreen(userId: Int, userName: String) {
        val intent = Intent(this, HomeScreen::class.java)
        intent.putExtra("CURRENT_USER_ID", userId)
        intent.putExtra("CURRENT_USER_NAME", userName)
        startActivity(intent)
        finish()
    }
}