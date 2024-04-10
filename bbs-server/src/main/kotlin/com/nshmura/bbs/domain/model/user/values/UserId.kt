package com.nshmura.bbs.domain.model.user.values

import com.nshmura.bbs.domain.model.ValueObject

/**
 * ユーザID
 */
class UserId private constructor(override val value: String) : ValueObject<String>() {

    companion object {

        private val PATTERN = Regex("^[a-zA-Z0-9_:-]{1,64}\$")

        /**
         * [UserId] を生成する
         *
         * @throws IllegalArgumentException [value] が不正な場合
         */
        fun of(value: String): UserId {
            require(PATTERN.matches(value)) { "Invalid UserId: $value" }
            return UserId(value)
        }
    }
}
