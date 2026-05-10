/**
 * @file MedicalRecordMySqlRepositoryTest.java
 * @brief MedicalRecordMySqlRepository için H2 unit testleri.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.model.MedicalRecord;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class MedicalRecordMySqlRepositoryTest
 * @brief MedicalRecordMySqlRepository için H2 in-memory testler.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
class MedicalRecordMySqlRepositoryTest {

    /** @brief Paylaşılan H2 bağlantısı. */
    private static Connection connection;
    /** @brief Test edilecek repository. */
    private MedicalRecordMySqlRepository repo;

    /**
     * @brief H2 bağlantısını açar.
     * @throws SQLException Bağlantı hatası
     */
    @BeforeAll
    static void setUpClass() throws SQLException {
        connection = DriverManager.getConnection(
            "jdbc:h2:mem:medmysql;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
    }

    /**
     * @brief Her testten önce tabloyu sıfırlar.
     * @throws SQLException SQL hatası
     */
    @BeforeEach
    void setUp() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS medical_records");
        }
        repo = new MedicalRecordMySqlRepository(connection);
    }

    /**
     * @brief Bağlantıyı kapatır.
     * @throws SQLException Kapatma hatası
     */
    @AfterAll
    static void tearDownClass() throws SQLException {
        if (connection != null && !connection.isClosed()) connection.close();
    }

    /**
     * @brief Yardımcı: test tıbbi kayıt oluşturur.
     * @return Oluşturulan MedicalRecord
     */
    private MedicalRecord makeRecord() {
        MedicalRecord m = new MedicalRecord();
        m.setPetId(1);
        m.setPetName("Buddy");
        m.setRecordDate(LocalDate.now());
        m.setRecordType("Muayene");
        m.setDiagnosis("Sağlıklı");
        m.setTreatment("Tedavi yok");
        m.setVeterinarianName("Dr. Test");
        m.setCost(150.0);
        m.setNextCheckDate(LocalDate.now().plusMonths(6));
        m.setNotes("Not yok");
        m.setVaccineName(null);
        return m;
    }

    // ── save / findById ──────────────────────────────────────────────

    /**
     * @test Kayıt kaydedilip ID ile bulunabilmeli.
     */
    @Test
    void testSaveAndFind() {
        MedicalRecord m = makeRecord();
        int id = repo.save(m);
        assertTrue(id > 0);
        Optional<MedicalRecord> found = repo.findById(id);
        assertTrue(found.isPresent());
        assertEquals("Buddy", found.get().getPetName());
    }

    /**
     * @test null kaydetme exception fırlatmalı.
     */
    @Test
    void testSaveNullThrows() {
        assertThrows(RepositoryException.class, () -> repo.save(null));
    }

    /**
     * @test Olmayan ID için empty döndürmeli.
     */
    @Test
    void testFindByIdNotFound() {
        assertTrue(repo.findById(9999).isEmpty());
    }

    // ── findAll ──────────────────────────────────────────────────────

    /**
     * @test findAll tüm kayıtları döndürmeli.
     */
    @Test
    void testFindAll() {
        repo.save(makeRecord());
        repo.save(makeRecord());
        List<MedicalRecord> all = repo.findAll();
        assertEquals(2, all.size());
    }

    /**
     * @test Boş tabloda findAll boş liste döndürmeli.
     */
    @Test
    void testFindAllEmpty() {
        assertTrue(repo.findAll().isEmpty());
    }

    // ── update ───────────────────────────────────────────────────────

    /**
     * @test Kayıt güncellenebilmeli.
     */
    @Test
    void testUpdate() {
        MedicalRecord m = makeRecord();
        int id = repo.save(m);
        m.setId(id);
        m.setDiagnosis("Hasta");
        assertTrue(repo.update(m));
        assertEquals("Hasta", repo.findById(id).get().getDiagnosis());
    }

    /**
     * @test null ile update false döndürmeli.
     */
    @Test
    void testUpdateNull() {
        assertFalse(repo.update(null));
    }

    // ── delete / count / deleteAll ───────────────────────────────────

    /**
     * @test Kayıt silinebilmeli.
     */
    @Test
    void testDelete() {
        int id = repo.save(makeRecord());
        assertTrue(repo.delete(id));
        assertTrue(repo.findById(id).isEmpty());
    }

    /**
     * @test Olmayan ID ile delete false döndürmeli.
     */
    @Test
    void testDeleteNonExistent() {
        assertFalse(repo.delete(9999));
    }

    /**
     * @test count doğru sayı döndürmeli.
     */
    @Test
    void testCount() {
        repo.save(makeRecord());
        assertEquals(1, repo.count());
    }

    /**
     * @test deleteAll tüm kayıtları temizlemeli.
     */
    @Test
    void testDeleteAll() {
        repo.save(makeRecord());
        repo.deleteAll();
        assertEquals(0, repo.count());
    }

    /**
     * @test getConnection null döndürmemeli.
     */
    @Test
    void testGetConnection() {
        assertNotNull(repo.getConnection());
    }

    /**
     * @test null next_check_date ve vaccine_name ile kayıt kaydedilebilmeli.
     */
    @Test
    void testSaveWithNullOptionalDates() {
        MedicalRecord m = makeRecord();
        m.setNextCheckDate(null);   // next_check_date nullable
        m.setVaccineName(null);     // vaccine_name nullable
        assertDoesNotThrow(() -> repo.save(m));
    }
}
