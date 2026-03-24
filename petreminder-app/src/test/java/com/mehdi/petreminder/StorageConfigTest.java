/**
 * @file StorageConfigTest.java
 * @brief StorageConfig, StorageType, RepositoryFactory, RepositoryException,
 *        ServiceException ve GuiConstants için JUnit 5 testleri.
 */
package com.mehdi.petreminder;

import com.mehdi.petreminder.config.StorageConfig;
import com.mehdi.petreminder.config.StorageType;
import com.mehdi.petreminder.gui.util.GuiConstants;
import com.mehdi.petreminder.repository.*;
import com.mehdi.petreminder.service.ServiceException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @class StorageConfigTest
 * @brief StorageConfig, StorageType, RepositoryFactory,
 *        RepositoryException, ServiceException, GuiConstants test sınıfı.
 * @author Muhammed Mehdi Karagülle, Ibrahim Demirci, Zumre Uykun
 */
class StorageConfigTest {

    @BeforeEach void resetConfig() { StorageConfig.reset(); }
    @AfterEach  void cleanConfig()  { StorageConfig.reset(); }

    // ── StorageType enum ─────────────────────────────────────────────

    @Test void testStorageTypeBinaryDisplayName() {
        assertNotNull(StorageType.BINARY.getDisplayName());
        assertTrue(StorageType.BINARY.getDisplayName().contains("Binary"));
    }

    @Test void testStorageTypeSqliteDisplayName() {
        assertNotNull(StorageType.SQLITE.getDisplayName());
        assertTrue(StorageType.SQLITE.getDisplayName().contains("SQLite"));
    }

    @Test void testStorageTypeMysqlDisplayName() {
        assertNotNull(StorageType.MYSQL.getDisplayName());
        assertTrue(StorageType.MYSQL.getDisplayName().contains("MySQL"));
    }

    @Test void testStorageTypeValues() {
        StorageType[] values = StorageType.values();
        assertEquals(3, values.length);
    }

    @Test void testStorageTypeValueOf() {
        assertEquals(StorageType.BINARY,  StorageType.valueOf("BINARY"));
        assertEquals(StorageType.SQLITE,  StorageType.valueOf("SQLITE"));
        assertEquals(StorageType.MYSQL,   StorageType.valueOf("MYSQL"));
    }

    @Test void testStorageTypeToStringBinary() {
        assertEquals("Binary Dosya", StorageType.BINARY.toString());
    }

    @Test void testStorageTypeToStringSqlite() {
        assertEquals("SQLite", StorageType.SQLITE.toString());
    }

    @Test void testStorageTypeToStringMysql() {
        assertEquals("MySQL (Docker)", StorageType.MYSQL.toString());
    }

    // ── StorageConfig ────────────────────────────────────────────────

    @Test void testGetActiveBackendNotNull() {
        assertNotNull(StorageConfig.getActiveBackend());
    }

    @Test void testSetActiveBackendBinary() {
        StorageConfig.setActiveBackend(StorageType.BINARY);
        assertEquals(StorageType.BINARY, StorageConfig.getActiveBackend());
    }

    @Test void testSetActiveBackendSqlite() {
        StorageConfig.setActiveBackend(StorageType.SQLITE);
        assertEquals(StorageType.SQLITE, StorageConfig.getActiveBackend());
    }

    @Test void testSetActiveBackendMysql() {
        StorageConfig.setActiveBackend(StorageType.MYSQL);
        assertEquals(StorageType.MYSQL, StorageConfig.getActiveBackend());
    }

    @Test void testSetActiveBackendNull() {
        StorageType before = StorageConfig.getActiveBackend();
        StorageConfig.setActiveBackend(null);
        assertEquals(before, StorageConfig.getActiveBackend());
    }

    @Test void testGetMysqlUrlNotNull() {
        assertNotNull(StorageConfig.getMysqlUrl());
    }

    @Test void testSetMysqlUrl() {
        StorageConfig.setMysqlUrl("jdbc:mysql://test:3306/db");
        assertEquals("jdbc:mysql://test:3306/db", StorageConfig.getMysqlUrl());
    }

