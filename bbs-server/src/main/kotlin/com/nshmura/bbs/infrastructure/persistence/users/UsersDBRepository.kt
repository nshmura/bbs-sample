package com.nshmura.bbs.infrastructure.persistence.users

import com.nshmura.bbs.domain.model.user.User
import com.nshmura.bbs.domain.model.user.UsersRepository
import com.nshmura.bbs.domain.model.user.values.UserId
import org.springframework.stereotype.Repository

@Repository
class UsersDBRepository(
    private val userDBMapper: UserDBMapper
) : UsersRepository {

    /**
     * [User] を取得する
     */
    override fun findUserById(id: UserId): User? {
        val userData = userDBMapper.selectUserById(id.value)
        return userData?.toModel()
    }

    /**
     * [User] の一覧を取得する
     */
    override fun findUsersByIds(ids: List<UserId>): List<User> {
        if (ids.isEmpty()) {
            return emptyList()
        }
        val usersData = userDBMapper.selectUsersByIds(ids.map { it.value })
        return usersData.map { it.toModel() }
    }

    /**
     * 全ての [User] を取得する
     */
    override fun findAll(): List<User> {
        val users = userDBMapper.selectAll()
        return users.map { it.toModel() }
    }

}
