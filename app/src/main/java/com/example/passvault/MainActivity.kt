package com.example.passvault
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
class MainActivity : AppCompatActivity() {
    lateinit var editTextName: EditText
    lateinit var editTextPassword: EditText
    lateinit var buttonSave: Button
    lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {  super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editTextName = findViewById(R.id.eTName)
        editTextPassword = findViewById(R.id.eTPassword)  buttonSave = findViewById(R.id.btnSave)
        textView = findViewById(R.id.result)
        buttonSave.setOnClickListener {
            val sharedPreferences= getSharedPreferences("Login Data",  MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val n = editTextName.text.toString()
            val p = editTextPassword.text.toString()
            editor.putString("Name", n)
            editor.putString("Password", p)
            editor.apply()
            Toast.makeText(this, "Data Saved",
                Toast.LENGTH_SHORT).show()
            val i = Intent(this, HomeScreen::class.java)  startActivity(i)
        }
    }
}
