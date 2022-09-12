package com.github.remynfv.emojitab

/**
 * A single emoji.
 * @param character Character of the emoji, e.g. "☁"
 * @param shortCode What should be typed in chat for this emoji. e.g. :cloud:, or just ☁.
 * @param unwrappedShortCode [shortCode] but without the wrapping characters. Used for emoji permissions.
 * @throws IllegalStateException if [shortCode] is over 16 characters.
 */
data class Emoji(val character: String, val shortCode: String, val unwrappedShortCode: String)
{
    /**
     * True if this character is the same as it's shortcode, meaning it
     * can be skipped when emojifying text.
     */
    val canBeSkipped = character == shortCode

    init
    {
        if (shortCode.length > 16)
            throw IllegalStateException("Tried to register emoji with shortcode greater than 16 characters! '$shortCode'")
    }
}