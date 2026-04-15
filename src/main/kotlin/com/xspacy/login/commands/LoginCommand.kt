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
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class LoginCommand : CustomCommand("login") {
    override fun getUsage(): String {
        val textComponent = TextComponent("Utilize /login <senha>.")

        textComponent.color = ChatColor.RED

        return textComponent.toLegacyText()
    }

    override fun onCommand(commandSender: CommandSender, args: Array<out String>) {
        if (!isPlayer(commandSender)) {
            throw IllegalArgumentException("Only players can use LoginCommand")
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

        if (userAlreadyExists == 0) {
            return commandSender.spigot().sendMessage(*ComponentBuilder("Você não está registrado. Utilize /registrar <senha>.").color(ChatColor.RED).create())
        }

        val storedHash = transaction {
            UsersTable.select(UsersTable.password)
                .where { UsersTable.id eq commandSender.uniqueId }
                .single()[UsersTable.password]
        }

        val passwordMatches = BCrypt.verifyer().verify(args[0].toCharArray(), storedHash.toCharArray()).verified

        if (!passwordMatches) {
            return commandSender.spigot().sendMessage(*ComponentBuilder("Senha incorreta.").color(ChatColor.RED).create())
        }

        if (LoginSpigotPlugin.AUTHENTICATED_PLAYERS.contains(commandSender.uniqueId)) {
            return commandSender.spigot().sendMessage(
                ComponentBuilder("Você já está autênticado.")
                    .color(ChatColor.RED)
                    .build()
            )
        }

        LoginSpigotPlugin.AUTHENTICATED_PLAYERS[commandSender.uniqueId] = System.currentTimeMillis()

        return commandSender.spigot().sendMessage(*ComponentBuilder("Logado com sucesso!").color(ChatColor.GREEN).create())
    }

    override fun getAliases(): List<String?> {
        return listOf("logar")
    }
}