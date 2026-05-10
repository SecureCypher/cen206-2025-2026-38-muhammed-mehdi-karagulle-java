/**
 * @file petreminderAppTest.java
 * @brief petreminderApp ana sınıfı için JUnit 5 test sınıfı.
 * @details PDF zorunluluğu: 100% JUnit5 coverage.
 *          Template'deki petreminderAppTest.java'nın JUnit5'e uyarlanmış hali.
 */
package com.mehdi.petreminder;

import com.mehdi.petreminder.config.StorageConfig;
import com.mehdi.petreminder.config.StorageType;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @class petreminderAppTest
 * @brief petreminderApp sınıfının test sınıfı.
 * @details JUnit 5 — @Test, @BeforeEach, @AfterEach.
 *          Template'deki testMainSuccess/testMainObject/testMainError metodları
 *          JUnit5'e uyarlanmıştır.
 * @author Muhammed Mehdi Karagülle, Ibrahim Demirci, Zumre Uykun
 */
class petreminderAppTest {

    /**
     * @brief Her testten önce StorageConfig sıfırlanır.
     */
    @BeforeEach
    void setUp() {
        StorageConfig.reset();
    }

    /**
     * @brief Her testten sonra StorageConfig geri yüklenir.
     */
    @AfterEach
    void tearDown() {
        StorageConfig.reset();
    }

    // ── parseStorageArg testleri ──────────────────────────────────────

    /** @brief null args → BINARY döner. */
    @Test void testParseStorageArgNull() {
        assertEquals(StorageType.BINARY, petreminderApp.parseStorageArg(null));
    }

    /** @brief Boş args → BINARY döner. */
    @Test void testParseStorageArgEmpty() {
        assertEquals(StorageType.BINARY,
            petreminderApp.parseStorageArg(new String[]{}));
    }

    /** @brief "--storage=binary" → BINARY döner. */
    @Test void testParseStorageArgBinary() {
        assertEquals(StorageType.BINARY,
            petreminderApp.parseStorageArg(new String[]{"--storage=binary"}));
    }

    /** @brief "--storage=sqlite" → SQLITE döner. */
    @Test void testParseStorageArgSqlite() {
        assertEquals(StorageType.SQLITE,
            petreminderApp.parseStorageArg(new String[]{"--storage=sqlite"}));
    }

    /** @brief "--storage=mysql" → MYSQL döner. */
    @Test void testParseStorageArgMysql() {
        assertEquals(StorageType.MYSQL,
            petreminderApp.parseStorageArg(new String[]{"--storage=mysql"}));
    }

    /** @brief Büyük/küçük harf farkı yok — "SQLITE" → SQLITE. */
    @Test void testParseStorageArgUpperCase() {
        assertEquals(StorageType.SQLITE,
            petreminderApp.parseStorageArg(new String[]{"--storage=SQLITE"}));
    }

    /** @brief Geçersiz değer → BINARY döner. */
    @Test void testParseStorageArgInvalid() {
        assertEquals(StorageType.BINARY,
            petreminderApp.parseStorageArg(new String[]{"--storage=unknown"}));
    }

    /** @brief İlgisiz argüman → BINARY döner. */
    @Test void testParseStorageArgIrrelevant() {
        assertEquals(StorageType.BINARY,
            petreminderApp.parseStorageArg(new String[]{"--verbose"}));
    }

    /** @brief null argüman içeren dizi → BINARY döner. */
    @Test void testParseStorageArgNullElement() {
        assertEquals(StorageType.BINARY,
            petreminderApp.parseStorageArg(new String[]{null}));
    }

    // ── Statik alanlar ────────────────────────────────────────────────

    /** @brief APP_NAME boş değil. */
    @Test void testAppNameNotNull() {
        assertNotNull(petreminderApp.APP_NAME);
        assertFalse(petreminderApp.APP_NAME.isEmpty());
    }

    /** @brief APP_VERSION boş değil. */
    @Test void testAppVersionNotNull() {
        assertNotNull(petreminderApp.APP_VERSION);
        assertFalse(petreminderApp.APP_VERSION.isEmpty());
    }

    /** @brief getVersion() APP_VERSION döndürür. */
    @Test void testGetVersion() {
        assertEquals(petreminderApp.APP_VERSION, petreminderApp.getVersion());
    }

    /** @brief getAppName() APP_NAME döndürür. */
    @Test void testGetAppName() {
        assertEquals(petreminderApp.APP_NAME, petreminderApp.getAppName());
    }

}
