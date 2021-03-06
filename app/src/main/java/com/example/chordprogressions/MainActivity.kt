package com.example.chordprogressions

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.media.MediaPlayer
import android.media.SoundPool
import android.view.View
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.support.v7.widget.AppCompatImageButton
import android.util.Log
import android.widget.*

//import kotlinx.android.synthetic.main.activity_main.*
//import kotlinx.android.synthetic.main.activity_main.view.*

const val defaultKey = "C"
const val defaultScale = "Major"
private const val READ_REQUEST_CODE: Int = 42

// adding AdapterView.OnItemSelectedListener to handle spinner in same file
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val semitones = arrayOf("c", "cs", "d", "ds", "e", "f", "fs", "g", "gs", "a", "as", "b")
    val semitoneSequenceMap = mapOf(
        "Major" to intArrayOf(0, 2, 4, 5, 7, 9, 11),
        "Minor" to intArrayOf(0, 2, 3, 5, 7, 8, 10),
        "Mixolydian" to intArrayOf(0, 2, 4, 5, 7, 9, 10)
    )
    val chordSequenceMap = mapOf(
        "Major" to arrayOf("I", "ii", "iii", "IV", "V", "vi", "viiº"),
        "Minor" to arrayOf("i", "iiº", "III", "iv", "v", "VI", "VII"),
        "Mixolydian" to arrayOf("I", "ii", "iiiº", "IV", "v", "vi", "VII")
    )
    private lateinit var soundPool: SoundPool
    private lateinit var playPauseButton: AppCompatImageButton
    private lateinit var keys: Array<String>
    private lateinit var scales: Array<String>
    private lateinit var sampleButtons: Array<Button>
    private var key = defaultKey.toLowerCase()
    private var scale = defaultScale
    private var mediaMetadataRetriever: MediaMetadataRetriever = MediaMetadataRetriever()
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var sampleArray = IntArray(7)
    private var trackVolume: Float = 0.5f
    private var sampleVolume: Float = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set up play/pause button
        playPauseButton = findViewById(R.id.playPauseButton)
        playPauseButton.isEnabled = false
        playPauseButton.isClickable = false

        // set up mediaPlayer
        mediaPlayer.setVolume(trackVolume, trackVolume)
        mediaPlayer.setOnPreparedListener(MediaPlayer.OnPreparedListener { handleMediaPlayerPrepared() })

        // set up sample buttons
        sampleButtons = Array(7) { i -> findViewById<Button>(this.resources.getIdentifier("s"+(i+1)+"button","id", this.packageName))}

        // set up key spinner
        keys = resources.getStringArray(R.array.keys_array)
        val keySpinner: Spinner = findViewById(R.id.keySpinner)
        with(keySpinner) {
            onItemSelectedListener = this@MainActivity
            setSelection(keys.indexOf(defaultKey))
        }

        // set up scale spinner
        scales = resources.getStringArray(R.array.scales_array)
        val scaleSpinner: Spinner = findViewById(R.id.scaleSpinner)
        with(scaleSpinner) {
            onItemSelectedListener = this@MainActivity
            setSelection(scales.indexOf(defaultScale))
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
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // set up sample volume seekbar
        val sampleVolumeSeekBar: SeekBar = findViewById(R.id.sampleVolumeSeekBar)
        sampleVolumeSeekBar.progress = (sampleVolume*100).toInt()
        sampleVolumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                sampleVolume = progress.toFloat() / 100
                Log.d("cp", "sample volume: $sampleVolume")
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // set up soundPool
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
        setSamples()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun getTitle(uri: Uri): String {
        mediaMetadataRetriever.setDataSource(this, uri)
        var title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)

        return title
    }

    private fun loadTrack(uri: Uri) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(this, uri)
            mediaPlayer.prepareAsync()

            var title = getTitle(uri)
            var trackNameView: TextView = findViewById(R.id.trackNameView)
            trackNameView.text = title

        } catch (e: Exception) {
            Toast.makeText(this, "error loading file", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    fun handlePlayPauseClick(view: View) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            playPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24px)
        } else {
            mediaPlayer.start()
            playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24px)
        }
    }

    private fun handleMediaPlayerPrepared() {
        playPauseButton.isEnabled = true
        playPauseButton.isClickable = true
        playPauseButton.setColorFilter(Color.BLACK)
        Toast.makeText(this, "prepared", Toast.LENGTH_SHORT).show()
    }

    // handle spinner selection
    override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.keySpinner -> key = keys[position]
            R.id.scaleSpinner -> scale = scales[position]
            else -> Log.d("cp", "Selection failed :(")
        }

        setSamples()
    }

    // required for Spinner
    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.d("cp","Nothing selected")
    }


    private fun setSamples() {
        val notesInScale = getNotesInScale()
        for ((i, note) in notesInScale.withIndex()) {
            sampleArray[i] = soundPool.load(
                this,
                this.resources.getIdentifier("db_"+notesInScale[i]+"_48k","raw", this.packageName),
                1)
        }
        for ((i, thisButton) in sampleButtons.withIndex()) {
            thisButton?.text = chordSequenceMap.getValue(scale)[i]
        }
    }

    private fun getNotesInScale(): Array<String?> {
        val root = semitones.indexOf(getScaleRoot(key))
        val semitoneSequence = semitoneSequenceMap[scale]
        var notesInScale = arrayOfNulls<String>(7)

        for ((i, offset) in semitoneSequence!!.withIndex()) {
            val index = if (root + offset < semitones.count()) root + offset else root + offset - semitones.count()
            notesInScale[i] = semitones[index]
        }

        return notesInScale
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

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    fun performFileSearch(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/*"
        }

        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                Log.i("cp", "Uri: $uri")
                loadTrack(uri)
            }
        }
    }
}