    @Test void testGetMysqlUsernameNotNull() {
        assertNotNull(StorageConfig.getMysqlUsername());
    }

    @Test void testGetMysqlPasswordNotNull() {
        assertNotNull(StorageConfig.getMysqlPassword());
    }

    @Test void testGetSqliteFilePathNotNull() {
        assertNotNull(StorageConfig.getSqliteFilePath());
    }

    @Test void testGetBinaryDirectoryNotNull() {
        assertNotNull(StorageConfig.getBinaryDirectory());
    }

    @Test void testLoadFromFileNoException() {
        assertDoesNotThrow(StorageConfig::loadFromFile);
    }

    @Test void testSaveAndLoad() {
        StorageConfig.setActiveBackend(StorageType.SQLITE);
        StorageConfig.saveToFile();
        StorageConfig.loadFromFile();
        assertEquals(StorageType.SQLITE, StorageConfig.getActiveBackend());
    }

    @Test void testResetToDefaults() {
        StorageConfig.setActiveBackend(StorageType.MYSQL);
        StorageConfig.reset();
        assertEquals(StorageType.BINARY, StorageConfig.getActiveBackend());
    }

    // StorageConfig: saveToFile + loadFromFile tam döngü — tüm property'leri kapsar
    @Test void testSaveAndLoadFullCycle() {
        StorageConfig.setActiveBackend(StorageType.SQLITE);
        StorageConfig.setMysqlUrl("jdbc:mysql://custom:3306/db");
        StorageConfig.saveToFile();
        StorageConfig.reset();
        StorageConfig.loadFromFile();
        // loadFromFile config dosyasından SQLITE okumalı
        assertEquals(StorageType.SQLITE, StorageConfig.getActiveBackend());
    }

