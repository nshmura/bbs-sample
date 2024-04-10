package com.nshmura.bbs.infrastructure.persistence.threads

import com.nshmura.bbs.domain.model.DateTime
import com.nshmura.bbs.domain.model.thread.Post
import com.nshmura.bbs.domain.model.thread.Thread
import com.nshmura.bbs.domain.model.thread.values.Start
import com.nshmura.bbs.domain.model.thread.values.ThreadId
import com.nshmura.bbs.domain.model.thread.values.ThreadTitle
import com.nshmura.bbs.domain.model.thread.values.Total
import com.nshmura.bbs.domain.model.user.values.UserId
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import java.sql.Timestamp

@Mapper
interface ThreadsDBMapper {

    @Select(
        """
        SELECT `id`
             , `title`
             , `user_id`
             , `created_at`
             , `updated_at`
             , `deleted_at`
          FROM `threads`
         WHERE `deleted_at` IS NULL
      ORDER BY `updated_at` DESC
        LIMIT #{offset}, #{limit}
        """
    )
    fun selectAll(offset: Int, limit: Int): List<ThreadData>

    @Select(
        """
        SELECT COUNT(*)
          FROM `threads`
         WHERE `deleted_at` IS NULL
        """
    )
    fun countAll(): Int

    @Select(
        """
        <script>
        SELECT `id`
             , `title`
             , `user_id`
             , `created_at`
             , `updated_at`
             , `deleted_at`
          FROM `threads`
         WHERE `id` = #{id}
        <if test='isLock'>
          FOR UPDATE
        </if>
        </script>
        """
    )
    fun selectById(id: String, isLock: Boolean = false): ThreadData?

    @Insert(
        """
        INSERT INTO `threads` (
            `id`
          , `title`
          , `user_id`
          , `created_at`
          , `updated_at`
          , `deleted_at`
        ) VALUES (
            #{id}
          , #{title}
          , #{userId}
          , #{createdAt}
          , #{updatedAt}
          , #{deletedAt}
        )
        """
    )
    fun insert(threadData: ThreadData): Int

    @Update(
        """
        UPDATE `threads`
           SET `title` = #{title}
             , `updated_at` = #{updatedAt}
             , `deleted_at` = #{deletedAt}
         WHERE `id` = #{id}
        """
    )
    fun update(threadData: ThreadData): Int
}

data class ThreadData(
    val id: String,
    val title: String,
    val userId: String,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
    val deletedAt: Timestamp?
) {
    constructor(thread: Thread) : this(
        id = thread.id.value,
        title = thread.title.value,
        userId = thread.userId.value,
        createdAt = thread.createdAt.toTimestamp(),
        updatedAt = thread.updatedAt.toTimestamp(),
        deletedAt = thread.deletedAt?.toTimestamp()
    )

    fun toModel(
        postTotal: Int,
        postStart: Int? = null,
        posts: List<Post> = emptyList()
    ): Thread =
        Thread(
            id = ThreadId.of(id),
            title = ThreadTitle.of(title),
            userId = UserId.of(userId),
            postsTotal = Total.of(postTotal),
            postsStart = postStart?.let { Start.of(it) },
            posts = posts,
            createdAt = DateTime(createdAt),
            updatedAt = DateTime(updatedAt),
            deletedAt = deletedAt?.let { DateTime(deletedAt) }
        )
}
