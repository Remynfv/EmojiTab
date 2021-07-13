package com.github.remynfv.emojitab

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.Bukkit

class EmojiTab : JavaPlugin()
{
    override fun onEnable()
    {
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage("Load!")
    }

    override fun onDisable()
    {
        // Plugin shutdown logic
    }
}