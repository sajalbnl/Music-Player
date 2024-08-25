package com.example.spotifyclone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spotifyclone.databinding.ActivityPlaylistBinding

class PlaylistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_SpotifyClone)
        binding=ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtnPlayList.setOnClickListener{finish()}

    }
}