package com.nshmura.bbs.domain.model.thread.values

import com.nshmura.bbs.domain.model.ValueObject
import kotlin.math.ceil

/**
 * スレッドや投稿の総数
 */
class Total private constructor(override val value: Int) : ValueObject<Int>() {

    /**
     * 最大ページ数を計算
     */
    fun toMaxPage(countPerPage: Count): Page =
        Page.of(ceil(value / countPerPage.value.toDouble()).toInt())

    /**
     * [value] の比較
     */
    operator fun compareTo(other: Total): Int {
        return value.compareTo(other.value)
    }

    companion object {

        private const val MIN = 0
        private const val MAX = 10000000

        fun of(value: Int): Total {
            require(value >= MIN) { "too small count value $value" }
            require(value <= MAX) { "too large count value $value" }
            return Total(value)
        }
    }
}
