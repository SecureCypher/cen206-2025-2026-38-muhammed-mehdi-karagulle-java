/**
 * @file UserMySqlRepositoryTest.java
 * @brief UserMySqlRepository için H2 unit testleri.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.model.User;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class UserMySqlRepositoryTest
 * @brief UserMySqlRepository için H2 in-memory testleri.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
class UserMySqlRepositoryTest {

    /** @brief Paylaşılan H2 bağlantısı. */
    private static Connection connection;
    /** @brief Test edilecek repository. */
    private UserMySqlRepository repo;

    /**
     * @brief H2 bağlantısını açar.
     * @throws SQLException Bağlantı hatası
     */
    @BeforeAll
    static void setUpClass() throws SQLException {
        connection = DriverManager.getConnection(
            "jdbc:h2:mem:usermysql;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
    }

    /**
     * @brief Her testten önce tabloyu sıfırlar.
     * @throws SQLException SQL hatası
     */
    @BeforeEach
    void setUp() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS users");
        }
        repo = new UserMySqlRepository(connection);
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
     * @brief Yardımcı: test kullanıcısı oluşturur.
     * @param username Kullanıcı adı
     * @return Oluşturulan User
     */
    private User makeUser(String username) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(username + "@test.com");
        u.setPasswordHash("hash123");
        u.setFullName("Test User");
        u.setCreatedAt(LocalDateTime.now());
        u.setActive(true);
        return u;
    }

    // ── save / findById ──────────────────────────────────────────────

    /**
     * @test Kullanıcı kaydedilip ID ile bulunabilmeli.
     */
    @Test
    void testSaveAndFind() {
        User u = makeUser("mehdi");
        int id = repo.save(u);
        assertTrue(id > 0);
        Optional<User> found = repo.findById(id);
        assertTrue(found.isPresent());
        assertEquals("mehdi", found.get().getUsername());
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
     * @test findAll tüm kullanıcıları döndürmeli.
     */
    @Test
    void testFindAll() {
        repo.save(makeUser("user1"));
        repo.save(makeUser("user2"));
        List<User> all = repo.findAll();
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
     * @test Kullanıcı güncellenebilmeli.
     */
    @Test
    void testUpdate() {
        User u = makeUser("original");
        int id = repo.save(u);
        u.setId(id);
        u.setFullName("Updated Name");
        assertTrue(repo.update(u));
        assertEquals("Updated Name", repo.findById(id).get().getFullName());
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
     * @test Kullanıcı silinebilmeli.
     */
    @Test
    void testDelete() {
        int id = repo.save(makeUser("todelete"));
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
     * @test count kayıt sayısını döndürmeli.
     */
    @Test
    void testCount() {
        repo.save(makeUser("a"));
        repo.save(makeUser("b"));
        assertEquals(2, repo.count());
    }

    /**
     * @test deleteAll tüm kayıtları silmeli.
     */
    @Test
    void testDeleteAll() {
        repo.save(makeUser("x"));
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
}
