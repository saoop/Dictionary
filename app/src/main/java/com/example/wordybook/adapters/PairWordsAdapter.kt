package com.example.wordybook.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wordybook.Constants
import com.example.wordybook.R
import com.example.wordybook.models.ItemWord

class PairWordsAdapter (val context: Context, val items: ArrayList<ItemWord>, val column: String, val tableName: String, val onClick: (pos: Int, column: String ) -> Unit): RecyclerView.Adapter<PairWordsAdapter.ViewHolder>(){
    private var mSelectedPos = RecyclerView.NO_POSITION

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvPairItemWord = view.findViewById<TextView>(R.id.tvPairItemWord)
        val llPairItemWord = view.findViewById<LinearLayout>(R.id.llPairItemWord)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_word_pair, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setSelected(mSelectedPos == position)

        if(column == Constants.QUESTION_SELECTION_KEY)
            holder.tvPairItemWord.text = items[position].word
        else
            holder.tvPairItemWord.text = items[position].translation

        holder.llPairItemWord.setOnClickListener {

            notifyItemChanged(mSelectedPos);
            mSelectedPos = holder.layoutPosition
            notifyItemChanged(mSelectedPos);
            onClick(position, column)

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun removeWord(pos: Int){
        items.removeAt(pos)
        notifyItemRemoved(pos)
        setItemDefault()
    }



    private fun setItemDefault(){
        mSelectedPos = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

}