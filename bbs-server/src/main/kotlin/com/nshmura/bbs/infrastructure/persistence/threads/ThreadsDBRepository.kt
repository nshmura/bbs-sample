package com.nshmura.bbs.infrastructure.persistence.threads

import com.nshmura.bbs.domain.model.thread.Post
import com.nshmura.bbs.domain.model.thread.SearchResults
import com.nshmura.bbs.domain.model.thread.Thread
import com.nshmura.bbs.domain.model.thread.Threads
import com.nshmura.bbs.domain.model.thread.ThreadsRepository
import com.nshmura.bbs.domain.model.thread.values.Count
import com.nshmura.bbs.domain.model.thread.values.Keyword
import com.nshmura.bbs.domain.model.thread.values.PostId
import com.nshmura.bbs.domain.model.thread.values.Start
import com.nshmura.bbs.domain.model.thread.values.ThreadId
import com.nshmura.bbs.domain.model.thread.values.Total
import org.springframework.stereotype.Repository

@Repository
class ThreadsDBRepository(
    private val threadsDBMapper: ThreadsDBMapper,
    private val postsDBMapper: PostsDBMapper,
    private val searchDBMapper: SearchDBMapper
) : ThreadsRepository {

    /**
     * [Thread] を保存する
     *
     * @param thread 保存する [Thread]
     * @return 保存した [Thread]
     */
    override fun saveThread(thread: Thread): Thread {
        if (threadsDBMapper.selectById(thread.id.value) == null) {
            // 新規登録
            threadsDBMapper.insert(ThreadData(thread)).also { checkOneAffectedRow(it) }
            searchDBMapper.insert(SearchData(thread)).also { checkOneAffectedRow(it) }
        } else {
            // 更新
            threadsDBMapper.update(ThreadData(thread)).also { checkOneAffectedRow(it) }
            if (thread.isDeleted()) {
                searchDBMapper.deleteThread(SearchData(thread))
            } else {
                searchDBMapper.update(SearchData(thread))
            }
        }
        return thread
    }

    /**
     * [Post] を保存する
     *
     * @param post 保存する [Post]
     * @return 保存した [Post]
     */
    override fun savePost(post: Post): Post {
        if (postsDBMapper.selectById(post.id.value) == null) {
            // 新規登録
            postsDBMapper.insert(PostData(post)).also { checkOneAffectedRow(it) }
            searchDBMapper.insert(SearchData(post)).also { checkOneAffectedRow(it) }
        } else {
            // 更新
            postsDBMapper.update(PostData(post)).also { checkOneAffectedRow(it) }
            if (post.isDeleted()) {
                searchDBMapper.deletePost(SearchData(post))
            } else {
                searchDBMapper.update(SearchData(post))
            }
        }
        return post
    }

    /**
     * 影響を受けた行数が1件かどうかチェックする
     *
     * @param effectedRecords 影響を受けた行数
     * @throws IllegalStateException 影響を受けた行数が1でない場合
     */
    private fun checkOneAffectedRow(effectedRecords: Int) {
        if (effectedRecords != 1) {
            throw IllegalStateException("Failed to save data.")
        }
    }

    /**
     * [Thread] 一覧を取得する
     * @param start [Thread] の取得開始位置
     * @param count [Thread] の取得件数
     * @return 取得した [Thread] 一覧
     */
    override fun findThreads(start: Start, count: Count): Threads {
        val threads = threadsDBMapper.selectAll(offset = start.value, limit = count.value)

        if (threads.isEmpty()) {
            return Threads(list = emptyList(), start = start, total = Total.of(0))
        }

        val totals = postsDBMapper.countAllByThreadIds(threads.map { it.id })
        val threadsWithTotal = threads.map { thread ->
            val postTotal = totals.firstOrNull { it.threadId == thread.id }?.count ?: 0
            thread.toModel(postTotal = postTotal)
        }

        val total = threadsDBMapper.countAll()
        return Threads(list = threadsWithTotal, start = start, total = Total.of(total))
    }

    /**
     * [Thread] を取得する
     *
     * @param threadId スレッドID
     * @param postStart [Post] の取得開始位置
     * @param postCount [Post] の取得件数
     * @return 取得した [Thread]
     */
    override fun findThread(threadId: ThreadId, postStart: Start?, postCount: Count?): Thread {
        return findThreadOrNull(threadId, postStart, postCount) ?: throw IllegalStateException("Thread not found")
    }

    /**
     * [Thread] を取得する
     *
     * スレッドが削除されている場合、関連する投稿は取得されない
     *
     * @param threadId スレッドID
     * @param postStart [Post] の取得開始位置
     * @param postCount [Post] の取得件数
     * @return 取得した [Thread]
     */
    override fun findThreadOrNull(threadId: ThreadId, postStart: Start?, postCount: Count?): Thread? {
        val thread = threadsDBMapper.selectById(threadId.value) ?: return null
        val postsTotal = postsDBMapper.countAllByThreadId(threadId.value)

        if (thread.deletedAt != null) {
            return thread.toModel(postsTotal) // スレッドが削除されている場合、関連する投稿は取得しない
        }

        if (postStart == null || postCount == null) {
            return thread.toModel(postTotal = postsTotal) // 投稿の取得開始位置および件数が指定されていない場合、関連する投稿は取得しない
        }

        val posts = postsDBMapper.selectAllByThreadId(threadId.value, postStart.value, postCount.value)

        return thread.toModel(
            postTotal = postsTotal,
            postStart = postStart.value,
            posts = posts.map { it.toModel() }
        )
    }

    /**
     * 全文検索によりスレッドおよび投稿を検索する
     *
     * @param keyword 検索キーワード
     * @param start 検索結果の取得開始位置
     * @param count 検索結果の取得件数
     * @return 検索結果
     */
    override fun search(keyword: Keyword, start: Start, count: Count): SearchResults {
        val results = searchDBMapper.search(keyword = keyword.value, offset = start.value, limit = count.value)
        val total =
            if (results.isNotEmpty()) {
                searchDBMapper.count(keyword = keyword.value, offset = start.value, limit = count.value)
            } else {
                0
            }

        return SearchResults(
            results.map { it.toModel() },
            start = start,
            total = Total.of(total)
        )
    }

    /**
     * [Thread] を更新用にロック取得する
     *
     * @param threadId スレッドID
     * @return ロック取得した [Thread]
     */
    override fun lockThread(threadId: ThreadId): Thread {
        return threadsDBMapper.selectById(threadId.value, isLock = true)
            ?.toModel(postsDBMapper.countAllByThreadId(threadId.value))
            ?: throw IllegalStateException("Thread not found")
    }

    /**
     * [Post] を更新用にロック取得する
     *
     * @param postId 投稿ID
     * @return ロック取得した [Post]
     */
    override fun lockPost(postId: PostId): Post {
        return postsDBMapper.selectById(postId.value, isLock = true)
            ?.toModel()
            ?: throw IllegalStateException("Post not found")
    }

    /**
     * [Post] を取得する
     */
    override fun findPost(postId: PostId): Post {
        return postsDBMapper.selectById(postId.value, isLock = false)
            ?.toModel()
            ?: throw IllegalStateException("Post not found")
    }
}
