package com.example.passvault

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class VaultActivity : AppCompatActivity() {

    lateinit var tvVaultDisplay: TextView
    lateinit var etSearch: EditText
    lateinit var btnBack: TextView
    lateinit var btnClear: ImageButton
    lateinit var dbHelper: VaultDatabaseHelper

    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vault)

        tvVaultDisplay = findViewById(R.id.tvVaultDisplay)
        etSearch = findViewById(R.id.etSearch)
        btnBack = findViewById(R.id.btnBack)
        btnClear = findViewById(R.id.btnClear)
        dbHelper = VaultDatabaseHelper(this)

        currentUserId = intent.getIntExtra("CURRENT_USER_ID", -1)

        loadVaultData("")

        // 1. Back Button Logic
        btnBack.setOnClickListener {
            finish() // This perfectly mimics the universal Android back button
        }

        // 2. Clear (X) Button Logic
        btnClear.setOnClickListener {
            etSearch.text.clear() // Empties the search bar
        }

        // 3. Search Bar Logic
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Show the 'X' button if there is text, hide it if empty
                if (s.isNullOrEmpty()) {
                    btnClear.visibility = View.GONE
                } else {
                    btnClear.visibility = View.VISIBLE
                }

                loadVaultData(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadVaultData(query: String) {
        val cursor = if (query.isEmpty()) {
            dbHelper.getUserVaultData(currentUserId)
        } else {
            dbHelper.searchUserVaultData(currentUserId, query)
        }

        if (cursor.count == 0) {
            if (query.isEmpty()) {
                tvVaultDisplay.text = "Your vault is currently empty."
            } else {
                tvVaultDisplay.text = "No matches found for '$query'."
            }
            cursor.close()
            return
        }

        val buffer = StringBuilder()
        buffer.append("--- DECRYPTED VAULT DATA ---\n\n")

        while (cursor.moveToNext()) {
            val platform = cursor.getString(cursor.getColumnIndexOrThrow(VaultDatabaseHelper.COL_PLATFORM))
            val username = cursor.getString(cursor.getColumnIndexOrThrow(VaultDatabaseHelper.COL_USERNAME))
            val encryptedPassword = cursor.getString(cursor.getColumnIndexOrThrow(VaultDatabaseHelper.COL_PASSWORD))

            val decryptedPassword = classicalDecrypt(encryptedPassword, 3)

            buffer.append("Platform: $platform\n")
            buffer.append("Username: $username\n")
            buffer.append("Password: $decryptedPassword\n")
            buffer.append("------------------------\n")
        }
        tvVaultDisplay.text = buffer.toString()
        cursor.close()
    }

    private fun classicalDecrypt(text: String, shift: Int): String {
        val result = StringBuilder()
        for (char in text) {
            if (char.isLetter()) {
                val base = if (char.isUpperCase()) 'A' else 'a'
                var decryptedChar = (char - base - shift) % 26
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