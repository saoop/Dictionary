package com.example.wordybook.adapters

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.wordybook.R
import com.example.wordybook.SqliteOpenHelper
import com.example.wordybook.models.ItemWord
import com.example.wordybook.utils.SwipeToDeleteCallback
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList

class WordsAdapter(val context: Context, var items: ArrayList<ItemWord>, val tableName: String): RecyclerView.Adapter<WordsAdapter.ViewHolder>() {
    var mCurrentlyOpendHolder: ViewHolder? = null
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val cvCard = view.findViewById<CardView>(R.id.cvItemWord)
        val tvWord = view.findViewById<TextView>(R.id.tvWord)
        val tvTranslation = view.findViewById<TextView>(R.id.tvTranslation)
        val btnDeleteword = view.findViewById<Button>(R.id.btnDeleteWord)
        val llExpandExpandItemWord = view.findViewById<LinearLayout>(R.id.llExpandItemWord)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_word, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvWord.text = items.get(position).word
        holder.tvTranslation.text = items.get(position).translation
        holder.tvWord.setOnClickListener{
            if(holder.llExpandExpandItemWord.visibility == View.VISIBLE){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(holder.cvCard, AutoTransition())
                }
                holder.llExpandExpandItemWord.visibility = View.GONE
            } else {
                if(mCurrentlyOpendHolder != null){
                    mCurrentlyOpendHolder!!.llExpandExpandItemWord.visibility = View.GONE
                }
                mCurrentlyOpendHolder = holder
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(holder.cvCard, AutoTransition())
                }
                holder.llExpandExpandItemWord.visibility  = View.VISIBLE
            }
        }

        holder.btnDeleteword.setOnClickListener{

            setUpConfirmationDialog(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


    fun filterList(filterList: ArrayList<ItemWord>){
        items = filterList
        notifyDataSetChanged()
    }

    fun deleteWord(position: Int){
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun deleteWordFromDatabase(word: ItemWord, position: Int){
        Log.i("Deleting", "in deleteWordFromDatabase")

        val dbHandler = SqliteOpenHelper(context, null)

        dbHandler.deleteWord(tableName, word.word)
        dbHandler.close()

        Log.i("Deleting", "deleted From Database")

    }

    private fun editWordInDatabase(oldItemWord: ItemWord, newItemWord: ItemWord){
        val db = SqliteOpenHelper(context, null)
        db.editWord(tableName, oldItemWord, newItemWord)
        Log.i("Editing word", "${oldItemWord.word}, ${oldItemWord.translation}, --> ${newItemWord.word}, ${newItemWord.translation}")
        db.close()
    }

    fun addWord(word: ItemWord, position: Int){
        items.add(position, word)
    }

    fun editWord(position: Int, word: String, translation: String){
        val newItemWord = ItemWord(word, translation)
        //items[position] = newItemWord
        editWordInDatabase(items[position], newItemWord)
    }

    fun setUpUndoSnackBar(position: Int){
        //val undoSnackBar = Snackbar.make()
    }

    fun setUpConfirmationDialog(position: Int){
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_confirmation)
        val btnYes = dialog.findViewById<Button>(R.id.btnConfirmationYes)
        val btnNo = dialog.findViewById<Button>(R.id.btnConfirmationNo)
        btnYes.setOnClickListener{
            deleteWord(position)
            dialog.dismiss()
        }

        btnNo.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

}