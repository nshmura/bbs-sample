package com.nshmura.bbs.domain.model

import org.junit.jupiter.api.Test

class ValueObjectTest {

    class TestValueObject(override val value: String) : ValueObject<String>()

    @Test
    fun testEquals() {
        val valueObject1 = TestValueObject("value")
        val valueObject2 = TestValueObject("value")
        val valueObject3 = TestValueObject("value2")

        assert(valueObject1 == valueObject2)
        assert(valueObject1 != valueObject3)
    }

    @Test
    fun testHashCode() {
        val valueObject1 = TestValueObject("value")
        val valueObject2 = TestValueObject("value")
        val valueObject3 = TestValueObject("value2")

        assert(valueObject1.hashCode() == valueObject2.hashCode())
        assert(valueObject1.hashCode() != valueObject3.hashCode())
    }

    @Test
    fun testToString() {
        val valueObject = TestValueObject("value")
        assert(valueObject.toString() == "value")
    }
}