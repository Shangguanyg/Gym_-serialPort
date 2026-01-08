package com.ganainy.DroidJet.interfaces

import android.graphics.Canvas

interface GameComponent {
    fun update()
    fun render(canvas: Canvas)
}