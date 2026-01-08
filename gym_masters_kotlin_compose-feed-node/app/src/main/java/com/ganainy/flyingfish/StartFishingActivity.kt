package com.ganainy.flyingfish

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ganainy.gymmasterscompose.R

class StartFishingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val getStartedButton = findViewById<Button>(R.id.play_btn)
        getStartedButton.setOnClickListener {
            // Start the HomeActivity when the button is clicked
            val intent = Intent(this, FlyingFishActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}