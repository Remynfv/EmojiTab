package com.github.remynfv.emojitab.commands

import com.github.remynfv.emojitab.EmojiTab
import com.github.remynfv.emojitab.utils.Messager
import com.mojang.authlib.GameProfile
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.EnumPlayerInfoAction
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.EntityPlayer
import net.minecraft.server.level.WorldServer
import net.minecraft.server.network.PlayerConnection
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_17_R1.CraftServer
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*


class TestCommand(val plugin: EmojiTab) : CommandExecutor
{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        if (sender !is Player)
            return true

        var player: Player = sender

        val server: MinecraftServer = (Bukkit.getServer() as CraftServer).server
        val world: WorldServer = (Bukkit.getServer().worlds[0] as CraftWorld).handle
        var npc: EntityPlayer = EntityPlayer(server, world, GameProfile(UUID.randomUUID(), "NPC"))


        //npc.setLocation(player.location.x, player.location.y, player.location.z, 0f, 0f)
        val connection: PlayerConnection = (player as CraftPlayer).handle.b //playerConnection
        connection.sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc))

        //connection.sendPacket(PacketPlayOutNamedEntitySpawn(npc))

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
            {
                Messager.broadcast("This message is shown after 3 seconds")
                var npcPlayer: Player = npc.bukkitEntity
                player.hidePlayer(plugin, npcPlayer)
            },
            60L) //20 Tick (1 Second) delay before run() is called



        return true
    }
}