package com.example.realworld.users_service.users

import java.sql.Timestamp
import java.util.UUID

final case class User(id: UUID,
                      username: String,
                      email: String,
                      passwordHash: String,
                      bio: Option[String],
                      image: Option[String],
                      createdAt: Timestamp,
                      updatedAt: Timestamp)
