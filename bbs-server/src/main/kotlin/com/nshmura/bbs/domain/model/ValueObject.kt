package com.nshmura.bbs.domain.model

/**
 * バリューオブジェクトの基底クラス
 */
abstract class ValueObject<T> {

    abstract val value: T

    /**
     * [value] が同じであれば同じ値である
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as ValueObject<*>
        return value == other.value
    }

    /**
     * [value] のハッシュコードを返す
     */
    override fun hashCode(): Int = value?.hashCode() ?: 0

    /**
     * [value] の文字列表現を返す
     */
    override fun toString(): String = value.toString()
}
