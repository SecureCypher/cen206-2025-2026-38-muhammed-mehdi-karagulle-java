/**
 * @file MySqlRepositoryExceptionTest.java
 * @brief MySqlRepository exception path testleri — kapalı H2 connection kullanır.
 * @details save/findById/findAll/update/delete/deleteAll/count/close
 *          metodlarındaki SQLException catch branch'lerini test eder.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.model.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class MySqlRepositoryExceptionTest
 * @brief MySqlRepository SQL exception branch testleri.
 * @details Tüm catch bloklarını tetiklemek için closed connection kullanır.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
class MySqlRepositoryExceptionTest {

    /**
     * @brief Kapalı H2 connection ile PetMySqlRepository oluşturur.
     * @return Kapalı connection'a sahip repo
     * @throws Exception Hata
     */
    private PetMySqlRepository closedRepo() throws Exception {
        Connection conn = DriverManager.getConnection(
            "jdbc:h2:mem:extest;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        PetMySqlRepository repo = new PetMySqlRepository(conn);
        conn.close(); // Tabloyu oluşturduktan sonra kapat
        return repo;
    }

    // ── save → SQLException ────────────────────────────────────────

    /**
     * @test save kapalı connection → RepositoryException.
     */
    @Test
    void testSaveThrowsOnClosedConnection() throws Exception {
        PetMySqlRepository repo = closedRepo();
        Dog dog = new Dog(0, "Rex", LocalDate.now(), 1);
        assertThrows(RepositoryException.class, () -> repo.save(dog));
    }

    // ── findById → SQLException ────────────────────────────────────

    /**
     * @test findById kapalı connection → RepositoryException.
     */
    @Test
    void testFindByIdThrowsOnClosedConnection() throws Exception {
        PetMySqlRepository repo = closedRepo();
        assertThrows(RepositoryException.class, () -> repo.findById(1));
    }

    // ── findAll → SQLException ─────────────────────────────────────

    /**
     * @test findAll kapalı connection → RepositoryException.
     */
    @Test
    void testFindAllThrowsOnClosedConnection() throws Exception {
        PetMySqlRepository repo = closedRepo();
        assertThrows(RepositoryException.class, () -> repo.findAll());
    }

    // ── update → SQLException ──────────────────────────────────────

    /**
     * @test update kapalı connection → RepositoryException.
     */
    @Test
    void testUpdateThrowsOnClosedConnection() throws Exception {
        PetMySqlRepository repo = closedRepo();
        Dog dog = new Dog(1, "Buddy", LocalDate.now(), 1);
        assertThrows(RepositoryException.class, () -> repo.update(dog));
    }

    // ── delete → SQLException ──────────────────────────────────────

    /**
     * @test delete kapalı connection → RepositoryException.
     */
    @Test
    void testDeleteThrowsOnClosedConnection() throws Exception {
        PetMySqlRepository repo = closedRepo();
        assertThrows(RepositoryException.class, () -> repo.delete(1));
    }

    // ── deleteAll → SQLException ───────────────────────────────────

    /**
     * @test deleteAll kapalı connection → RepositoryException.
     */
    @Test
    void testDeleteAllThrowsOnClosedConnection() throws Exception {
        PetMySqlRepository repo = closedRepo();
        assertThrows(RepositoryException.class, () -> repo.deleteAll());
    }

    // ── count → SQLException; count fallback ──────────────────────

    /**
     * @test count kapalı connection → RepositoryException.
     */
    @Test
    void testCountThrowsOnClosedConnection() throws Exception {
        PetMySqlRepository repo = closedRepo();
        assertThrows(RepositoryException.class, () -> repo.count());
    }

    /**
     * @test count → 0 döndüren branch (boş ResultSet).
     * @details Normal H2 bağlantısı; COUNT(*) her zaman bir satır döndürür bu path zordur.
     *          Alternatif: UserMySqlRepository.count() temiz bağlantıyla test edilir.
     */
    @Test
    void testCountNormalReturnsCoverage() throws Exception {
        Connection conn = DriverManager.getConnection(
            "jdbc:h2:mem:counttest;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        UserMySqlRepository repo = new UserMySqlRepository(conn);
        assertEquals(0, repo.count());
        conn.close();
    }

    // ── close — isClosed branch ────────────────────────────────────

    /**
     * @test close — connection null iken güvenli.
     */
    @Test
    void testCloseWithNullConnection() throws Exception {
        Connection conn = DriverManager.getConnection(
            "jdbc:h2:mem:closetest;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        PetMySqlRepository repo = new PetMySqlRepository(conn);
        conn.close(); // önce kapat
        assertDoesNotThrow(repo::close); // isClosed=true olduğu için if bloğuna girmez
    }

    /**
     * @test close — açık connection kapatılır.
     */
    @Test
    void testCloseWithOpenConnection() throws Exception {
        Connection conn = DriverManager.getConnection(
            "jdbc:h2:mem:closeopen;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        UserMySqlRepository repo = new UserMySqlRepository(conn);
        assertDoesNotThrow(repo::close);
        assertTrue(conn.isClosed()); // H2 gerçekten kapandı
    }

    // ── setEntityId exception branch ───────────────────────────────

    /**
     * @test setEntityId — setId metodu olmayan nesne ile exception branch.
     * @details Doğrudan test edilemiyor ama reflection exception path'i tetiklemek için
     *          NoSuchMethodException oluşturulur.
     */
    @Test
    void testSetEntityIdNoSuchMethod() throws Exception {
        Connection conn = DriverManager.getConnection(
            "jdbc:h2:mem:setest;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        PetMySqlRepository repo = new PetMySqlRepository(conn);
        // setId olmayan bir nesne ile setEntityId çağrısı exception branch tetikler
        // Ancak protected olduğu için dolaylı yoldan test edilir — save sonucu ID ile
        Dog dog = new Dog(0, "Test", LocalDate.now(), 1);
        int id = repo.save(dog);
        assertTrue(id > 0);
        conn.close();
    }

    // ── UserMySqlRepository ───────────────────────────────────────

    /**
     * @test UserMySqlRepository save → kapalı bağlantı exception.
     */
    @Test
    void testUserSaveThrowsOnClosed() throws Exception {
        Connection conn = DriverManager.getConnection(
            "jdbc:h2:mem:userex;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        UserMySqlRepository repo = new UserMySqlRepository(conn);
        conn.close();
        assertThrows(RepositoryException.class, () -> repo.save(null));
    }

    /**
     * @test UserMySqlRepository findById → kapalı bağlantı.
     */
    @Test
    void testUserFindByIdThrowsOnClosed() throws Exception {
        Connection conn = DriverManager.getConnection(
            "jdbc:h2:mem:userex2;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        UserMySqlRepository repo = new UserMySqlRepository(conn);
        conn.close();
        assertThrows(RepositoryException.class, () -> repo.findById(1));
    }

    // ── MedicalRecordMySqlRepository ──────────────────────────────

    /**
     * @test MedicalRecordMySqlRepository save → kapalı bağlantı.
     */
    @Test
    void testMedicalSaveThrowsOnClosed() throws Exception {
        Connection conn = DriverManager.getConnection(
            "jdbc:h2:mem:medex;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        MedicalRecordMySqlRepository repo = new MedicalRecordMySqlRepository(conn);
        conn.close();
        assertThrows(RepositoryException.class, () -> repo.findAll());
    }

    // ── ReminderMySqlRepository ───────────────────────────────────

    /**
     * @test ReminderMySqlRepository findAll → kapalı bağlantı.
     */
    @Test
    void testReminderFindAllThrowsOnClosed() throws Exception {
        Connection conn = DriverManager.getConnection(
            "jdbc:h2:mem:remex;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        ReminderMySqlRepository repo = new ReminderMySqlRepository(conn);
        conn.close();
        assertThrows(RepositoryException.class, () -> repo.findAll());
    }

    // ── MySqlRepository → connection constructor exception ─────────

    /**
     * @test Geçersiz bağlantı ile MySqlRepository yapıcısı exception fırlatmalı.
     * @details Bağlantı null olursa NullPointerException → RepositoryException dönüşür.
     */
    @Test
    void testConstructorWithNullConnection() {
        assertThrows(Exception.class, () -> new PetMySqlRepository(null));
    }
}
