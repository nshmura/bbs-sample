package com.nshmura.bbs.presentation.controller.rest

import com.nshmura.bbs.application.service.ThreadsService
import com.nshmura.bbs.domain.model.DateTime
import com.nshmura.bbs.domain.model.thread.values.Count
import com.nshmura.bbs.domain.model.thread.values.Keyword
import com.nshmura.bbs.domain.model.thread.values.Page
import com.nshmura.bbs.domain.model.thread.values.PostContent
import com.nshmura.bbs.domain.model.thread.values.PostId
import com.nshmura.bbs.domain.model.thread.values.ThreadId
import com.nshmura.bbs.domain.model.thread.values.ThreadTitle
import com.nshmura.bbs.domain.model.user.values.UserId
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * この API は使われていません。
 *
 * REST API の実装例です。
 */

@RestController
class RestAPIController(
    private val threadsService: ThreadsService
) {

    /**
     * スレッドを作成する
     */
    @PostMapping("/v1/api/threads", consumes = ["application/json"])
    fun createThread(
        @RequestBody request: ThreadCreateRequest,
        req: HttpServletRequest
    ): ThreadResponse {
        val userId = req.session.getAttribute("userId") as String
        val thread = threadsService.createThread(UserId.of(userId), ThreadTitle.of(request.title), DateTime.now())
        return ThreadResponse.of(thread)
    }

    /**
     * スレッドを更新する
     */
    @PutMapping("/v1/api/threads/{threadId}", consumes = ["application/json"])
    fun updateThread(
        @PathVariable threadId: String,
        @RequestBody request: ThreadUpdateRequest,
        req: HttpServletRequest
    ): ThreadResponse {
        val userId = req.session.getAttribute("userId") as String
        val thread =
            threadsService.updateThread(
                UserId.of(userId),
                ThreadId.of(threadId),
                ThreadTitle.of(request.title),
                DateTime.now()
            )
        return ThreadResponse.of(thread)
    }

    /**
     * スレッドを削除する
     */
    @DeleteMapping("/v1/api/threads/{threadId}")
    fun deleteThread(
        @PathVariable threadId: String,
        req: HttpServletRequest
    ): ThreadResponse {
        val userId = req.session.getAttribute("userId") as String
        val thread = threadsService.deleteThread(
            UserId.of(userId),
            ThreadId.of(threadId),
            DateTime.now()
        )
        return ThreadResponse.of(thread)
    }

    /**
     * 投稿を作成する
     */
    @PostMapping("/v1/api/threads/{threadId}/posts", consumes = ["application/json"])
    fun createPost(
        @PathVariable threadId: String,
        @RequestBody request: PostCreateRequest,
        req: HttpServletRequest
    ): PostResponse {
        val userId = req.session.getAttribute("userId") as String
        val post = threadsService.createPost(
            UserId.of(userId),
            ThreadId.of(threadId),
            PostContent.of(request.content),
            DateTime.now()
        )
        return PostResponse.of(post)
    }

    /**
     * 投稿を更新する
     */
    @PutMapping("/v1/api/threads/{threadId}/posts/{postId}", consumes = ["application/json"])
    fun updatePost(
        @PathVariable threadId: String,
        @PathVariable postId: String,
        @RequestBody request: PostUpdateRequest,
        req: HttpServletRequest
    ): PostResponse {
        val userId = req.session.getAttribute("userId") as String
        val post = threadsService.updatePost(
            UserId.of(userId),
            PostId.of(postId),
            PostContent.of(request.content),
            DateTime.now()
        )
        return PostResponse.of(post)
    }

    /**
     * 投稿を削除する
     */
    @DeleteMapping("/v1/api/threads/{threadId}/posts/{postId}")
    fun deletePost(
        @PathVariable threadId: String,
        @PathVariable postId: String,
        req: HttpServletRequest
    ): PostResponse {
        val userId = req.session.getAttribute("userId") as String
        val post = threadsService.deletePost(
            UserId.of(userId),
            PostId.of(postId),
            DateTime.now()
        )
        return PostResponse.of(post)
    }

    /**
     * スレッド一覧を取得する
     */
    @GetMapping("/v1/api/threads", produces = ["application/json"])
    fun findThreads(
        @RequestParam page: Int?,
        @RequestParam count: Int?
    ): ThreadsResponse {
        val threads = threadsService.findThreads(Page.of(page), Count.of(count))
        return ThreadsResponse(
            data = threads.list.map { ThreadResponse.of(it) },
            page = PageResponse(
                start = threads.start.value,
                total = threads.total.value
            )
        )
    }

    /**
     * スレッドとスレッドに紐づく投稿一覧を取得する
     */
    @GetMapping("/v1/api/threads/{threadId}", produces = ["application/json"])
    fun findThreadById(
        @PathVariable threadId: String,
        @RequestParam postPage: Int?,
        @RequestParam postCount: Int?
    ): ThreadResponse {
        val thread = threadsService.findThreadById(ThreadId.of(threadId), Page.of(postPage), Count.of(postCount))
            ?: throw IllegalAccessError("thread not found")
        return ThreadResponse.of(thread)
    }

    /**
     * スレッドおよび投稿を全文検索する
     */
    @GetMapping("/v1/api/search")
    fun searchThreadsAndPosts(
        @RequestParam keyword: String,
        @RequestParam page: Int?,
        @RequestParam count: Int?
    ): SearchResultsResponse {
        val searchResults = threadsService.search(Keyword.of(keyword), Page.of(page), Count.of(count))
        return SearchResultsResponse.of(searchResults)
    }
}
