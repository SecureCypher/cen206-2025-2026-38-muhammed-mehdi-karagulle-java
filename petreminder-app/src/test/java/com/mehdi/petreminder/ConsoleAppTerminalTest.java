/**
 * @file ConsoleAppTerminalTest.java
 * @brief ConsoleApp selectMenuOption terminal branch testleri.
 * @details Mockito ile sahte Terminal enjekte edilerek
 *          Tab, Enter, Down/Up arrow, ESC, exception ve fallback
 *          branch'leri %100 kapsar.
 *          NOT: Terminal.enterRawMode() → Attributes döner (void değil),
 *               Terminal.echo(boolean) → boolean döner.
 *          Bu nedenle when(...).thenReturn(...) kullanılır.
 */
package com.mehdi.petreminder;

import com.mehdi.petreminder.config.StorageConfig;
import com.mehdi.petreminder.config.StorageType;
import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @class ConsoleAppTerminalTest
 * @brief ConsoleApp terminal-modu interaktif menü branch testleri.
 * @details Mockito ile {@link Terminal} mock'u enjekte edilerek
 *          selectMenuOption()'ın tüm dalları kapsanır.
 * @author Muhammed Mehdi Karagülle, Ibrahim Demirci, Zumre Uykun
 * @version 1.0
 */
class ConsoleAppTerminalTest {

    /** @brief Original stdout. */
    private PrintStream originalOut;

