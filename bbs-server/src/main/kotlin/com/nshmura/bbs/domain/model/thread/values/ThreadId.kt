package com.nshmura.bbs.domain.model.thread.values

import com.nshmura.bbs.domain.model.ValueObject

/**
 * スレッドID
 */
class ThreadId private constructor(override val value: String) : ValueObject<String>() {

    companion object {

        private val PATTERN = Regex("^[a-zA-Z0-9_:-]{1,64}\$")

        /**
         * [ThreadId] を生成する
         *
         * @throws IllegalArgumentException [value] が不正な場合
         */
        fun of(value: String): ThreadId {
            require(PATTERN.matches(value)) { "Invalid ThreadId: $value" }
            return ThreadId(value)
        }

        /**
         * [ThreadId] を生成する
         *
         * [value] は UUID で生成される
         */
        fun generate(): ThreadId =
            ThreadId(java.util.UUID.randomUUID().toString())
    }
}
