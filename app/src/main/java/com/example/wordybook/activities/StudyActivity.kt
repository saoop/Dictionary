package com.example.wordybook.activities

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.KeyListener
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.wordybook.Constants
import com.example.wordybook.R
import com.example.wordybook.models.QuizStudyClass
import com.example.wordybook.SqliteOpenHelper
import com.example.wordybook.activities.StudyActivity.Companion.PAIR_KEY
import com.example.wordybook.activities.StudyActivity.Companion.QUIZ_KEY
import com.example.wordybook.activities.StudyActivity.Companion.TYPE_IN_KEY
import com.example.wordybook.databinding.ActivityStudyBinding
import com.example.wordybook.models.ItemWord
import com.example.wordybook.models.PairStudyClass
import com.example.wordybook.models.TypeInAnswerClass
import java.util.*
import kotlin.collections.ArrayList

class StudyActivity : AppCompatActivity() {
    lateinit var binding: ActivityStudyBinding

    companion object {
        const val QUIZ_KEY = "quiz_key"
        const val TYPE_IN_KEY = "type_in_key"
        const val PAIR_KEY = "pair_key"
        val listOfKeys = listOf(QUIZ_KEY, TYPE_IN_KEY)
    }

    var mCurrentExercise: String? = null

    var mCurrentWord = -1
    var nextIsActivated = false

    lateinit var quiz: QuizStudyClass
    lateinit var pair: PairStudyClass
    lateinit var typeIn: TypeInAnswerClass
    lateinit var listWords: ArrayList<ItemWord>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityStudyBinding.inflate(LayoutInflater.from(this))
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.llStudyQuiz.visibility = View.GONE

        val tableName = intent.getStringExtra(Constants.TABLE_NAME).toString()
        binding.tbStudyToolbar.setTitle(tableName)
        setSupportActionBar(binding.tbStudyToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbStudyToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val dbHandler = SqliteOpenHelper(this, null)
        listWords = dbHandler.getWords(tableName)


        pair = PairStudyClass(binding, this, tableName)

        quiz = QuizStudyClass(binding, this, tableName)
        typeIn = TypeInAnswerClass(binding)
        nextExercise()




        binding.tvSubmitBtn.setOnClickListener {
            if (mCurrentWord < listWords.size) {
                when (mCurrentExercise) {
                    QUIZ_KEY -> {
                        if (nextIsActivated) { //if we push next question
                            binding.tvSubmitBtn.text = "SUBMIT"

                            quiz.mSelectedPosition = -1

                            quiz.setVisibility(false)
                            quiz.mIsOptionEnabled = false
                            nextIsActivated = false

                            nextExercise()


                        } else { // if we submit
                            if (quiz.mIsOptionEnabled) {
                                nextIsActivated = true
                                binding.tvSubmitBtn.text = "NEXT"
                                quiz.mIsFinished = true
                                if (quiz.mSelectedPosition == quiz.mCurrentQuestion!!.rightOp) {
                                    //implement wrong answer
                                    dbHandler.setNewReviewDate(tableName, listWords[mCurrentWord])
                                } else {

                                }

                                quiz.setBackgroundAnswers(
                                    quiz.mSelectedPosition,
                                    R.drawable.item_border_wrong_quiz
                                )
                                quiz.setBackgroundAnswers(
                                    quiz.mCurrentQuestion!!.rightOp,
                                    R.drawable.item_border_right_quiz
                                )



                            }
                        }
                    }

                    PAIR_KEY -> {

                    }

                    TYPE_IN_KEY -> {

                        if (!nextIsActivated) {

                            if (typeIn.check()) {
                                typeIn.onCorrectAnswer()
                                dbHandler.setNewReviewDate(tableName, listWords[mCurrentWord])                            } else {
                                typeIn.onWrongAnswer()

                            }
                            binding.tvSubmitBtn.text = "NEXT"
                            nextIsActivated = true


                        } else {
                            binding.tvSubmitBtn.text = "SUBMIT"
                            nextIsActivated = false
                            typeIn.disableTypeIn()
                            nextExercise()
                        }

                    }

                }

            }

        }

        binding.etTypeInTranslation.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(view: View?, keyCode: Int, keyEvent: KeyEvent?): Boolean {
                if (keyEvent?.getAction()!=KeyEvent.ACTION_DOWN)
                    return false;
                if(keyCode == KeyEvent.KEYCODE_ENTER ){
                    binding.tvSubmitBtn.callOnClick()
                    return true;
                }
                return false;
            }
        })


    }

    private fun closeKeyboard(){
        val view = this.currentFocus
        if(view != null){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun nextExercise() {
        closeKeyboard()
        mCurrentWord++
        if (mCurrentWord < listWords.size) {
            chooseExercise()
            setUpNextExercise(listWords[mCurrentWord])

        } else {
            finishExercising()
        }
    }

    private fun chooseExercise() {

        mCurrentExercise = listOfKeys.random()
        Log.i("StudyActivity", "Current exercise is $mCurrentExercise")
    }

    private fun setUpNextExercise(word: ItemWord) {
        when (mCurrentExercise) {
            QUIZ_KEY -> {
                quiz.setUpQuiz(word)
            }
            PAIR_KEY -> {
                pair.setUpPairs(listWords)
            }
            TYPE_IN_KEY -> {
                typeIn.setUpTypeIn(word)
            }
        }

    }

    private fun finishExercising() {
        Toast.makeText(this, "Exercise is finished...", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@StudyActivity, FinishStudyActivity::class.java)
        startActivity(intent)
        finish()


    }

}