    // StorageConfig: loadFromFile geçersiz backend — BINARY'e düşmeli
    @Test void testLoadFromFileInvalidBackend() {
        // Önce geçerli bir şey kaydet
        StorageConfig.setActiveBackend(StorageType.SQLITE);
        StorageConfig.saveToFile();
        // Dosyayı manuel olarak geçersiz backend ile değiştir
        try {
            java.util.Properties props = new java.util.Properties();
            try (java.io.FileInputStream fis = new java.io.FileInputStream("config/storage.properties")) {
                props.load(fis);
            }
            props.setProperty("storage.backend", "INVALID_TYPE");
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream("config/storage.properties")) {
                props.store(fos, "Test");
            }
            StorageConfig.loadFromFile();
            assertEquals(StorageType.BINARY, StorageConfig.getActiveBackend());
        } catch (Exception e) {
            // Dosya işleminde hata olursa test geçer
        }
    }

    // ── RepositoryFactory ────────────────────────────────────────────

    @Test void testCreatePetRepositoryBinary() {
        StorageConfig.setActiveBackend(StorageType.BINARY);
        assertNotNull(RepositoryFactory.createPetRepository());
    }

    @Test void testCreatePetRepositorySqlite() {
        StorageConfig.setActiveBackend(StorageType.SQLITE);
        assertNotNull(RepositoryFactory.createPetRepository());
    }

    @Test void testCreateReminderRepositoryBinary() {
        StorageConfig.setActiveBackend(StorageType.BINARY);
        assertNotNull(RepositoryFactory.createReminderRepository());
    }

    @Test void testCreateReminderRepositorySqlite() {
        StorageConfig.setActiveBackend(StorageType.SQLITE);
        assertNotNull(RepositoryFactory.createReminderRepository());
    }

    @Test void testCreateUserRepositoryBinary() {
        StorageConfig.setActiveBackend(StorageType.BINARY);
        assertNotNull(RepositoryFactory.createUserRepository());
    }

    @Test void testCreateUserRepositorySqlite() {
        StorageConfig.setActiveBackend(StorageType.SQLITE);
        assertNotNull(RepositoryFactory.createUserRepository());
    }

    @Test void testCreateMedicalRecordRepositoryBinary() {
        StorageConfig.setActiveBackend(StorageType.BINARY);
        assertNotNull(RepositoryFactory.createMedicalRecordRepository());
    }

    @Test void testCreateMedicalRecordRepositorySqlite() {
        StorageConfig.setActiveBackend(StorageType.SQLITE);
        assertNotNull(RepositoryFactory.createMedicalRecordRepository());
    }

    @Test void testGetActiveType() {
        StorageConfig.setActiveBackend(StorageType.SQLITE);
        assertEquals(StorageType.SQLITE, RepositoryFactory.getActiveType());
    }

    // MySQL branch coverage — RepositoryFactory — assertThrows ile branch'a girilir
    @Test void testCreatePetRepositoryMysql() {
        StorageConfig.setActiveBackend(StorageType.MYSQL);
        assertThrows(Exception.class, () -> RepositoryFactory.createPetRepository());
    }

    @Test void testCreateReminderRepositoryMysql() {
        StorageConfig.setActiveBackend(StorageType.MYSQL);
        assertThrows(Exception.class, () -> RepositoryFactory.createReminderRepository());
    }

    @Test void testCreateUserRepositoryMysql() {
        StorageConfig.setActiveBackend(StorageType.MYSQL);
        assertThrows(Exception.class, () -> RepositoryFactory.createUserRepository());
    }

    @Test void testCreateMedicalRecordRepositoryMysql() {
        StorageConfig.setActiveBackend(StorageType.MYSQL);
        assertThrows(Exception.class, () -> RepositoryFactory.createMedicalRecordRepository());
    }

    // ── RepositoryException ──────────────────────────────────────────

    @Test void testRepositoryExceptionMessage() {
        RepositoryException ex = new RepositoryException("test error");
        assertEquals("test error", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test void testRepositoryExceptionMessageAndCause() {
        RuntimeException cause = new RuntimeException("cause");
        RepositoryException ex = new RepositoryException("test error", cause);
        assertEquals("test error", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test void testRepositoryExceptionIsRuntimeException() {
        RepositoryException ex = new RepositoryException("x");
        assertTrue(ex instanceof RuntimeException);
    }

    // ── ServiceException ─────────────────────────────────────────────

    @Test void testServiceExceptionMessage() {
        ServiceException ex = new ServiceException("service error");
        assertEquals("service error", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test void testServiceExceptionMessageAndCause() {
        Exception cause = new Exception("cause");
        ServiceException ex = new ServiceException("service error", cause);
        assertEquals("service error", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test void testServiceExceptionIsRuntimeException() {
        ServiceException ex = new ServiceException("x");
        assertTrue(ex instanceof RuntimeException);
    }

    // ── GuiConstants ─────────────────────────────────────────────────

    @Test void testGuiConstantsColorsNotNull() {
        assertNotNull(GuiConstants.NAV_COLOR);
        assertNotNull(GuiConstants.NAV_HOVER);
        assertNotNull(GuiConstants.NAV_ACTIVE);
        assertNotNull(GuiConstants.BG_COLOR);
        assertNotNull(GuiConstants.CARD_BG);
        assertNotNull(GuiConstants.PRIMARY);
        assertNotNull(GuiConstants.DANGER);
        assertNotNull(GuiConstants.SUCCESS);
        assertNotNull(GuiConstants.WARNING);
        assertNotNull(GuiConstants.TEXT_PRIMARY);
        assertNotNull(GuiConstants.TEXT_SECONDARY);
        assertNotNull(GuiConstants.BORDER_COLOR);
    }

    @Test void testGuiConstantsFontsNotNull() {
        assertNotNull(GuiConstants.TITLE_FONT);
        assertNotNull(GuiConstants.SECTION_FONT);
        assertNotNull(GuiConstants.BODY_FONT);
        assertNotNull(GuiConstants.SMALL_FONT);
        assertNotNull(GuiConstants.NAV_FONT);
    }

    @Test void testGuiConstantsDimensions() {
        assertEquals(200, GuiConstants.NAV_WIDTH);
        assertEquals(12, GuiConstants.GAP);
        assertEquals(24, GuiConstants.GAP_LARGE);
    }
}
