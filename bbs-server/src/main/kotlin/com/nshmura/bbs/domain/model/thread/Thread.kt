package com.nshmura.bbs.domain.model.thread

import com.nshmura.bbs.domain.model.DateTime
import com.nshmura.bbs.domain.model.thread.values.PostContent
import com.nshmura.bbs.domain.model.thread.values.PostId
import com.nshmura.bbs.domain.model.thread.values.Start
import com.nshmura.bbs.domain.model.thread.values.ThreadId
import com.nshmura.bbs.domain.model.thread.values.ThreadTitle
import com.nshmura.bbs.domain.model.thread.values.Total
import com.nshmura.bbs.domain.model.user.User
import com.nshmura.bbs.domain.model.user.values.UserId

/**
 * スレッド
 */
class Thread(
    val id: ThreadId,
    val title: ThreadTitle,
    val userId: UserId,
    val posts: List<Post> = emptyList(),
    val createdAt: DateTime,
    val updatedAt: DateTime,
    val deletedAt: DateTime? = null,
    val postsTotal: Total,
    val postsStart: Start? = null, // 取得した投稿の開始位置。未取得の場合は null
    val user: User? = null, // user は [UserRepository] から取得するため初期値は null
) {

    fun isDeleted(): Boolean = deletedAt != null

    /**
     * スレッドを更新する
     *
     * @param userId ユーザID
     * @param threadId スレッドID
     * @param title スレッドタイトル
     * @param now 現在時刻
     *
     * @return 更新後のスレッド
     *
     * @throws [IllegalArgumentException] ユーザIDが一致しない場合
     */
    fun update(
        userId: UserId,
        threadId: ThreadId,
        title: ThreadTitle,
        now: DateTime
    ): Thread {
        if (userId != this.userId) {
            throw IllegalArgumentException("ユーザIDが一致しません")
        }
        if (deletedAt != null) {
            return this
        }
        return Thread(
            id = threadId,
            title = title,
            userId = userId,
            posts = posts,
            postsTotal = postsTotal,
            postsStart = postsStart,
            user = user,
            createdAt = createdAt,
            updatedAt = now
        )
    }

    /**
     * スレッドを削除する
     *
     * @param userId ユーザID
     * @param threadId スレッドID
     * @param now 現在時刻
     *
     * @return 削除後のスレッド
     *
     * @throws [IllegalArgumentException] ユーザIDが一致しない場合
     */
    fun delete(userId: UserId, threadId: ThreadId, now: DateTime): Thread {
        if (userId != this.userId) {
            throw IllegalArgumentException("ユーザIDが一致しません")
        }
        if (deletedAt != null) {
            return this
        }
        return Thread(
            id = threadId,
            title = ThreadTitle.DELETED,
            userId = userId,
            posts = posts,
            postsTotal = postsTotal,
            postsStart = postsStart,
            user = user,
            createdAt = createdAt,
            updatedAt = now,
            deletedAt = now
        )
    }

    /**
     * 関連するユーザID一覧を取得する
     */
    fun getUserIds(): List<UserId> =
        (listOf(userId) + posts.map { it.userId }).distinct()

    /**
     * [User] を紐付ける
     */
    fun attachUsers(users: List<User>): Thread {
        val newUser = users.find { it.id == userId }
        val newPosts = posts.map { it.attachUser(users) }

        return Thread(
            id = id,
            title = title,
            userId = userId,
            posts = newPosts,
            postsTotal = postsTotal,
            postsStart = postsStart,
            user = newUser,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )
    }

    /**
     * 投稿を作成する
     *
     * @param generate 投稿ID
     * @param userId ユーザID
     * @param threadId スレッドID
     * @param content 投稿内容
     * @param now 現在時刻
     * @param maxPostsPerThread 最大投稿数
     * @return 作成した投稿
     * @throws IllegalStateException すでに削除済みのスレッドには投稿できません
     * @throws IllegalStateException 投稿数が上限に達しているため、投稿できません
     */
    fun createPost(
        generate: PostId,
        userId: UserId,
        threadId: ThreadId,
        content: PostContent,
        now: DateTime,
        maxPostsPerThread: Int
    ): Post {
        if (deletedAt != null) {
            throw IllegalStateException("すでに削除済みのスレッドには投稿できません")
        }
        if (postsTotal.value >= maxPostsPerThread) {
            throw IllegalStateException("投稿数が上限に達しているため、投稿できません")
        }
        return Post.create(generate, userId, threadId, content, now)
    }

    /**
     * 投稿を更新する
     */
    fun updatePost(post: Post, userId: UserId, content: PostContent, now: DateTime): Post {
        if (deletedAt != null) {
            throw IllegalStateException("すでに削除済みのスレッドの投稿は更新できません")
        }
        if (userId != post.userId) {
            throw IllegalStateException("ユーザIDが一致しません")
        }
        return post.update(content, now)
    }

    /**
     * 投稿を削除する
     */
    fun deletePost(post: Post, userId: UserId, now: DateTime): Post {
        if (deletedAt != null) {
            throw IllegalStateException("すでに削除済みのスレッドの投稿は削除できません")
        }
        if (userId != post.userId) {
            throw IllegalStateException("ユーザIDが一致しません")
        }
        return post.delete(now)
    }

    companion object {

        /**
         * スレッドを作成する
         */
        fun create(id: ThreadId, title: ThreadTitle, userId: UserId, now: DateTime): Thread =
            Thread(
                id = id,
                title = title,
                userId = userId,
                createdAt = now,
                updatedAt = now,
                postsTotal = Total.of(0)
            )
    }
}
