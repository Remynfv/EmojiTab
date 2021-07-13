package com.github.remynfv.emojitab

import com.github.remynfv.emojitab.commands.TestCommand
import com.github.remynfv.emojitab.utils.Messager
import org.bukkit.plugin.java.JavaPlugin


class EmojiTab : JavaPlugin()
{

    override fun onEnable()
    {
        // Plugin startup logic
        Messager.send("Loaded!")

        getCommand("test")!!.setExecutor(TestCommand(this))

    }

    override fun onDisable()
    {
        // Plugin shutdown logic
    }
}