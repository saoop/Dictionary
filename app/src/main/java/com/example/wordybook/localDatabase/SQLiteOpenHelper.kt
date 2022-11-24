package com.example.wordybook

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.wordybook.models.ItemCollection
import com.example.wordybook.models.ItemWord
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SqliteOpenHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "wordyBook.db"
        private const val TABLE_COLLECTIONS = "collections"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_COLLECTION_NAME = "collection_name"
        private const val COLUMN_COLLECTION_DATE = "collection_date"
        private const val COLUMN_WORD_ID = "_word_id"
        private const val COLUMN_WORD = "word"
        private const val COLUMN_TRANSLATION = "translation"
        private const val COLUMN_WORD_DATE = "word_date"
        private const val COLUMN_WORD_DATE_LAST_REVIEWED = "COLUMN_WORD_DATE_LAST_REVIEWED"
        private const val COLUMN_WORD_LEVEL = "column_word_level"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_COLLECTIONS ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_COLLECTION_NAME TEXT, $COLUMN_COLLECTION_DATE TEXT)"
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + TABLE_COLLECTIONS)
    }

    fun addValue(name: String, date: String) {

        val values = ContentValues()
        values.put(COLUMN_COLLECTION_NAME, name)
        values.put(COLUMN_COLLECTION_DATE, date)
        val db = this.writableDatabase
        db.insert(TABLE_COLLECTIONS, null, values)
        db.close()
    }

    fun getAllCollections(): ArrayList<ItemCollection> {
        val list = ArrayList<ItemCollection>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_COLLECTIONS", null)

        while (cursor.moveToNext()) {

            val name = (cursor.getString(cursor.getColumnIndex(COLUMN_COLLECTION_NAME)))
            val date = (cursor.getString(cursor.getColumnIndex(COLUMN_COLLECTION_DATE)))
            val collection = ItemCollection(name, date)
            list.add(collection)
        }
        cursor.close()
        db.close()
        return list
    }

    fun createNewTableWords(_name: String){
        val db = this.writableDatabase
        val name = _name.lowercase().replace("\\s".toRegex(), "")
        val CREATE_WORD_TABLE = "CREATE TABLE $name ($COLUMN_WORD_ID INTEGER PRIMARY KEY," +
                " $COLUMN_WORD TEXT, $COLUMN_TRANSLATION TEXT, $COLUMN_WORD_DATE TEXT, " +
                "$COLUMN_WORD_DATE_LAST_REVIEWED TEXT, $COLUMN_WORD_LEVEL INTEGER)"
        db.execSQL(CREATE_WORD_TABLE)
        db.close()
    }

    fun addWord(tableName: String, word: String, translation: String, date: String){
        val _tableName = tableName.lowercase().replace("\\s".toRegex(), "")
        val values = ContentValues()
        values.put(COLUMN_WORD, word)
        values.put(COLUMN_TRANSLATION, translation)
        values.put(COLUMN_WORD_DATE, date)
        values.put(COLUMN_WORD_LEVEL, 0)
        val db = this.writableDatabase
        db.insert(_tableName, null, values)

        db.close()
    }

    fun getWords(_tableName: String): ArrayList<ItemWord>{
        val list = ArrayList<ItemWord>()
        val db = this.readableDatabase
        val tableName = _tableName.lowercase().replace("\\s".toRegex(), "")
        val cursor = db.rawQuery("SELECT * FROM $tableName", null)
        while (cursor.moveToNext()){
            val word = cursor.getString(cursor.getColumnIndex(COLUMN_WORD))
            val translation = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSLATION))
            //val dateAdded = cursor.getString(cursor.getColumnIndex(COLUMN_WORD_DATE))
            list.add(ItemWord(word, translation))
        }
        db.close()
        return list
    }

    fun deleteCollection(tableName: String){
        val tN = tableName.lowercase().replace("\\s".toRegex(), "")
        val delete_collection = "DELETE FROM $TABLE_COLLECTIONS WHERE $COLUMN_COLLECTION_NAME = " + "'" + tableName + "'"
        val delete_table_words = "DROP TABLE " + "'" + tN + "'"
        this.writableDatabase.execSQL(delete_collection)
        this.writableDatabase.execSQL(delete_table_words)
    }

    fun deleteWord(tableName: String, word: String){
        val _tableName = tableName.lowercase().replace("\\s".toRegex(), "")
        val delete_word = "DELETE FROM " + "'" + _tableName + "'" + " WHERE $COLUMN_WORD =" + "'" + word + "'"
        val db = this.writableDatabase
        db.execSQL(delete_word)
        db.close()

    }

    fun editWord(tableName: String, oldItemWord: ItemWord, newItemWord: ItemWord){
        val _tableName = tableName.lowercase().replace("\\s".toRegex(), "")
        //val edit_word_word = "UPDATE $_tableName SET $COLUMN_WORD = ${newItemWord.word} WHERE $COLUMN_WORD = ${oldItemWord.word}"
        //val edit_word_translation = "UPDATE $_tableName SET $COLUMN_TRANSLATION = ${newItemWord.translation} WHERE $COLUMN_WORD = ${oldItemWord.word}"
        val cv = ContentValues()
        cv.put(COLUMN_WORD, newItemWord.word)
        cv.put(COLUMN_TRANSLATION, newItemWord.translation)

        val db = this.writableDatabase
        //db.execSQL(edit_word_word)
        //db.execSQL(edit_word_translation)

        db.update(_tableName, cv, COLUMN_WORD + " = "  + "'" + oldItemWord.word + "'", null)
        db.close()
    }

    fun setNewReviewDate(tableName: String, word: ItemWord){
        val _tableName = tableName.lowercase().replace("\\s".toRegex(), "")
        val cv = ContentValues()
        val db = this.writableDatabase

        val date = Constants.getCurrentDate()

        cv.put(COLUMN_WORD_DATE_LAST_REVIEWED, date)
        db.update(_tableName, cv, COLUMN_WORD + " = " + "'" + word.word + "'", null)
        db.close()
    }

    fun getNumberOfRowsInTable(tableName: String): Long{
        val _tableName = tableName.lowercase().replace("\\s".toRegex(), "")
        val db = this.readableDatabase
        val count = DatabaseUtils.queryNumEntries(db, _tableName)
        db.close()
        return count
    }

    fun getLastSevenDaysStats(tableName: String): ArrayList<Pair<String, Int>> {
        val _tableName = tableName.lowercase().replace("\\s".toRegex(), "")
        val db = this.readableDatabase
        val cal  = Calendar.getInstance()
        val datesWords =  ArrayList<Pair<String, Int>>()
        val sdf = SimpleDateFormat(Constants.SDF, Locale.getDefault())

        for (i in 1..7){
            val answ = sdf.format(cal.time)
            val _count = "SELECT COUNT(*) FROM " + "'"  + _tableName + "'" + " WHERE " + COLUMN_WORD_DATE + " = ?"
            val cursor = db.rawQuery(_count, arrayOf(answ) )
            var count = 0
            if(null != cursor){
                if(cursor.getCount() > 0){
                    cursor.moveToFirst()
                    count = cursor.getInt(0)
                }
                cursor.close()
            }
            datesWords.add(Pair(answ.dropLast(4), count))
            Log.i("Time", "Cursor: " + " " + answ + " " + datesWords.get(datesWords.size - 1).toString())
            cal.add(Calendar.DATE, -1)
        }
        db.close()
        return datesWords
    }
    fun getLastSevenDaysStatsReviews(tableName: String): ArrayList<Pair<String, Int>> {
        val _tableName = tableName.lowercase().replace("\\s".toRegex(), "")
        val db = this.readableDatabase
        val cal  = Calendar.getInstance()
        val datesWords =  ArrayList<Pair<String, Int>>()
        val sdf = SimpleDateFormat(Constants.SDF, Locale.getDefault())

        for (i in 1..7){
            val answ = sdf.format(cal.time)
            val _count = "SELECT COUNT(*) FROM " + "'"  + _tableName + "'" + " WHERE " + COLUMN_WORD_DATE_LAST_REVIEWED + " = ?"
            val cursor = db.rawQuery(_count, arrayOf(answ) )
            var count = 0
            if(null != cursor){
                if(cursor.getCount() > 0){
                    cursor.moveToFirst()
                    count = cursor.getInt(0)
                }
                cursor.close()
            }
            datesWords.add(Pair(answ.dropLast(4), count))
            Log.i("Time", "Cursor: " + " " + answ + " " + datesWords.get(datesWords.size - 1).toString())
            cal.add(Calendar.DATE, -1)
        }
        db.close()
        return datesWords
    }
}