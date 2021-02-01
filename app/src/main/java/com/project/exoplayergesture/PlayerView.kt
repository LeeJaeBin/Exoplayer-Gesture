package com.project.exoplayergesture

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.custom_constrolview.view.*

class PlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var playImage : Int = -1
    var pauseImage : Int = -1
    private var playImageButton: ImageButton ?= null
    private var pauseImageButton: ImageButton ?= null

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.player_customview, this, true)

        playImageButton = view.exo_play
        pauseImageButton = view.exo_pause

        initAttributes(attrs)
        applyUIChanges()
    }

    fun initAttributes(attrs: AttributeSet?){

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.playerView, 0, 0)
            playImage = typedArray.getResourceId(R.styleable.playerView_play_icon, -1)
            pauseImage = typedArray.getResourceId(R.styleable.playerView_pause_icon, -1)

            typedArray.recycle()
        }
    }

    fun applyUIChanges () {
        if (playImage != -1) {
            playImageButton?.setImageResource(playImage)
        }
        if (pauseImage != -1) {
            pauseImageButton?.setImageResource(pauseImage)
        }
    }
}