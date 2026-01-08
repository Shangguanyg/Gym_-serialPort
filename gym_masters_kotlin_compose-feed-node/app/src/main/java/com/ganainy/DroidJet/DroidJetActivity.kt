package com.ganainy.DroidJet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ganainy.DroidJet.fragments.GameFragment
import com.ganainy.DroidJet.fragments.GameOverFragment
import com.ganainy.DroidJet.fragments.HomeFragment
import com.ganainy.gymmasterscompose.R

class DroidJetActivity : AppCompatActivity(), GameFragment.GameOverCallback,
    GameOverFragment.RestartListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_droidjet)

        if (savedInstanceState == null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.fragmentContainer, HomeFragment.newInstance())
            fragmentTransaction.commit()
        }
    }

    override fun onGameOver(score: Float) {
        val gameOverFragment = GameOverFragment.newInstance()
        val bundle = Bundle()
        bundle.putFloat("score", score)
        gameOverFragment.arguments = bundle

        gameOverFragment.restartListener = this

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, gameOverFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onRestartClicked() {
        val gameFragment = GameFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, gameFragment)
            .commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment is GameOverFragment || currentFragment is HomeFragment) {
            finish()
        } else {
            (currentFragment as GameFragment).onPause()
            showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Game Paused")
            .setMessage("Do you want to quit the game and discard the current score?")
            .setNegativeButton("Discard") { _, _ ->
                finish()
            }.setPositiveButton("Resume") { _, _ ->
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                if (currentFragment is GameFragment) {
                    currentFragment.onResume()
                }
            }
            .show()
    }
}
