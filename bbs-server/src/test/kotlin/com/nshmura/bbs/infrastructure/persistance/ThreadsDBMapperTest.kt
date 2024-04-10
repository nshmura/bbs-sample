package com.nshmura.bbs.infrastructure.persistance

import com.nshmura.bbs.domain.model.DateTime
import com.nshmura.bbs.infrastructure.persistence.threads.ThreadData
import com.nshmura.bbs.infrastructure.persistence.threads.ThreadsDBMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest
import org.springframework.beans.factory.annotation.Autowired
import java.time.temporal.ChronoUnit

@MybatisTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ThreadsDBMapperTest {

    @Autowired
    private lateinit var threadsDBMapper: ThreadsDBMapper

    private val now = DateTime.now()

    @BeforeAll
    fun setup() {
        val thread1 = ThreadData(
            id = "1",
            title = "title",
            userId = "userId1",
            createdAt = now.toTimestamp(),
            updatedAt = now.toTimestamp(),
            deletedAt = null
        )
        threadsDBMapper.insert(thread1)

        val thread2 = ThreadData(
            id = "2",
            title = "title",
            userId = "userId2",
            createdAt = now.toTimestamp(),
            updatedAt = now.toTimestamp(),
            deletedAt = null
        )
        threadsDBMapper.insert(thread2)
        val updated = ThreadData(
            id = "2",
            title = "title updated",
            userId = "userId3", // 更新されない
            createdAt = now.plus(2, ChronoUnit.DAYS).toTimestamp(), // 更新されない
            updatedAt = now.plus(3, ChronoUnit.DAYS).toTimestamp(),
            deletedAt = null,
        )
        threadsDBMapper.update(updated)

        val deleted = ThreadData(
            id = "99",
            title = "title",
            userId = "userId2",
            createdAt = now.toTimestamp(),
            updatedAt = now.plus(1, ChronoUnit.DAYS).toTimestamp(),
            deletedAt = now.plus(2, ChronoUnit.DAYS).toTimestamp(),
        )
        threadsDBMapper.insert(deleted)
    }

    @Nested
    inner class SelectAll {

        @Test
        fun `offset limit が正しく設定できていること`() {
            val threads = threadsDBMapper.selectAll(offset = 1, limit = 1)
            assertEquals(1, threads.size)
            assertEquals("1", threads[0].id)
        }

        @Test
        fun `offset limit が対象外の範囲を指している場合は値が取れないこと`() {
            val threads = threadsDBMapper.selectAll(offset = 2, limit = 1)
            assertEquals(0, threads.size)
        }
    }

    @Nested
    inner class CountAll {

        @Test
        fun `正しくスレッド数を取得できること`() {
            assertEquals(2, threadsDBMapper.countAll())
        }
    }

    @Nested
    inner class SelectByUserId {

        @Test
        fun `正しくスレッドを取得できること`() {
            val thread = threadsDBMapper.selectById("1")
            assertEquals("1", thread?.id)
        }

        @Test
        fun `ロックを取得できること`() {
            val thread = threadsDBMapper.selectById("1", isLock = true)
            assertEquals("1", thread?.id)
        }

        @Test
        fun `存在しないスレッドを取得できないこと`() {
            val thread = threadsDBMapper.selectById("999")
            assertEquals(null, thread)
        }

        @Test
        fun `削除済みのスレッドを取得できること`() {
            val thread = threadsDBMapper.selectById("99", isLock = true)
            assertEquals("99", thread?.id)
            assertNotNull(thread?.deletedAt)
        }
    }

    @Nested
    inner class Insert {

        @Test
        fun `追加したスレッドの値が正しいこと`() {
            val actual = threadsDBMapper.selectById("99")
            assertEquals("99", actual!!.id)
            assertEquals("title", actual.title)
            assertEquals("userId2", actual.userId)
            assertEquals(now, DateTime(actual.createdAt))
            assertEquals(now.plus(1, ChronoUnit.DAYS), DateTime(actual.updatedAt))
            assertEquals(now.plus(2, ChronoUnit.DAYS), DateTime(actual.deletedAt!!))
        }
    }

    @Nested
    inner class Update {

        @Test
        fun `更新したスレッドの値が正しいこと`() {
            val actual = threadsDBMapper.selectById("2")
            assertEquals("2", actual!!.id)
            assertEquals("title updated", actual.title)
            assertEquals("userId2", actual.userId)
            assertEquals(now, DateTime(actual.createdAt))
            assertEquals(now.plus(3, ChronoUnit.DAYS), DateTime(actual.updatedAt))
            assertNull(actual.deletedAt)
        }
    }
}
