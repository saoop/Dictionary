package com.example.wordybook.models

import android.graphics.Color
import android.view.View
import com.example.wordybook.databinding.ActivityStudyBinding

class TypeInAnswerClass(
    val binding: ActivityStudyBinding
) {
    var mCurrentWord: ItemWord? = null
    init {
        binding.llStudyTypeIn.visibility = View.GONE
    }

    fun setUpTypeIn(word: ItemWord){
        binding.llStudyTypeIn.visibility = View.VISIBLE
        setBackGroundDefault()
        mCurrentWord = word
        binding.tvTypeInWord.text = word.word
    }

    fun onWrongAnswer(){
        binding.tiTypeInTranslation.boxBackgroundColor = Color.parseColor("#AA2222")
    }

    fun onCorrectAnswer(){
        binding.tiTypeInTranslation.boxBackgroundColor = Color.parseColor("#22AA22")

    }

    fun disableTypeIn(){
        binding.llStudyTypeIn.visibility = View.GONE
    }

    fun check(): Boolean{
        return mCurrentWord?.translation == binding.etTypeInTranslation.text.toString()
    }

    private fun setBackGroundDefault(){
        binding.tiTypeInTranslation.boxBackgroundColor = Color.parseColor("#FFFFFF")
        binding.etTypeInTranslation.setText("")
    }

}