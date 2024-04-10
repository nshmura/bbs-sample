package com.nshmura.bbs.domain.model.thread.values

import com.nshmura.bbs.domain.model.ValueObject

/**
 * 投稿の本文
 */
class PostContent private constructor(override val value: String) : ValueObject<String>() {

    companion object {

        val DELETED: PostContent = PostContent("この投稿は削除されました")

        private const val MAX_LENGTH = 1000

        /**
         * [PostContent]を生成する
         *
         * @throws IllegalArgumentException [value] の長さが範囲外の場合
         */
        fun of(value: String?): PostContent {
            require(!value.isNullOrEmpty()) { "投稿の本文が空です" }
            require(value.length <= MAX_LENGTH) { "too long post content $value" }
            return PostContent(value)
        }
    }
}
