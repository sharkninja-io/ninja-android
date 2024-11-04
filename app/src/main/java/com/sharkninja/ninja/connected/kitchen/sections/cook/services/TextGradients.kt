package com.sharkninja.ninja.connected.kitchen.sections.cook.services

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader


class TextGradients {
    fun getPreheatGradient(width: Float, textSize: Float): Shader {
        return LinearGradient(
            0f, 0f, width, textSize, intArrayOf(
                Color.parseColor("#A56495"),
                Color.parseColor("#E3B5D8")
            ), null, Shader.TileMode.CLAMP
        )
    }

    fun getCookGradient(width: Float, textSize: Float): Shader {
        return LinearGradient(
            0f, 0f, width, textSize, intArrayOf(
                Color.parseColor("#D55743"),
                Color.parseColor("#E6CD9B")
            ), null, Shader.TileMode.CLAMP
        )
    }

    fun getRestGradient(width: Float, textSize: Float): Shader {
        return LinearGradient(
            0f, 0f, width, textSize, intArrayOf(
                Color.parseColor("#716FE7"),
                Color.parseColor("#A2659A")
            ), null, Shader.TileMode.CLAMP
        )
    }

    fun getDoneGradient(width: Float, textSize: Float): Shader {
        return LinearGradient(
            0f, 0f, width, textSize, intArrayOf(
                Color.parseColor("#45BE55"),
                Color.parseColor("#45BE55")
            ), null, Shader.TileMode.CLAMP
        )
    }

    fun getErrorGradient(width: Float, textSize: Float): Shader {
        return LinearGradient(
            0f, 0f, width, textSize, intArrayOf(
                Color.parseColor("#F52A4C"),
                Color.parseColor("#F52A4C")
            ), null, Shader.TileMode.CLAMP
        )
    }

    fun getLidOpenGradient(width: Float, textSize: Float): Shader {
        return LinearGradient(
            0f, 0f, width, textSize, intArrayOf(
                Color.parseColor("#342D5C"),
                Color.parseColor("#342D5C")
            ), null, Shader.TileMode.CLAMP
        )
    }
}