package com.example.realworld.users_service.custom_exceptions

final case class AlreadyExistsException(private val message: String,
                                        private val cause: Throwable =
                                          None.orNull)
    extends Exception(message, cause)
