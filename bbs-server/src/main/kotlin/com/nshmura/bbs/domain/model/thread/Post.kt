package com.nshmura.bbs.domain.model.thread

import com.nshmura.bbs.domain.model.DateTime
import com.nshmura.bbs.domain.model.thread.values.PostContent
import com.nshmura.bbs.domain.model.thread.values.PostId
import com.nshmura.bbs.domain.model.thread.values.ThreadId
import com.nshmura.bbs.domain.model.user.User
import com.nshmura.bbs.domain.model.user.values.UserId

/**
 * スレッドに紐づく投稿
 */
class Post(
    val id: PostId,
    val userId: UserId,
    val threadId: ThreadId,
    val content: PostContent,
    val createdAt: DateTime,
    val updatedAt: DateTime,
    val deletedAt: DateTime? = null,
    val user: User? = null, // user は [UserRepository] から取得するため、初期値は null
) {

    fun isDeleted(): Boolean = deletedAt != null

    /**
     * 投稿を更新する
     *
     * @param content 投稿内容
     * @param now 現在時刻
     * @return 更新後の投稿
     * @throws IllegalStateException すでに削除済みの投稿だった場合
     */
    fun update(content: PostContent, now: DateTime): Post {
        if (deletedAt != null) {
            throw IllegalStateException("すでに削除済みの投稿は更新できません")
        }

        return Post(
            id = id,
            userId = userId,
            threadId = threadId,
            content = content,
            user = user,
            createdAt = createdAt,
            updatedAt = now
        )
    }

    /**
     * 投稿を削除する
     *
     * @param now 現在時刻
     * @return 削除後の投稿
     * @throws IllegalStateException ユーザIDが一致しない場合
     */
    fun delete(now: DateTime): Post {
        if (deletedAt != null) {
            return this
        }
        return Post(
            id = id,
            userId = userId,
            threadId = threadId,
            content = PostContent.DELETED,
            user = user,
            createdAt = createdAt,
            updatedAt = now,
            deletedAt = now
        )
    }

    /**
     * [User] を紐付ける
     */
    fun attachUser(users: List<User>): Post {
        val user = users.find { it.id == userId }
        return Post(
            id = id,
            userId = userId,
            threadId = threadId,
            content = content,
            user = user,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )
    }

    companion object {

        /**
         * 投稿を生成する
         */
        fun create(id: PostId, userId: UserId, threadId: ThreadId, content: PostContent, now: DateTime): Post =
            Post(
                id = id,
                userId = userId,
                threadId = threadId,
                content = content,
                createdAt = now,
                updatedAt = now
            )
    }
}
