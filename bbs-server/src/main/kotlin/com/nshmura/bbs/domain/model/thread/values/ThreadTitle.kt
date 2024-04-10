package com.nshmura.bbs.domain.model.thread.values

import com.nshmura.bbs.domain.model.ValueObject

/**
 * スレッドタイトル
 */
class ThreadTitle private constructor(override val value: String) : ValueObject<String>() {

    companion object {

        val DELETED: ThreadTitle = ThreadTitle("このスレッドは削除されました")

        private const val MAX_LENGTH = 100

        /**
         * [ThreadTitle]を生成する
         *
         * @throws IllegalArgumentException [value] の長さが範囲外の場合
         */
        fun of(value: String?): ThreadTitle {
            require(!value.isNullOrEmpty()) { "スレッドのタイトルが空です" }
            require(value.length <= MAX_LENGTH) { "スレッドのタイトルが長すぎます: $value" }
            return ThreadTitle(value)
        }
    }
}
