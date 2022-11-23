package com.example.wordybook.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.example.wordybook.Constants
import com.example.wordybook.R
import com.example.wordybook.databinding.ActivityCreateNewWordBinding
import com.example.wordybook.databinding.ActivityFinishStudyBinding

class CreateNewWordActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreateNewWordBinding
    var requestCode: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNewWordBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        setSupportActionBar(binding.tbCreateNewWord)
        supportActionBar!!.setTitle(intent.getStringExtra(Constants.TABLE_NAME))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.tbCreateNewWord.setNavigationOnClickListener{
            onBackPressed()
        }

        requestCode = intent?.getIntExtra(CollectionActivity.REQUEST_CODE, CollectionActivity.CREATE_WORD_REQUEST_KEY)

        var word: String? = null
        var translation: String? = null

        if (requestCode == CollectionActivity.EDIT_WORD_REQUEST_KEY){
            word = intent.getStringExtra(CollectionActivity.WORD_EXTRA)
            translation = intent.getStringExtra(CollectionActivity.TRANSLATION_EXTRA)
            binding.etCreateWordEnter.setText(word)
            binding.etCreateWordTranslation.setText(translation)
        }


        binding.tvCreateNewWordBtn.setOnClickListener {
            when (requestCode) {
                CollectionActivity.CREATE_WORD_REQUEST_KEY -> {
                    setUpReturnIntent()
                }
                CollectionActivity.EDIT_WORD_REQUEST_KEY ->{
                    setUpReturnIntent()
                }
            }
        }

    }
    private fun setUpReturnIntent(){
        val returnIntent = Intent()
        returnIntent.putExtra(
            CollectionActivity.WORD_EXTRA,
            binding.etCreateWordEnter.text.toString()
        )
        returnIntent.putExtra(
            CollectionActivity.TRANSLATION_EXTRA,
            binding.etCreateWordTranslation.text.toString()
        )
        if(requestCode == CollectionActivity.EDIT_WORD_REQUEST_KEY){
            returnIntent.putExtra(CollectionActivity.ADAPTER_POSITION_EXTRA,
                intent.getIntExtra(CollectionActivity.ADAPTER_POSITION_EXTRA, 0))
            Log.i("Editing word", "CreateNewWordActivity, position: ${intent.getIntExtra(CollectionActivity.ADAPTER_POSITION_EXTRA, 0)}")
        }


        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}