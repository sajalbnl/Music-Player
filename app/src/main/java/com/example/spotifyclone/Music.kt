package com.example.spotifyclone

import android.media.MediaMetadataRetriever
import java.util.concurrent.TimeUnit

data class Music(val id:String, val title:String, val album:String ,val artist:String, val duration: Long = 0, val path: String,
                 val artUri:String) {


    companion object {
        fun formatDuration(duration: Long): String {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
        fun getImgArt(path: String): ByteArray? {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(path)
            return retriever.embeddedPicture
        }
    }


}
