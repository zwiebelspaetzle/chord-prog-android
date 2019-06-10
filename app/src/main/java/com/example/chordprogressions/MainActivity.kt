package com.example.chordprogressions

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.media.MediaPlayer
import android.media.SoundPool
import android.view.View
import android.media.AudioManager
import android.media.AudioAttributes
import android.os.Build
import android.util.Log
import android.widget.*

val defaultKey = "C"

// adding AdapterView.OnItemSelectedListener to handle spinner in same file
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val semitones = arrayOf("c", "cs", "d", "ds", "e", "f", "fs", "g", "gs", "a", "as", "b")
    private lateinit var soundPool: SoundPool
    private lateinit var keys: Array<String>
    private var currentKey = "c"
    private var sampleArray = IntArray(7)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set up spinner
        keys = resources.getStringArray(R.array.keys_array)
        val keySpinner: Spinner = findViewById(R.id.keySpinner)
        with(keySpinner) {
            onItemSelectedListener = this@MainActivity
            setSelection(keys.indexOf(defaultKey))
        }

        // set up track volume seekbar
        val trackVolumeSeekBar: SeekBar = findViewById(R.id.trackVolumeSeekBar)
        trackVolumeSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                           fromUser: Boolean) {
                Toast.makeText(applicationContext, "seekbar progress: $progress", Toast.LENGTH_SHORT).show()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                Toast.makeText(applicationContext, "seekbar touch started!", Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Toast.makeText(applicationContext, "seekbar touch stopped!", Toast.LENGTH_SHORT).show()
            }
        })

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setFlags(AudioAttributes.FLAG_LOW_LATENCY)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(7)
            .setAudioAttributes(audioAttributes)
            .build()

        // set samples from our selected key
        setSamplesFromKey(keys[keySpinner.selectedItemPosition].toLowerCase())
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    // handle spinner selection
    override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
        Log.d("cp", "Key ${keys[position]} selected")
        setSamplesFromKey(keys[position])
    }

    // required for Spinner
    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.d("cp","Nothing selected")
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
        val root = semitones.indexOf(getScaleRoot(key))
        var scale = arrayOfNulls<String>(7)

        for ((i, offset) in semitoneSequence.withIndex()) {
            val index = if (root + offset < semitones.count()) root + offset else root + offset - semitones.count()
            scale[i] = semitones[index]
        }

        return scale
    }

    // just some translation from human-formatted keys to note file names
    private fun getScaleRoot(key: String): String {
        var root = key.toLowerCase()
        if (key.contains('♭', ignoreCase = true)) {  // change d♭ to cs, etc
            var rootChar = root[0]
            rootChar = if (rootChar == 'a') 'g' else rootChar-1
            root =  rootChar + "s"
        }

        Log.d("cp", "Key: $key, Root: $root")

        return root
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

    fun playRemoteClick(view: View) {
        playRemote()
    }

    fun playRemote(url: String = "https://www.youtube.com/watch?v=6s8Mmc69CP8") {
        val mediaPlayer: MediaPlayer? = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(url)
            prepare() // buffer
            start()
        }
    }
}
