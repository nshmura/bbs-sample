package com.nshmura.bbs.domain.model.thread

import com.nshmura.bbs.domain.model.thread.values.Count
import com.nshmura.bbs.domain.model.thread.values.Keyword
import com.nshmura.bbs.domain.model.thread.values.PostId
import com.nshmura.bbs.domain.model.thread.values.Start
import com.nshmura.bbs.domain.model.thread.values.ThreadId

/**
 * スレッドのリポジトリ
 */
interface ThreadsRepository {

    /**
     * [Thread] を保存する
     *
     * @param thread 保存する [Thread]
     * @return 保存した [Thread]
     */
    fun saveThread(thread: Thread): Thread

    /**
     * [Post] を保存する
     *
     * @param post 保存する [Post]
     * @return 保存した [Post]
     */
    fun savePost(post: Post): Post

    /**
     * [Thread] 一覧を取得する
     * @param start [Thread] の取得開始位置
     * @param count [Thread] の取得件数
     */
    fun findThreads(start: Start, count: Count): Threads

    /**
     * [Thread] を取得する
     *
     * @param threadId スレッドID
     */
    fun findThread(threadId: ThreadId, postStart: Start? = null, postCount: Count? = null): Thread

    /**
     * [Thread] を取得する
     *
     * @param threadId スレッドID
     * @param postStart [Post] の取得開始位置
     * @param postCount [Post] の取得件数
     */
    fun findThreadOrNull(threadId: ThreadId, postStart: Start? = null, postCount: Count? = null): Thread?

    /**
     * 全文検索によりスレッドおよび投稿を検索する
     */
    fun search(keyword: Keyword, start: Start, count: Count): SearchResults

    /**
     * [Thread] を更新用にロック取得する
     *
     * @param threadId スレッドID
     */
    fun lockThread(threadId: ThreadId): Thread

    /**
     * [Post] を更新用にロック取得する
     */
    fun lockPost(postId: PostId): Post

    /**
     * [Post] を取得する
     */
    fun findPost(postId: PostId): Post
}