    /**
     * @brief Her testten önce stdout susturulur.
     */
    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        StorageConfig.setActiveBackend(StorageType.SQLITE);
    }

    /**
     * @brief Her testten sonra stdout geri yüklenir.
     */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        StorageConfig.reset();
    }

    // ─── Yardımcı: mock Terminal ────────────────────────────────────

    /**
     * @brief Sadece read() stublaması olan temel mock Terminal üretir.
     *        enterRawMode → null (Attributes), echo → false döner.
     * @param keys Dönecek karakter dizisi
     * @return Mock Terminal
     * @throws Exception Hata
     */
    private Terminal mockTerminalReturning(int... keys) throws Exception {
        Terminal terminal = Mockito.mock(Terminal.class);

        // enterRawMode() → Attributes döner (void DEĞİL)
        when(terminal.enterRawMode()).thenReturn(new Attributes());
        // echo(boolean) → boolean döner (void DEĞİL)
        when(terminal.echo(anyBoolean())).thenReturn(false);

        // reader() → mock NonBlockingReader
        org.jline.utils.NonBlockingReader reader =
            Mockito.mock(org.jline.utils.NonBlockingReader.class);
        when(terminal.reader()).thenReturn(reader);

        // read() → successive key values
        if (keys.length == 1) {
            when(reader.read()).thenReturn(keys[0]);
        } else {
            org.mockito.stubbing.OngoingStubbing<Integer> stub = when(reader.read());
            for (int i = 0; i < keys.length - 1; i++) {
                stub = stub.thenReturn(keys[i]);
            }
            stub.thenReturn(keys[keys.length - 1]);
        }
        // Timed reads (ESC sequences)
        when(reader.read(anyLong())).thenReturn(-1);

        return terminal;
    }

    // ─── Enter tuşu (CR=13) ────────────────────────────────────────

    /**
     * @test Terminal modda Enter (13) → ilk seçenek döner.
     */
    @Test
    void testTerminalEnterReturnsFirstOption() throws Exception {
        ConsoleApp app = new ConsoleApp(new Scanner(""));
        app.setRunning(true);
        app.setTerminal(mockTerminalReturning(13));
        String result = app.selectMenuOption("Title",
            new String[]{"A", "B"}, new String[]{"1", "2"});
        assertEquals("1", result);
    }

    /**
     * @test Terminal modda LF (10) → seçim döner.
     */
    @Test
    void testTerminalLFEnterReturnsOption() throws Exception {
        ConsoleApp app = new ConsoleApp(new Scanner(""));
        app.setRunning(true);
        app.setTerminal(mockTerminalReturning(10)); // LF
        String result = app.selectMenuOption("Title",
            new String[]{"A", "B"}, new String[]{"1", "2"});
        assertEquals("1", result);
    }

    // ─── Tab tuşu ─────────────────────────────────────────────────

    /**
     * @test Tab (9) → index +1, ardından Enter → ikinci seçenek.
     */
    @Test
    void testTerminalTabThenEnter() throws Exception {
        ConsoleApp app = new ConsoleApp(new Scanner(""));
        app.setRunning(true);
        app.setTerminal(mockTerminalReturning(9, 13)); // Tab, Enter
        String result = app.selectMenuOption("Title",
            new String[]{"A", "B"}, new String[]{"1", "2"});
        assertEquals("2", result);
    }

    /**
     * @test Tab → Tab → Enter → 3. seçenek.
     */
    @Test
    void testTerminalTwoTabsThenEnter() throws Exception {
        ConsoleApp app = new ConsoleApp(new Scanner(""));
        app.setRunning(true);
        app.setTerminal(mockTerminalReturning(9, 9, 13)); // Tab, Tab, Enter
        String result = app.selectMenuOption("Title",
            new String[]{"A", "B", "C"}, new String[]{"1", "2", "3"});
        assertEquals("3", result);
    }

    /**
     * @test Tab wrap-around: 2 seçenek — Tab, Tab, Enter → index=0 → "1".
     */
    @Test
    void testTerminalTabWrapAround() throws Exception {
        ConsoleApp app = new ConsoleApp(new Scanner(""));
        app.setRunning(true);
        app.setTerminal(mockTerminalReturning(9, 9, 13)); // Tab (0→1), Tab (1→0), Enter
        String result = app.selectMenuOption("Title",
            new String[]{"A", "B"}, new String[]{"1", "2"});
        assertEquals("1", result);
    }

    // ─── ESC arrow keys ────────────────────────────────────────────

    /**
     * @test ESC + '[' + 'B' (Down arrow) → index +1, ardından Enter.
     */
    @Test
    void testTerminalDownArrowThenEnter() throws Exception {
        ConsoleApp app = new ConsoleApp(new Scanner(""));
        app.setRunning(true);

        Terminal terminal = Mockito.mock(Terminal.class);
        when(terminal.enterRawMode()).thenReturn(new Attributes());
        when(terminal.echo(anyBoolean())).thenReturn(false);
        org.jline.utils.NonBlockingReader reader =
            Mockito.mock(org.jline.utils.NonBlockingReader.class);
        when(terminal.reader()).thenReturn(reader);
        // read(): ESC=27, then Enter=13
        when(reader.read()).thenReturn(27, 13);
        // read(100) for ESC sequence: '[' (91), 'B' (66) = Down
        when(reader.read(100L)).thenReturn(91, 66);

        app.setTerminal(terminal);
        String result = app.selectMenuOption("Title",
            new String[]{"A", "B"}, new String[]{"1", "2"});
        assertEquals("2", result);
    }

    /**
     * @test ESC + '[' + 'A' (Up arrow) → index wraps, ardından Enter.
     */
    @Test
    void testTerminalUpArrowThenEnter() throws Exception {
        ConsoleApp app = new ConsoleApp(new Scanner(""));
        app.setRunning(true);

        Terminal terminal = Mockito.mock(Terminal.class);
        when(terminal.enterRawMode()).thenReturn(new Attributes());
        when(terminal.echo(anyBoolean())).thenReturn(false);
        org.jline.utils.NonBlockingReader reader =
            Mockito.mock(org.jline.utils.NonBlockingReader.class);
        when(terminal.reader()).thenReturn(reader);
        // ESC + '[' + 'A' (65, Up) → index wraps to 1 (from 0), then Enter
        when(reader.read()).thenReturn(27, 13);
        when(reader.read(100L)).thenReturn(91, 65);

        app.setTerminal(terminal);
        String result = app.selectMenuOption("Title",
            new String[]{"A", "B"}, new String[]{"1", "2"});
        assertEquals("2", result);
    }

    /**
     * @test ESC + non-91 (not '[') → index stays 0, ardından Enter.
     */
    @Test
    void testTerminalEscNon91ThenEnter() throws Exception {
        ConsoleApp app = new ConsoleApp(new Scanner(""));
        app.setRunning(true);

        Terminal terminal = Mockito.mock(Terminal.class);
        when(terminal.enterRawMode()).thenReturn(new Attributes());
        when(terminal.echo(anyBoolean())).thenReturn(false);
        org.jline.utils.NonBlockingReader reader =
            Mockito.mock(org.jline.utils.NonBlockingReader.class);
        when(terminal.reader()).thenReturn(reader);
        // ESC + non-91 → no change, then Enter
        when(reader.read()).thenReturn(27, 13);
        when(reader.read(100L)).thenReturn(99, 99);

        app.setTerminal(terminal);
        String result = app.selectMenuOption("Title",
            new String[]{"A", "B"}, new String[]{"1", "2"});
        assertEquals("1", result);
    }

    /**
     * @test ESC + '[' + non-arrow → index stays 0, ardından Enter.
     */
    @Test
    void testTerminalEscBracketNonArrowThenEnter() throws Exception {
        ConsoleApp app = new ConsoleApp(new Scanner(""));
        app.setRunning(true);

        Terminal terminal = Mockito.mock(Terminal.class);
        when(terminal.enterRawMode()).thenReturn(new Attributes());
        when(terminal.echo(anyBoolean())).thenReturn(false);
        org.jline.utils.NonBlockingReader reader =
            Mockito.mock(org.jline.utils.NonBlockingReader.class);
        when(terminal.reader()).thenReturn(reader);
        // ESC + '[' + 'C' (right arrow, not handled) → index stays 0, then Enter
        when(reader.read()).thenReturn(27, 13);
        when(reader.read(100L)).thenReturn(91, 67);

        app.setTerminal(terminal);
        String result = app.selectMenuOption("Title",
            new String[]{"A", "B"}, new String[]{"1", "2"});
        assertEquals("1", result);
    }

    // ─── secondDraw path (firstDraw=false) ─────────────────────────

    /**
     * @test İkinci döngüde ANSI cursor up branch (firstDraw=false).
     */
    @Test
    void testTerminalSecondDrawAnsiCursorPath() throws Exception {
        ConsoleApp app = new ConsoleApp(new Scanner(""));
        app.setRunning(true);

        Terminal terminal = Mockito.mock(Terminal.class);
        when(terminal.enterRawMode()).thenReturn(new Attributes());
        when(terminal.echo(anyBoolean())).thenReturn(false);
        org.jline.utils.NonBlockingReader reader =
            Mockito.mock(org.jline.utils.NonBlockingReader.class);
        when(terminal.reader()).thenReturn(reader);
        // Tab → second loop iteration (firstDraw=false), then Enter
        when(reader.read()).thenReturn(9, 13);
        when(reader.read(anyLong())).thenReturn(-1);

        app.setTerminal(terminal);
        String result = app.selectMenuOption("Title",
            new String[]{"A", "B"}, new String[]{"1", "2"});
        assertEquals("2", result);
    }

    // ─── Exception path → fallback to scanner ─────────────────────

    /**
     * @test Terminal'den exception fırlatılırsa fallback scanner kullanılır.
     */
    @Test
    void testTerminalExceptionFallsBackToScanner() throws Exception {
        ConsoleApp app = new ConsoleApp(new Scanner("2\n"));
        app.setRunning(true);

        Terminal terminal = Mockito.mock(Terminal.class);
        // enterRawMode throws → catch block triggered
        when(terminal.enterRawMode()).thenThrow(new RuntimeException("terminal error"));
        when(terminal.echo(anyBoolean())).thenReturn(false);

        app.setTerminal(terminal);
        String result = app.selectMenuOption("Title",
            new String[]{"A", "B"}, new String[]{"1", "2"});
        assertEquals("2", result);
    }

    // ─── running=false → while döngüsüne girilmez → fallback ──────

    /**
     * @test running=false iken terminal varken döngüye girilmez → fallback.
     */
    @Test
    void testTerminalRunningFalseSkipsLoop() throws Exception {
        ConsoleApp app = new ConsoleApp(new Scanner("1\n"));
        // running=false (default) → döngüye girilmez, fallback
        Terminal terminal = Mockito.mock(Terminal.class);
        when(terminal.enterRawMode()).thenReturn(new Attributes());
        when(terminal.echo(anyBoolean())).thenReturn(false);
        org.jline.utils.NonBlockingReader reader =
            Mockito.mock(org.jline.utils.NonBlockingReader.class);
        when(terminal.reader()).thenReturn(reader);

        app.setTerminal(terminal);
        String result = app.selectMenuOption("Title",
            new String[]{"A", "B"}, new String[]{"1", "2"});
        assertEquals("1", result); // fallback to scanner
    }

    // ─── terminal=null path (normal scanner mode) ────────────────────

    /**
     * @test terminal=null iken normal scanner modu çalışır.
     */
    @Test
    void testNullTerminalUsesScanner() {
        ConsoleApp app = new ConsoleApp(new Scanner("2\n"));
        // terminal=null by default → normal scanner mode
        String result = app.selectMenuOption("Title",
            new String[]{"A", "B"}, new String[]{"1", "2"});
        assertEquals("2", result);
    }

    /**
     * @test initTerminal — headless ortamda terminal null kalır.
     */
    @Test
    void testInitTerminalNullConsole() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.showMainMenu()); // running=false → immediate return
    }
}
