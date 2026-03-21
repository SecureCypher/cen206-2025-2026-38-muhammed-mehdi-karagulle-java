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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
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

    /** @brief Orijinal System.in. */
    private InputStream originalIn;
    /** @brief Orijinal System.out. */
    private PrintStream originalOut;

    /**
     * @brief Her testten önce stream'ler saklanır ve StorageConfig sıfırlanır.
     */
    @BeforeEach
    void setUp() {
        originalIn  = System.in;
        originalOut = System.out;
        StorageConfig.reset();
    }

    /**
     * @brief Her testten sonra stream'ler geri yüklenir.
     */
    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
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

    // ── isGuiRequested testleri ───────────────────────────────────────

    /** @brief null args → false. */
    @Test void testIsGuiRequestedNull() {
        assertFalse(petreminderApp.isGuiRequested(null));
    }

    /** @brief Boş args → false. */
    @Test void testIsGuiRequestedEmpty() {
        assertFalse(petreminderApp.isGuiRequested(new String[]{}));
    }

    /** @brief "--gui" → true. */
    @Test void testIsGuiRequestedTrue() {
        assertTrue(petreminderApp.isGuiRequested(new String[]{"--gui"}));
    }

    /** @brief "--GUI" büyük harf → true. */
    @Test void testIsGuiRequestedUpperCase() {
        assertTrue(petreminderApp.isGuiRequested(new String[]{"--GUI"}));
    }

    /** @brief İlgisiz argüman → false. */
    @Test void testIsGuiRequestedIrrelevant() {
        assertFalse(petreminderApp.isGuiRequested(new String[]{"--verbose"}));
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

    // ── main() testleri (template uyumu) ─────────────────────────────

    /**
     * @brief main() başarıyla çalışır — "0" (çıkış) argümanı verilir.
     * @details Template'deki testMainSuccess metodunun JUnit5 karşılığı.
     */
    @Test void testMainSuccess() {
        // "0" girişi → ConsoleApp hemen çıkış yapar
        String input = "0" + System.lineSeparator();
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        assertDoesNotThrow(() ->
            petreminderApp.main(new String[]{"--storage=binary"})
        );
    }

    /**
     * @brief main() storage=sqlite ile çalışır.
     */
    @Test void testMainSqlite() {
        String input = "0" + System.lineSeparator();
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        assertDoesNotThrow(() ->
            petreminderApp.main(new String[]{"--storage=sqlite"})
        );
    }

    /**
     * @brief main() argümansız çalışır.
     * @details Template'deki testMainObject metodunun karşılığı.
     */
    @Test void testMainNoArgs() {
        String input = "0" + System.lineSeparator();
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        assertDoesNotThrow(() ->
            petreminderApp.main(new String[]{})
        );
    }

    /**
     * @brief main() geçersiz storage ile çalışır — BINARY'e düşer.
     * @details Template'deki testMainError metodunun karşılığı.
     */
    @Test void testMainInvalidStorage() {
        String input = "0" + System.lineSeparator();
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        assertDoesNotThrow(() ->
            petreminderApp.main(new String[]{"--storage=gecersiz"})
        );
    }

    /**
     * @brief startConsole() testi.
     */
    @Test void testStartConsole() {
        String input = "0" + System.lineSeparator();
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        assertDoesNotThrow(() ->
            petreminderApp.startConsole(new String[]{})
        );
    }

}
