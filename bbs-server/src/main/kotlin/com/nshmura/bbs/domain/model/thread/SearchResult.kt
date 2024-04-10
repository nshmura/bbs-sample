package com.nshmura.bbs.domain.model.thread

import com.nshmura.bbs.domain.model.DateTime
import com.nshmura.bbs.domain.model.thread.values.PostId
import com.nshmura.bbs.domain.model.thread.values.SearchRecordType
import com.nshmura.bbs.domain.model.thread.values.ThreadId
import com.nshmura.bbs.domain.model.user.User
import com.nshmura.bbs.domain.model.user.values.UserId

/**
 * スレッドと投稿の検索結果
 */
class SearchResult(
    val type: SearchRecordType, // 検索がヒットした種別（スレッド もしくは 投稿）
    val message: String,        // 検索がヒットした本文（スレッドタイトル もしくは 投稿内容）
    val threadId: ThreadId?,
    val postId: PostId?,
    val userId: UserId,        // 関連するユーザID
    val createdAt: DateTime,   // 作成日時
    val updatedAt: DateTime,   // 更新日時
    val user: User? = null     // 関連するユーザ。[UserRepository] から取得するため初期値は null
) {

    /**
     * ユーザを紐付ける
     */
    fun attachUser(users: List<User>): SearchResult {
        val user = users.find { it.id == userId }
        return SearchResult(type, message, threadId, postId, userId, createdAt, updatedAt, user)
    }
}
