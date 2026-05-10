/**
 * @file MedicalRecordSqliteRepositoryTest.java
 * @brief MedicalRecordSqliteRepository JUnit5 testleri.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.model.MedicalRecord;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class MedicalRecordSqliteRepositoryTest
 * @brief MedicalRecord repository H2 testleri.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
@DisplayName("MedicalRecordSqliteRepository Testleri")
class MedicalRecordSqliteRepositoryTest {

    /** @brief H2 bağlantısı. */
    private Connection conn;

    /** @brief Test repo. */
    private MedicalRecordSqliteRepository repo;

    /**
     * @brief Setup.
     * @throws Exception bağlantı hatası
     */
    @BeforeEach
    void setUp() throws Exception {
        conn = DriverManager.getConnection(
            "jdbc:h2:mem:medtest_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
        repo = new MedicalRecordSqliteRepository(conn);
    }

    /**
     * @brief Teardown.
     * @throws Exception kapatma hatası
     */
    @AfterEach
    void tearDown() throws Exception {
        if (conn != null && !conn.isClosed()) conn.close();
    }

    /** @brief Test kaydı üretici. */
    private MedicalRecord makeRecord(int petId, String petName) {
        return new MedicalRecord(0, petId, petName,
            LocalDate.of(2026, 1, 10), "KONTROL", "Sağlıklı", "Dr.Veli");
    }

    @Test
    @DisplayName("save: kayıt eklenir")
    void testSave() {
        int id = repo.save(makeRecord(1, "Rex"));
        assertTrue(id > 0);
    }

    @Test
    @DisplayName("save null: exception")
    void testSaveNull() {
        assertThrows(RepositoryException.class, () -> repo.save(null));
    }

    @Test
    @DisplayName("findById: kayıt döner")
    void testFindById() {
        int id = repo.save(makeRecord(1, "Rex"));
        var found = repo.findById(id);
        assertTrue(found.isPresent());
        assertEquals("Rex", found.get().getPetName());
    }

    @Test
    @DisplayName("findById olmayan: empty")
    void testFindByIdEmpty() {
        assertTrue(repo.findById(9999).isEmpty());
    }

    @Test
    @DisplayName("findAll: tüm kayıtlar")
    void testFindAll() {
        repo.save(makeRecord(1, "Rex"));
        repo.save(makeRecord(2, "Boncuk"));
        assertEquals(2, repo.findAll().size());
    }

    @Test
    @DisplayName("findAll boş: empty liste")
    void testFindAllEmpty() {
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    @DisplayName("update: teşhis değişir")
    void testUpdate() {
        MedicalRecord r = makeRecord(1, "Rex");
        repo.save(r);
        r.setDiagnosis("Hafif ateş");
        assertTrue(repo.update(r));
        assertEquals("Hafif ateş", repo.findById(r.getId()).get().getDiagnosis());
    }

    @Test
    @DisplayName("update null: false")
    void testUpdateNull() {
        assertFalse(repo.update(null));
    }

    @Test
    @DisplayName("delete: kayıt silinir")
    void testDelete() {
        int id = repo.save(makeRecord(1, "Rex"));
        assertTrue(repo.delete(id));
        assertTrue(repo.findById(id).isEmpty());
    }

    @Test
    @DisplayName("delete olmayan: false")
    void testDeleteNotFound() {
        assertFalse(repo.delete(9999));
    }

    @Test
    @DisplayName("deleteAll: tümü silinir")
    void testDeleteAll() {
        repo.save(makeRecord(1, "Rex"));
        repo.deleteAll();
        assertEquals(0, repo.count());
    }

    @Test
    @DisplayName("count: doğru sayı")
    void testCount() {
        assertEquals(0, repo.count());
        repo.save(makeRecord(1, "Rex"));
        assertEquals(1, repo.count());
    }

    @Test
    @DisplayName("findByPetId: filtreler")
    void testFindByPetId() {
        repo.save(makeRecord(1, "Rex"));
        repo.save(makeRecord(2, "Boncuk"));
        List<MedicalRecord> list = repo.findByPetId(1);
        assertEquals(1, list.size());
        assertEquals("Rex", list.get(0).getPetName());
    }

    @Test
    @DisplayName("close: exception yok")
    void testClose() {
        assertDoesNotThrow(() -> repo.close());
    }

    @Test
    @DisplayName("maliyet ve aşı adı: kaydedilir")
    void testCostAndVaccine() {
        MedicalRecord r = makeRecord(1, "Rex");
        r.setCost(350.0);
        r.setVaccineName("Kuduz Aşısı");
        r.setNextCheckDate(LocalDate.of(2027, 1, 10));
        int id = repo.save(r);
        var found = repo.findById(id);
        assertTrue(found.isPresent());
        assertEquals(350.0, found.get().getCost(), 0.01);
        assertEquals("Kuduz Aşısı", found.get().getVaccineName());
        assertNotNull(found.get().getNextCheckDate());
    }

    @Test
    @DisplayName("null tarih alanları: kayıt yine de eklenir")
    void testNullDates() {
        MedicalRecord r = new MedicalRecord(0, 1, "Rex",
            LocalDate.now(), "KONTROL", "OK", "Vet");
        r.setNextCheckDate(null);
        int id = repo.save(r);
        assertTrue(id > 0);
    }


    @Test
    @DisplayName("null next_check_date: mapRow branch")
    void testNullNextCheckDateInMapRow() {
        MedicalRecord r = makeRecord(1, "Rex");
        r.setNextCheckDate(null);
        int id = repo.save(r);
        var found = repo.findById(id);
        assertTrue(found.isPresent());
        assertNull(found.get().getNextCheckDate());
    }

    // update — setUpdateParams tam coverage
    @Test
    @DisplayName("update: tam alan güncelleme")
    void testFullUpdate() {
        MedicalRecord r = makeRecord(1, "Rex");
        r.setTreatment("Antibiyotik");
        r.setNotes("İyileşiyor");
        r.setVaccineName("Kuduz");
        r.setCost(500.0);
        r.setNextCheckDate(LocalDate.of(2027, 6, 1));
        repo.save(r);
        r.setDiagnosis("Güncellenmiş");
        assertTrue(repo.update(r));
    }

    // update — null nextCheckDate setUpdateParams branch
    @Test
    @DisplayName("update: null next_check_date ile güncelleme")
    void testUpdateWithNullNextCheckDate() {
        MedicalRecord r = makeRecord(1, "Rex");
        r.setNextCheckDate(null);
        repo.save(r);
        r.setDiagnosis("Updated");
        assertTrue(repo.update(r));
    }

    // findByPetId exception — bağlantı kapalıyken çağrı
    @Test
    @DisplayName("findByPetId: kapalı bağlantı exception")
    void testFindByPetIdException() throws Exception {
        conn.close();
        assertThrows(RepositoryException.class, () -> repo.findByPetId(1));
    }
}
