package com.nshmura.bbs.domain.model

import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.TemporalUnit

/**
 * 日時を表すバリューオブジェクト
 */
class DateTime(override val value: Instant) : ValueObject<Instant>() {

    /**
     * [java.sql.Timestamp] から生成する
     */
    constructor(value: Timestamp) : this(value.toInstant())

    /**
     * [java.sql.Timestamp] に変換する
     */
    fun toTimestamp(): Timestamp {
        return Timestamp.from(value)
    }

    /**
     * 指定した時間を加算した日時を取得する
     */
    fun plus(v: Int, unit: TemporalUnit): DateTime {
        return DateTime(value.plus(v.toLong(), unit))
    }

    companion object {

        /**
         * 現在の日時を取得する
         */
        fun now(): DateTime = DateTime(Instant.now())
    }
}
