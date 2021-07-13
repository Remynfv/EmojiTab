package com.github.remynfv.emojitab;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class EmojiTab extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage("Load!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
