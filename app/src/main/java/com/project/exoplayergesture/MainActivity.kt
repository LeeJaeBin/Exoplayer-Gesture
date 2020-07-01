package com.project.exoplayergesture

import android.content.Context
import android.graphics.Point
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var player: SimpleExoPlayer
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var isControlHidden: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        var isLeftGesture = true
        var x = 0F
        var y = 0F

        var brightness = 0F

        val audioManager: AudioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        getScreenSize()
        makeFullScreen()

        val uriString = "Put your contents Uri"
        val uri = Uri.parse(uriString)

        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayer"))
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)

        val lp = window.attributes

        player = SimpleExoPlayer.Builder(this).build()
        player.prepare(mediaSource)
        player.playWhenReady = true

        playerView.useController = true
        playerView.player = player

        playerView.setOnTouchListener { _, motionEvent ->

            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    isControlHidden = if(isControlHidden) {
                        playerView.showController()
                        false
                    } else{
                        playerView.hideController()
                        true
                    }
                    x = motionEvent.x
                    y = motionEvent.y
                    isLeftGesture = x < (screenWidth / 2)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    gestureImage.visibility = View.GONE
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val x2: Double = motionEvent.x.toDouble()
                    val y2: Double = motionEvent.y.toDouble()

                    val diffX = (Math.floor(x2 - x)).toInt()
                    var diffY = (Math.floor(y2 - y)).toInt()

                    if(Math.abs(diffY) > Math.abs(diffX) && Math.abs(diffY) >= 70) {
                        if(isLeftGesture){
                            brightness = if(y < y2) {
                                (brightness - 0.1F)
                            } else {
                                (brightness + 0.1F)
                            }
                            if(brightness < 0) {
                                brightness = 0F
                            }
                            else if(brightness > 1.0) {
                                brightness = 1F
                            }
                            lp.screenBrightness = brightness
                            window.attributes = lp
                            gestureImage.setBackgroundResource(R.drawable.brightness)
                            gestureImage.visibility = View.VISIBLE
                        }
                        else{
                            if(y < y2) {
                                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND)
                            }
                            else {
                                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND)
                            }
                            gestureImage.setBackgroundResource(R.drawable.sound)
                            gestureImage.visibility = View.VISIBLE
                        }
                        y = y2.toFloat()
                    }
                    true
                }
                else -> super.onTouchEvent(motionEvent)
            }
        }
    }

    private fun makeFullScreen() {
        val decorView = window.decorView
        var uiOption = window.decorView.systemUiVisibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) uiOption =
            uiOption or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) uiOption =
            uiOption or View.SYSTEM_UI_FLAG_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) uiOption =
            uiOption or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        decorView.systemUiVisibility = uiOption
    }

    private fun getScreenSize() {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
        screenHeight = size.y
    }

    override fun onPause() {
        super.onPause()
        player.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.release()
    }
}