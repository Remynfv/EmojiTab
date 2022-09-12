package com.github.remynfv.emojitab.utils

import org.bukkit.entity.Player

object Settings
{
    /**
     * Emojis being disabled hides them from tab-complete.
     * It does not prevent you from using them at present.
     *
     * @return Whether this player has toggled emojis off.
     */
    fun getEmojiDisabled(player: Player): Boolean
    {
        //Get metadata to find out if emojis are already toggled
        var emojisOff = false
        for (meta in player.getMetadata("emoji_off"))
        {
            if (meta.asBoolean())
            {
                emojisOff = meta.asBoolean()
                break
            }
        }

        return emojisOff
    }
}