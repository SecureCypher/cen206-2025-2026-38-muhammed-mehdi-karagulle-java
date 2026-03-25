/**
 * @file ConsoleAppTest.java
 * @brief ConsoleApp sınıfı için JUnit 5 test sınıfı.
 * @details Public API ve doğrudan çağrılabilen metodları kapsar.
 *          while(running) döngüsü running=false iken girmez — sonsuz döngü riski yok.
 */
package com.mehdi.petreminder;

import com.mehdi.petreminder.config.StorageConfig;
import com.mehdi.petreminder.config.StorageType;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @class ConsoleAppTest
 * @brief ConsoleApp public API testleri.
 * @author Muhammed Mehdi Karagülle, Ibrahim Demirci, Zumre Uykun
 * @version 5.0
 */
class ConsoleAppTest {

    /** @brief Original System.out. */
    private PrintStream originalOut;
    /** @brief Original System.in. */
    private InputStream originalIn;

    /**
     * @brief Her testten önce output susturulur.
     */
    @BeforeEach
    void setUp() {
        originalOut = System.out;
        originalIn  = System.in;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        StorageConfig.setActiveBackend(StorageType.SQLITE);
    }

    /**
     * @brief Her testten sonra restore edilir.
     */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        StorageConfig.setActiveBackend(StorageType.SQLITE);
    }

    // ═══════════════════════════════════════════════════════════════
    // YAPILAR & TEMEL DURUM
    // ═══════════════════════════════════════════════════════════════

    /** @test Varsayılan yapıcı. */
    @Test void testDefaultConstructor() {
        assertNotNull(new ConsoleApp());
    }

    /** @test Scanner yapıcısı. */
    @Test void testScannerConstructor() {
        ConsoleApp a = new ConsoleApp(new Scanner(""));
        assertNotNull(a);
        assertFalse(a.isRunning());
    }

    /** @test setRunning true. */
    @Test void testSetRunningTrue() {
        ConsoleApp a = new ConsoleApp();
        a.setRunning(true);
        assertTrue(a.isRunning());
    }

    /** @test setRunning false. */
    @Test void testSetRunningFalse() {
        ConsoleApp a = new ConsoleApp();
        a.setRunning(false);
        assertFalse(a.isRunning());
    }

    // ═══════════════════════════════════════════════════════════════
    // readInput()
    // ═══════════════════════════════════════════════════════════════

    /** @test readInput normal. */
    @Test void testReadInputNormal() {
        ConsoleApp a = new ConsoleApp(new Scanner("merhaba\n"));
        assertEquals("merhaba", a.readInput());
    }

    /** @test readInput boş satır. */
    @Test void testReadInputEmpty() {
        ConsoleApp a = new ConsoleApp(new Scanner("\n"));
        assertEquals("", a.readInput());
    }

    /** @test readInput trim. */
    @Test void testReadInputTrimmed() {
        ConsoleApp a = new ConsoleApp(new Scanner("  abc  \n"));
        assertEquals("abc", a.readInput());
    }

    /** @test readInput bitti. */
    @Test void testReadInputFinished() {
        ConsoleApp a = new ConsoleApp(new Scanner(""));
        assertEquals("", a.readInput());
    }

    /** @test readInput exception → boş. */
    @Test void testReadInputException() {
        Scanner sc = new Scanner("x\n");
        sc.close();
        ConsoleApp a = new ConsoleApp(sc);
        assertEquals("", a.readInput());
    }

    // ═══════════════════════════════════════════════════════════════
    // exitApp()
    // ═══════════════════════════════════════════════════════════════

    /** @test exitApp → running false. */
    @Test void testExitApp() {
        ConsoleApp a = new ConsoleApp();
        a.setRunning(true);
        a.exitApp();
        assertFalse(a.isRunning());
    }

    // ═══════════════════════════════════════════════════════════════
    // printMainMenu()
    // ═══════════════════════════════════════════════════════════════

    /** @test printMainMenu exception yok. */
    @Test void testPrintMainMenu() {
        assertDoesNotThrow(() -> new ConsoleApp().printMainMenu());
    }

    // ═══════════════════════════════════════════════════════════════
    // handleStorageSwitch()
    // ═══════════════════════════════════════════════════════════════

    /** @test BINARY. */
    @Test void testStorageBinary() {
        new ConsoleApp().handleStorageSwitch("1");
        assertEquals(StorageType.BINARY, StorageConfig.getActiveBackend());
    }

    /** @test SQLITE. */
    @Test void testStorageSqlite() {
        new ConsoleApp().handleStorageSwitch("2");
        assertEquals(StorageType.SQLITE, StorageConfig.getActiveBackend());
    }

    /** @test MYSQL. */
    @Test void testStorageMysql() {
        new ConsoleApp().handleStorageSwitch("3");
        assertEquals(StorageType.MYSQL, StorageConfig.getActiveBackend());
    }

    /** @test "0" back. */
    @Test void testStorageBack() {
        assertDoesNotThrow(() -> new ConsoleApp().handleStorageSwitch("0"));
    }

    /** @test geçersiz. */
    @Test void testStorageInvalid() {
        assertDoesNotThrow(() -> new ConsoleApp().handleStorageSwitch("X"));
    }

    /** @test null. */
    @Test void testStorageNull() {
        assertDoesNotThrow(() -> new ConsoleApp().handleStorageSwitch(null));
    }

    // ═══════════════════════════════════════════════════════════════
    // handleMainMenuChoice() — running=false, döngüsüz dispatch testi
    // ═══════════════════════════════════════════════════════════════

    /** @test null seçim. */
    @Test void testMainMenuNull() {
        assertDoesNotThrow(() -> new ConsoleApp().handleMainMenuChoice(null));
    }

    /** @test geçersiz seçim. */
    @Test void testMainMenuInvalid() {
        assertDoesNotThrow(() -> new ConsoleApp().handleMainMenuChoice("X"));
    }

    /** @test "0" → exitApp. */
    @Test void testMainMenuExit() {
        ConsoleApp a = new ConsoleApp();
        a.setRunning(true);
        a.handleMainMenuChoice("0");
        assertFalse(a.isRunning());
    }

    /**
     * @test "1" → showPetsMenu — running=false, iç döngüye girmez.
     */
    @Test void testMainMenuPets() {
        // scanner ile "0" veriyoruz ama döngüye girilmediği için kullanılmaz
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner("0\n")).handleMainMenuChoice("1"));
    }

    /** @test "2" → showRemindersMenu — running=false. */
    @Test void testMainMenuReminders() {
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner("0\n")).handleMainMenuChoice("2"));
    }

    /** @test "3" → showVetMenu — running=false. */
    @Test void testMainMenuVet() {
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner("0\n")).handleMainMenuChoice("3"));
    }

    /** @test "4" → showMedicalMenu — running=false. */
    @Test void testMainMenuMedical() {
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner("0\n")).handleMainMenuChoice("4"));
    }

    /** @test "5" → showSettingsMenu — settings dispatch. */
    @Test void testMainMenuSettings() {
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner("0\n")).handleMainMenuChoice("5"));
    }

    // ═══════════════════════════════════════════════════════════════
    // showMainMenu() — running=false → döngüye girmez
    // ═══════════════════════════════════════════════════════════════

    /** @test showMainMenu running=false. */
    @Test void testShowMainMenuNotRunning() {
        assertDoesNotThrow(() -> new ConsoleApp().showMainMenu());
    }

    // ═══════════════════════════════════════════════════════════════
    // showXMenu() direct — running=false → döngüye girmez
    // ═══════════════════════════════════════════════════════════════

    /** @test showPetsMenu running=false. */
    @Test void testShowPetsMenuNotRunning() {
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner("")).showPetsMenu());
    }

    /** @test showRemindersMenu running=false. */
    @Test void testShowRemindersMenuNotRunning() {
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner("")).showRemindersMenu());
    }

    /** @test showVetMenu running=false. */
    @Test void testShowVetMenuNotRunning() {
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner("")).showVetMenu());
    }

    /** @test showMedicalMenu running=false. */
    @Test void testShowMedicalMenuNotRunning() {
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner("")).showMedicalMenu());
    }

    // ═══════════════════════════════════════════════════════════════
    // showSettingsMenu() — running=false, selectMenuOption → readInput() 1 kez
    // ═══════════════════════════════════════════════════════════════

    /** @test showSettingsMenu "0" geri. */
    @Test void testSettingsMenuBack() {
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner("0\n")).showSettingsMenu());
    }

    /** @test showSettingsMenu "1" BINARY. */
    @Test void testSettingsMenuBinary() {
        new ConsoleApp(new Scanner("1\n")).showSettingsMenu();
        assertEquals(StorageType.BINARY, StorageConfig.getActiveBackend());
    }

    /** @test showSettingsMenu "2" SQLite. */
    @Test void testSettingsMenuSqlite() {
        new ConsoleApp(new Scanner("2\n")).showSettingsMenu();
        assertEquals(StorageType.SQLITE, StorageConfig.getActiveBackend());
    }

    /** @test showSettingsMenu "3" MySQL. */
    @Test void testSettingsMenuMysql() {
        new ConsoleApp(new Scanner("3\n")).showSettingsMenu();
        assertEquals(StorageType.MYSQL, StorageConfig.getActiveBackend());
    }

    // ═══════════════════════════════════════════════════════════════
    // start() — "0" ile hızlı exit
    // ═══════════════════════════════════════════════════════════════

    /** @test start → "0" → exit. */
    @Test void testStartExitsOnZero() {
        ConsoleApp a = new ConsoleApp(new Scanner("0\n"));
        a.start();
        assertFalse(a.isRunning());
    }

    /** @test start invalid → "0" → exit. */
    @Test void testStartInvalidThenExit() {
        ConsoleApp a = new ConsoleApp(new Scanner("X\n0\n"));
        assertDoesNotThrow(a::start);
        assertFalse(a.isRunning());
    }

    // ═══════════════════════════════════════════════════════════════
    // setScannerSource
    // ═══════════════════════════════════════════════════════════════

    /** @test setScannerSource no-op. */
    @Test void testSetScannerSourceNoOp() {
        assertDoesNotThrow(() -> new ConsoleApp().setScannerSource("test"));
    }
}
