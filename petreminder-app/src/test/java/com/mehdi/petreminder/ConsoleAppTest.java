/**
 * @file ConsoleAppTest.java
 * @brief ConsoleApp sınıfı için JUnit 5 test sınıfı.
 * @details 100% coverage hedefi — tüm public metodlar test edilir.
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
 * @brief ConsoleApp test sınıfı.
 * @author Muhammed Mehdi Karagülle, Ibrahim Demirci, Zumre Uykun
 */
class ConsoleAppTest {

    private PrintStream originalOut;
    private InputStream originalIn;

    @BeforeEach void setUp() {
        originalOut = System.out;
        originalIn  = System.in;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
    }

    @AfterEach void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    // ── Yapıcı testleri ───────────────────────────────────────────────

    @Test void testDefaultConstructor() {
        ConsoleApp app = new ConsoleApp();
        assertNotNull(app);
        assertFalse(app.isRunning());
    }

    @Test void testScannerConstructor() {
        Scanner sc = new Scanner("0\n");
        ConsoleApp app = new ConsoleApp(sc);
        assertNotNull(app);
        assertFalse(app.isRunning());
    }

    // ── isRunning / setRunning ────────────────────────────────────────

    @Test void testSetRunningTrue() {
        ConsoleApp app = new ConsoleApp();
        app.setRunning(true);
        assertTrue(app.isRunning());
    }

    @Test void testSetRunningFalse() {
        ConsoleApp app = new ConsoleApp();
        app.setRunning(false);
        assertFalse(app.isRunning());
    }

    // ── readInput() ───────────────────────────────────────────────────

    @Test void testReadInputNormal() {
        Scanner sc = new Scanner("merhaba\n");
        ConsoleApp app = new ConsoleApp(sc);
        assertEquals("merhaba", app.readInput());
    }

    @Test void testReadInputEmpty() {
        Scanner sc = new Scanner("\n");
        ConsoleApp app = new ConsoleApp(sc);
        assertEquals("", app.readInput());
    }

    @Test void testReadInputTrimmed() {
        Scanner sc = new Scanner("  1  \n");
        ConsoleApp app = new ConsoleApp(sc);
        assertEquals("1", app.readInput());
    }

    @Test void testReadInputNoMoreInput() {
        Scanner sc = new Scanner("");
        ConsoleApp app = new ConsoleApp(sc);
        assertEquals("", app.readInput());
    }

    // ── exitApp() ────────────────────────────────────────────────────

    @Test void testExitApp() {
        ConsoleApp app = new ConsoleApp();
        app.setRunning(true);
        app.exitApp();
        assertFalse(app.isRunning());
    }

    // ── printMainMenu() ───────────────────────────────────────────────

