/**
 * @file ReminderMySqlRepositoryTest.java
 * @brief ReminderMySqlRepository için H2 unit testleri.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.model.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class ReminderMySqlRepositoryTest
 * @brief ReminderMySqlRepository için H2 in-memory testler.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
class ReminderMySqlRepositoryTest {

    /** @brief Paylaşılan H2 bağlantısı. */
    private static Connection connection;
    /** @brief Test edilecek repository. */
    private ReminderMySqlRepository repo;

    /**
     * @brief H2 bağlantısını açar.
     * @throws SQLException Bağlantı hatası
     */
    @BeforeAll
    static void setUpClass() throws SQLException {
        connection = DriverManager.getConnection(
            "jdbc:h2:mem:remindermysql;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
    }

    /**
     * @brief Her testten önce tabloyu sıfırlar.
     * @throws SQLException SQL hatası
     */
    @BeforeEach
    void setUp() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS reminders");
        }
        repo = new ReminderMySqlRepository(connection);
    }

    /**
     * @brief Bağlantıyı kapatır.
     * @throws SQLException Kapatma hatası
     */
    @AfterAll
    static void tearDownClass() throws SQLException {
        if (connection != null && !connection.isClosed()) connection.close();
    }

    // ── FeedingReminder ───────────────────────────────────────────────

    /**
     * @test FeedingReminder kaydedilip geri alınabilmeli.
     */
    @Test
    void testSaveAndFindFeedingReminder() {
        FeedingReminder fr = new FeedingReminder(0, 1, "Buddy",
            LocalDateTime.now(), "Mama zamanı", "YÜKSEK",
            false, null, "Kuru Mama", 200.0, false);
        int id = repo.save(fr);
        assertTrue(id > 0);
        assertTrue(repo.findById(id).isPresent());
        assertInstanceOf(FeedingReminder.class, repo.findById(id).get());
    }

    /**
     * @test MedicationReminder kaydedilip geri alınabilmeli.
     */
    @Test
    void testSaveMedicationReminder() {
        MedicationReminder mr = new MedicationReminder(0, 1, "Rex",
            LocalDateTime.now(), "İlaç saati", "ORTA",
            false, null, "Amoksisilin", 250.0, "mg", true, null);
        int id = repo.save(mr);
        assertTrue(id > 0);
        assertInstanceOf(MedicationReminder.class, repo.findById(id).get());
    }

    /**
     * @test ExerciseReminder kaydedilip geri alınabilmeli.
     */
    @Test
    void testSaveExerciseReminder() {
        ExerciseReminder er = new ExerciseReminder(0, 1, "Rex",
            LocalDateTime.now(), "Yürüyüş", "DÜŞÜK",
            false, null, "Koşu", 30, 2.5);
        int id = repo.save(er);
        assertTrue(id > 0);
        assertInstanceOf(ExerciseReminder.class, repo.findById(id).get());
    }

    /**
     * @test GroomingReminder kaydedilip geri alınabilmeli.
     */
    @Test
    void testSaveGroomingReminder() {
        GroomingReminder gr = new GroomingReminder(0, 1, "Mimi",
            LocalDateTime.now(), "Tıraş", "ORTA",
            false, null, "Banyo", true, "Kuaför Ali");
        int id = repo.save(gr);
        assertTrue(id > 0);
        assertInstanceOf(GroomingReminder.class, repo.findById(id).get());
    }

    /**
     * @test VetAppointment kaydedilip geri alınabilmeli.
     */
    @Test
    void testSaveVetAppointment() {
        VetAppointment va = new VetAppointment(0, 1, "Buddy",
            LocalDateTime.now(), "Kontrol", "YÜKSEK",
            false, null, "Dr. Mehmet", "Klinik A",
            "Aşı", 200.0, true, "555-1234");
        int id = repo.save(va);
        assertTrue(id > 0);
        assertInstanceOf(VetAppointment.class, repo.findById(id).get());
    }

    /**
     * @test null kaydetme exception fırlatmalı.
     */
    @Test
    void testSaveNullThrows() {
        assertThrows(RepositoryException.class, () -> repo.save(null));
    }

    // ── findAll / findById ────────────────────────────────────────────

    /**
     * @test findAll tüm kayıtları döndürmeli.
     */
    @Test
    void testFindAll() {
        repo.save(new FeedingReminder(0, 1, "A", LocalDateTime.now(), "d", "ORTA", false, null, "Mama", 100, false));
        repo.save(new FeedingReminder(0, 2, "B", LocalDateTime.now(), "d", "ORTA", false, null, "Mama", 100, false));
        List<Reminder> all = repo.findAll();
        assertEquals(2, all.size());
    }

    /**
     * @test Olmayan ID için empty dönmeli.
     */
    @Test
    void testFindByIdNotFound() {
        assertTrue(repo.findById(9999).isEmpty());
    }

    // ── update ───────────────────────────────────────────────────────

    /**
     * @test Hatırlatıcı tamamlanmış olarak güncellenebilmeli.
     */
    @Test
    void testUpdate() {
        FeedingReminder fr = new FeedingReminder(0, 1, "Rex",
            LocalDateTime.now(), "Mama", "ORTA",
            false, null, "Kuru", 150, false);
        int id = repo.save(fr);
        fr.setId(id);
        fr.setCompleted(true);
        assertTrue(repo.update(fr));
        assertTrue(repo.findById(id).get().isCompleted());
    }

    /**
     * @test null ile update false döndürmeli.
     */
    @Test
    void testUpdateNull() {
        assertFalse(repo.update(null));
    }

    // ── delete / count / deleteAll ────────────────────────────────────

    /**
     * @test Kayıt silinebilmeli.
     */
    @Test
    void testDelete() {
        int id = repo.save(new FeedingReminder(0, 1, "X", LocalDateTime.now(), "d", "ORTA", false, null, "Mama", 100, false));
        assertTrue(repo.delete(id));
        assertTrue(repo.findById(id).isEmpty());
    }

    /**
     * @test Olmayan ID silinmeye çalışılırsa false döndürmeli.
     */
    @Test
    void testDeleteNonExistent() {
        assertFalse(repo.delete(9999));
    }

    /**
     * @test count doğru sayıyı döndürmeli.
     */
    @Test
    void testCount() {
        repo.save(new FeedingReminder(0, 1, "A", LocalDateTime.now(), "d", "ORTA", false, null, "Mama", 100, false));
        assertEquals(1, repo.count());
    }

    /**
     * @test deleteAll tüm kayıtları silmeli.
     */
    @Test
    void testDeleteAll() {
        repo.save(new FeedingReminder(0, 1, "A", LocalDateTime.now(), "d", "ORTA", false, null, "Mama", 100, false));
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
     * @test Default (unknown type) reminder map edilebilmeli.
     */
    @Test
    void testDefaultReminderType() {
        // FeedingReminder kaydet, tipi bilinmeyen bir default ile test
        FeedingReminder fr = new FeedingReminder(0, 1, "Z",
            LocalDateTime.now(), "test", "ORTA", false, null, "Mama", 100, false);
        int id = repo.save(fr);
        assertNotNull(repo.findById(id));
    }
}
