package com.ecommerce.shopmitt.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class BaseDB(mContext: Context) : SQLiteOpenHelper(mContext, DB_ALIS, null, DB_VERSION) {

    companion object {
        const val FIELD_ID = "id"
        var DB_ALIS = "app_db"
        var DB_VERSION = 1
    }

    override fun onCreate(p0: SQLiteDatabase?) {

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }


}