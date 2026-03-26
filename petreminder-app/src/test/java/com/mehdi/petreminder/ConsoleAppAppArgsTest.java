/**
 * @file ConsoleAppAppArgsTest.java
 * @brief petreminderApp yardımcı metod testleri.
 * @details parseStorageArg, isGuiRequested, getVersion, getAppName
 *          metodlarının tüm branch'leri ve dönüş değerleri doğrulanır.
 */
package com.mehdi.petreminder;

import com.mehdi.petreminder.config.StorageType;
import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class ConsoleAppAppArgsTest
 * @brief petreminderApp — komut satırı argüman ayrıştırma testleri.
 * @details Her test metodu tek bir argüman kombinasyonunu doğrular.
 * @author Muhammed Mehdi Karagülle, Ibrahim Demirci, Zumre Uykun
 * @version 1.0
 */
class ConsoleAppAppArgsTest {

    /** @brief Orijinal stdout. */
    private PrintStream originalOut;

    /**
     * @brief Her testten önce stdout susturulur.
     */
    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
    }

    /**
     * @brief Her testten sonra stdout sıfırlanır.
     */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // ── parseStorageArg branch'leri ───────────────────────────────────

    /**
     * @test parseStorageArg — null argüman → BINARY döner.
     */
    @Test
    void testParseStorageArgNull() {
        assertEquals(StorageType.BINARY, petreminderApp.parseStorageArg(null));
    }

    /**
     * @test parseStorageArg — "--storage=SQLITE" → SQLITE döner.
     */
    @Test
    void testParseStorageArgSqlite() {
        assertEquals(StorageType.SQLITE,
            petreminderApp.parseStorageArg(new String[]{"--storage=SQLITE"}));
    }

    /**
     * @test parseStorageArg — "--storage=MYSQL" → MYSQL döner.
     */
    @Test
    void testParseStorageArgMysql() {
        assertEquals(StorageType.MYSQL,
            petreminderApp.parseStorageArg(new String[]{"--storage=MYSQL"}));
    }

    /**
     * @test parseStorageArg — "--storage=BINARY" → BINARY döner.
     */
    @Test
    void testParseStorageArgBinary() {
        assertEquals(StorageType.BINARY,
            petreminderApp.parseStorageArg(new String[]{"--storage=BINARY"}));
    }

    /**
     * @test parseStorageArg — geçersiz değer → BINARY fallback.
     */
    @Test
    void testParseStorageArgInvalid() {
        assertEquals(StorageType.BINARY,
            petreminderApp.parseStorageArg(new String[]{"--storage=INVALID"}));
    }

    /**
     * @test parseStorageArg — dizide null eleman → BINARY döner.
     */
    @Test
    void testParseStorageArgNullElement() {
        assertEquals(StorageType.BINARY,
            petreminderApp.parseStorageArg(new String[]{null}));
    }

    /**
     * @test parseStorageArg — storage argümanı yok → BINARY döner.
     */
    @Test
    void testParseStorageArgNoStorageArg() {
        assertEquals(StorageType.BINARY,
            petreminderApp.parseStorageArg(new String[]{"--gui"}));
    }

    // ── isGuiRequested branch'leri ────────────────────────────────────

    /**
     * @test isGuiRequested — null → false.
     */
    @Test
    void testIsGuiRequestedNull() {
        assertFalse(petreminderApp.isGuiRequested(null));
    }

    /**
     * @test isGuiRequested — "--gui" içeriyor → true.
     */
    @Test
    void testIsGuiRequestedTrue() {
        assertTrue(petreminderApp.isGuiRequested(new String[]{"--gui"}));
    }

    /**
     * @test isGuiRequested — "--gui" yok → false.
     */
    @Test
    void testIsGuiRequestedFalse() {
        assertFalse(petreminderApp.isGuiRequested(new String[]{"--storage=SQLITE"}));
    }

    /**
     * @test isGuiRequested — boş dizi → false.
     */
    @Test
    void testIsGuiRequestedEmptyArray() {
        assertFalse(petreminderApp.isGuiRequested(new String[]{}));
    }

    // ── getVersion ve getAppName ──────────────────────────────────────

    /**
     * @test getVersion — beklenen versiyon string döner.
     */
    @Test
    void testGetVersion() {
        assertEquals("1.0.0", petreminderApp.getVersion());
    }

    /**
     * @test getAppName — uygulama adı doğru döner.
     */
    @Test
    void testGetAppName() {
        assertEquals("Pet Care Reminder System", petreminderApp.getAppName());
    }
}
