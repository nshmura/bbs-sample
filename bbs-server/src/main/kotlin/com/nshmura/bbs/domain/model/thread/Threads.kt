package com.nshmura.bbs.domain.model.thread

import com.nshmura.bbs.domain.model.thread.values.Start
import com.nshmura.bbs.domain.model.thread.values.Total
import com.nshmura.bbs.domain.model.user.User
import com.nshmura.bbs.domain.model.user.values.UserId

/**
 * スレッドの一覧
 */
class Threads(
    val list: List<Thread>,
    val start: Start, // 取得したThreadsの開始位置
    val total: Total  // 取得したThreadsの総数
) {

    /**
     * 関連するユーザID一覧を取得する
     */
    fun getUserIds(): List<UserId> =
        list.flatMap { it.getUserIds() }.distinct()

    /**
     * [User] を紐付ける
     */
    fun attachUsers(users: List<User>): Threads =
        Threads(list.map { it.attachUsers(users) }, start, total)
}
