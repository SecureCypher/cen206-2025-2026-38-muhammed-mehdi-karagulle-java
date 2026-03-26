/**
 * @file ConsoleAppStorageTest.java
 * @brief ConsoleApp — Storage backend değişimi ve ana menü testleri.
 * @details handleStorageSwitch (tüm branch'ler),
 *          handleMainMenuChoice (tüm branch'ler),
 *          printMainMenu, exitApp, showSettingsMenu, start/running state.
 */
package com.mehdi.petreminder;

import com.mehdi.petreminder.config.StorageConfig;
import com.mehdi.petreminder.config.StorageType;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class ConsoleAppStorageTest
 * @brief Storage geçişi ve ana menü davranış testleri.
 * @details Her test metodu tek bir branch'i veya durumu doğrular.
 * @author Muhammed Mehdi Karagülle, Ibrahim Demirci, Zumre Uykun
 * @version 1.0
 */
class ConsoleAppStorageTest {

    /** @brief Orijinal stdout. */
    private PrintStream originalOut;

    /**
     * @brief Her testten önce stdout susturulur.
     */
    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        StorageConfig.reset();
    }

    /**
     * @brief Her testten sonra stdout ve config sıfırlanır.
     */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        StorageConfig.reset();
    }

    // ── handleStorageSwitch tüm branch'ler ───────────────────────────

    /**
     * @test handleStorageSwitch — null parametresi → no-op, hata yok.
     */
    @Test
    void testHandleStorageSwitchNull() {
        assertDoesNotThrow(() -> new ConsoleApp().handleStorageSwitch(null));
    }

    /**
     * @test handleStorageSwitch — "1" → BINARY seçilir.
     */
    @Test
    void testHandleStorageSwitchBinary() {
        ConsoleApp app = new ConsoleApp();
        app.handleStorageSwitch("1");
        assertEquals(StorageType.BINARY, StorageConfig.getActiveBackend());
    }

    /**
     * @test handleStorageSwitch — "2" → SQLITE seçilir.
     */
    @Test
    void testHandleStorageSwitchSqlite() {
        ConsoleApp app = new ConsoleApp();
        app.handleStorageSwitch("2");
        assertEquals(StorageType.SQLITE, StorageConfig.getActiveBackend());
    }

    /**
     * @test handleStorageSwitch — "3" → MYSQL seçilir.
     */
    @Test
    void testHandleStorageSwitchMysql() {
        ConsoleApp app = new ConsoleApp();
        app.handleStorageSwitch("3");
        assertEquals(StorageType.MYSQL, StorageConfig.getActiveBackend());
    }

    /**
     * @test handleStorageSwitch — "0" geri → backend değişmez.
     */
    @Test
    void testHandleStorageSwitchBack() {
        assertDoesNotThrow(() -> new ConsoleApp().handleStorageSwitch("0"));
    }

    /**
     * @test handleStorageSwitch — geçersiz seçenek → default, hata yok.
     */
    @Test
    void testHandleStorageSwitchDefault() {
        assertDoesNotThrow(() -> new ConsoleApp().handleStorageSwitch("X"));
    }

    // ── handleMainMenuChoice tüm branch'ler ──────────────────────────

    /**
     * @test handleMainMenuChoice — null → no-op.
     */
    @Test
    void testHandleMainMenuChoiceNull() {
        assertDoesNotThrow(() -> new ConsoleApp().handleMainMenuChoice(null));
    }

    /**
     * @test handleMainMenuChoice — "1" → showPetsMenu çağrılır.
     */
    @Test
    void testHandleMainMenuChoice1Pets() {
        assertDoesNotThrow(() -> new ConsoleApp().handleMainMenuChoice("1"));
    }

    /**
     * @test handleMainMenuChoice — "2" → showRemindersMenu çağrılır.
     */
    @Test
    void testHandleMainMenuChoice2Reminders() {
        assertDoesNotThrow(() -> new ConsoleApp().handleMainMenuChoice("2"));
    }

    /**
     * @test handleMainMenuChoice — "3" → showVetMenu çağrılır.
     */
    @Test
    void testHandleMainMenuChoice3Vet() {
        assertDoesNotThrow(() -> new ConsoleApp().handleMainMenuChoice("3"));
    }

    /**
     * @test handleMainMenuChoice — "4" → showMedicalMenu çağrılır.
     */
    @Test
    void testHandleMainMenuChoice4Medical() {
        assertDoesNotThrow(() -> new ConsoleApp().handleMainMenuChoice("4"));
    }

    /**
     * @test handleMainMenuChoice — "5" → showSettingsMenu + initServices çağrılır.
     */
    @Test
    void testHandleMainMenuChoice5Settings() {
        // "0" = back from settings
        assertDoesNotThrow(() ->
            new ConsoleApp(new Scanner("0\n")).handleMainMenuChoice("5"));
    }

    /**
     * @test handleMainMenuChoice — "0" → exitApp, running=false.
     */
    @Test
    void testHandleMainMenuChoice0Exit() {
        ConsoleApp app = new ConsoleApp();
        app.setRunning(true);
        app.handleMainMenuChoice("0");
        assertFalse(app.isRunning());
    }

    /**
     * @test handleMainMenuChoice — geçersiz seçenek → hata mesajı, exception yok.
     */
    @Test
    void testHandleMainMenuChoiceDefault() {
        assertDoesNotThrow(() -> new ConsoleApp().handleMainMenuChoice("X"));
    }

    // ── printMainMenu ve exitApp ──────────────────────────────────────

    /**
     * @test printMainMenu — menü yazdırılır, exception yok.
     */
    @Test
    void testPrintMainMenu() {
        assertDoesNotThrow(new ConsoleApp()::printMainMenu);
    }

    /**
     * @test exitApp — running false yapılır.
     */
    @Test
    void testExitApp() {
        ConsoleApp app = new ConsoleApp();
        app.setRunning(true);
        app.exitApp();
        assertFalse(app.isRunning());
    }

    // ── showSettingsMenu branch'leri ──────────────────────────────────

    /**
     * @test showSettingsMenu — "0" (geri) seçimi, hata yok.
     */
    @Test
    void testShowSettingsMenuBack() {
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner("0\n")).showSettingsMenu());
    }

    /**
     * @test showSettingsMenu — "1" Binary seçimi.
     */
    @Test
    void testShowSettingsMenuBinary() {
        ConsoleApp app = new ConsoleApp(new Scanner("1\n"));
        app.showSettingsMenu();
        assertEquals(StorageType.BINARY, StorageConfig.getActiveBackend());
    }

    /**
     * @test showSettingsMenu — "2" SQLite seçimi.
     */
    @Test
    void testShowSettingsMenuSqlite() {
        ConsoleApp app = new ConsoleApp(new Scanner("2\n"));
        app.showSettingsMenu();
        assertEquals(StorageType.SQLITE, StorageConfig.getActiveBackend());
    }
}
