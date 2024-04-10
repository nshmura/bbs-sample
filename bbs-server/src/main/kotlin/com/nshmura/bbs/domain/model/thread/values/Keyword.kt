package com.nshmura.bbs.domain.model.thread.values

class Keyword private constructor(val value: String) {

    companion object {
        private const val MAX_LENGTH = 100

        fun of(value: String?): Keyword {
            if (value == null) {
                return Keyword("")
            }
            require(value.length <= MAX_LENGTH) { "too long keyword value $value" }
            return Keyword(value)
        }
    }
}
