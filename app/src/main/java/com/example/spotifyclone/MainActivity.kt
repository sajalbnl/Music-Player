package com.example.spotifyclone

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spotifyclone.databinding.ActivityMainBinding
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        lateinit var MusicListMA: ArrayList<Music>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRuntimePermission()
    }

    @SuppressLint("SetTextI18n")
    private fun initializeLayout() {
        setTheme(R.style.Theme_SpotifyClone)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayUseLogoEnabled(true)

        MusicListMA = getAllAudio()
        binding.rcvSongs.setHasFixedSize(true)
        binding.rcvSongs.setItemViewCacheSize(13)
        binding.rcvSongs.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(this, MusicListMA)
        binding.rcvSongs.adapter = musicAdapter

        binding.totalSongs.text = "Total Songs: ${musicAdapter.itemCount}"

        setClickListeners()
    }

    private fun setClickListeners() {
        binding.favbtn.setOnClickListener {
            startActivity(Intent(this, FavouriteActivity::class.java))
        }

        binding.shuffleBtn.setOnClickListener {
            val intent=Intent(this, PlayerActivity::class.java)

            intent.putExtra("index",0)
            intent.putExtra("class","MainPlayerActivity")
            startActivity(intent)
        }

        binding.playlistBtn.setOnClickListener {
            startActivity(Intent(this, PlaylistActivity::class.java))
        }

        binding.nav1.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedBack -> showToast("Feedback")
                R.id.navSetting -> showToast("Setting")
                R.id.about -> showToast("About")
                R.id.exit -> exitProcess(1)
            }
            true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun requestRuntimePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 12)
        } else {
            initializeLayout()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 12) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("Permission granted")
                initializeLayout()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 12)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("Range")
    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = "${MediaStore.Audio.Media.IS_MUSIC}!=0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
            "${MediaStore.Audio.Media.DATE_ADDED} DESC", null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val title = it.getString(it.getColumnIndex(MediaStore.Audio.Media.TITLE)) ?: "Unknown"
                val id = it.getString(it.getColumnIndex(MediaStore.Audio.Media._ID)) ?: "Unknown"
                val album = it.getString(it.getColumnIndex(MediaStore.Audio.Media.ALBUM)) ?: "Unknown"
                val artist = it.getString(it.getColumnIndex(MediaStore.Audio.Media.ARTIST)) ?: "Unknown"
                val path = it.getString(it.getColumnIndex(MediaStore.Audio.Media.DATA))
                val duration = it.getLong(it.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                val uri = Uri.parse("content://media/external/audio/albumart")
                val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
                val music = Music(id, title, album, artist, duration, path,artUriC)
                val file = File(music.path)
                if (file.exists()) {
                    tempList.add(music)
                }
            }
        }

        return tempList
    }
}
