package com.github.remynfv.emojitab.utils

import org.bukkit.entity.Player

//This code is supported by SuperVanish, PremiumVanish, VanishNoPacket and a few more vanish plugins.
object VanishAPI
{
    fun isVanished(player: Player): Boolean
    {
        for (meta in player.getMetadata("vanished"))
        {
            if (meta.asBoolean()) return true
        }
        return false
    }
}