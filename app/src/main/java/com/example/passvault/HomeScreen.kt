package com.example.passvault

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeScreen : AppCompatActivity() {

    lateinit var etPlatform: EditText
    lateinit var etVaultUsername: EditText
    lateinit var etVaultPassword: EditText
    lateinit var btnSaveToVault: Button
    lateinit var btnGoToVault: Button
    lateinit var btnLogout: Button
    lateinit var textView: TextView
    lateinit var dbHelper: VaultDatabaseHelper

    private var currentUserId: Int = -1
    private var currentUserName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        etPlatform = findViewById(R.id.etPlatform)
        etVaultUsername = findViewById(R.id.etVaultUsername)
        etVaultPassword = findViewById(R.id.etVaultPassword)
        btnSaveToVault = findViewById(R.id.btnSaveToVault)
        btnGoToVault = findViewById(R.id.btnGoToVault)
        btnGoToVault.paintFlags = btnGoToVault.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        btnLogout = findViewById(R.id.btnLogout)
        textView = findViewById(R.id.textView)

        dbHelper = VaultDatabaseHelper(this)

        currentUserId = intent.getIntExtra("CURRENT_USER_ID", -1)
        currentUserName = intent.getStringExtra("CURRENT_USER_NAME")

        textView.text = "Welcome to your Vault, $currentUserName"

        btnSaveToVault.setOnClickListener {
            val platform = etPlatform.text.toString()
            val username = etVaultUsername.text.toString()
            val rawPassword = etVaultPassword.text.toString()

            if (platform.isNotEmpty() && username.isNotEmpty() && rawPassword.isNotEmpty()) {
                val encryptedPassword = classicalEncrypt(rawPassword, 3)

                val isInserted = dbHelper.insertVaultData(currentUserId, platform, username, encryptedPassword)

                if (isInserted) {
                    Toast.makeText(this, "Secured in Vault!", Toast.LENGTH_SHORT).show()
                    etPlatform.text.clear()
                    etVaultUsername.text.clear()
                    etVaultPassword.text.clear()
                } else {
                    Toast.makeText(this, "Error saving to database", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoToVault.setOnClickListener {
            val intent = Intent(this, VaultActivity::class.java)
            intent.putExtra("CURRENT_USER_ID", currentUserId)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun classicalEncrypt(text: String, shift: Int): String {
        val result = StringBuilder()
        for (char in text) {
            if (char.isLetter()) {
                val base = if (char.isUpperCase()) 'A' else 'a'
                val encryptedChar = ((char - base + shift) % 26 + base.code).toChar()
                result.append(encryptedChar)
            } else {
                result.append(char)
            }
        }
        return result.toString()
    }
}