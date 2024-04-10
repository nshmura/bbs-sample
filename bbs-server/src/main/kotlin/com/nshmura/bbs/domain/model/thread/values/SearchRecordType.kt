package com.nshmura.bbs.domain.model.thread.values

import com.nshmura.bbs.domain.model.ValueObject

/**
 * 検索結果の種別
 */
class SearchRecordType private constructor(override val value: String) : ValueObject<String>() {

    companion object {
        val THREAD = SearchRecordType("THREAD")
        val POST = SearchRecordType("POST")

        fun of(value: String): SearchRecordType {
            return when (value) {
                THREAD.value -> THREAD
                POST.value -> POST
                else -> throw IllegalArgumentException("Unknown SearchResultType: $value")
            }
        }
    }
}
