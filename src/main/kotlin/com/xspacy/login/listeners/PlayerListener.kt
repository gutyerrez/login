package com.xspacy.login.listeners

import com.xspacy.login.LoginSpigotPlugin
import com.xspacy.login.schemas.UsersTable
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class PlayerListener : Listener {
    @EventHandler
    fun on(event: PlayerMoveEvent) {
        val player = event.player

        if (LoginSpigotPlugin.AUTHENTICATED_PLAYERS.contains(player.uniqueId)) {
            return
        }

        val from = event.from
        val to = event.to ?: return

        if (to.y != from.y || to.x != from.x || to.z != from.z) {
            player.teleport(Location(from.world, from.x, from.y, from.z))
        }
    }

    @EventHandler
    fun on(event: PlayerJoinEvent) {
        val player = event.player

        event.joinMessage = null

        LoginSpigotPlugin.AUTHENTICATED_PLAYERS.remove(player.uniqueId)

        transaction {
            UsersTable.update({ UsersTable.id eq player.uniqueId }) {
                it[username] = player.name
            }
        }
    }

    @EventHandler
    fun on(event: PlayerQuitEvent) {
        val player = event.player

        event.quitMessage = null

        LoginSpigotPlugin.AUTHENTICATED_PLAYERS.remove(player.uniqueId)
    }

    @EventHandler
    fun on(event: PlayerInteractEvent) {
        val player = event.player

        if (LoginSpigotPlugin.AUTHENTICATED_PLAYERS.contains(player.uniqueId)) {
            return
        }

        event.isCancelled = true

        event.setUseItemInHand(Event.Result.DENY)

        event.setUseInteractedBlock(Event.Result.DENY)
    }

    @EventHandler
    fun on(event: PlayerInteractAtEntityEvent) {
        val player = event.player

        if (LoginSpigotPlugin.AUTHENTICATED_PLAYERS.contains(player.uniqueId)) {
            return
        }

        event.isCancelled
    }

    @EventHandler
    fun on(event: PlayerInteractEntityEvent) {
        val player = event.player

        if (LoginSpigotPlugin.AUTHENTICATED_PLAYERS.contains(player.uniqueId)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun on(event: PlayerItemDamageEvent) {
        val player = event.player

        if (LoginSpigotPlugin.AUTHENTICATED_PLAYERS.contains(player.uniqueId)) {
            return
        }

        event.isCancelled = true
        event.damage = 0
    }

    @EventHandler
    fun on(event: EntityDamageByEntityEvent) {
        val entity = event.entity

        if (entity !is Player) {
            return
        }

        if (LoginSpigotPlugin.AUTHENTICATED_PLAYERS.contains(entity.uniqueId)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun on(event: BlockPlaceEvent) {
        val player = event.player

        if (LoginSpigotPlugin.AUTHENTICATED_PLAYERS.contains(player.uniqueId)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun on(event: BlockBreakEvent) {
        val player = event.player

        if (LoginSpigotPlugin.AUTHENTICATED_PLAYERS.contains(player.uniqueId)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun on(event: PlayerItemHeldEvent) {
        val player = event.player

        if (LoginSpigotPlugin.AUTHENTICATED_PLAYERS.contains(player.uniqueId)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun on(event: AsyncPlayerChatEvent) {
        val player = event.player

        if (LoginSpigotPlugin.AUTHENTICATED_PLAYERS.contains(player.uniqueId)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun on(event: EntityDamageEvent) {
        val entity = event.entity

        if (entity !is Player) {
            return
        }

        if (LoginSpigotPlugin.AUTHENTICATED_PLAYERS.contains(entity.uniqueId)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun on(event: EntityDamageByBlockEvent) {
        val entity = event.entity

        if (entity !is Player) {
            return
        }

        if (LoginSpigotPlugin.AUTHENTICATED_PLAYERS.contains(entity.uniqueId)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun on(event: PlayerCommandPreprocessEvent) {
        val player = event.player

        if (LoginSpigotPlugin.AUTHENTICATED_PLAYERS.contains(player.uniqueId)) {
            return
        }

        if (arrayOf("/login", "/logar", "/register", "/registrar").any { command -> event.message.startsWith(command) }) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun on(event: PlayerDeathEvent) {
        event.deathMessage = null
    }

    @EventHandler
    fun on(event: PlayerRespawnEvent) {
        event.respawnLocation = LoginSpigotPlugin.SPAWN_LOCATION
    }
}