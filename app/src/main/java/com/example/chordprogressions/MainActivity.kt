package com.example.chordprogressions

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.media.MediaPlayer
import android.media.SoundPool
import android.view.View
import android.media.AudioAttributes
import android.util.Log
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.Spinner


val defaultKey = "C"

// adding AdapterView.OnItemSelectedListener to handle spinner in same file
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val semitones = arrayOf("c", "cs", "d", "ds", "e", "f", "fs", "g", "gs", "a", "as", "b")
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var soundPool: SoundPool
    private lateinit var keys: Array<String>
    private var sampleArray = IntArray(7)
    private var trackVolume: Float = 0.5f
    private var sampleVolume: Float = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set up mediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.whiskey)
        mediaPlayer.setVolume(trackVolume, trackVolume)

        // set up spinner
        keys = resources.getStringArray(R.array.keys_array)
        val keySpinner: Spinner = findViewById(R.id.keySpinner)
        with(keySpinner) {
            onItemSelectedListener = this@MainActivity
            setSelection(keys.indexOf(defaultKey))
        }

        // set up track volume seekbar
        val trackVolumeSeekBar: SeekBar = findViewById(R.id.trackVolumeSeekBar)
        trackVolumeSeekBar.progress = (trackVolume*100).toInt()
        trackVolumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                trackVolume = progress.toFloat() / 100
                mediaPlayer.setVolume(trackVolume, trackVolume)
                Log.d("cp", "track volume: $trackVolume")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                Log.d("cp", "start tracking volume seekbar touch")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Log.d("cp", "stop tracking volume seekbar touch")
            }
        })

        // set up sample volume seekbar
        val sampleVolumeSeekBar: SeekBar = findViewById(R.id.sampleVolumeSeekBar)
        sampleVolumeSeekBar.progress = (sampleVolume*100).toInt()
        sampleVolumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                sampleVolume = progress.toFloat() / 100
                Log.d("cp", "sample volume: $sampleVolume")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                Log.d("cp", "start tracking sample volume touch")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Log.d("cp", "stop tracking sample volume touch")
            }
        })

        // set up samples
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setFlags(AudioAttributes.FLAG_LOW_LATENCY)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(7)
            .setAudioAttributes(audioAttributes)
            .build()

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
            R.id.s1button -> soundPool.play(sampleArray[0], sampleVolume, sampleVolume, 0, 0, 1f)
            R.id.s2button -> soundPool.play(sampleArray[1], sampleVolume, sampleVolume, 0, 0, 1f)
            R.id.s3button -> soundPool.play(sampleArray[2], sampleVolume, sampleVolume, 0, 0, 1f)
            R.id.s4button -> soundPool.play(sampleArray[3], sampleVolume, sampleVolume, 0, 0, 1f)
            R.id.s5button -> soundPool.play(sampleArray[4], sampleVolume, sampleVolume, 0, 0, 1f)
            R.id.s6button -> soundPool.play(sampleArray[5], sampleVolume, sampleVolume, 0, 0, 1f)
            R.id.s7button -> soundPool.play(sampleArray[6], sampleVolume, sampleVolume, 0, 0, 1f)
        }
    }

    fun playMediaPlayer(view: View) {
        mediaPlayer?.start()
    }
}
