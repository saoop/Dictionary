package com.example.wordybook.models

import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordybook.Constants
import com.example.wordybook.R
import com.example.wordybook.adapters.PairWordsAdapter

import com.example.wordybook.databinding.ActivityStudyBinding

class PairStudyClass(
    val binding: ActivityStudyBinding,
    val context: Context,
    val tableName: String,

) {
    var mQuestionsAnswered = 0
    var mIsFinished  = false
    val mListQuestions = ArrayList<ItemWord>()
    val mListAnswers = ArrayList<ItemWord>()


    var mCurrentQuestion: Int = -1
    var mCurrentAnswer: Int = -1

    init {
        binding.llStudyPair.visibility = View.GONE

        binding.tvQuestion0.setOnClickListener{
            mCurrentQuestion = 0
            checkCorrectPair()
        }
        binding.tvQuestion1.setOnClickListener{
            mCurrentQuestion = 1
            checkCorrectPair()
        }
        binding.tvQuestion2.setOnClickListener{
            mCurrentQuestion = 2
            checkCorrectPair()
        }
        binding.tvQuestion3.setOnClickListener{
            mCurrentQuestion = 3
            checkCorrectPair()
        }

        binding.tvAnswer0.setOnClickListener{
            mCurrentAnswer = 0
            checkCorrectPair()
        }
        binding.tvAnswer1.setOnClickListener{
            mCurrentAnswer = 1
            checkCorrectPair()
        }
        binding.tvAnswer2.setOnClickListener{
            mCurrentAnswer = 2
            checkCorrectPair()
        }
        binding.tvAnswer3.setOnClickListener{
            mCurrentAnswer = 3
            checkCorrectPair()
        }
        //mListQuestions += Constants.getWords(this, tableName)

        //mListAnswers += mListQuestions



    }

    fun setUpPairs(listWords: ArrayList<ItemWord>){
        binding.llStudyPair.visibility = View.VISIBLE
        for (i in 0..listWords.size - 1){
            mListQuestions.add(listWords[i])
            mListAnswers.add(listWords[i])
        }
        mListQuestions.shuffle()
        mListAnswers.shuffle()
        setQuestions()
        setAnswers()



    }



    private fun checkCorrectPair(){
        if(mCurrentAnswer != -1 && mCurrentQuestion != -1){
            if(mListAnswers[mCurrentAnswer] == mListQuestions[mCurrentQuestion]){
                when(mCurrentAnswer){
                    0 ->  disableView(binding.tvAnswer0)
                    1 ->  disableView(binding.tvAnswer1)
                    2 ->  disableView(binding.tvAnswer2)
                    3 ->  disableView(binding.tvAnswer3)
                }
                when(mCurrentQuestion){
                    0 ->  disableView(binding.tvQuestion0)
                    1 ->  disableView(binding.tvQuestion1)
                    2 ->  disableView(binding.tvQuestion2)
                    3 ->  disableView(binding.tvQuestion3)
                }
                mCurrentQuestion = -1
                mCurrentAnswer = -1
            }
        }
    }

    private fun disableView(view: View){
        view.isClickable = false
        view.visibility = View.INVISIBLE
    }

    private fun setQuestions(){
        binding.tvQuestion0.text = mListQuestions[0].word
        binding.tvQuestion1.text = mListQuestions[1].word
        binding.tvQuestion2.text = mListQuestions[2].word
        binding.tvQuestion3.text = mListQuestions[3].word
    }

    private fun setAnswers(){
        binding.tvAnswer0.text = mListAnswers[0].translation
        binding.tvAnswer1.text = mListAnswers[1].translation
        binding.tvAnswer2.text = mListAnswers[2].translation
        binding.tvAnswer3.text = mListAnswers[3].translation

    }
}