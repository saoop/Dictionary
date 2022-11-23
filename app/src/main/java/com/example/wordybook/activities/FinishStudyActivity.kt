package com.example.wordybook.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.wordybook.R
import com.example.wordybook.databinding.ActivityFinishStudyBinding


class FinishStudyActivity : AppCompatActivity() {
    lateinit var binding: ActivityFinishStudyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishStudyBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.tvGoBackBtn.setOnClickListener{
            onBackPressed()

        }
    }
}