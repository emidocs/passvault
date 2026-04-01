package com.example.passvault

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class VaultDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MultiUserVault.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users_table"
        const val COL_USER_ID = "USER_ID"
        const val COL_MASTER_NAME = "MASTER_NAME"
        const val COL_MASTER_PASS = "MASTER_PASS"

        const val TABLE_VAULT = "vault_table"
        const val COL_VAULT_ID = "VAULT_ID"
        const val COL_OWNER_ID = "OWNER_ID"
        const val COL_PLATFORM = "PLATFORM"
        const val COL_USERNAME = "USERNAME"
        const val COL_PASSWORD = "PASSWORD"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_MASTER_NAME + " TEXT, "
                + COL_MASTER_PASS + " TEXT)")
        db.execSQL(createUsersTable)

        val createVaultTable = ("CREATE TABLE " + TABLE_VAULT + " ("
                + COL_VAULT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_OWNER_ID + " INTEGER, "
                + COL_PLATFORM + " TEXT, "
                + COL_USERNAME + " TEXT, "
                + COL_PASSWORD + " TEXT, "
                + "FOREIGN KEY(" + COL_OWNER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))")
        db.execSQL(createVaultTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_VAULT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun registerUser(name: String, pass: String): Long {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COL_MASTER_NAME = ?", arrayOf(name))
        if (cursor.count > 0) {
            cursor.close()
            return -1L
        }
        cursor.close()

        val contentValues = ContentValues()
        contentValues.put(COL_MASTER_NAME, name)
        contentValues.put(COL_MASTER_PASS, pass)
        return db.insert(TABLE_USERS, null, contentValues)
    }

    fun authenticateUser(name: String, pass: String): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COL_USER_ID FROM $TABLE_USERS WHERE $COL_MASTER_NAME = ? AND $COL_MASTER_PASS = ?", arrayOf(name, pass))
        var id = -1
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0)
        }
        cursor.close()
        return id
    }

    fun insertVaultData(userId: Int, platform: String, username: String, password: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_OWNER_ID, userId)
        contentValues.put(COL_PLATFORM, platform)
        contentValues.put(COL_USERNAME, username)
        contentValues.put(COL_PASSWORD, password)

        val result = db.insert(TABLE_VAULT, null, contentValues)
        return result != -1L
    }

    fun getUserVaultData(userId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_VAULT WHERE $COL_OWNER_ID = ?", arrayOf(userId.toString()))
    }

    fun searchUserVaultData(userId: Int, query: String): Cursor {
        val db = this.readableDatabase
        val sqlQuery = "SELECT * FROM $TABLE_VAULT WHERE $COL_OWNER_ID = ? AND ($COL_PLATFORM LIKE ? OR $COL_USERNAME LIKE ?)"
        return db.rawQuery(sqlQuery, arrayOf(userId.toString(), "%$query%", "%$query%"))
    }
}