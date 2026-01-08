package com.ganainy.flyingfish

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import java.util.Timer
import java.util.TimerTask
import com.ganainy.gymmasterscompose.R

class FlyingFishActivity : AppCompatActivity() {
    private lateinit var gameView: GameView
    private val handler = Handler()
    private val interval: Long = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flyingfish)
        gameView = GameView(this)
        setContentView(gameView)

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    gameView.invalidate()
                }
            }
        }, 0, interval)
    }
}