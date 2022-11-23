package com.example.wordybook

import android.app.Dialog
import android.content.Context
import android.widget.Button
import com.example.wordybook.models.ItemWord
import com.example.wordybook.models.QuizzQuestion
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Constants {
    companion object {

        const val TABLE_NAME = "table_name"
        const val COLLECTION_NAME = "collection_name"
        const val ANSWER_SELECTION_KEY = "answer_selection_key"
        const val QUESTION_SELECTION_KEY = "question_selection_key"

        const val PAIR_EXERCISE_SELECTED = "pair_exercise_selected"
        const val QUIZ_EXERCISE_SELECTED = "quiz_exercise_selected"
        const val TYPE_EXERCISE_SELECTED = "type_exercise_selected"
        const val NO_EXERCISE_SELECTED = "no_exercise_selected"

        const val SDF = "dd MMM yyyy"


        fun getQuizz(context: Context, tableName: String, numberOfQuestions: Int?) : ArrayList<QuizzQuestion>{
            val list = ArrayList<QuizzQuestion>()

            val dbHandler = SqliteOpenHelper(context, null)
            val listWords = dbHandler.getWords(tableName)

            for (word in listWords){

                val question = getQuizQuestion(context, tableName, word)
                //val question = QuizzQuestion(word, word, listWords.random(), listWords.random(), listWords.random(), )

                list.add(question)
            }

            return list
        }

        fun getQuizQuestion(context: Context, tableName: String, word: ItemWord) : QuizzQuestion {
            val options = ArrayList<ItemWord>()
            val dbHandler = SqliteOpenHelper(context, null)
            val listWords = dbHandler.getWords(tableName)
            val questionWord = word
            options.add(questionWord)
            var op1 = listWords.random()
            while(options.contains(op1)) op1 = listWords.random()
            options.add(op1)

            var op2 = listWords.random()
            while(options.contains(op2)) op2 = listWords.random()

            options.add(op2)
            var op3 = listWords.random()
            while(options.contains(op3)) op3 = listWords.random()
            options.add(op3)

            //options.shuffle()

            val rightPos = when (questionWord) {
                options.get(0) -> 0
                options.get(1) -> 1
                options.get(2) -> 2
                options.get(3) -> 3
                else -> -1
            }

            return QuizzQuestion(questionWord, options[0], options[1], options[2], options[3], rightPos)
        }

        fun getConfirmationDialog(context: Context) : Dialog {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.dialog_confirmation)

            val btnYes = dialog.findViewById<Button>(R.id.btnConfirmationYes)
            val btnNo = dialog.findViewById<Button>(R.id.btnConfirmationNo)

            btnNo.setOnClickListener {
                dialog.dismiss()
            }


            return dialog
        }

        fun getCurrentDate(): String{
            val calendar = Calendar.getInstance()
            val dateTime = calendar.time

            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = sdf.format(dateTime)
            return date
        }

        fun getWords(context: Context, tableName: String): ArrayList<ItemWord>{
            val dbHandler = SqliteOpenHelper(context, null)
            val list = dbHandler.getWords(tableName)
            return list
        }

        //fun getWordsWithDates()

    }

}