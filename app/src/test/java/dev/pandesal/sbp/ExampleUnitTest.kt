package dev.pandesal.sbp

import org.junit.Test
import org.junit.Assert.*
import dev.pandesal.sbp.extensions.format

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun double_format_isCorrect() {
        assertEquals("1.50", 1.5.format())
    }
}