package com.xspacy.login.commands

import at.favre.lib.crypto.bcrypt.BCrypt
import com.xspacy.core.commands.CustomCommand
import com.xspacy.login.LoginSpigotPlugin
import com.xspacy.login.schemas.UsersTable
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class RegisterCommand : CustomCommand("registrar") {
    override fun getUsage(): String {
        val textComponent = TextComponent("Utilize /registrar <senha>.")

        textComponent.color = ChatColor.RED

        return textComponent.toLegacyText()
    }

    override fun onCommand(commandSender: CommandSender, args: Array<out String>) {
        if (!isPlayer(commandSender)) {
            throw IllegalArgumentException("Only players can register commands")
        }

        commandSender as Player

        if (args.size != 1) {
            return commandSender.sendMessage(usage)
        }

        val userAlreadyExists = transaction {
            UsersTable.select(
                UsersTable.id.count()
            ).where {
                UsersTable.id eq commandSender.uniqueId
            }.single()[UsersTable.id.count()]
        }.toInt()

        if (userAlreadyExists != 0) {
            return commandSender.spigot().sendMessage(*ComponentBuilder("Você já está registrado. Utilize /logar <senha>.").color(ChatColor.RED).create())
        }

        transaction {
            UsersTable.insert {
                it[id] = commandSender.uniqueId
                it[username] = commandSender.name
                it[password] = BCrypt.withDefaults().hashToString(12, args[0].toCharArray())
            }
        }

        LoginSpigotPlugin.AUTHENTICATED_PLAYERS[commandSender.uniqueId] = System.currentTimeMillis()

        return commandSender.spigot().sendMessage(*ComponentBuilder("Yay! Você se registrou com sucesso!").color(ChatColor.GREEN).create())
    }

    override fun getAliases(): List<String?> {
        return listOf("register")
    }
}
