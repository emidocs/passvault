package com.example.passvault

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class VaultDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // The Companion Object holds our database constants so we don't misspell them later
    companion object {
        private const val DATABASE_NAME = "PasswordVault.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "vault_table"
        const val COL_ID = "ID"
        const val COL_PLATFORM = "PLATFORM" // e.g., "Student Portal"
        const val COL_USERNAME = "USERNAME"
        const val COL_PASSWORD = "PASSWORD" // We will store the encrypted password here
    }

    // onCreate is called the very first time the database is created
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PLATFORM + " TEXT, "
                + COL_USERNAME + " TEXT, "
                + COL_PASSWORD + " TEXT)")
        db.execSQL(createTable)
    }

    // onUpgrade handles updates if you ever change the database version
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Method to insert a new credential into the vault
    fun insertData(platform: String, username: String, password: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_PLATFORM, platform)
        contentValues.put(COL_USERNAME, username)
        contentValues.put(COL_PASSWORD, password)

        // db.insert returns -1 if there was an error
        val result = db.insert(TABLE_NAME, null, contentValues)
        return result != -1L
    }

    // Method to fetch all saved data to display on your HomeScreen
    fun getAllData(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }
}