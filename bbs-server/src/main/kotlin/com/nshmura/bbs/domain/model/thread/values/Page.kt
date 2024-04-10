package com.nshmura.bbs.domain.model.thread.values

import com.nshmura.bbs.domain.model.ValueObject

/**
 * ページ番号
 */
class Page private constructor(override val value: Int) : ValueObject<Int>() {

    /**
     * ページ番号を [Start] に変換
     */
    fun toStart(count: Count): Start {
        val start = when {
            value <= 1 -> 0
            value > MAX_PAGE -> (MAX_PAGE - 1) * count.value
            else -> (value - 1) * count.value
        }
        return Start.of(start)
    }

    companion object {

        private const val MAX_PAGE = 10000

        /**
         * [Page] を生成する
         */
        fun of(value: Int?): Page {
            if (value == null) {
                return Page(1)
            }
            if (value <= 0) {
                return Page(1)
            }
            require(value <= MAX_PAGE) { "too large page value $value" }
            return Page(value)
        }
    }
}
