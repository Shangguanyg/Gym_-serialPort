package com.ganainy.LD47

import com.badlogic.gdx.Screen
import com.ganainy.LD47.graphics.Fonts
import com.ganainy.LD47.screens.GameOver
import com.ganainy.LD47.screens.InfiniteRace
import com.ganainy.LD47.screens.LevelSelection
import com.ganainy.LD47.screens.Title
import ktx.app.KtxGame
import kotlin.random.Random

class LD47 : KtxGame<Screen>() {

    companion object {
        val random = Random(System.currentTimeMillis())
    }

    override fun create() {
        super.create()

        Fonts.genFonts()

        addScreen(Title(this))
        addScreen(LevelSelection(this))
        addScreen(InfiniteRace(this))
        addScreen(GameOver(this))
        setScreen<Title>()
    }

    override fun render() {
        super.render()
        SoundManager.update()
    }

    override fun <Type : Screen> setScreen(type: Class<Type>) {
        if (currentScreen is com.ganainy.LD47.screens.Screen)
                (currentScreen as com.ganainy.LD47.screens.Screen).assetManager.clear()
        super.setScreen(type)
    }

}