package com.xspacy.login.schemas

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.timestamp
import java.time.Instant

object UsersTable : Table("users") {
    val id = javaUUID("id")
    val username = varchar("username", 16).uniqueIndex()
    val password = varchar("password", 255)
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val updatedAt = timestamp("updated_at").nullable()

    override val primaryKey = PrimaryKey(id)
}
