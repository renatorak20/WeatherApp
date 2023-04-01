package com.renato.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.renato.weatherapp.databinding.ActivityAboutBinding
import com.renato.weatherapp.databinding.ActivityMainBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}