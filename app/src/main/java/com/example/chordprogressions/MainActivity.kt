package com.example.chordprogressions

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.media.MediaPlayer
import android.media.SoundPool
import android.view.View
import android.media.AudioManager
import android.media.AudioAttributes
import android.os.Build



class MainActivity : AppCompatActivity() {

    private lateinit var soundPool: SoundPool
    private var sample1: Int = 0
    private var sample2: Int = 0
    private var sample3: Int = 0
    private var sample4: Int = 0
    private var sample5: Int = 0
    private var sample6: Int = 0
    private var sample7: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setFlags(AudioAttributes.FLAG_LOW_LATENCY)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(7)
                .setAudioAttributes(audioAttributes)
                .build()
//        } else {
//            soundPool = SoundPool(7, AudioManager.STREAM_MUSIC, 0)
//        }

        sample1 = soundPool.load(this, R.raw.db_d_48k, 1)
        sample2 = soundPool.load(this, R.raw.db_e_48k, 1)
        sample3 = soundPool.load(this, R.raw.db_fs_48k, 1)
        sample4 = soundPool.load(this, R.raw.db_g_48k, 1)
        sample5 = soundPool.load(this, R.raw.db_a_48k, 1)
        sample6 = soundPool.load(this, R.raw.db_b_48k, 1)
        sample7 = soundPool.load(this, R.raw.db_cs_48k, 1)
    }

    fun playSample(v: View) {
        when (v.id) {
            R.id.s1button -> soundPool.play(sample1, 1.toFloat(), 1.toFloat(), 0, 0, 1.toFloat())
            R.id.s2button -> soundPool.play(sample2, 1.toFloat(), 1.toFloat(), 0, 0, 1.toFloat())
            R.id.s3button -> soundPool.play(sample3, 1.toFloat(), 1.toFloat(), 0, 0, 1.toFloat())
            R.id.s4button -> soundPool.play(sample4, 1.toFloat(), 1.toFloat(), 0, 0, 1.toFloat())
            R.id.s5button -> soundPool.play(sample5, 1.toFloat(), 1.toFloat(), 0, 0, 1.toFloat())
            R.id.s6button -> soundPool.play(sample6, 1.toFloat(), 1.toFloat(), 0, 0, 1.toFloat())
            R.id.s7button -> soundPool.play(sample7, 1.toFloat(), 1.toFloat(), 0, 0, 1.toFloat())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
//        soundPool = null
    }

    fun playWhiskey(view: View) {
        var mediaPlayer: MediaPlayer? = MediaPlayer.create(this, R.raw.whiskey)
        mediaPlayer?.start()
    }
}
