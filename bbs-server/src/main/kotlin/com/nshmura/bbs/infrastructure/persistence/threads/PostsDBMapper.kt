package com.nshmura.bbs.infrastructure.persistence.threads

import com.nshmura.bbs.domain.model.DateTime
import com.nshmura.bbs.domain.model.thread.Post
import com.nshmura.bbs.domain.model.thread.values.PostContent
import com.nshmura.bbs.domain.model.thread.values.PostId
import com.nshmura.bbs.domain.model.thread.values.ThreadId
import com.nshmura.bbs.domain.model.user.values.UserId
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import java.sql.Timestamp

@Mapper
interface PostsDBMapper {

    @Select(
        """
        SELECT `id`
             , `content`
             , `user_id`
             , `thread_id`
             , `created_at`
             , `updated_at`
             , `deleted_at`
          FROM `posts`
         WHERE `thread_id` = #{threadId}
      ORDER BY `created_at` DESC
        LIMIT #{offset}, #{limit}
        """
    )
    fun selectAllByThreadId(threadId: String, offset: Int, limit: Int): List<PostData>

    @Select(
        """
        SELECT COUNT(*)
          FROM `posts`
         WHERE `thread_id` = #{threadId}
        """
    )
    fun countAllByThreadId(threadId: String): Int

    @Select(
        """
        <script>
        SELECT `thread_id`, COUNT(*) AS `count`
          FROM `posts`
         WHERE `thread_id` IN
        <foreach item="item" index="index" collection="threadIds" open="(" separator="," close=")">
            #{item}
        </foreach>
        GROUP BY `thread_id`
        </script>
        """
    )
    fun countAllByThreadIds(threadIds: List<String>): List<PostsCountData>

    @Select(
        """
        <script>
        SELECT `id`
             , `content`
             , `user_id`
             , `thread_id`
             , `created_at`
             , `updated_at`
             , `deleted_at`
          FROM `posts`
         WHERE `id` = #{id}
        <if test='isLock'>
          FOR UPDATE
        </if>
        </script>
        """
    )
    fun selectById(id: String, isLock: Boolean = false): PostData?

    @Insert(
        """
        INSERT INTO `posts` (
            `id`
          , `content`
          , `user_id`
          , `thread_id`
          , `created_at`
          , `updated_at`
          , `deleted_at`
        ) VALUES (
            #{id}
          , #{content}
          , #{userId}
          , #{threadId}
          , #{createdAt}
          , #{updatedAt}
          , #{deletedAt}
        )
        """
    )
    fun insert(postData: PostData): Int

    @Update(
        """
        UPDATE `posts`
           SET `content` = #{content}
             , `updated_at` = #{updatedAt}
             , `deleted_at` = #{deletedAt}
         WHERE `id` = #{id}
        """
    )
    fun update(postData: PostData): Int
}

data class PostData(
    val id: String,
    val content: String,
    val userId: String,
    val threadId: String,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
    val deletedAt: Timestamp?
) {
    constructor(post: Post) : this(
        id = post.id.value,
        content = post.content.value,
        userId = post.userId.value,
        threadId = post.threadId.value,
        createdAt = post.createdAt.toTimestamp(),
        updatedAt = post.updatedAt.toTimestamp(),
        deletedAt = post.deletedAt?.toTimestamp()
    )

    fun toModel(): Post =
        Post(
            id = PostId.of(id),
            userId = UserId.of(userId),
            threadId = ThreadId.of(threadId),
            content = PostContent.of(content),
            createdAt = DateTime(createdAt),
            updatedAt = DateTime(updatedAt),
            deletedAt = deletedAt?.let { DateTime(deletedAt) }
        )
}


class PostsCountData(
    val threadId: String,
    val count: Int
)