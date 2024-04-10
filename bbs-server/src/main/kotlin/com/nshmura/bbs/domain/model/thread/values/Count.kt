package com.nshmura.bbs.domain.model.thread.values

import com.nshmura.bbs.domain.model.ValueObject

/**
 * 検索結果の取得数
 */
class Count private constructor(override val value: Int) : ValueObject<Int>() {

    companion object {

        private const val MIN = 0
        private const val MAX = 10000
        private const val DEFAULT = 10

        /**
         * [Count] を生成する
         *
         * @throws IllegalArgumentException [value] が範囲外の場合
         */
        fun of(value: Int?): Count {
            if (value == null) {
                return Count(DEFAULT)
            }
            require(value >= MIN) { "too small count value $value" }
            require(value <= MAX) { "too large count value $value" }
            return Count(value)
        }
    }
}
