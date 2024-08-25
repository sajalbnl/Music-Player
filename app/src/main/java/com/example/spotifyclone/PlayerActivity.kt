package com.example.spotifyclone

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spotifyclone.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity(),MediaPlayer.OnCompletionListener,ServiceConnection {

    private lateinit var runnable: Runnable
    companion object{
        lateinit var musicListPA:ArrayList<Music>
        var songPosition:Int=0
        var isPlaying:Boolean=false
        var repeat:Boolean=false
        var  musicService:MusicService?=null

        @SuppressLint("StaticFieldLeak")
        lateinit var binding:ActivityPlayerBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_SpotifyClone)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent=Intent(this,MusicService::class.java)
        bindService(intent,this, BIND_AUTO_CREATE)
        startService(intent)
        initializeLayout()
        binding.playPausePA.setOnClickListener{
            if(isPlaying)pauseMusic()
            else
                playMusic()
        }
        binding.prevBtn.setOnClickListener{
            prevNext(false)
        }
        binding.nextBtn.setOnClickListener {
            prevNext(true)
        }
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) =Unit
        })
        binding.repeatBtn.setOnClickListener{
            if(repeat){
                repeat=false
                binding.repeatBtn.setColorFilter(ContextCompat.getColor(this,R.color.color_pink))

            }else{
                repeat=true
                binding.repeatBtn.setColorFilter(ContextCompat.getColor(this,R.color.color_green))
            }
        }
        binding.backbtnPA.setOnClickListener{finish()}
        binding.shareBtn.setOnClickListener {
            val intent= Intent()
            intent.action=Intent.ACTION_SEND
            intent.type="audio/*"
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(intent,"Share Your File"))
        }
    }
    private fun setLayout(){
        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_splash_screen).centerCrop())
            .into(binding.songImgPA)
        binding.SongNamePA.text= musicListPA[songPosition].title
        if(repeat)binding.repeatBtn.setColorFilter(ContextCompat.getColor(this,R.color.color_green))

    }
    private fun createMediaPlayer(){
        try {
            if(musicService!!.mediaPlayer==null) musicService!!.mediaPlayer=MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying=true
            binding.playPausePA.setIconResource(R.drawable.pause__1_)
            binding.seekbarStart.text=Music.formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.seekbarEnd.text=Music.formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekbar.progress= 0
            binding.seekbar.max= musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            setSeekbar()

        }catch (e:Exception){
            return
        }
    }
    private fun initializeLayout(){
        songPosition=intent.getIntExtra("index",0)
        when(intent.getStringExtra("class")){

            "MusicAdapter"->{
                musicListPA= ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setLayout()
                createMediaPlayer()
            }
            "MainPlayerActivity"->{
                musicListPA= ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()
                createMediaPlayer()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun playMusic(){
        binding.playPausePA.setIconResource(R.drawable.pause__1_)
        musicService!!.showNotification(R.drawable.pause__1_)
        isPlaying=true
        musicService!!.mediaPlayer!!.start()

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private  fun pauseMusic(){
        binding.playPausePA.setIconResource(R.drawable.play)
        musicService!!.showNotification(R.drawable.play)
        isPlaying=false
        musicService!!.mediaPlayer!!.pause()

    }
    private fun prevNext(increment:Boolean){
        if(increment){
            finalSongPosition(true)
            setLayout()


        }else{
            finalSongPosition(false)
            setLayout()
        }
    }
    private fun finalSongPosition(increment: Boolean){
        if(!repeat){
            if(increment){
                if(musicListPA.size-1== songPosition){
                    songPosition=0
                }else ++songPosition
            }else{
                if (songPosition==0){
                    songPosition= musicListPA.size-1
                }else{
                    --songPosition
                }
            }
        }
    }
    private fun setSeekbar(){
        runnable= Runnable {
            binding.seekbarStart.text=Music.formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.seekbar.progress= musicService!!.mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        finalSongPosition(true)
        createMediaPlayer()
        try {
            setLayout()
        }catch(e:Error){return}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val  binder=service as MusicService.MyBinder
        musicService=binder.currentService()
        createMediaPlayer()
        musicService!!.showNotification(R.drawable.pause__1_)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService=null
    }
}