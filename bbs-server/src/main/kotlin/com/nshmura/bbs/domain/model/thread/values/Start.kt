package com.nshmura.bbs.domain.model.thread.values

import com.nshmura.bbs.domain.model.ValueObject

/**
 * 一覧の取得開始位置
 */
class Start private constructor(override val value: Int) : ValueObject<Int>() {

    companion object {

        private const val MIN = 0
        private const val MAX = 10000

        /**
         * [Start] を生成する
         *
         * @throws IllegalArgumentException [value] が範囲外の場合
         */
        fun of(value: Int?): Start {
            if (value == null) {
                return Start(MIN)
            }
            require(value >= MIN) { "too small start value $value" }
            require(value <= MAX) { "too large start value $value" }
            return Start(value)
        }
    }
}
