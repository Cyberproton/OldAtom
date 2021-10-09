package me.cyberproton.atom.api.util

fun fromSecond(second: Double): Int = (second * 20).toInt()

fun fromSecondAsLong(second: Double): Long = (second * 20).toLong()

fun toSecond(tick: Long): Double = tick / 20.0