package com.nshmura.bbs.domain.model.thread.values

import com.nshmura.bbs.domain.model.ValueObject

/**
 * 投稿ID
 */
class PostId private constructor(override val value: String) : ValueObject<String>() {

    companion object {

        private val PATTERN = Regex("^[a-zA-Z0-9_:-]{1,64}\$")

        /**
         * [PostId] を生成する
         *
         * @throws IllegalArgumentException [value] が不正な場合
         */
        fun of(value: String): PostId {
            require(PATTERN.matches(value)) { "Invalid PostId: $value" }
            return PostId(value)
        }

        fun generate(): PostId =
            PostId(java.util.UUID.randomUUID().toString())
    }
}
