package com.nshmura.bbs.domain.model.thread

import com.nshmura.bbs.domain.model.DateTime
import com.nshmura.bbs.domain.model.thread.values.ThreadId
import com.nshmura.bbs.domain.model.thread.values.ThreadTitle
import com.nshmura.bbs.domain.model.thread.values.Total
import com.nshmura.bbs.domain.model.user.values.UserId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ThreadTest {

    @Nested
    inner class Create {

        @Test
        fun `スレッドを作成できること`() {
            val userId = UserId.of("userId")
            val threadId = ThreadId.generate()
            val title = ThreadTitle.of("title")
            val now = DateTime.now()
            val thread = Thread.create(
                id = threadId,
                title = title,
                userId = userId,
                now = now
            )
            assertEquals(threadId, thread.id)
            assertEquals(title, thread.title)
            assertEquals(userId, thread.userId)
            assertEquals(now, thread.createdAt)
            assertEquals(now, thread.updatedAt)
            assertEquals(null, thread.deletedAt)
            assertEquals(Total.of(0), thread.postsTotal)
            assertEquals(null, thread.postsStart)
            assertEquals(null, thread.user)
        }
    }

    @Nested
    inner class Update {

        @Test
        fun `スレッドを更新できること`() {
            val userId = UserId.of("userId")
            val threadId = ThreadId.generate()
            val title = ThreadTitle.of("title")
            val now = DateTime.now()
            val thread = Thread.create(
                id = threadId,
                title = title,
                userId = userId,
                now = now
            )
            val updatedTitle = ThreadTitle.of("updated title")
            val updatedThread = thread.update(
                userId = userId,
                threadId = threadId,
                title = updatedTitle,
                now = now
            )
            assertEquals(threadId, updatedThread.id)
            assertEquals(updatedTitle, updatedThread.title)
            assertEquals(userId, updatedThread.userId)
            assertEquals(now, updatedThread.createdAt)
            assertEquals(now, updatedThread.updatedAt)
            assertEquals(null, updatedThread.deletedAt)
            assertEquals(Total.of(0), updatedThread.postsTotal)
            assertEquals(null, updatedThread.postsStart)
            assertEquals(null, updatedThread.user)
        }

        @Test
        fun `userId が違う場合は更新できないこと`() {
            val userId = UserId.of("userId")
            val threadId = ThreadId.generate()
            val title = ThreadTitle.of("title")
            val now = DateTime.now()
            val thread = Thread.create(
                id = threadId,
                title = title,
                userId = userId,
                now = now
            )
            val updatedTitle = ThreadTitle.of("updated title")
            val otherUserId = UserId.of("otherUserId")

            assertThrows<IllegalArgumentException> {
                thread.update(
                    userId = otherUserId,
                    threadId = threadId,
                    title = updatedTitle,
                    now = now
                )
            }
        }

        @Test
        fun `すでに削除済みの場合は更新できないこと`() {
            val userId = UserId.of("userId")
            val threadId = ThreadId.generate()
            val title = ThreadTitle.of("title")
            val now = DateTime.now()
            val thread = Thread.create(
                id = threadId,
                title = title,
                userId = userId,
                now = now
            ).delete(
                userId = userId,
                threadId = threadId,
                now = now
            )
            val updatedTitle = ThreadTitle.of("updated title")

            val updatedThread = thread.update(
                userId = userId,
                threadId = threadId,
                title = updatedTitle,
                now = now
            )
            assertEquals(thread, updatedThread)
        }
    }

    // メモ その他のメソッドのテストは省略
}