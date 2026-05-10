/**
 * @file petreminderTest.java
 * @brief petreminder yardımcı sınıfı için JUnit 5 test sınıfı.
 * @details PDF zorunluluğu: 100% JUnit5 coverage.
 */
package com.mehdi.petreminder;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @class petreminderTest
 * @brief petreminder sınıfının test sınıfı.
 * @details isNullOrEmpty ve getCurrentTimeFormatted metodlarının testleri.
 * @author Muhammed Mehdi Karagülle, Ibrahim Demirci, Zumre Uykun
 */
class petreminderTest {

    /** @brief Test objesi. */
    private petreminder lib;

    /** @brief Her testten önce yeni instance. */
    @BeforeEach
    void setUp() {
        lib = new petreminder();
    }

    // ── isNullOrEmpty() ──────────────────────────────────────────────

    /** @brief null değer → true döner. */
    @Test void testIsNullOrEmptyNull()    { assertTrue(lib.isNullOrEmpty(null)); }

    /** @brief Boş string → true döner. */
    @Test void testIsNullOrEmptyBlank()   { assertTrue(lib.isNullOrEmpty("")); }

    /** @brief Sadece boşluklar → true döner. */
    @Test void testIsNullOrEmptySpaces()  { assertTrue(lib.isNullOrEmpty("   ")); }

    /** @brief Normal değer → false döner. */
    @Test void testIsNullOrEmptyValue()   { assertFalse(lib.isNullOrEmpty("test")); }

    /** @brief Boşluklu normal değer → false döner. */
    @Test void testIsNullOrEmptySpacedV() { assertFalse(lib.isNullOrEmpty(" a ")); }

    // ── getCurrentTimeFormatted() ─────────────────────────────────────

    /** @brief Dönen değer null değil. */
    @Test void testGetCurrentTimeNotNull() {
        assertNotNull(lib.getCurrentTimeFormatted());
    }

    /** @brief Dönen değer dd/MM/yyyy HH:mm formatında. */
    @Test void testGetCurrentTimeFormat() {
        String t = lib.getCurrentTimeFormatted();
        assertTrue(t.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}"));
    }
}
