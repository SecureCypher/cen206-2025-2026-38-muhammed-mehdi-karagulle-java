/**
 * @file CoverageGapTest.java
 * @brief Kalan coverage açıklarını kapatır: RepositoryFactory MySQL branch,
 *        StorageConfig loadFromFile, BinaryRepository exception path,
 *        RepositoryFactory private constructor, SqliteRepository gaps.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.config.StorageConfig;
import com.mehdi.petreminder.config.StorageType;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class CoverageGapTest
 * @brief RepositoryFactory MYSQL branch, StorageConfig file I/O,
 *        BinaryRepository exception, ve SQLite repo gap testleri.
 * @author Muhammed Mehdi Karagülle, Ibrahim Demirci, Zumre Uykun
 * @version 1.0
 */
class CoverageGapTest {

    /** @brief Orijinal stdout. */
    private PrintStream originalOut;

    /**
     * @brief Setup — MYSQL backend'e geçiş.
     */
    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
    }

    /**
     * @brief Teardown — SQLITE'a dön.
     */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        StorageConfig.reset();
        StorageConfig.setActiveBackend(StorageType.SQLITE);
    }

    // ─── RepositoryFactory private constructor (reflection) ──────────

    /**
     * @test RepositoryFactory private constructor — reflection ile erişilir.
     */
    @Test
    void testRepositoryFactoryPrivateConstructor() throws Exception {
        Constructor<RepositoryFactory> c = RepositoryFactory.class.getDeclaredConstructor();
        c.setAccessible(true);
        assertNotNull(c.newInstance());
    }

    // ─── RepositoryFactory MYSQL branch'leri ────────────────────────

    /**
     * @test createPetRepository MYSQL → PetMySqlRepository (Docker yoksa exception).
     */
    @Test
    void testCreatePetRepositoryMysql() {
        StorageConfig.setActiveBackend(StorageType.MYSQL);
        assertThrows(RepositoryException.class, RepositoryFactory::createPetRepository);
    }

    /**
     * @test createReminderRepository MYSQL → ReminderMySqlRepository.
     */
    @Test
    void testCreateReminderRepositoryMysql() {
        StorageConfig.setActiveBackend(StorageType.MYSQL);
        assertThrows(RepositoryException.class, RepositoryFactory::createReminderRepository);
    }

    /**
     * @test createUserRepository MYSQL → UserMySqlRepository.
     */
    @Test
    void testCreateUserRepositoryMysql() {
        StorageConfig.setActiveBackend(StorageType.MYSQL);
        assertThrows(RepositoryException.class, RepositoryFactory::createUserRepository);
    }

    /**
     * @test createMedicalRecordRepository MYSQL → MedicalRecordMySqlRepository.
     */
    @Test
    void testCreateMedicalRecordRepositoryMysql() {
        StorageConfig.setActiveBackend(StorageType.MYSQL);
        assertThrows(RepositoryException.class, RepositoryFactory::createMedicalRecordRepository);
    }

    /**
     * @test getActiveType — aktif tür döner.
     */
    @Test
    void testGetActiveType() {
        StorageConfig.setActiveBackend(StorageType.SQLITE);
        assertEquals(StorageType.SQLITE, RepositoryFactory.getActiveType());
    }

    // ─── StorageConfig loadFromFile — valid file path ────────────────

    /**
     * @test loadFromFile — geçerli properties dosyası ile tam branch.
     */
    @Test
    void testLoadFromFileValidFile() throws Exception {
        // Write a valid properties file to config/storage.properties
        File configDir = new File("config");
        configDir.mkdirs();
        File configFile = new File("config/storage.properties");
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            java.util.Properties p = new java.util.Properties();
            p.setProperty("storage.backend", "SQLITE");
            p.setProperty("mysql.url", "jdbc:mysql://test:3306/db");
            p.setProperty("mysql.username", "user");
            p.setProperty("mysql.password", "pass");
            p.setProperty("sqlite.path", "data/test.db");
            p.setProperty("binary.dir", "data/bin");
            p.store(fos, "test");
        }
        StorageConfig.loadFromFile();
        assertEquals(StorageType.SQLITE, StorageConfig.getActiveBackend());
        configFile.delete();
        configDir.delete();
    }

    /**
     * @test loadFromFile — invalid backend value → BINARY fallback.
     */
    @Test
    void testLoadFromFileInvalidBackend() throws Exception {
        File configDir = new File("config");
        configDir.mkdirs();
        File configFile = new File("config/storage.properties");
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            java.util.Properties p = new java.util.Properties();
            p.setProperty("storage.backend", "INVALID_TYPE");
            p.store(fos, "test");
        }
        StorageConfig.loadFromFile();
        assertEquals(StorageType.BINARY, StorageConfig.getActiveBackend());
        configFile.delete();
        configDir.delete();
    }

    /**
     * @test loadFromFile — dosya yok → return immediately (no-op).
     */
    @Test
    void testLoadFromFileMissing() {
        // Ensure file doesn't exist
        new File("config/storage.properties").delete();
        assertDoesNotThrow(StorageConfig::loadFromFile);
    }

    /**
     * @test saveToFile — config dir oluşturur ve dosyayı yazar.
     */
    @Test
    void testSaveToFile() {
        StorageConfig.setActiveBackend(StorageType.SQLITE);
        // setActiveBackend calls saveToFile internally — just verify it doesn't throw
        assertDoesNotThrow(() -> StorageConfig.setActiveBackend(StorageType.BINARY));
        // Cleanup
        new File("config/storage.properties").delete();
        new File("config").delete();
    }

    /**
     * @test StorageConfig.reset — tüm alanlar default'a döner.
     */
    @Test
    void testReset() {
        StorageConfig.setActiveBackend(StorageType.MYSQL);
        StorageConfig.reset();
        assertEquals(StorageType.BINARY, StorageConfig.getActiveBackend());
    }

    /**
     * @test StorageConfig getters.
     */
    @Test
    void testGetters() {
        StorageConfig.reset();
        assertNotNull(StorageConfig.getMysqlUrl());
        assertNotNull(StorageConfig.getMysqlUsername());
        assertNotNull(StorageConfig.getMysqlPassword());
        assertNotNull(StorageConfig.getSqliteFilePath());
        assertNotNull(StorageConfig.getBinaryDirectory());
        StorageConfig.setMysqlUrl("jdbc:mysql://custom:3306/db");
        assertEquals("jdbc:mysql://custom:3306/db", StorageConfig.getMysqlUrl());
    }

    // ─── BinaryRepository exception paths ────────────────────────────

    /**
     * @test BinaryRepository.findAll — corrupt directory → empty list.
     */
    @Test
    void testBinaryRepositoryWithDir() throws Exception {
        StorageConfig.setActiveBackend(StorageType.BINARY);
        BinaryRepository<com.mehdi.petreminder.model.Pet> repo =
            new BinaryRepository<>("data/binary", "pets");
        assertNotNull(repo.findAll());
    }

    /**
     * @test BinaryRepository.count — returns correct count.
     */
    @Test
    void testBinaryRepositoryCount() {
        BinaryRepository<com.mehdi.petreminder.model.Pet> repo =
            new BinaryRepository<>("data/binary_test_gap", "pets");
        assertEquals(0, repo.count()); // empty dir
    }

    /**
     * @test BinaryRepository.close — no-op.
     */
    @Test
    void testBinaryRepositoryClose() {
        BinaryRepository<com.mehdi.petreminder.model.Pet> repo =
            new BinaryRepository<>("data/binary_test_gap2", "pets");
        assertDoesNotThrow(repo::close);
    }

    /**
     * @test BinaryRepository.deleteAll — no files.
     */
    @Test
    void testBinaryRepositoryDeleteAll() {
        BinaryRepository<com.mehdi.petreminder.model.Pet> repo =
            new BinaryRepository<>("data/binary_test_gap3", "pets");
        assertDoesNotThrow(repo::deleteAll);
    }

    /**
     * @test BinaryRepository.findById — missing file returns empty.
     */
    @Test
    void testBinaryRepositoryFindByIdMissing() {
        BinaryRepository<com.mehdi.petreminder.model.Pet> repo =
            new BinaryRepository<>("data/binary_test_gap4", "pets");
        assertTrue(repo.findById(99999).isEmpty());
    }

    /**
     * @test BinaryRepository.delete — missing file returns false.
     */
    @Test
    void testBinaryRepositoryDeleteMissing() {
        BinaryRepository<com.mehdi.petreminder.model.Pet> repo =
            new BinaryRepository<>("data/binary_test_gap5", "pets");
        assertFalse(repo.delete(99999));
    }
}
