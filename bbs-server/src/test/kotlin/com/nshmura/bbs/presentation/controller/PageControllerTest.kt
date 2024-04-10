package com.nshmura.bbs.presentation.controller

import com.nshmura.bbs.application.service.ThreadsService
import com.nshmura.bbs.application.service.UsersService
import com.nshmura.bbs.domain.model.DateTime
import com.nshmura.bbs.domain.model.thread.Thread
import com.nshmura.bbs.domain.model.thread.Threads
import com.nshmura.bbs.domain.model.thread.values.Page
import com.nshmura.bbs.domain.model.thread.values.Start
import com.nshmura.bbs.domain.model.thread.values.ThreadId
import com.nshmura.bbs.domain.model.thread.values.ThreadTitle
import com.nshmura.bbs.domain.model.thread.values.Total
import com.nshmura.bbs.domain.model.user.values.UserId
import com.nshmura.bbs.presentation.controller.page.PageController
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

@SpringBootTest
@AutoConfigureMockMvc
class PageControllerTest {

    private val now = DateTime.now()

    @MockBean
    private lateinit var threadsService: ThreadsService

    @MockBean
    private lateinit var usersService: UsersService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Nested
    inner class IndexRequest {

        @Test
        fun index() {
            mockMvc.perform(get("/"))
                .andExpect(status().isOk)
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("users"))
        }
    }

    @Nested
    inner class ThreadsRequest {

        private val threads = Threads(
            list = emptyList(),
            start = Start.of(0),
            total = Total.of(0)
        )

        @Test
        fun `スレッド一覧ページを正しく表示できる`() {
            val page = Page.of(1)
            whenever(threadsService.findThreads(page, PageController.THREADS_PER_PAGE))
                .thenReturn(threads)
            mockMvc.perform(
                get("/threads")
                    .cookie(Cookie("accessToken", "accessToken1"))
            )
                .andExpect(status().isOk)
                .andExpect(view().name("threads"))
                .andExpect(model().attributeExists("threads"))
        }

        @Test
        fun `ページ番号つきのスレッド一覧ページを正しく表示できる`() {
            val page = Page.of(2)
            whenever(threadsService.findThreads(page, PageController.THREADS_PER_PAGE))
                .thenReturn(threads)

            mockMvc.perform(
                get("/threads")
                    .param("page", "2")
                    .cookie(Cookie("accessToken", "accessToken1"))
            )
                .andExpect(status().isOk)
                .andExpect(view().name("threads"))
                .andExpect(model().attributeExists("threads"))
        }

        @Test
        fun `小さすぎるページ番号の場合は、ページ番号 1 として扱われる`() {
            val page = Page.of(1)
            whenever(threadsService.findThreads(page, PageController.THREADS_PER_PAGE))
                .thenReturn(threads)

            mockMvc.perform(
                get("/threads")
                    .param("page", "-1")
                    .cookie(Cookie("accessToken", "accessToken1"))
            )
                .andExpect(status().isOk)
                .andExpect(view().name("threads"))
                .andExpect(model().attributeExists("threads"))
        }
    }

    // メモ
    // /search, /threads/{threadId}, /threads/{threadId}/posts/{postId} のテストは省略

    @Nested
    inner class CreateThread {

        @Test
        fun `スレッドを作成する`() {
            val userId = "userId1"
            val title = "title"
            val thread = Thread(
                id = ThreadId.of("1"),
                title = ThreadTitle.of(title),
                userId = UserId.of(userId),
                createdAt = now,
                updatedAt = now,
                postsTotal = Total.of(0)
            )
            whenever(threadsService.createThread(eq(UserId.of(userId)), eq(ThreadTitle.of(title)), any()))
                .thenReturn(thread)

            mockMvc.perform(
                post("/threads/create")
                    .cookie(Cookie("accessToken", "userId1"))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("title", title)
                    .with(csrf())
            )
                .andExpect(status().isFound)
                .andExpect(redirectedUrl("/threads/${thread.id.value}"))
        }

        @Test
        fun `スレッドを作成する - タイトルが未入力の場合はエラー`() {
            mockMvc.perform(
                post("/threads/create")
                    .cookie(Cookie("accessToken", "userId1"))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .with(csrf())
            )
                .andExpect(status().isBadRequest)
        }
    }

    // メモ /threads/{threadId}/delete, /threads/{threadId}/posts/create, /threads/{threadId}/posts/{postId}/delete のテストは省略
}
