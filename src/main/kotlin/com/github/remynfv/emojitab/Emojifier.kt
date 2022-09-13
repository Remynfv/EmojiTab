package com.github.remynfv.emojitab

import com.github.remynfv.emojitab.utils.Messager
import com.github.remynfv.emojitab.utils.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.permissions.Permissible
import org.jetbrains.annotations.NotNull
import java.util.regex.Pattern

class Emojifier(private val plugin: EmojiTab)
{
    /**
     *
     */
    var emojiList = mutableListOf<Emoji>()

    /**
     * Returns a string with shortcodes replaced by emojis
     */
    fun emojifyString(message: String): String
    {
        var newMessage = message
        for (emoji in emojiList)
        {
            if (message.contains(emoji.shortCode))
                newMessage = message.replace(emoji.shortCode, emoji.character)
        }
        return newMessage
    }

    /**
     * Returns Component with shortcodes replaced by emojis
     */
    fun emojifyMessage(message: @NotNull Component, permissionHolder: Permissible?): Component
    {
        var newMessage = message
        for (emoji in emojiList)
        {
            if (emoji.canBeSkipped)
                continue // Some emojis may not need to be replaced.

            if (plugin.individualPermissions
                && permissionHolder?.hasPermission(Permissions.USE_PREFIX + emoji.unwrappedShortCode) == false)
                continue

            //Create a Pattern to find and replace case insensitively
            val regex: Pattern = Pattern.compile(emoji.shortCode, Pattern.LITERAL + Pattern.CASE_INSENSITIVE)
            val replacement: TextReplacementConfig = TextReplacementConfig.builder().match(regex).replacement(emoji.character).build()
            newMessage = newMessage.replaceText(replacement)
        }
        return newMessage
    }

    /**
     * Reads from emojis.yml and saves :shortcode: -> emoji pairs to [emojiList].
     */
    fun loadEmojisFromConfig()
    {
        emojiList = mutableListOf()
        val config: FileConfiguration = plugin.emojisConfig
        val keys: MutableSet<String> = checkNotNull(config.getConfigurationSection("emojis")?.getKeys(false))

        for (character: String in keys)
        {
            //Register the main emoji
            val name = config.getString("emojis.$character.name")
            if (name != null)
                registerEmoji(character, name, true) // Register ☁ as :cloud:
            else
                registerEmoji(character, character, false) // Register ☁ as ☁

            //Register a list of aliases
            val aliases = config.getStringList("emojis.$character.aliases")
            if (aliases.isNotEmpty())
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
                val alias = config.getString("emojis.$character.aliases")?: continue
                 registerEmoji(character, alias)
            }
        }
    }

    /**
     * Registers a new emoji with the given shortcode and character.
     * @param character The character/string being registered.
     * @param shortcode The string to search for and replace. (Including wrapping characters.)
     */
    private fun registerEmoji(character: String, shortcode: String, wrap: Boolean = true)
    {
        val wrappingCharacter = plugin.wrappingCharacter
        val maxLength = 16 - (2 * wrappingCharacter.length)

        if (shortcode.length > maxLength)
            Messager.warn("Emoji name '$wrappingCharacter$shortcode$wrappingCharacter' is over 16 characters and will be trimmed!")

        val shortcodeCut = shortcode.take(maxLength)
        val shortcodeWithWrapping = if (wrap) wrappingCharacter + shortcodeCut + wrappingCharacter else shortcodeCut

        if (emojiList.any { it.shortCode == shortcodeWithWrapping })
            Messager.warn("Duplicate emoji name \"$shortcode\" Please double check your emojis.yml file!")

        //Log emojis if verbose
        if(plugin.verbose)
            if (!emojiList.any { it.character == character })
                Messager.send("Registered emoji $character to shortcode $shortcodeWithWrapping")
            else
                Messager.send("§8Registered emoji $character to shortcode $shortcodeWithWrapping (as alias)")

        emojiList.add(Emoji(character, shortcodeWithWrapping, shortcodeCut))
    }
}