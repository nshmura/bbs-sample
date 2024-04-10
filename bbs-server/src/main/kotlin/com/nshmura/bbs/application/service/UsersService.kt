package com.nshmura.bbs.application.service

import com.nshmura.bbs.domain.model.user.User
import com.nshmura.bbs.domain.model.user.UsersRepository
import com.nshmura.bbs.domain.model.user.values.UserId
import org.springframework.stereotype.Service

@Service
class UsersService(
    private val usersRepository: UsersRepository
) {

    fun findUsers(): List<User> {
        return usersRepository.findAll()
    }

    fun findUserById(userId: UserId): User {
        return usersRepository.findUserById(userId)
            ?: throw IllegalArgumentException("$userId のユーザが見つかりません")
    }
}
