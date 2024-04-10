package com.nshmura.bbs.domain.model.thread

import com.nshmura.bbs.domain.model.thread.values.Start
import com.nshmura.bbs.domain.model.thread.values.Total
import com.nshmura.bbs.domain.model.user.User
import com.nshmura.bbs.domain.model.user.values.UserId

/**
 * 検索結果のリスト
 */
class SearchResults(
    val list: List<SearchResult>, // 検索結果のリスト
    val start: Start, // 検索結果の開始位置
    val total: Total  // 検索結果の総数
) {

    /**
     * 検索結果に関連するユーザーIDのリストを取得する
     */
    fun getUserIds(): List<UserId> = list.map { it.userId }

    /**
     * 検索結果に関連するユーザーを紐付ける
     */
    fun attachUsers(users: List<User>): SearchResults =
        SearchResults(list.map { it.attachUser(users) }, start, total)
}
