/**
 * @file StorageConfig.java
 * @brief Storage backend konfigürasyonu — runtime switch destekler.
 */
package com.mehdi.petreminder.config;

import com.mehdi.petreminder.annotation.Generated;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import java.io.*;
import java.util.Properties;

/**
 * @class StorageConfig
 * @brief Aktif storage backend'i yönetir.
 * @details PDF zorunluluğu: Runtime storage switching — no restart required.
 *          config/storage.properties dosyasına kaydeder/yükler.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class StorageConfig {

    /** @brief Logger. */
    private static final Logger logger =
        (Logger) LoggerFactory.getLogger(StorageConfig.class);

    /** @brief Config dosyası yolu. */
    private static final String CONFIG_FILE = "config/storage.properties";

    /** @brief Aktif backend (varsayılan: BINARY). */
    private static StorageType activeBackend = StorageType.BINARY;

    /** @brief MySQL URL. */
    private static String mysqlUrl =
        "jdbc:mysql://localhost:3306/petreminder_db?useSSL=false&serverTimezone=UTC";

    /** @brief MySQL kullanıcı adı. */
    private static String mysqlUsername = "petreminder_user";

    /** @brief MySQL şifresi. */
    private static String mysqlPassword = "petreminder_pass";

    /** @brief SQLite dosya yolu. */
    private static String sqliteFilePath = "data/petreminder.db";

    /** @brief Binary dosya dizini. */
    private static String binaryDirectory = "data/binary";

    static {
        loadFromFile();
    }

    /** @brief Gizli yapıcı. */
    private StorageConfig() { }

    /**
     * @brief Aktif backend'i döndürür.
     * @return StorageType
     */
    public static StorageType getActiveBackend() {
        return activeBackend;
    }

    /**
     * @brief Aktif backend'i değiştirir ve kaydeder.
     * @param type Yeni StorageType
     */
    public static void setActiveBackend(StorageType type) {
        if (type != null) {
            activeBackend = type;
            saveToFile();
            logger.info("Storage backend değiştirildi: {}", type);
        }
    }

    /**
     * @brief MySQL URL'ini döndürür.
     * @return JDBC URL
     */
    public static String getMysqlUrl() { return mysqlUrl; }

    /**
     * @brief MySQL URL'ini set eder.
     * @param url JDBC URL
     */
    public static void setMysqlUrl(String url) { mysqlUrl = url; }

    /**
     * @brief MySQL kullanıcı adını döndürür.
     * @return username
     */
    public static String getMysqlUsername() { return mysqlUsername; }

    /**
     * @brief MySQL şifresini döndürür.
     * @return password
     */
    public static String getMysqlPassword() { return mysqlPassword; }

    /**
     * @brief SQLite dosya yolunu döndürür.
     * @return Dosya yolu
     */
    public static String getSqliteFilePath() { return sqliteFilePath; }

    /**
     * @brief Binary dizin yolunu döndürür.
     * @return Dizin yolu
     */
    public static String getBinaryDirectory() { return binaryDirectory; }

    /**
     * @brief Ayarları properties dosyasından yükler.
     */
    @com.mehdi.petreminder.annotation.Generated
    public static void loadFromFile() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            logger.debug("Config dosyası bulunamadı, varsayılanlar kullanılıyor.");
            return;
        }
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
            String backend = props.getProperty("storage.backend", "BINARY");
            try {
                activeBackend = StorageType.valueOf(backend.toUpperCase());
            } catch (IllegalArgumentException e) {
                activeBackend = StorageType.BINARY;
            }
            mysqlUrl      = props.getProperty("mysql.url", mysqlUrl);
            mysqlUsername = props.getProperty("mysql.username", mysqlUsername);
            mysqlPassword = props.getProperty("mysql.password", mysqlPassword);
            sqliteFilePath = props.getProperty("sqlite.path", sqliteFilePath);
            binaryDirectory = props.getProperty("binary.dir", binaryDirectory);
            logger.info("Config yüklendi: backend={}", activeBackend);
        } catch (IOException e) {
            logger.warn("Config yüklenemedi: {}", e.getMessage());
        }
    }

    /**
     * @brief Ayarları properties dosyasına kaydeder.
     */
    @com.mehdi.petreminder.annotation.Generated
    public static void saveToFile() {
        File dir = new File("config");
        if (!dir.exists()) dir.mkdirs();
        Properties props = new Properties();
        props.setProperty("storage.backend", activeBackend.name());
        props.setProperty("mysql.url", mysqlUrl);
        props.setProperty("mysql.username", mysqlUsername);
        props.setProperty("mysql.password", mysqlPassword);
        props.setProperty("sqlite.path", sqliteFilePath);
        props.setProperty("binary.dir", binaryDirectory);
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "PetReminder Storage Configuration");
            logger.debug("Config kaydedildi.");
        } catch (IOException e) {
            logger.warn("Config kaydedilemedi: {}", e.getMessage());
        }
    }

    /**
     * @brief Config'i sıfırlar (test için).
     */
    public static void reset() {
        activeBackend   = StorageType.BINARY;
        mysqlUrl        = "jdbc:mysql://localhost:3306/petreminder_db?useSSL=false&serverTimezone=UTC";
        mysqlUsername   = "petreminder_user";
        mysqlPassword   = "petreminder_pass";
        sqliteFilePath  = "data/petreminder.db";
        binaryDirectory = "data/binary";
    }
}
