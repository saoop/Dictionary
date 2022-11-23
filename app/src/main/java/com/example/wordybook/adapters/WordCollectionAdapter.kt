package com.example.wordybook.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wordybook.Constants
import com.example.wordybook.R
import com.example.wordybook.SqliteOpenHelper
import com.example.wordybook.activities.CollectionActivity
import com.example.wordybook.models.ItemCollection

class WordCollectionAdapter(val context: Context, val items: ArrayList<ItemCollection>) : RecyclerView.Adapter<WordCollectionAdapter.ViewHolder>()   {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvCollectionName = view.findViewById<TextView>(R.id.tvCollectionName)
        val btnDelete = view.findViewById<Button>(R.id.btnDeleteCollection)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_words_collection, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCollectionName.text = items.get(position).name
        holder.tvCollectionName.setOnClickListener{
            val intent = Intent(context, CollectionActivity::class.java)
            intent.putExtra(Constants.TABLE_NAME, holder.tvCollectionName.text.toString())
            context.startActivity(intent)
        }

        holder.btnDelete.setOnClickListener{
            //deleteCollection(holder.tvCollectionName.text.toString(), position)
            val tableName = holder.tvCollectionName.text.toString()

            setUpConfirmationDialog(tableName, position)

        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun deleteCollection(tableName: String, position: Int){
        val dbHandler = SqliteOpenHelper(context, null)
        dbHandler.deleteCollection(tableName)
        items.removeAt(position)
        notifyDataSetChanged()
    }

    private fun setUpConfirmationDialog(tableName: String, position: Int){
        val dialog = Constants.getConfirmationDialog(context)

        dialog.findViewById<Button>(R.id.btnConfirmationYes).setOnClickListener {
            deleteCollection(tableName, position)
            dialog.dismiss()
        }
        dialog.show()
    }

}