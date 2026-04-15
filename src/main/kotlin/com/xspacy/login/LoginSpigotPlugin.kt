package com.xspacy.login

import com.xspacy.core.plugin.CustomPlugin
import com.xspacy.login.commands.LoginCommand
import com.xspacy.login.commands.RegisterCommand
import com.xspacy.login.listeners.PlayerListener
import com.xspacy.login.schemas.UsersTable
import org.bukkit.Bukkit
import org.bukkit.Location
import org.jetbrains.exposed.v1.core.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import java.util.UUID

class LoginSpigotPlugin : CustomPlugin() {
    companion object {
        lateinit var SPAWN_LOCATION: Location

        val AUTHENTICATED_PLAYERS = mutableMapOf<UUID, Long>()
    }

    @OptIn(ExperimentalDatabaseMigrationApi::class)
    override fun onEnable() {
        transaction {
            MigrationUtils.statementsRequiredForDatabaseMigration(UsersTable).forEach { exec(it) }
        }

        val commandMap = (Bukkit.getServer() as org.bukkit.craftbukkit.CraftServer).commandMap
        commandMap.register("register", RegisterCommand())
        commandMap.register("login", LoginCommand())

        Bukkit.getPluginManager().registerEvents(PlayerListener(), this)

        SPAWN_LOCATION = Location(
            Bukkit.getWorld("world"),
            0.5,
            75.0,
            0.5,
            -180F,
            1F
        )
    }
}
