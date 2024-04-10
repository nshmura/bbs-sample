package com.nshmura.bbs.infrastructure.persistance

import com.nshmura.bbs.domain.model.DateTime
import com.nshmura.bbs.domain.model.thread.Post
import com.nshmura.bbs.domain.model.thread.Thread
import com.nshmura.bbs.domain.model.thread.values.Count
import com.nshmura.bbs.domain.model.thread.values.Keyword
import com.nshmura.bbs.domain.model.thread.values.PostContent
import com.nshmura.bbs.domain.model.thread.values.PostId
import com.nshmura.bbs.domain.model.thread.values.Start
import com.nshmura.bbs.domain.model.thread.values.ThreadId
import com.nshmura.bbs.domain.model.thread.values.ThreadTitle
import com.nshmura.bbs.domain.model.user.values.UserId
import com.nshmura.bbs.infrastructure.persistence.threads.PostData
import com.nshmura.bbs.infrastructure.persistence.threads.PostsCountData
import com.nshmura.bbs.infrastructure.persistence.threads.PostsDBMapper
import com.nshmura.bbs.infrastructure.persistence.threads.SearchDBMapper
import com.nshmura.bbs.infrastructure.persistence.threads.SearchData
import com.nshmura.bbs.infrastructure.persistence.threads.ThreadData
import com.nshmura.bbs.infrastructure.persistence.threads.ThreadsDBMapper
import com.nshmura.bbs.infrastructure.persistence.threads.ThreadsDBRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ThreadsDBRepositoryTest {

    private val now = DateTime.now()
    private val threadsDBMapper: ThreadsDBMapper = mock()
    private val postsDBMapper: PostsDBMapper = mock()
    private val searchDBMapper: SearchDBMapper = mock()

    private val threadsDBRepository = ThreadsDBRepository(
        threadsDBMapper,
        postsDBMapper,
        searchDBMapper
    )

    @BeforeEach
    fun setup() {
        reset(threadsDBMapper, postsDBMapper, searchDBMapper)
    }

    @Nested
    inner class SaveThread {

        private val thread = Thread.create(
            id = ThreadId.of("thread1"),
            title = ThreadTitle.of("title"),
            userId = UserId.of("user1"),
            now = now
        )

        @Test
        fun `新規登録の場合、ThreadData と SearchData が登録されること`() {

            whenever(threadsDBMapper.selectById(thread.id.value)).thenReturn(null)
            whenever(threadsDBMapper.insert(ThreadData(thread))).thenReturn(1)
            whenever(searchDBMapper.insert(SearchData(thread))).thenReturn(1)

            threadsDBRepository.saveThread(thread)

            verify(threadsDBMapper).insert(ThreadData(thread))
            verify(searchDBMapper).insert(SearchData(thread))
        }

        @Test
        fun `新規登録時の thread テーブルへの insert で反映件数が 0 件の場合、例外がスローされること`() {
            whenever(threadsDBMapper.selectById(thread.id.value)).thenReturn(null)
            whenever(threadsDBMapper.insert(ThreadData(thread))).thenReturn(1)
            whenever(searchDBMapper.insert(SearchData(thread))).thenReturn(0)

            assertThrows<IllegalStateException> {
                threadsDBRepository.saveThread(thread)
            }
        }

        @Test
        fun `新規登録時の search テーブルへの insert で反映件数が 0 件の場合、例外がスローされること`() {
            whenever(threadsDBMapper.selectById(thread.id.value)).thenReturn(null)
            whenever(threadsDBMapper.insert(ThreadData(thread))).thenReturn(0)

            assertThrows<IllegalStateException> {
                threadsDBRepository.saveThread(thread)
            }
        }

        @Test
        fun `更新の場合、ThreadData と SearchData が更新されること`() {
            whenever(threadsDBMapper.selectById(thread.id.value)).thenReturn(ThreadData(thread))
            whenever(threadsDBMapper.update(ThreadData(thread))).thenReturn(1)
            whenever(searchDBMapper.update(SearchData(thread))).thenReturn(1)

            threadsDBRepository.saveThread(thread)

            verify(threadsDBMapper).update(ThreadData(thread))
            verify(searchDBMapper).update(SearchData(thread))
        }

        @Test
        fun `更新の場合の thread テーブルへの反映で反映件数が 0 件の場合、例外がスローされること`() {
            whenever(threadsDBMapper.selectById(thread.id.value)).thenReturn(ThreadData(thread))
            whenever(threadsDBMapper.update(ThreadData(thread))).thenReturn(0)

            assertThrows<IllegalStateException> {
                threadsDBRepository.saveThread(thread)
            }
        }
    }

    @Nested
    inner class SavePost {

        private val post = Post.create(
            id = PostId.of("post1"),
            userId = UserId.of("user1"),
            threadId = ThreadId.of("thread1"),
            content = PostContent.of("content"),
            now = now
        )

        @Test
        fun `新規登録の場合、PostData と SearchData が登録されること`() {
            whenever(postsDBMapper.selectById(post.id.value)).thenReturn(null)
            whenever(postsDBMapper.insert(PostData(post))).thenReturn(1)
            whenever(searchDBMapper.insert(SearchData(post))).thenReturn(1)

            threadsDBRepository.savePost(post)

            verify(postsDBMapper).insert(PostData(post))
            verify(searchDBMapper).insert(SearchData(post))
        }

        @Test
        fun `新規登録時の post テーブルへの insert で反映件数が 0 件の場合、例外がスローされること`() {
            whenever(postsDBMapper.selectById(post.id.value)).thenReturn(null)
            whenever(postsDBMapper.insert(PostData(post))).thenReturn(0)

            assertThrows<IllegalStateException> {
                threadsDBRepository.savePost(post)
            }
        }

        @Test
        fun `新規登録時の search テーブルへの insert で反映件数が 0 件の場合、例外がスローされること`() {
            whenever(postsDBMapper.selectById(post.id.value)).thenReturn(null)
            whenever(postsDBMapper.insert(PostData(post))).thenReturn(1)
            whenever(searchDBMapper.insert(SearchData(post))).thenReturn(0)

            assertThrows<IllegalStateException> {
                threadsDBRepository.savePost(post)
            }
        }

        @Test
        fun `更新の場合、PostData と SearchData が更新されること`() {
            whenever(postsDBMapper.selectById(post.id.value)).thenReturn(PostData(post))
            whenever(postsDBMapper.update(PostData(post))).thenReturn(1)
            whenever(searchDBMapper.update(SearchData(post))).thenReturn(1)

            threadsDBRepository.savePost(post)

            verify(postsDBMapper).update(PostData(post))
            verify(searchDBMapper).update(SearchData(post))
        }

        @Test
        fun `更新の場合の post テーブルへの反映で反映件数が 0 件の場合、例外がスローされること`() {
            whenever(postsDBMapper.selectById(post.id.value)).thenReturn(PostData(post))
            whenever(postsDBMapper.update(PostData(post))).thenReturn(0)

            assertThrows<IllegalStateException> {
                threadsDBRepository.savePost(post)
            }
        }
    }

    @Nested
    inner class FindThreadList {

        private val threads = listOf(
            ThreadData(
                Thread.create(
                    id = ThreadId.of("thread1"),
                    title = ThreadTitle.of("title1"),
                    userId = UserId.of("user1"),
                    now = now
                )
            ),
            ThreadData(
                Thread.create(
                    id = ThreadId.of("thread2"),
                    title = ThreadTitle.of("title2"),
                    userId = UserId.of("user2"),
                    now = now
                )
            )
        )

        private val postsCounts = listOf(
            PostsCountData("thread1", 10),
            PostsCountData("thread2", 20),
        )

        @Test
        fun `スレッド一覧を取得できる`() {
            val start = Start.of(0)
            val count = Count.of(10)

            whenever(threadsDBMapper.selectAll(start.value, count.value)).thenReturn(threads)
            whenever(postsDBMapper.countAllByThreadIds(listOf("thread1", "thread2"))).thenReturn(postsCounts)
            whenever(threadsDBMapper.countAll()).thenReturn(2)

            val result = threadsDBRepository.findThreads(start, count)

            assertEquals(2, result.total.value)
            assertEquals(0, result.start.value)
            assertEquals(2, result.list.size)
            assertEquals("thread1", result.list[0].id.value)
            assertEquals("thread2", result.list[1].id.value)

            verify(threadsDBMapper).selectAll(start.value, count.value)
            verify(postsDBMapper).countAllByThreadIds(listOf("thread1", "thread2"))
            verify(threadsDBMapper).countAll()
        }

        @Test
        fun `スレッド一覧が存在しない場合、空のリストが返る`() {
            val start = Start.of(0)
            val count = Count.of(10)

            whenever(threadsDBMapper.selectAll(start.value, count.value)).thenReturn(emptyList())

            val result = threadsDBRepository.findThreads(start, count)

            assertEquals(0, result.total.value)
            assertEquals(0, result.start.value)
            assertEquals(0, result.list.size)

            verify(threadsDBMapper).selectAll(start.value, count.value)
            verify(postsDBMapper, never()).countAllByThreadIds(emptyList())
            verify(threadsDBMapper, never()).countAll()
        }
    }

    @Nested
    inner class FindThread {
        @Test
        fun `スレッドが存在しない場合、例外がスローされる`() {
            whenever(threadsDBMapper.selectById("thread1")).thenReturn(null)

            assertThrows<IllegalStateException> {
                threadsDBRepository.findThread(ThreadId.of("thread1"))
            }
        }
    }

    @Nested
    inner class FindThreadOrNull {

        private val thread = ThreadData(
            Thread.create(
                id = ThreadId.of("thread1"),
                title = ThreadTitle.of("title1"),
                userId = UserId.of("user1"),
                now = now
            )
        )

        private val posts = listOf(
            PostData(
                Post.create(
                    id = PostId.of("post1"),
                    userId = UserId.of("user1"),
                    threadId = ThreadId.of("thread1"),
                    content = PostContent.of("content1"),
                    now = now
                )
            ),
            PostData(
                Post.create(
                    id = PostId.of("post2"),
                    userId = UserId.of("user2"),
                    threadId = ThreadId.of("thread1"),
                    content = PostContent.of("content2"),
                    now = now
                )
            )
        )

        @Test
        fun `スレッドが存在する場合、スレッドが取得できる`() {
            val threadId = ThreadId.of("thread1")
            val start = Start.of(0)
            val count = Count.of(10)

            whenever(threadsDBMapper.selectById(threadId.value)).thenReturn(thread)
            whenever(postsDBMapper.countAllByThreadId(threadId.value)).thenReturn(10)
            whenever(postsDBMapper.selectAllByThreadId(threadId.value, start.value, count.value)).thenReturn(posts)

            val result = threadsDBRepository.findThreadOrNull(threadId, start, count)

            assertEquals("thread1", result?.id?.value)
            assertEquals("title1", result?.title?.value)
            assertEquals("user1", result?.userId?.value)
            assertEquals(2, result?.posts?.size)
            assertEquals("post1", result?.posts?.get(0)?.id?.value)
            assertEquals("post2", result?.posts?.get(1)?.id?.value)
        }

        @Test
        fun `スレッドが存在しない場合、null が返る`() {
            whenever(threadsDBMapper.selectById("thread1")).thenReturn(null)

            val result = threadsDBRepository.findThreadOrNull(ThreadId.of("thread1"))

            assertEquals(null, result)
        }

        @Test
        fun `postStart の指定がない場合、関連する投稿は取得されない`() {
            val threadId = ThreadId.of("thread1")

            whenever(threadsDBMapper.selectById(threadId.value)).thenReturn(thread)
            whenever(postsDBMapper.countAllByThreadId(threadId.value)).thenReturn(10)

            val result = threadsDBRepository.findThreadOrNull(threadId)

            assertEquals("thread1", result?.id?.value)
            assertEquals("title1", result?.title?.value)
            assertEquals("user1", result?.userId?.value)
            assertEquals(0, result?.posts?.size)
        }

        @Test
        fun `postCount の指定がない場合、関連する投稿は取得されない`() {
            val threadId = ThreadId.of("thread1")
            val start = Start.of(0)

            whenever(threadsDBMapper.selectById(threadId.value)).thenReturn(thread)
            whenever(postsDBMapper.countAllByThreadId(threadId.value)).thenReturn(10)

            val result = threadsDBRepository.findThreadOrNull(threadId, start)

            assertEquals("thread1", result?.id?.value)
            assertEquals("title1", result?.title?.value)
            assertEquals("user1", result?.userId?.value)
            assertEquals(0, result?.posts?.size)
        }
    }

    @Nested
    inner class Search {

        private val threads = listOf(
            SearchData(
                Thread.create(
                    id = ThreadId.of("thread1"),
                    title = ThreadTitle.of("title1"),
                    userId = UserId.of("user1"),
                    now = now
                )
            ),
            SearchData(
                Post.create(
                    id = PostId.of("post1"),
                    threadId = ThreadId.of("thread2"),
                    userId = UserId.of("user1"),
                    content = PostContent.of("content"),
                    now = now
                )
            )
        )

        @Test
        fun `キーワードで検索できる`() {
            val keyword = Keyword.of("keyword")
            val start = Start.of(0)
            val count = Count.of(10)

            whenever(searchDBMapper.search(keyword.value, start.value, count.value)).thenReturn(threads)
            whenever(searchDBMapper.count(keyword.value, start.value, count.value)).thenReturn(2)

            val result = threadsDBRepository.search(keyword, start, count)

            assertEquals(2, result.total.value)
            assertEquals(0, result.start.value)
            assertEquals(2, result.list.size)
            assertEquals("thread1", result.list[0].threadId?.value)
            assertEquals("thread2", result.list[1].threadId?.value)
            assertEquals("post1", result.list[1].postId?.value)

            verify(searchDBMapper).search(keyword.value, start.value, count.value)
            verify(searchDBMapper).count(keyword.value, start.value, count.value)
        }

        @Test
        fun `キーワードで検索できない場合、空のリストが返る`() {
            val keyword = Keyword.of("keyword")
            val start = Start.of(0)
            val count = Count.of(10)

            whenever(searchDBMapper.search(keyword.value, start.value, count.value)).thenReturn(emptyList())

            val result = threadsDBRepository.search(keyword, start, count)

            assertEquals(0, result.total.value)
            assertEquals(0, result.start.value)
            assertEquals(0, result.list.size)

            verify(searchDBMapper).search(keyword.value, start.value, count.value)
            verify(searchDBMapper, never()).count(keyword.value, start.value, count.value)
        }
    }
}
