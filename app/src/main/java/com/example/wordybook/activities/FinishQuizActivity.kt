package com.example.wordybook.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.example.wordybook.Constants
import com.example.wordybook.databinding.ActivityFinishQuizBinding

class FinishQuizActivity : AppCompatActivity() {

    lateinit var binding: ActivityFinishQuizBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFinishQuizBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val tableName = intent.getStringExtra(Constants.TABLE_NAME).toString()

        binding.btnBackToCollection.setOnClickListener{
            Log.i("CollectionActivity", "FinishActivity, tableName: " + tableName)
            val intent = Intent(this, CollectionActivity::class.java)
            intent.putExtra(Constants.TABLE_NAME, tableName)
            startActivity(intent)
            finish()
        }

    }
}