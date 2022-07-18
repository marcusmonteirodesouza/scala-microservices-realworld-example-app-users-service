package com.example.realworld.users_service.users

import akka.http.scaladsl.model.DateTime

import java.util.UUID

final case class User(id: UUID,
                      username: String,
                      email: String,
                      passwordHash: String,
                      bio: Option[String],
                      image: Option[String],
                      createdAt: DateTime,
                      updatedAt: DateTime)
