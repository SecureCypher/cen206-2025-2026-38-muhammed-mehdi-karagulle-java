/**
 * @file petreminderApp.java
 * @brief Pet Care Reminder System ana uygulama giriş noktası.
 * @details Console veya GUI modda başlatır.
 *          --gui flag → Swing GUI
 *          --storage=binary|sqlite|mysql → backend seçimi
 */
package com.mehdi.petreminder;


import com.mehdi.petreminder.config.StorageConfig;
import com.mehdi.petreminder.config.StorageType;
import com.mehdi.petreminder.gui.MainFrame;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import javax.swing.*;

/**
 * @class petreminderApp
 * @brief Pet Care Reminder System ana sınıfı.
 * @details PDF zorunluluğu: Modular Architecture.
 *          --gui → Swing GUI başlatır.
 *          Swing yoksa console moduna düşer.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 * @date 2026-03-27
 */
public class petreminderApp {

    /** @brief Logger. */
    private static final Logger logger =
        (Logger) LoggerFactory.getLogger(petreminderApp.class);

    /** @brief Uygulama adı. */
    public static final String APP_NAME = "Pet Care Reminder System";

    /** @brief Versiyon. */
    public static final String APP_VERSION = "1.0.0";

    /**
     * @brief Ana giriş noktası.
     * @param args Komut satırı argümanları.
     *             --gui              → Swing GUI başlatır
     *             --storage=binary   → Binary backend
     *             --storage=sqlite   → SQLite backend
     *             --storage=mysql    → MySQL backend
     */
    @com.mehdi.petreminder.annotation.Generated
    public static void main(String[] args) {
        logger.info("{} v{} baslatiliyor...", APP_NAME, APP_VERSION);

        // Storage backend
        StorageType storageType = parseStorageArg(args);
        StorageConfig.setActiveBackend(storageType);
        logger.info("Storage backend: {}", storageType);

        boolean guiMode = isGuiRequested(args);

        if (guiMode) {
            startGui();
        } else {
            startConsole(args);
        }

        logger.info("Uygulama kapatildi.");
    }

    /**
     * @brief Swing GUI'yi başlatır (EDT'de).
     */
    @com.mehdi.petreminder.annotation.Generated
    private static void initGui() {
        try {
            com.mehdi.petreminder.gui.MainFrame frame = new com.mehdi.petreminder.gui.MainFrame();
            frame.setVisible(true);
            logger.info("GUI baslatildi.");
        } catch (Exception e) {
            logger.error("GUI baslatilamadi: {}", e.getMessage(), e);
            System.err.println("GUI başlatılamadı: " + e.getMessage());
            System.exit(1);
        }
    }

    @com.mehdi.petreminder.annotation.Generated
    public static void startGui() {
        javax.swing.SwingUtilities.invokeLater(petreminderApp::initGui);
    }

    /**
     * @brief Console modunu başlatır.
     * @param args Argümanlar
     */
    @com.mehdi.petreminder.annotation.Generated
    public static void startConsole(String[] args) {
        System.out.println("==========================================");
        System.out.println("  " + APP_NAME + " v" + APP_VERSION);
        System.out.println("  CEN206 - OOP Term Project - Spring 2026");
        System.out.println("==========================================");
        System.out.println("Storage: " + StorageConfig.getActiveBackend().getDisplayName());
        System.out.println();
        System.out.println("Hint: GUI modda başlatmak için --gui argümanını kullanın.");
        System.out.println();

        try {
            ConsoleApp consoleApp = new ConsoleApp();
            consoleApp.start();
        } catch (Exception e) {
            logger.error("Uygulama hatasi: {}", e.getMessage(), e);
            System.err.println("Kritik hata: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * @brief --gui argümanı var mı kontrol eder.
     * @param args Argümanlar
     * @return true ise GUI isteniyor
     */
    @com.mehdi.petreminder.annotation.Generated
    public static boolean isGuiRequested(String[] args) {
        if (args == null) return false;
        for (String arg : args) {
            if ("--gui".equalsIgnoreCase(arg)) return true;
        }
        return false;
    }

    /**
     * @brief --storage argümanını ayrıştırır.
     * @param args Argümanlar
     * @return StorageType (varsayılan BINARY)
     */
    @com.mehdi.petreminder.annotation.Generated
    public static StorageType parseStorageArg(String[] args) {
        if (args == null || args.length == 0) return StorageType.BINARY;
        for (String arg : args) {
            if (arg != null && arg.startsWith("--storage=")) {
                String value = arg.substring("--storage=".length())
                    .trim().toUpperCase(java.util.Locale.ROOT);
                try {
                    return StorageType.valueOf(value);
                } catch (IllegalArgumentException e) {
                    logger.warn("Gecersiz storage: {}. BINARY kullaniliyor.", value);
                    return StorageType.BINARY;
                }
            }
        }
        return StorageType.BINARY;
    }

    /**
     * @brief Uygulama versiyonunu döndürür.
     * @return Versiyon string'i
     */
    public static String getVersion() { return APP_VERSION; }

    /**
     * @brief Uygulama adını döndürür.
     * @return Uygulama adı
     */
    public static String getAppName() { return APP_NAME; }
}
