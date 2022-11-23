package com.example.wordybook.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.wordybook.R
import com.example.wordybook.databinding.ActivityAboutTheAppBinding

class AboutTheAppActivity : AppCompatActivity() {
    lateinit var binding: ActivityAboutTheAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutTheAppBinding.inflate(LayoutInflater.from(this))

        setContentView(binding.root)

        binding.tbAboutTheApp.setTitle("Dictionary")
        setSupportActionBar(binding.tbAboutTheApp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbAboutTheApp.setNavigationOnClickListener {
            onBackPressed()
        }

    }
}