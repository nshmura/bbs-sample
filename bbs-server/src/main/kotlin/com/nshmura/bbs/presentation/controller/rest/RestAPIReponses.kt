package com.nshmura.bbs.presentation.controller.rest

import com.nshmura.bbs.domain.model.thread.Post
import com.nshmura.bbs.domain.model.thread.SearchResults
import com.nshmura.bbs.domain.model.thread.Thread
import com.nshmura.bbs.domain.model.thread.values.SearchRecordType
import com.nshmura.bbs.domain.model.user.User

class UserResponse(
    val id: String,
    val name: String
) {
    companion object {
        fun of(user: User?): UserResponse? =
            user?.let { UserResponse(it.id.value, it.name.value) }
    }
}

class ThreadsResponse(
    val data: List<ThreadResponse>,
    val page: PageResponse
)

class ThreadResponse(
    val id: String,
    val title: String,
    val userId: String,
    val user: UserResponse?,
    val totalPostsCount: Int,
    val postsStart: Int?,
    val posts: List<PostResponse>
) {

    companion object {

        fun of(thread: Thread): ThreadResponse =
            ThreadResponse(
                id = thread.id.value,
                title = thread.title.value,
                userId = thread.userId.value,
                user = UserResponse.of(thread.user),
                totalPostsCount = thread.posts.size,
                postsStart = thread.postsStart?.value,
                posts = thread.posts.map { PostResponse.of(it) }
            )
    }
}


class PostResponse(
    val id: String,
    val content: String,
    val userId: String,
    val user: UserResponse?,
    val threadId: String,
) {
    companion object {
        fun of(post: Post): PostResponse =
            PostResponse(
                id = post.id.value,
                content = post.content.value,
                userId = post.userId.value,
                user = UserResponse.of(post.user),
                threadId = post.threadId.value
            )
    }
}

class PageResponse(
    val start: Int,
    val total: Int
)

class SearchResultsResponse(
    val data: List<SearchResultResponse>,
    val page: PageResponse
) {
    companion object {
        fun of(searchResults: SearchResults): SearchResultsResponse =
            SearchResultsResponse(
                data = searchResults.list.map {

                    val url = when (it.type) {
                        SearchRecordType.THREAD -> "/v1/api/threads/${it.threadId}"
                        SearchRecordType.POST -> "/v1/api/threads/${it.threadId}/posts/${it.postId}"
                        else -> throw RuntimeException("Invalid SearchResultType")
                    }

                    SearchResultResponse(it.type.value, it.message, url)
                },
                page = PageResponse(searchResults.start.value, searchResults.total.value)
            )
    }
}

class SearchResultResponse(
    val type: String,
    val result: String,
    val url: String
)
