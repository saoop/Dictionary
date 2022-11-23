package com.example.wordybook.models

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.wordybook.Constants
import com.example.wordybook.R
import com.example.wordybook.databinding.ActivityStudyBinding

class QuizStudyClass(
    val binding: ActivityStudyBinding,
    val context: Context,
    val tableName: String
) {

    var mSelectedPosition = -1
    var mIsNextButtonActivated = false
    var mIsOptionEnabled = false
    var mIsFinished = false
    var mCurrentWord: ItemWord? = null
    var mCurrentQuestion: QuizzQuestion? = null


    init {
        setVisibility(false)
        binding.tvOption1.setOnClickListener {
            setSelectedBackground(binding.tvOption1)
            mSelectedPosition = 0
            mIsOptionEnabled = true
        }
        binding.tvOption2.setOnClickListener {
            setSelectedBackground(binding.tvOption2)
            mSelectedPosition = 1
            mIsOptionEnabled = true
        }
        binding.tvOption3.setOnClickListener {
            setSelectedBackground(binding.tvOption3)
            mSelectedPosition = 2
            mIsOptionEnabled = true
        }
        binding.tvOption4.setOnClickListener {
            setSelectedBackground(binding.tvOption4)
            mSelectedPosition = 3
            mIsOptionEnabled = true
        }


    }

    fun setUpQuiz(word: ItemWord) {
        mIsFinished = false
        mCurrentWord = word
        mCurrentQuestion = Constants.getQuizQuestion(context, tableName, mCurrentWord!!)
        clearAllQuestions()
        setUpNextQuestion(mCurrentQuestion!!)
        setVisibility(true)

    }

    fun setVisibility(visible: Boolean) {
        if (visible) binding.llStudyQuiz.visibility = View.VISIBLE
        else binding.llStudyQuiz.visibility = View.GONE
    }

    fun setBackgroundAnswers(pos: Int, res: Int) {
        when (pos) {
            0 -> binding.tvOption1.setBackgroundResource(res)
            1 -> binding.tvOption2.setBackgroundResource(res)
            2 -> binding.tvOption3.setBackgroundResource(res)
            3 -> binding.tvOption4.setBackgroundResource(res)
        }


    }

    fun clearAllQuestions() {
        binding.tvOption1.setBackgroundResource(R.drawable.item_border_normal_quiz)
        binding.tvOption2.setBackgroundResource(R.drawable.item_border_normal_quiz)
        binding.tvOption3.setBackgroundResource(R.drawable.item_border_normal_quiz)
        binding.tvOption4.setBackgroundResource(R.drawable.item_border_normal_quiz)
    }

    fun setSelectedBackground(tv: TextView) {
        clearAllQuestions()

        tv.setBackgroundResource(R.drawable.item_border_selected_quiz)
    }

    fun setUpNextQuestion(question: QuizzQuestion) {
        binding.tvQuestionWord.text = question.word.word
        binding.tvOption1.text = question.op1.translation
        binding.tvOption2.text = question.op2.translation
        binding.tvOption3.text = question.op3.translation
        binding.tvOption4.text = question.op4.translation

    }

}