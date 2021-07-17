package com.github.remynfv.emojitab

import com.github.remynfv.emojitab.utils.Messager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.configuration.file.FileConfiguration
import org.jetbrains.annotations.NotNull
import java.util.regex.Pattern

class Emojifier(private val plugin: EmojiTab)
{
    //Hashmap that stores all :shortcode: -> Emoji pairs
    var emojiMap = HashMap<String, String>()

    //Returns a string with shortcodes replaced by emojis
    fun emojifyString(message: String): String
    {
        var newMessage = message
        for (shortcode in emojiMap.keys)
        {
            if (message.contains(shortcode))
            {
                newMessage = message.replace(shortcode, emojiMap.getValue(shortcode))
            }
        }
        return newMessage
    }

    //Returns Component with shortcodes replaced by emojis
    fun emojifyMessage(message: @NotNull Component): Component
    {
        var newMessage = message
        for (shortcode in emojiMap.keys)
        {
            //Create a Pattern to find and replace case insensitively
            val regex: Pattern = Pattern.compile(shortcode, Pattern.LITERAL + Pattern.CASE_INSENSITIVE)
            val replacement: TextReplacementConfig = TextReplacementConfig.builder().match(regex).replacement(emojiMap.getValue(shortcode)).build()
            newMessage = newMessage.replaceText(replacement)
        }
        return newMessage
    }

    //Reads from emojis.yml and saves pairs to a hashmap
    fun loadEmojisFromConfig()
    {
        emojiMap = HashMap()
        val config: FileConfiguration = plugin.getEmojisConfig()
        val keys: MutableSet<String> = checkNotNull(config.getConfigurationSection("emojis")?.getKeys(false))

        for (character: String in keys)
        {
            //Register the main emoji
            val name = config.getString("emojis.$character.name")
            name?.let { registerEmoji(character, it) }

            //Register a list of aliases
            val aliases = config.getStringList("emojis.$character.aliases")
            if (!aliases.isNullOrEmpty())
            {
                for (alias in aliases)
                {
                    if (!alias.isNullOrBlank())
                        registerEmoji(character, alias)
                }
            }
            else //If the "aliases" is null, it must not be a list, so it is therefore a string
            {
                //Register a single string alias
                val alias = config.getString("emojis.$character.aliases")
                alias?.let { registerEmoji(character, it) }
            }
        }
    }

    private fun registerEmoji(character: String, shortcode: String)
    {
        val wrappingCharacter = plugin.wrappingCharacter
        val maxLength = 16 - (2 * wrappingCharacter.length)

        if (shortcode.length > maxLength)
        {
            Messager.warn("Emoji name '$wrappingCharacter$shortcode$wrappingCharacter' is over 16 characters and will be trimmed!")
        }
        val shortcodeCut = shortcode.take(maxLength)
        val shortcodeWithWrapping = wrappingCharacter + shortcodeCut + wrappingCharacter

        if (emojiMap.containsKey(shortcodeWithWrapping))
        {
            Messager.warn("Duplicate emoji name \"$shortcode\" Please double check your emojis.yml file!")
        }

        //Log emojis if verbose
        if(plugin.verbose)
        {
            if (!emojiMap.containsKey(character))
                Messager.send("Registered emoji $character to shortcode $shortcodeWithWrapping")
            else
                Messager.send("§8Registered emoji $character to shortcode $shortcodeWithWrapping (as alias)")
        }

        emojiMap[shortcodeWithWrapping] = character
    }
}