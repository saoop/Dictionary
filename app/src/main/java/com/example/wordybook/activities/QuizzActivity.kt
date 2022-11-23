package com.example.wordybook.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.example.wordybook.Constants
import com.example.wordybook.models.QuizzQuestion
import com.example.wordybook.R
import com.example.wordybook.databinding.ActivityQuizzBinding

class QuizzActivity : AppCompatActivity() {

    lateinit var binding: ActivityQuizzBinding

    var mCurrentPosition = -1
    var mSelectedPosition = -1
    var mIsNextButtonActivated = false
    var mIsFinished = false
    var mIsEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuizzBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val tableName = intent.getStringExtra(Constants.TABLE_NAME).toString()

        setSupportActionBar(binding.tbActivityQuiz)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(tableName)
        binding.tbActivityQuiz.setNavigationOnClickListener {
            onBackPressed()
        }

        val mListOfQuestions = Constants.getQuizz(this, tableName, 0)

        mCurrentPosition++
        setUpNextQuestion(mListOfQuestions.get(mCurrentPosition))

        binding.tvOption1.setOnClickListener {
            setSelectedBackground(binding.tvOption1)
            mSelectedPosition = 0
            mIsEnabled = true
        }
        binding.tvOption2.setOnClickListener {
            setSelectedBackground(binding.tvOption2)
            mSelectedPosition = 1
            mIsEnabled = true
        }
        binding.tvOption3.setOnClickListener {
            setSelectedBackground(binding.tvOption3)
            mSelectedPosition = 2
            mIsEnabled = true
        }
        binding.tvOption4.setOnClickListener {
            setSelectedBackground(binding.tvOption4)
            mSelectedPosition = 3
            mIsEnabled = true
        }

        binding.tvSubmitBtn.setOnClickListener {
            if (!mIsFinished) {

                if (mIsNextButtonActivated) { //if we push next question
                    mSelectedPosition = -1
                    mIsNextButtonActivated = false
                    binding.tvSubmitBtn.text = "SUBMIT"
                    mIsEnabled = false
                    clearAllQuestions()

                    setUpNextQuestion(mListOfQuestions.get(mCurrentPosition))
                } else { // if we submit
                    Log.i("QuizActivity", mCurrentPosition.toString() + " "  + mListOfQuestions.size)
                    if (mCurrentPosition < mListOfQuestions.size - 1 && mIsEnabled) {

                        mIsNextButtonActivated = true
                        binding.tvSubmitBtn.text = "NEXT"


                        if (mSelectedPosition == mListOfQuestions.get(mCurrentPosition).rightOp) {
                            //implement wrong answer
                        }

                        setBackgroundAnswers(mSelectedPosition, R.drawable.item_border_wrong_quiz)
                        setBackgroundAnswers(mListOfQuestions.get(mCurrentPosition).rightOp,
                            R.drawable.item_border_right_quiz
                        )

                        mCurrentPosition++
                    } else if (mCurrentPosition == mListOfQuestions.size - 1) {
                        mIsNextButtonActivated = true
                        binding.tvSubmitBtn.text = "FINISH"
                        setBackgroundAnswers(mSelectedPosition, R.drawable.item_border_wrong_quiz)
                        setBackgroundAnswers(mListOfQuestions.get(mCurrentPosition).rightOp,
                            R.drawable.item_border_right_quiz
                        )
                        mIsFinished = true

                    }

                }

            } else {
                //finishActivity(i)
                Log.i("CollectionActivity", "QuizActivity, tableName: " + tableName)
                val intent = Intent(this, FinishQuizActivity::class.java)
                intent.putExtra(Constants.TABLE_NAME, tableName)
                startActivity(intent)
                finish()
            }
        }

    }

    override fun onBackPressed() {

        val dialog = Constants.getConfirmationDialog(this)

        dialog.findViewById<Button>(R.id.btnConfirmationYes).setOnClickListener {
            super.onBackPressed()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setBackgroundAnswers(pos: Int, res: Int){
        when (pos) {
            0 -> binding.tvOption1.setBackgroundResource(res)
            1 -> binding.tvOption2.setBackgroundResource(res)
            2 -> binding.tvOption3.setBackgroundResource(res)
            3 -> binding.tvOption4.setBackgroundResource(res)
        }


    }


    private fun clearAllQuestions() {
        binding.tvOption1.setBackgroundResource(R.drawable.item_border_normal_quiz)
        binding.tvOption2.setBackgroundResource(R.drawable.item_border_normal_quiz)
        binding.tvOption3.setBackgroundResource(R.drawable.item_border_normal_quiz)
        binding.tvOption4.setBackgroundResource(R.drawable.item_border_normal_quiz)
    }

    private fun setSelectedBackground(tv: TextView) {
        clearAllQuestions()

        tv.setBackgroundResource(R.drawable.item_border_selected_quiz)
    }

    private fun setUpNextQuestion(question: QuizzQuestion) {
        binding.tvQuestionWord.text = question.word.word
        binding.tvOption1.text = question.op1.translation
        binding.tvOption2.text = question.op2.translation
        binding.tvOption3.text = question.op3.translation
        binding.tvOption4.text = question.op4.translation

    }

}