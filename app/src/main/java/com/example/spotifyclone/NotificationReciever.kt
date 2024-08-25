package com.example.spotifyclone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlin.system.exitProcess

class NotificationReceiver:BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            //only play next or prev song, when music list contains more than one song
            ApplicationClass.PREVIOUS -> Toast.makeText(context,"Prev Clicked",Toast.LENGTH_SHORT).show()
            ApplicationClass.PLAY -> if(PlayerActivity.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> Toast.makeText(context,"next Clicked",Toast.LENGTH_SHORT).show()
            ApplicationClass.EXIT ->{
                PlayerActivity.musicService!!.stopForeground(true)
                PlayerActivity.musicService=null
                exitProcess(1)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun playMusic(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause__1_)
        PlayerActivity.binding.playPausePA.setIconResource(R.drawable.pause__1_)
        //for handling app crash during notification play - pause btn (While app opened through intent)
        //try{ NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon) }catch (_: Exception){}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun pauseMusic(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play)
        PlayerActivity.binding.playPausePA.setIconResource(R.drawable.play)
        //for handling app crash during notification play - pause btn (While app opened through intent)
        //try{ NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.play_icon) }catch (_: Exception){}
    }


}