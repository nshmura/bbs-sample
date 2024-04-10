package com.nshmura.bbs.domain.model.user

import com.nshmura.bbs.domain.model.user.values.UserId
import org.springframework.stereotype.Repository

/**
 * ユーザのリポジトリ
 */
@Repository
interface UsersRepository {

    /**
     * userId から [User] を取得する
     */
    fun findUserById(id: UserId): User?

    /**
     * userId のリストから [User] のリストを取得する
     */
    fun findUsersByIds(ids: List<UserId>): List<User>

    /**
     * 全ての [User] を取得する
     */
    fun findAll(): List<User>
}