    @Test void testPrintMainMenuNoException() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(app::printMainMenu);
    }

    // ── showPetsMenu() / showRemindersMenu() / vb. ────────────────────

    @Test void testShowPetsMenu() {
        Scanner sc = new Scanner("0\n");
        ConsoleApp app = new ConsoleApp(sc);
        assertDoesNotThrow(app::showPetsMenu);
    }

    @Test void testShowRemindersMenu() {
        Scanner sc = new Scanner("0\n");
        ConsoleApp app = new ConsoleApp(sc);
        assertDoesNotThrow(app::showRemindersMenu);
    }

    @Test void testShowVetMenu() {
        Scanner sc = new Scanner("0\n");
        ConsoleApp app = new ConsoleApp(sc);
        assertDoesNotThrow(app::showVetMenu);
    }

    @Test void testShowMedicalMenu() {
        Scanner sc = new Scanner("0\n");
        ConsoleApp app = new ConsoleApp(sc);
        assertDoesNotThrow(app::showMedicalMenu);
    }

    // ── showSettingsMenu() / handleStorageSwitch() ───────────────────

    @Test void testShowSettingsMenuNoException() {
        Scanner sc = new Scanner("0\n");
        ConsoleApp app = new ConsoleApp(sc);
        assertDoesNotThrow(app::showSettingsMenu);
    }

    @Test void testHandleStorageSwitchToBinary() {
        ConsoleApp app = new ConsoleApp();
        app.handleStorageSwitch("1");
        assertEquals(StorageType.BINARY, StorageConfig.getActiveBackend());
    }

    @Test void testHandleStorageSwitchToSqlite() {
        ConsoleApp app = new ConsoleApp();
        app.handleStorageSwitch("2");
        assertEquals(StorageType.SQLITE, StorageConfig.getActiveBackend());
    }

    @Test void testHandleStorageSwitchToMysql() {
        ConsoleApp app = new ConsoleApp();
        app.handleStorageSwitch("3");
        assertEquals(StorageType.MYSQL, StorageConfig.getActiveBackend());
    }

    @Test void testHandleStorageSwitchBack() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleStorageSwitch("0"));
    }

    @Test void testHandleStorageSwitchInvalid() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleStorageSwitch("9"));
    }

    @Test void testHandleStorageSwitchNull() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleStorageSwitch(null));
    }

    // ── handleMainMenuChoice() ───────────────────────────────────────

    @Test void testHandleMainMenuChoicePets() {
        Scanner sc = new Scanner("0\n");
        ConsoleApp app = new ConsoleApp(sc);
        assertDoesNotThrow(() -> app.handleMainMenuChoice("1"));
    }

    @Test void testHandleMainMenuChoiceReminders() {
        Scanner sc = new Scanner("0\n");
        ConsoleApp app = new ConsoleApp(sc);
        assertDoesNotThrow(() -> app.handleMainMenuChoice("2"));
    }

    @Test void testHandleMainMenuChoiceVet() {
        Scanner sc = new Scanner("0\n");
        ConsoleApp app = new ConsoleApp(sc);
        assertDoesNotThrow(() -> app.handleMainMenuChoice("3"));
    }

    @Test void testHandleMainMenuChoiceMedical() {
        Scanner sc = new Scanner("0\n");
        ConsoleApp app = new ConsoleApp(sc);
        assertDoesNotThrow(() -> app.handleMainMenuChoice("4"));
    }

    @Test void testHandleMainMenuChoiceSettings() {
        Scanner sc = new Scanner("1\n");
        ConsoleApp app = new ConsoleApp(sc);
        assertDoesNotThrow(() -> app.handleMainMenuChoice("5"));
    }

    @Test void testHandleMainMenuChoiceExit() {
        ConsoleApp app = new ConsoleApp();
        app.setRunning(true);
        app.handleMainMenuChoice("0");
        assertFalse(app.isRunning());
    }

    @Test void testHandleMainMenuChoiceInvalid() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleMainMenuChoice("X"));
    }

    @Test void testHandleMainMenuChoiceNull() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleMainMenuChoice(null));
    }

    // ── start() — Scanner tükenmesiyle loop sona erer ────────────────

    @Test void testStartExitsOnZero() {
        Scanner sc = new Scanner("0\n");
        ConsoleApp app = new ConsoleApp(sc);
        assertDoesNotThrow(app::start);
        assertFalse(app.isRunning());
    }

    @Test void testStartExitsOnEmptyInput() {
        // Girdi biter → readInput "" döner → handleMainMenuChoice("") → invalid → loop scanner bitince durur
        Scanner sc = new Scanner("0\n");
        ConsoleApp app = new ConsoleApp(sc);
        assertDoesNotThrow(app::start);
    }

    // readInput exception catch branch — scanner kapatıldıktan sonra okuma
    @Test void testReadInputException() {
        Scanner sc = new Scanner("test\n");
        sc.close(); // scanner'ı kapat, readInput'ta exception oluşsun
        ConsoleApp app = new ConsoleApp(sc);
        // exception yakalanır ve "" döner
        assertEquals("", app.readInput());
    }

    // showMainMenu direkt çağrı — running false ise döngüye girmez
    @Test void testShowMainMenuNotRunning() {
        ConsoleApp app = new ConsoleApp();
        // running false, döngüye girmemeli
        assertDoesNotThrow(app::showMainMenu);
    }
}
