package com.nshmura.bbs.domain.model.user.values

import com.nshmura.bbs.domain.model.ValueObject

/**
 * ユーザ名
 */
class UserName private constructor(override val value: String) : ValueObject<String>() {

    companion object {

        private const val MAX_LENGTH = 64

        /**
         * [UserName] を生成する
         *
         * @throws IllegalArgumentException [value] が不正な場合
         */
        fun of(value: String): UserName {
            require(value.isNotBlank()) { "ユーザ名が空です" }
            require(value.length <= MAX_LENGTH) { "ユーザ名が長すぎます: $value" }
            return UserName(value)
        }
    }
}
