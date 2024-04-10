package com.nshmura.bbs.application.service

import com.nshmura.bbs.domain.model.DateTime
import com.nshmura.bbs.domain.model.thread.SearchResult
import com.nshmura.bbs.domain.model.thread.SearchResults
import com.nshmura.bbs.domain.model.thread.Thread
import com.nshmura.bbs.domain.model.thread.Threads
import com.nshmura.bbs.domain.model.thread.ThreadsRepository
import com.nshmura.bbs.domain.model.thread.values.Count
import com.nshmura.bbs.domain.model.thread.values.Keyword
import com.nshmura.bbs.domain.model.thread.values.Page
import com.nshmura.bbs.domain.model.thread.values.PostContent
import com.nshmura.bbs.domain.model.thread.values.PostId
import com.nshmura.bbs.domain.model.thread.values.SearchRecordType
import com.nshmura.bbs.domain.model.thread.values.Start
import com.nshmura.bbs.domain.model.thread.values.ThreadId
import com.nshmura.bbs.domain.model.thread.values.ThreadTitle
import com.nshmura.bbs.domain.model.thread.values.Total
import com.nshmura.bbs.domain.model.user.User
import com.nshmura.bbs.domain.model.user.UsersRepository
import com.nshmura.bbs.domain.model.user.values.UserId
import com.nshmura.bbs.domain.model.user.values.UserName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ThreadsServiceTest {
    private val usersRepository: UsersRepository = mock()
    private val threadsRepository: ThreadsRepository = mock()
    private val maxPostsPerThread = 2
    private val threadsService = ThreadsService(usersRepository, threadsRepository, maxPostsPerThread)

    @BeforeEach
    fun setup() {
        reset(usersRepository, threadsRepository)
    }

    val userId = UserId.of("userId")
    val threadId = ThreadId.generate()
    val postId = PostId.generate()
    val title = ThreadTitle.of("title")
    val now = DateTime.now()
    val content = PostContent.of("content")

    val thread = Thread.create(
        id = threadId,
        title = title,
        userId = userId,
        now = now
    )

    val post = thread.createPost(
        generate = postId,
        userId = userId,
        threadId = threadId,
        content = content,
        now = now,
        maxPostsPerThread = maxPostsPerThread
    )

    @Nested
    inner class CreateThread {

        @Test
        fun `スレッドを作成する`() {
            whenever(threadsRepository.saveThread(any())).thenReturn(thread)
            threadsService.createThread(userId, title, now)
            verify(threadsRepository).saveThread(any())
        }
    }

    @Nested
    inner class UpdateThread {

        @Test
        fun `スレッドを更新する`() {
            whenever(threadsRepository.lockThread(any())).thenReturn(thread)
            whenever(threadsRepository.saveThread(any())).thenReturn(thread)

            threadsService.updateThread(userId, threadId, title, now)

            verify(threadsRepository).lockThread(any())
            verify(threadsRepository).saveThread(any())
        }
    }

    @Nested
    inner class DeleteThread {

        @Test
        fun `スレッドを削除する`() {
            whenever(threadsRepository.lockThread(any())).thenReturn(thread)
            whenever(threadsRepository.saveThread(any())).thenReturn(thread)

            threadsService.deleteThread(userId, threadId, now)

            verify(threadsRepository).lockThread(any())
            verify(threadsRepository).saveThread(any())
        }
    }

    @Nested
    inner class CreatePost {

        @Test
        fun `投稿を作成する`() {
            whenever(threadsRepository.findThread(any(), anyOrNull(), anyOrNull())).thenReturn(thread)
            whenever(threadsRepository.savePost(any())).thenReturn(post)

            threadsService.createPost(userId, threadId, content = content, now)

            verify(threadsRepository).findThread(any(), anyOrNull(), anyOrNull())
            verify(threadsRepository).savePost(any())
        }
    }

    @Nested
    inner class UpdatePost {

        @Test
        fun `投稿を更新する`() {
            whenever(threadsRepository.findThread(any(), anyOrNull(), anyOrNull())).thenReturn(thread)
            whenever(threadsRepository.lockPost(any())).thenReturn(post)
            whenever(threadsRepository.savePost(any())).thenReturn(post)

            threadsService.updatePost(userId, post.id, content, now)

            verify(threadsRepository).findThread(any(), anyOrNull(), anyOrNull())
            verify(threadsRepository).lockPost(any())
            verify(threadsRepository).savePost(any())
        }
    }

    @Nested
    inner class DeletePost {

        @Test
        fun `投稿を削除する`() {
            whenever(threadsRepository.findThread(any(), anyOrNull(), anyOrNull())).thenReturn(thread)
            whenever(threadsRepository.lockPost(any())).thenReturn(post)
            whenever(threadsRepository.savePost(any())).thenReturn(post)

            threadsService.deletePost(userId, post.id, now)

            verify(threadsRepository).findThread(any(), anyOrNull(), anyOrNull())
            verify(threadsRepository).lockPost(any())
            verify(threadsRepository).savePost(any())
        }
    }

    @Nested
    inner class FindThreads {
        @Test
        fun `スレッドを取得する`() {
            val page = Page.of(1)
            val start = Start.of(0)
            val count = Count.of(10)
            val total = Total.of(1)
            val threads = Threads(listOf(thread), start, total)
            val users = listOf(User(thread.userId, UserName.of("userName")))

            whenever(threadsRepository.findThreads(start, count)).thenReturn(threads)
            whenever(usersRepository.findUsersByIds(any())).thenReturn(users)

            val actual = threadsService.findThreads(page, count)

            assertEquals(thread.id, actual.list[0].id)
            assertEquals(thread.userId, actual.list[0].user?.id)

            verify(threadsRepository).findThreads(start, count)
        }
    }

    @Nested
    inner class FindThreadById {
        @Test
        fun `スレッドを取得する`() {
            val postPage = Page.of(1)
            val postStart = Start.of(0)
            val postCount = Count.of(10)
            val users = listOf(User(thread.userId, UserName.of("userName")))

            whenever(threadsRepository.findThreadOrNull(threadId, postStart, postCount)).thenReturn(thread)
            whenever(usersRepository.findUsersByIds(any())).thenReturn(users)

            val actual = threadsService.findThreadById(threadId, postPage, postCount)

            assertEquals(thread.id, actual?.id)
            assertEquals(thread.userId, actual?.user?.id)

            verify(threadsRepository).findThreadOrNull(threadId, postStart, postCount)
            verify(usersRepository).findUsersByIds(any())
        }
    }

    @Nested
    inner class Search {
        @Test
        fun `スレッドと投稿を検索する`() {
            val keyword = Keyword.of("keyword")
            val page = Page.of(1)
            val start = Start.of(0)
            val count = Count.of(10)
            val total = Total.of(1)
            val result = SearchResult(
                type = SearchRecordType.THREAD,
                message = thread.title.value,
                threadId = thread.id,
                postId = postId,
                userId = userId,
                createdAt = now,
                updatedAt = now
            )

            val threads = SearchResults(listOf(result), start, total)
            val users = listOf(User(thread.userId, UserName.of("userName")))

            whenever(threadsRepository.search(keyword, start, count)).thenReturn(threads)
            whenever(usersRepository.findUsersByIds(any())).thenReturn(users)

            val actual = threadsService.search(keyword, page, count)

            assertEquals(thread.id, actual.list[0].threadId)
            assertEquals(thread.userId, actual.list[0].user?.id)

            verify(threadsRepository).search(keyword, start, count)
            verify(usersRepository).findUsersByIds(any())
        }
    }
}
