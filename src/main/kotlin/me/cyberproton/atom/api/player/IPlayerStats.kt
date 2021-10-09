package me.cyberproton.atom.api.player

import me.cyberproton.atom.api.stat.container.StatContainer
import me.cyberproton.atom.api.stat.DoubleStat
import me.cyberproton.atom.api.stat.Stat

interface IPlayerStats {
    fun getInstance(stat: DoubleStat): StatContainer

    fun getValue(stat: Stat): Double
}