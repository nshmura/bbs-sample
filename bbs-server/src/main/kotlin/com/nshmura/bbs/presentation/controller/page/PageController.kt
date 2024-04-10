package com.nshmura.bbs.presentation.controller.page

import com.nshmura.bbs.application.service.ThreadsService
import com.nshmura.bbs.application.service.UsersService
import com.nshmura.bbs.domain.model.DateTime
import com.nshmura.bbs.domain.model.thread.values.Count
import com.nshmura.bbs.domain.model.thread.values.Keyword
import com.nshmura.bbs.domain.model.thread.values.Page
import com.nshmura.bbs.domain.model.thread.values.PostContent
import com.nshmura.bbs.domain.model.thread.values.PostId
import com.nshmura.bbs.domain.model.thread.values.ThreadId
import com.nshmura.bbs.domain.model.thread.values.ThreadTitle
import com.nshmura.bbs.domain.model.user.values.UserId
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PageController(
    private val threadsService: ThreadsService,
    private val usersService: UsersService
) {
    /**
     * テスト用のユーザ選択ページ
     */
    @GetMapping("/")
    fun index(
        @RequestParam userId: String?,
        model: Model,
        res: HttpServletResponse
    ): String {

        if (userId != null) {
            val user = usersService.findUserById(UserId.of(userId))
            res.addCookie(Cookie("accessToken", user.id.value)) // 簡単のため accessToken に userId をセットしている
            return "redirect:/threads"
        }

        val users = usersService.findUsers()
        model.addAttribute("users", users)
        return "index"
    }

    /**
     * スレッド一覧ページ
     */
    @GetMapping("/threads")
    fun threads(
        @RequestParam page: Int?,
        model: Model
    ): String {
        val threads = threadsService.findThreads(Page.of(page), THREADS_PER_PAGE)
        model.addAttribute("threads", threads)
        model.addAttribute("page", Page.of(page).value)
        model.addAttribute("maxPage", threads.total.toMaxPage(THREADS_PER_PAGE).value)
        return "threads"
    }

    /**
     * 検索ページ
     */
    @GetMapping("/search")
    fun search(
        @RequestParam keyword: String?,
        @RequestParam page: Int?,
        model: Model
    ): String {
        val results = threadsService.search(Keyword.of(keyword), Page.of(page), SEARCH_RESULTS_PER_PAGE)
        model.addAttribute("results", results)
        model.addAttribute("keyword", keyword)
        model.addAttribute("page", Page.of(page).value)
        model.addAttribute("maxPage", results.total.toMaxPage(SEARCH_RESULTS_PER_PAGE).value)
        return "search_result"
    }

    /**
     * スレッドの詳細ページ
     */
    @GetMapping("/threads/{threadId}")
    fun threadsDetail(
        @PathVariable threadId: String,
        @RequestParam page: Int?,
        model: Model,
        req: HttpServletRequest
    ): String {
        val userId = req.session.getAttribute("userId") as String
        val thread = threadsService.findThreadById(ThreadId.of(threadId), Page.of(page), POSTS_PER_PAGE)
        model.addAttribute("userId", userId)
        model.addAttribute("thread", thread)
        model.addAttribute("page", Page.of(page).value)
        model.addAttribute("maxPage", thread?.postsTotal?.toMaxPage(POSTS_PER_PAGE)?.value)
        return "thread_detail"
    }

    /**
     * スレッドの作成
     */
    @PostMapping("/threads/create", consumes = ["application/x-www-form-urlencoded"])
    fun createThread(
        @RequestParam request: Map<String, String>,
        req: HttpServletRequest
    ): String {
        val userId = req.session.getAttribute("userId") as String
        val title = request["title"]
        val thread = threadsService.createThread(UserId.of(userId), ThreadTitle.of(title), DateTime.now())
        return "redirect:/threads/${thread.id.value}"
    }

    /**
     * スレッドの削除
     */
    @PostMapping("/threads/{threadId}/delete", consumes = ["application/x-www-form-urlencoded"])
    fun deleteThread(
        @PathVariable threadId: String,
        req: HttpServletRequest
    ): String {
        val userId = req.session.getAttribute("userId") as String
        val thread = threadsService.deleteThread(UserId.of(userId), ThreadId.of(threadId), DateTime.now())
        return "redirect:/threads/${thread.id.value}"
    }

    /**
     * 投稿の作成
     */
    @PostMapping("/threads/{threadId}/posts/create", consumes = ["application/x-www-form-urlencoded"])
    fun createPost(
        @PathVariable threadId: String,
        @RequestParam request: Map<String, String>,
        req: HttpServletRequest
    ): String {
        val userId = req.session.getAttribute("userId") as String
        val content = request["content"] ?: throw IllegalArgumentException("content is required")
        val post =
            threadsService.createPost(UserId.of(userId), ThreadId.of(threadId), PostContent.of(content), DateTime.now())
        return "redirect:/threads/${post.threadId.value}"
    }

    /**
     * 投稿の編集フォーム
     */
    @GetMapping("/posts/{postId}/edit")
    fun editPostForm(
        @PathVariable postId: String,
        @RequestParam request: Map<String, String>,
        req: HttpServletRequest,
        model: Model
    ): String {
        val post = threadsService.findPost(PostId.of(postId))
        model.addAttribute("post", post)
        return "post_edit"
    }

    /**
     * 投稿の編集
     */
    @PostMapping("/posts/{postId}/edit")
    fun editPost(
        @PathVariable postId: String,
        @RequestParam request: Map<String, String>,
        req: HttpServletRequest
    ): String {
        val userId = req.session.getAttribute("userId") as String
        val post = threadsService.updatePost(
            UserId.of(userId),
            PostId.of(postId),
            PostContent.of(request["content"]),
            DateTime.now()
        )
        return "redirect:/threads/${post.threadId.value}"
    }

    /**
     * 投稿の削除
     */
    @PostMapping("/posts/{postId}/delete", consumes = ["application/x-www-form-urlencoded"])
    fun deletePost(
        @PathVariable postId: String,
        req: HttpServletRequest
    ): String {
        val userId = req.session.getAttribute("userId") as String
        val post = threadsService.deletePost(UserId.of(userId), PostId.of(postId), DateTime.now())
        return "redirect:/threads/${post.threadId.value}"
    }

    companion object {
        val THREADS_PER_PAGE: Count = Count.of(5)
        val POSTS_PER_PAGE: Count = Count.of(5)
        val SEARCH_RESULTS_PER_PAGE: Count = Count.of(5)
    }
}
