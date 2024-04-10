package com.nshmura.bbs.application.service

import com.nshmura.bbs.domain.model.DateTime
import com.nshmura.bbs.domain.model.thread.Post
import com.nshmura.bbs.domain.model.thread.SearchResults
import com.nshmura.bbs.domain.model.thread.Thread
import com.nshmura.bbs.domain.model.thread.Threads
import com.nshmura.bbs.domain.model.thread.ThreadsRepository
import com.nshmura.bbs.domain.model.thread.values.Count
import com.nshmura.bbs.domain.model.thread.values.Keyword
import com.nshmura.bbs.domain.model.thread.values.Page
import com.nshmura.bbs.domain.model.thread.values.PostContent
import com.nshmura.bbs.domain.model.thread.values.PostId
import com.nshmura.bbs.domain.model.thread.values.ThreadId
import com.nshmura.bbs.domain.model.thread.values.ThreadTitle
import com.nshmura.bbs.domain.model.user.UsersRepository
import com.nshmura.bbs.domain.model.user.values.UserId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ThreadsService(
    private val usersRepository: UsersRepository,
    private val threadsRepository: ThreadsRepository,
    @Value("\${bbs.max-posts-per-thread}")
    private val maxPostsPerThread: Int
) {

    /**
     * スレッドを作成する
     */
    @Transactional
    fun createThread(userId: UserId, title: ThreadTitle, now: DateTime): Thread {
        val thread = Thread.create(ThreadId.generate(), title, userId, now)
        return threadsRepository.saveThread(thread)
    }

    /**
     * スレッドを更新する
     */
    @Transactional
    fun updateThread(userId: UserId, threadId: ThreadId, title: ThreadTitle, now: DateTime): Thread {
        val thread = threadsRepository.lockThread(threadId)
        val updatedThread = thread.update(userId, threadId, title, now)
        return threadsRepository.saveThread(updatedThread)
    }

    /**
     * スレッドを削除する
     */
    @Transactional
    fun deleteThread(userId: UserId, threadId: ThreadId, now: DateTime): Thread {
        val thread = threadsRepository.lockThread(threadId)
        val deletedThread = thread.delete(userId, threadId, now)
        return threadsRepository.saveThread(deletedThread)
    }

    /**
     * 投稿を作成する
     */
    @Transactional
    fun createPost(userId: UserId, threadId: ThreadId, content: PostContent, now: DateTime): Post {
        val thread = threadsRepository.findThread(threadId)
        val post = thread.createPost(PostId.generate(), userId, threadId, content, now, maxPostsPerThread)
        return threadsRepository.savePost(post)
    }

    /**
     * 投稿を更新する
     */
    @Transactional
    fun updatePost(userId: UserId, postId: PostId, content: PostContent, now: DateTime): Post {
        val post = threadsRepository.lockPost(postId)
        val thread = threadsRepository.findThread(post.threadId)
        val updatedPost = thread.updatePost(post, userId, content, now)
        return threadsRepository.savePost(updatedPost)
    }

    /**
     * 投稿を削除する
     */
    @Transactional
    fun deletePost(userId: UserId, postId: PostId, now: DateTime): Post {
        val post = threadsRepository.lockPost(postId)
        val thread = threadsRepository.findThread(post.threadId)
        val deletedPost = thread.deletePost(post, userId, now)
        return threadsRepository.savePost(deletedPost)
    }

    /**
     * スレッド一覧を取得する
     */
    @Transactional(readOnly = true)
    fun findThreads(page: Page, count: Count): Threads {
        val threads = threadsRepository.findThreads(page.toStart(count), count)
        val users = usersRepository.findUsersByIds(threads.getUserIds())
        return threads.attachUsers(users)
    }

    /**
     * スレッドを取得する
     */
    @Transactional(readOnly = true)
    fun findThreadById(id: ThreadId, postPage: Page, postCount: Count): Thread? {
        val thread = threadsRepository.findThreadOrNull(id, postPage.toStart(postCount), postCount) ?: return null
        val users = usersRepository.findUsersByIds(thread.getUserIds())
        return thread.attachUsers(users)
    }

    /**
     * スレッドと投稿を検索する
     */
    @Transactional(readOnly = true)
    fun search(keyword: Keyword, page: Page, count: Count): SearchResults {
        val results = threadsRepository.search(keyword, page.toStart(count), count)
        val users = usersRepository.findUsersByIds(results.getUserIds())
        return results.attachUsers(users)
    }

    /**
     * 投稿を取得する
     */
    fun findPost(postId: PostId): Post {
        return threadsRepository.findPost(postId)
    }
}
