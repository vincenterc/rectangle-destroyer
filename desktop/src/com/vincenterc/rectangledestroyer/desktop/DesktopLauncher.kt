package com.vincenterc.rectangledestroyer.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.vincenterc.rectangledestroyer.RectangleGame

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration().apply {
            title = "Rectangle Destroyer"
            width = 832
            height = 640
        }
        LwjglApplication(RectangleGame(), config)
    }
}