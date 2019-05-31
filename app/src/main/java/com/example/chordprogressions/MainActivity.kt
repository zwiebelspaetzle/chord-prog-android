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
    private val semitones = arrayOf("c", "cs", "d", "ds", "e", "f", "fs", "g", "gs", "a", "as", "b")
    private lateinit var soundPool: SoundPool
    private var currentKey = "c"
    private var sampleArray = IntArray(7)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setFlags(AudioAttributes.FLAG_LOW_LATENCY)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(7)
            .setAudioAttributes(audioAttributes)
            .build()

        setSamplesFromKey("d")
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun setSamplesFromKey(key: String) {
        val scale = getScale(key)
        for ((i, note) in scale.withIndex()) {
            sampleArray[i] = soundPool.load(
                this,
                this.resources.getIdentifier("db_"+scale[i]+"_48k","raw", this.packageName),
                1)
        }
    }

    private fun getScale(key: String): Array<String?> {
        val semitoneSequence: IntArray = intArrayOf(0, 2, 4, 5, 7, 9, 11)
        val root = semitones.indexOf(key)
        var scale =  arrayOfNulls<String>(7)

        for ((i, offset) in semitoneSequence.withIndex()) {
            val index = if (root + offset < semitones.count()) root + offset else root + offset - semitones.count()
            scale[i] = semitones[index]
        }

        return scale
    }

    fun playSample(v: View) {
        when (v.id) {
            R.id.s1button -> soundPool.play(sampleArray[0], 1.toFloat(), 1.toFloat(), 0, 0, 1.toFloat())
            R.id.s2button -> soundPool.play(sampleArray[1], 1.toFloat(), 1.toFloat(), 0, 0, 1.toFloat())
            R.id.s3button -> soundPool.play(sampleArray[2], 1.toFloat(), 1.toFloat(), 0, 0, 1.toFloat())
            R.id.s4button -> soundPool.play(sampleArray[3], 1.toFloat(), 1.toFloat(), 0, 0, 1.toFloat())
            R.id.s5button -> soundPool.play(sampleArray[4], 1.toFloat(), 1.toFloat(), 0, 0, 1.toFloat())
            R.id.s6button -> soundPool.play(sampleArray[5], 1.toFloat(), 1.toFloat(), 0, 0, 1.toFloat())
            R.id.s7button -> soundPool.play(sampleArray[6], 1.toFloat(), 1.toFloat(), 0, 0, 1.toFloat())
        }
    }

    fun playWhiskey(view: View) {
        var mediaPlayer: MediaPlayer? = MediaPlayer.create(this, R.raw.whiskey)
        mediaPlayer?.start()
    }


}
