package com.example.spotifyclone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spotifyclone.databinding.ActivityFavouriteBinding

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFavouriteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_SpotifyClone)
        binding=ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtnFav.setOnClickListener { finish() }

    }
}