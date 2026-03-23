package com.example.passvault

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
    lateinit var resultTextView: TextView

    lateinit var dbHelper: VaultDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        // Bind UI elements
        etPlatform = findViewById(R.id.etPlatform)
        etVaultUsername = findViewById(R.id.etVaultUsername)
        etVaultPassword = findViewById(R.id.etVaultPassword)
        btnSaveToVault = findViewById(R.id.btnSaveToVault)
        resultTextView = findViewById(R.id.result)

        dbHelper = VaultDatabaseHelper(this)

        // Load existing data when the screen opens
        loadVaultData()

        // Handle saving a new password
        btnSaveToVault.setOnClickListener {
            val platform = etPlatform.text.toString()
            val username = etVaultUsername.text.toString()
            val rawPassword = etVaultPassword.text.toString()

            if (platform.isNotEmpty() && username.isNotEmpty() && rawPassword.isNotEmpty()) {

                // 1. Encrypt the password before storing it
                val encryptedPassword = classicalEncrypt(rawPassword, 3)

                // 2. Save to SQLite
                val isInserted = dbHelper.insertData(platform, username, encryptedPassword)

                if (isInserted) {
                    Toast.makeText(this, "Secured in Vault!", Toast.LENGTH_SHORT).show()
                    // Clear the input fields
                    etPlatform.text.clear()
                    etVaultUsername.text.clear()
                    etVaultPassword.text.clear()

                    // 3. Refresh the displayed list immediately
                    loadVaultData()
                } else {
                    Toast.makeText(this, "Error saving to database", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to fetch, decrypt, and display the data
    private fun loadVaultData() {
        val cursor = dbHelper.getAllData()

        if (cursor.count == 0) {
            val sharedPreferences = getSharedPreferences("Login Data", MODE_PRIVATE)
            val name = sharedPreferences.getString("Name", "User")
            resultTextView.text = "Logged in as: $name\nYour vault is currently empty."
            return
        }

        val buffer = StringBuilder()
        buffer.append("--- YOUR SECURE VAULT ---\n\n")

        // Loop through the database rows
        while (cursor.moveToNext()) {
            val platform = cursor.getString(1) // Column 1 is Platform
            val username = cursor.getString(2) // Column 2 is Username
            val encryptedPassword = cursor.getString(3) // Column 3 is Password

            // Decrypt the password before showing it
            val decryptedPassword = classicalDecrypt(encryptedPassword, 3)

            buffer.append("Platform: $platform\n")
            buffer.append("Username: $username\n")
            buffer.append("Password: $decryptedPassword\n")
            buffer.append("------------------------\n")
        }

        // Display the final text on the screen
        resultTextView.text = buffer.toString()
    }

    // Encrypts the password by shifting letters forward
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

    // Decrypts the password by shifting letters backward
    private fun classicalDecrypt(text: String, shift: Int): String {
        val result = StringBuilder()
        for (char in text) {
            if (char.isLetter()) {
                val base = if (char.isUpperCase()) 'A' else 'a'
                var decryptedChar = (char - base - shift) % 26
                // Handle negative numbers to wrap around the alphabet correctly
                if (decryptedChar < 0) {
                    decryptedChar += 26
                }
                result.append((decryptedChar + base.code).toChar())
            } else {
                result.append(char)
            }
        }
        return result.toString()
    }
}