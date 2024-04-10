package com.nshmura.bbs.infrastructure.persistence.threads

import com.nshmura.bbs.domain.model.DateTime
import com.nshmura.bbs.domain.model.thread.Post
import com.nshmura.bbs.domain.model.thread.SearchResult
import com.nshmura.bbs.domain.model.thread.Thread
import com.nshmura.bbs.domain.model.thread.values.PostId
import com.nshmura.bbs.domain.model.thread.values.SearchRecordType
import com.nshmura.bbs.domain.model.thread.values.ThreadId
import com.nshmura.bbs.domain.model.user.values.UserId
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import java.sql.Timestamp

@Mapper
interface SearchDBMapper {

    /**
     * 全文検索
     */
    @Select(
        """
        SELECT
            `type`,
            `message`,
            `user_id`,
            `thread_id`,
            `post_id`,
            `created_at`,
            `updated_at`
        FROM
            `search`
        WHERE
            MATCH(`message`) AGAINST(#{keyword})
        ORDER BY
            MATCH(`message`) AGAINST(#{keyword}) DESC
        LIMIT
            #{offset}, #{limit}
        """
    )
    fun search(keyword: String, offset: Int, limit: Int): List<SearchData>

    /**
     * 全文検索結果の件数
     */
    @Select(
        """
        SELECT
            COUNT(*)
        FROM
            `search`
        WHERE
            MATCH(`message`) AGAINST(#{keyword})
        """
    )
    fun count(keyword: String, offset: Int, limit: Int): Int


    /**
     * 検索用レコードを登録する
     */
    @Insert(
        """
        INSERT INTO `search` (
            `type`,
            `message`,
            `user_id`,
            `thread_id`,
            `post_id`,
            `created_at`,
            `updated_at`
        ) VALUES (
            #{type},
            #{message},
            #{userId},
            #{threadId},
            #{postId},
            #{createdAt},
            #{updatedAt}
        )
        """
    )
    fun insert(searchData: SearchData): Int

    /**
     * 検索用レコードを更新する
     */
    @Update(
        """
        UPDATE `search` SET
            `message` = #{message},
            `updated_at` = #{updatedAt}
        WHERE
            `type` = #{type}
        AND `thread_id` = #{threadId}
        AND `post_id` = #{postId}
        """
    )
    fun update(searchData: SearchData): Int

    /**
     * スレッドに紐づく検索用レコードを削除する
     */
    @Insert(
        """
        DELETE FROM `search`
        WHERE `thread_id` = #{threadId}
        """
    )
    fun deleteThread(searchData: SearchData): Int

    /**
     * 投稿の検索用レコードを削除する
     */
    @Insert(
        """
        DELETE FROM `search`
        WHERE
            `type` = #{type}
        AND `thread_id` = #{threadId}
        AND `post_id` = #{postId}
        """
    )
    fun deletePost(searchData: SearchData): Int
}

data class SearchData(
    val type: String,
    val message: String,
    val userId: String,
    val threadId: String,
    val postId: String,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
) {
    constructor(thread: Thread) : this(
        type = SearchRecordType.THREAD.value,
        message = thread.title.value,
        userId = thread.userId.value,
        threadId = thread.id.value,
        postId = "",
        createdAt = thread.createdAt.toTimestamp(),
        updatedAt = thread.updatedAt.toTimestamp()
    )

    constructor(post: Post) : this(
        type = SearchRecordType.POST.value,
        message = post.content.value,
        userId = post.userId.value,
        threadId = post.threadId.value,
        postId = post.id.value,
        createdAt = post.createdAt.toTimestamp(),
        updatedAt = post.updatedAt.toTimestamp()
    )

    fun toModel(): SearchResult {
        return SearchResult(
            type = SearchRecordType.of(type),
            message = message,
            userId = UserId.of(userId),
            threadId = ThreadId.of(threadId),
            postId = if (postId.isNotEmpty()) PostId.of(postId) else null,
            createdAt = DateTime(createdAt),
            updatedAt = DateTime(updatedAt)
        )
    }
}

