package com.nshmura.bbs.domain.model.thread.values

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CountTest {

    @Test
    fun testOf() {
        val count = Count.of(10)
        assert(count.value == 10)
    }

    @Test
    fun testValidation() {
        assertThrows<IllegalArgumentException> {
            Count.of(-1)
        }
        assertThrows<IllegalArgumentException> {
            Count.of(10001)
        }
    }

    @Test
    fun testDefault() {
        val count = Count.of(null)
        assert(count.value == 10)
    }
}
