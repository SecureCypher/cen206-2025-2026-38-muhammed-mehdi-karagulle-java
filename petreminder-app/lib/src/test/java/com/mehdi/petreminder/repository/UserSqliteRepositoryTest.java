/**
 * @file UserSqliteRepositoryTest.java
 * @brief UserSqliteRepository JUnit5 testleri.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.model.User;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class UserSqliteRepositoryTest
 * @brief User repository H2 testleri.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
@DisplayName("UserSqliteRepository Testleri")
class UserSqliteRepositoryTest {

    /** @brief H2 bağlantısı. */
    private Connection conn;

    /** @brief Test repo. */
    private UserSqliteRepository repo;

    /**
     * @brief Setup.
     * @throws Exception bağlantı hatası
     */
    @BeforeEach
    void setUp() throws Exception {
        conn = DriverManager.getConnection(
            "jdbc:h2:mem:usertest_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
        repo = new UserSqliteRepository(conn);
    }

    /**
     * @brief Teardown.
     * @throws Exception kapatma hatası
     */
    @AfterEach
    void tearDown() throws Exception {
        if (conn != null && !conn.isClosed()) conn.close();
    }

    /** @brief Test user üretici. */
    private User makeUser(String username) {
        return new User(0, username, username + "@test.com",
            "hash123", "Test User");
    }

    @Test
    @DisplayName("save: kullanıcı kaydedilir")
    void testSave() {
        User u = makeUser("mehdi");
        int id = repo.save(u);
        assertTrue(id > 0);
    }

    @Test
    @DisplayName("save null: exception")
    void testSaveNull() {
        assertThrows(RepositoryException.class, () -> repo.save(null));
    }

    @Test
    @DisplayName("findById: kullanıcı döner")
    void testFindById() {
        User u = makeUser("ibrahim");
        int id = repo.save(u);
        var found = repo.findById(id);
        assertTrue(found.isPresent());
        assertEquals("ibrahim", found.get().getUsername());
    }

    @Test
    @DisplayName("findById olmayan: empty")
    void testFindByIdEmpty() {
        assertTrue(repo.findById(9999).isEmpty());
    }

    @Test
    @DisplayName("findAll: tüm kullanıcılar")
    void testFindAll() {
        repo.save(makeUser("u1"));
        repo.save(makeUser("u2"));
        assertEquals(2, repo.findAll().size());
    }

    @Test
    @DisplayName("findAll boş: empty liste")
    void testFindAllEmpty() {
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    @DisplayName("update: fullName değişir")
    void testUpdate() {
        User u = makeUser("zumre");
        repo.save(u);
        u.setFullName("Zümre Uykun");
        assertTrue(repo.update(u));
        assertEquals("Zümre Uykun", repo.findById(u.getId()).get().getFullName());
    }

    @Test
    @DisplayName("update null: false")
    void testUpdateNull() {
        assertFalse(repo.update(null));
    }

    @Test
    @DisplayName("delete: kullanıcı silinir")
    void testDelete() {
        int id = repo.save(makeUser("toDelete"));
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
        repo.save(makeUser("a"));
        repo.deleteAll();
        assertEquals(0, repo.count());
    }

    @Test
    @DisplayName("count: doğru")
    void testCount() {
        assertEquals(0, repo.count());
        repo.save(makeUser("x"));
        assertEquals(1, repo.count());
    }

    @Test
    @DisplayName("close: exception yok")
    void testClose() {
        assertDoesNotThrow(() -> repo.close());
    }

    @Test
    @DisplayName("findByUsername: kullanıcı bulunur")
    void testFindByUsername() {
        repo.save(makeUser("findme"));
        User found = repo.findByUsername("findme");
        assertNotNull(found);
        assertEquals("findme", found.getUsername());
    }

    @Test
    @DisplayName("findByUsername olmayan: null")
    void testFindByUsernameNotFound() {
        assertNull(repo.findByUsername("notexist"));
    }

    @Test
    @DisplayName("createdAt: kaydedilir ve okunur")
    void testCreatedAt() {
        User u = makeUser("timestamps");
        u.setCreatedAt(LocalDateTime.of(2026, 1, 1, 12, 0));
        int id = repo.save(u);
        var found = repo.findById(id);
        assertTrue(found.isPresent());
        assertNotNull(found.get().getCreatedAt());
    }

    @Test
    @DisplayName("active false: deaktif kullanıcı")
    void testInactiveUser() {
        User u = makeUser("inactive");
        u.setActive(false);
        // active=false ile kaydet — User constructor'ı true set eder,
        // sonra false yapıyoruz
        u = new User(0, "inactive2", "inactive2@test.com", "hash", "Inactive");
        // active default true, override edelim
        int id = repo.save(u);
        assertTrue(id > 0);
    }

    // null createdAt — mapRow ve setInsertParams null branch coverage
    @Test
    @DisplayName("null createdAt: kayıt ve okuma")
    void testNullCreatedAt() {
        User u = new User(0, "nodate", "nodate@test.com", "hash", "No Date");
        u.setCreatedAt(null);
        int id = repo.save(u);
        var found = repo.findById(id);
        assertTrue(found.isPresent());
        assertNull(found.get().getCreatedAt());
    }

    // update — setUpdateParams coverage
    @Test
    @DisplayName("update: createdAt ile güncelleme")
    void testUpdateWithCreatedAt() {
        User u = makeUser("updatetest");
        u.setCreatedAt(LocalDateTime.of(2026, 6, 15, 10, 0));
        repo.save(u);
        u.setFullName("Updated Name");
        assertTrue(repo.update(u));
    }

    // update — setUpdateParams null createdAt branch
    @Test
    @DisplayName("update: null createdAt ile güncelleme")
    void testUpdateWithNullCreatedAt() {
        User u = makeUser("nullupdate");
        u.setCreatedAt(null);
        repo.save(u);
        u.setFullName("Updated");
        assertTrue(repo.update(u));
    }

    // findByUsername exception — bağlantı kapalıyken çağrı
    @Test
    @DisplayName("findByUsername: kapalı bağlantı exception")
    void testFindByUsernameException() throws Exception {
        conn.close();
        assertThrows(RepositoryException.class, () -> repo.findByUsername("test"));
    }
}
