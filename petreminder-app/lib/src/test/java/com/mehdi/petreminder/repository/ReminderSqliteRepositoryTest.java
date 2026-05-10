/**
 * @file ReminderSqliteRepositoryTest.java
 * @brief ReminderSqliteRepository JUnit5 testleri.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.model.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class ReminderSqliteRepositoryTest
 * @brief Reminder repository H2 in-memory testleri.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
@DisplayName("ReminderSqliteRepository Testleri")
class ReminderSqliteRepositoryTest {

    /** @brief H2 bağlantısı. */
    private Connection conn;

    /** @brief Test repo. */
    private ReminderSqliteRepository repo;

    /** @brief Sabit test zamanı. */
    private static final LocalDateTime T =
        LocalDateTime.of(2026, 12, 1, 10, 30);

    /**
     * @brief Her testten önce.
     * @throws Exception bağlantı hatası
     */
    @BeforeEach
    void setUp() throws Exception {
        conn = DriverManager.getConnection(
            "jdbc:h2:mem:remtest_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
        repo = new ReminderSqliteRepository(conn);
    }

    /**
     * @brief Her testten sonra.
     * @throws Exception kapatma hatası
     */
    @AfterEach
    void tearDown() throws Exception {
        if (conn != null && !conn.isClosed()) conn.close();
    }

    @Test
    @DisplayName("save FeedingReminder: ID üretilir")
    void testSaveFeeding() {
        FeedingReminder fr = new FeedingReminder(0, 1, "Rex", T, "Mama", 150);
        int id = repo.save(fr);
        assertTrue(id > 0);
        assertEquals(id, fr.getId());
    }

    @Test
    @DisplayName("save MedicationReminder")
    void testSaveMedication() {
        MedicationReminder mr = new MedicationReminder(0, 1, "Rex", T,
            "Antibiyotik", 250, "mg");
        assertTrue(repo.save(mr) > 0);
    }

    @Test
    @DisplayName("save ExerciseReminder")
    void testSaveExercise() {
        ExerciseReminder er = new ExerciseReminder(0, 1, "Rex", T, "Yürüyüş", 30);
        assertTrue(repo.save(er) > 0);
    }

    @Test
    @DisplayName("save GroomingReminder")
    void testSaveGrooming() {
        GroomingReminder gr = new GroomingReminder(0, 1, "Rex", T, "Tıraş", true);
        assertTrue(repo.save(gr) > 0);
    }

    @Test
    @DisplayName("save VetAppointment")
    void testSaveVet() {
        VetAppointment va = new VetAppointment(0, 1, "Rex", T, "Dr.Ali", "Klinik", "Kontrol");
        assertTrue(repo.save(va) > 0);
    }

    @Test
    @DisplayName("save null: RepositoryException")
    void testSaveNull() {
        assertThrows(RepositoryException.class, () -> repo.save(null));
    }

    @Test
    @DisplayName("findById: FeedingReminder geri döner")
    void testFindByIdFeeding() {
        FeedingReminder fr = new FeedingReminder(0, 1, "Rex", T, "Mama", 200);
        int id = repo.save(fr);
        var found = repo.findById(id);
        assertTrue(found.isPresent());
        assertEquals("Besleme", found.get().getReminderType());
    }

    @Test
    @DisplayName("findById: olmayan ID empty")
    void testFindByIdEmpty() {
        assertTrue(repo.findById(9999).isEmpty());
    }

    @Test
    @DisplayName("findAll: tüm reminder'lar listelenir")
    void testFindAll() {
        repo.save(new FeedingReminder(0, 1, "Rex", T, "Mama", 100));
        repo.save(new VetAppointment(0, 1, "Rex", T, "Vet", "Klinik", "Kontrol"));
        assertEquals(2, repo.findAll().size());
    }

    @Test
    @DisplayName("findAll: boş repo boş liste")
    void testFindAllEmpty() {
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    @DisplayName("update: completed flag değişir")
    void testUpdate() {
        FeedingReminder fr = new FeedingReminder(0, 1, "Rex", T, "Mama", 100);
        repo.save(fr);
        fr.setCompleted(true);
        assertTrue(repo.update(fr));
        assertTrue(repo.findById(fr.getId()).get().isCompleted());
    }

    @Test
    @DisplayName("update null: false döner")
    void testUpdateNull() {
        assertFalse(repo.update(null));
    }

    @Test
    @DisplayName("delete: reminder silinir")
    void testDelete() {
        FeedingReminder fr = new FeedingReminder(0, 1, "Rex", T, "Mama", 100);
        int id = repo.save(fr);
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
        repo.save(new FeedingReminder(0, 1, "Rex", T, "Mama", 100));
        repo.deleteAll();
        assertEquals(0, repo.count());
    }

    @Test
    @DisplayName("count: doğru sayı")
    void testCount() {
        assertEquals(0, repo.count());
        repo.save(new FeedingReminder(0, 1, "Rex", T, "Mama", 100));
        assertEquals(1, repo.count());
    }

    @Test
    @DisplayName("findByPetId: petId'ye göre filtreler")
    void testFindByPetId() {
        repo.save(new FeedingReminder(0, 1, "Rex", T, "Mama", 100));
        repo.save(new FeedingReminder(0, 2, "Boncuk", T, "Mama", 80));
        List<Reminder> list = repo.findByPetId(1);
        assertEquals(1, list.size());
        assertEquals("Rex", list.get(0).getPetName());
    }

    @Test
    @DisplayName("close: kaynaklar serbest bırakılır")
    void testClose() {
        assertDoesNotThrow(() -> repo.close());
    }

    @Test
    @DisplayName("getConnection: null değil")
    void testGetConnection() {
        assertNotNull(repo.getConnection());
    }

    @Test
    @DisplayName("MedicationReminder tam alanlar mapRow")
    void testMedicationMapRow() {
        MedicationReminder mr = new MedicationReminder(0, 1, "Rex", T,
            "desc", "YUKSEK", false, "", "Antibiyotik", 250, "mg", true, "Dr.Ali");
        int id = repo.save(mr);
        var found = repo.findById(id);
        assertTrue(found.isPresent());
        assertEquals("Ilac", found.get().getReminderType());
    }

    @Test
    @DisplayName("ExerciseReminder tam alanlar mapRow")
    void testExerciseMapRow() {
        ExerciseReminder er = new ExerciseReminder(0, 1, "Rex", T,
            "desc", "ORTA", true, "DAILY", "Koşu", 45, 3.5);
        int id = repo.save(er);
        var found = repo.findById(id);
        assertTrue(found.isPresent());
        ExerciseReminder back = (ExerciseReminder) found.get();
        assertEquals("Koşu", back.getExerciseType());
        assertEquals(45, back.getDurationMinutes());
    }

    @Test
    @DisplayName("GroomingReminder tam alanlar mapRow")
    void testGroomingMapRow() {
        GroomingReminder gr = new GroomingReminder(0, 1, "Rex", T,
            "desc", "DUSUK", false, "", "Banyo", true, "Güzel Kuaför");
        int id = repo.save(gr);
        var found = repo.findById(id);
        assertTrue(found.isPresent());
        assertEquals("Bakim", found.get().getReminderType());
    }

    @Test
    @DisplayName("VetAppointment tam alanlar mapRow")
    void testVetMapRow() {
        VetAppointment va = new VetAppointment(0, 1, "Rex", T,
            "desc", "YUKSEK", false, "",
            "Dr.Ali", "Rize Vet", "Kontrol", 150.0, true, "0464000000");
        int id = repo.save(va);
        var found = repo.findById(id);
        assertTrue(found.isPresent());
        VetAppointment back = (VetAppointment) found.get();
        assertEquals("Dr.Ali", back.getVeterinarianName());
        assertTrue(back.isConfirmed());
    }

    // bilinmeyen reminder_type default case — fallback FeedingReminder
    @Test
    @DisplayName("mapRow: bilinmeyen reminder_type default case")
    void testMapRowDefaultType() throws Exception {
        FeedingReminder fr = new FeedingReminder(0, 1, "Rex", T, "Mama", 100);
        int id = repo.save(fr);
        // reminder_type'ı SQL ile bilinmeyen bir değere güncelle
        conn.createStatement().executeUpdate(
            "UPDATE reminders SET reminder_type='Bilinmeyen' WHERE id=" + id);
        var found = repo.findById(id);
        assertTrue(found.isPresent());
        assertEquals("Besleme", found.get().getReminderType()); // default FeedingReminder
    }


    @Test
    @DisplayName("update FeedingReminder: setUpdateParams")
    void testUpdateFeeding() {
        FeedingReminder fr = new FeedingReminder(0, 1, "Rex", T, "Mama", 100);
        repo.save(fr);
        fr.setDescription("Updated");
        assertTrue(repo.update(fr));
    }

    // update MedicationReminder — setUpdateParams type branch coverage
    @Test
    @DisplayName("update MedicationReminder")
    void testUpdateMedication() {
        MedicationReminder mr = new MedicationReminder(0, 1, "Rex", T,
            "Antibiyotik", 250, "mg");
        repo.save(mr);
        mr.setDescription("Updated");
        assertTrue(repo.update(mr));
    }

    // update ExerciseReminder — setUpdateParams type branch coverage
    @Test
    @DisplayName("update ExerciseReminder")
    void testUpdateExercise() {
        ExerciseReminder er = new ExerciseReminder(0, 1, "Rex", T, "Yürüyüş", 30);
        repo.save(er);
        er.setDescription("Updated");
        assertTrue(repo.update(er));
    }

    // update GroomingReminder — setUpdateParams type branch coverage
    @Test
    @DisplayName("update GroomingReminder")
    void testUpdateGrooming() {
        GroomingReminder gr = new GroomingReminder(0, 1, "Rex", T, "Tıraş", true);
        repo.save(gr);
        gr.setDescription("Updated");
        assertTrue(repo.update(gr));
    }

    // update VetAppointment — setUpdateParams type branch coverage
    @Test
    @DisplayName("update VetAppointment")
    void testUpdateVet() {
        VetAppointment va = new VetAppointment(0, 1, "Rex", T, "Dr.Ali", "Klinik", "Kontrol");
        repo.save(va);
        va.setDescription("Updated");
        assertTrue(repo.update(va));
    }

    // findByPetId exception — bağlantı kapalıyken çağrı
    @Test
    @DisplayName("findByPetId: kapalı bağlantı exception")
    void testFindByPetIdException() throws Exception {
        conn.close();
        assertThrows(RepositoryException.class, () -> repo.findByPetId(1));
    }

    // findByOwnerId (Pet repo benzeri) exception — bağlantı kapalıyken çağrı
    @Test
    @DisplayName("findByPetId: boş sonuç")
    void testFindByPetIdEmpty() {
        var result = repo.findByPetId(9999);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
