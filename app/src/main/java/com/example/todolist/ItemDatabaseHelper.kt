package com.example.todolist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object ItemRecord: BaseColumns {
    const val TABLE_NAME = "items"
    const val COLUMN_NAME_CONTENT = "content"
    const val COLUMN_NAME_IS_COMPLETE = "is_complete"
    const val COLUMN_NAME_POSITION = "position"
}

private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${ItemRecord.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${ItemRecord.COLUMN_NAME_CONTENT} TEXT," +
            "${ItemRecord.COLUMN_NAME_IS_COMPLETE} BOOLEAN," +
            "${ItemRecord.COLUMN_NAME_POSITION} INTEGER)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${ItemRecord.TABLE_NAME}"


class ItemDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "todolist.db"
    }

    fun insertItem(item: Item) {
        val contentValues = ContentValues()
        val db = this.writableDatabase
        contentValues.put(ItemRecord.COLUMN_NAME_CONTENT, item.content)
        contentValues.put(ItemRecord.COLUMN_NAME_IS_COMPLETE, item.isComplete)
        contentValues.put(ItemRecord.COLUMN_NAME_POSITION, item.position)
        db.insert(ItemRecord.TABLE_NAME, null, contentValues)
        db.close()
    }

    fun deleteItem(item: Item) {
        val db = this.writableDatabase
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf("${item.id}")
        db.delete(ItemRecord.TABLE_NAME, selection, selectionArgs)
        db.close()
    }

    fun getAllItems(): MutableList<Item> {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${ItemRecord.TABLE_NAME}", null)
        var list = mutableListOf<Item>()
        if (cursor.moveToFirst()) {
            do {
                val content = cursor.getString(cursor.getColumnIndex(ItemRecord.COLUMN_NAME_CONTENT))
                val position = cursor.getInt(cursor.getColumnIndex(ItemRecord.COLUMN_NAME_POSITION))
                val id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
                val isComplete = cursor.getInt(cursor.getColumnIndex(ItemRecord.COLUMN_NAME_IS_COMPLETE)) > 0
                val newItem = Item(content, position, isComplete, id)
                list.add(newItem)
            } while (cursor.moveToNext())
        }
        db.close()
        return list
    }

    fun updateItem(item: Item) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(ItemRecord.COLUMN_NAME_CONTENT, item.content)
            put(ItemRecord.COLUMN_NAME_POSITION, item.position)
            put(ItemRecord.COLUMN_NAME_IS_COMPLETE, item.isComplete)
        }
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf("${item.id}")
        db.update(ItemRecord.TABLE_NAME, values, selection, selectionArgs)
        db.close()
    }

}
