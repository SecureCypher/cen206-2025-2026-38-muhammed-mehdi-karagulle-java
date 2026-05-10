/**
 * @file PetMySqlRepositoryTest.java
 * @brief PetMySqlRepository için H2 (MySQL-compat mode) unit testleri.
 * @details Docker gerektirmez; H2 in-memory database kullanır.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.model.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class PetMySqlRepositoryTest
 * @brief PetMySqlRepository CRUD testleri — H2 in-memory DB kullanır.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
class PetMySqlRepositoryTest {

    /** @brief Paylaşılan H2 bağlantısı. */
    private static Connection connection;
    /** @brief Test edilecek repository. */
    private PetMySqlRepository repo;

    /**
     * @brief H2 in-memory veritabanı bağlantısı açar.
     * @throws SQLException Bağlantı hatası
     */
    @BeforeAll
    static void setUpClass() throws SQLException {
        // H2 MySQL-compat mode — AUTO_INCREMENT destekler
        connection = DriverManager.getConnection(
            "jdbc:h2:mem:petmysql;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
    }

    /**
     * @brief Her testten önce temiz tablo oluşturur.
     * @throws SQLException SQL hatası
     */
    @BeforeEach
    void setUp() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS pets");
        }
        repo = new PetMySqlRepository(connection);
    }

    /**
     * @brief Veritabanı bağlantısını kapatır.
     * @throws SQLException Kapatma hatası
     */
    @AfterAll
    static void tearDownClass() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // ── save / findById ──────────────────────────────────────────────

    /**
     * @test Dog kaydedilip ID ile bulunabilmeli.
     */
    @Test
    void testSaveAndFindDog() {
        Dog dog = new Dog(0, "Buddy", LocalDate.of(2020, 1, 1), 1);
        dog.setBreed("Labrador");
        dog.setTrained(true);
        int id = repo.save(dog);
        assertTrue(id > 0);

        Optional<Pet> found = repo.findById(id);
        assertTrue(found.isPresent());
        assertEquals("Buddy", found.get().getName());
    }

    /**
     * @test Cat kaydedilip geri alınabilmeli.
     */
    @Test
    void testSaveAndFindCat() {
        Cat cat = new Cat(0, "Whiskers", LocalDate.of(2021, 5, 10), 1);
        cat.setIndoor(true);
        int id = repo.save(cat);
        Optional<Pet> found = repo.findById(id);
        assertTrue(found.isPresent());
        assertEquals("Whiskers", found.get().getName());
    }

    /**
     * @test Bird kaydedilip geri alınabilmeli.
     */
    @Test
    void testSaveAndFindBird() {
        Bird bird = new Bird(0, "Tweety", LocalDate.of(2022, 3, 15), 1);
        bird.setBirdType("Papağan");
        bird.setCanTalk(true);
        int id = repo.save(bird);
        Optional<Pet> found = repo.findById(id);
        assertTrue(found.isPresent());
        assertInstanceOf(Bird.class, found.get());
        assertEquals("Papağan", ((Bird) found.get()).getBirdType());
    }

    /**
     * @test null entity kaydedilmeye çalışılırsa exception fırlatılmalı.
     */
    @Test
    void testSaveNullThrows() {
        assertThrows(RepositoryException.class, () -> repo.save(null));
    }

    // ── findAll ──────────────────────────────────────────────────────

    /**
     * @test findAll tüm kayıtları döndürmeli.
     */
    @Test
    void testFindAll() {
        repo.save(new Dog(0, "Rex", LocalDate.now(), 1));
        repo.save(new Cat(0, "Mimi", LocalDate.now(), 1));
        List<Pet> all = repo.findAll();
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
     * @test Kayıt güncelleme çalışmalı.
     */
    @Test
    void testUpdate() {
        Dog dog = new Dog(0, "Old Name", LocalDate.now(), 1);
        int id = repo.save(dog);
        dog.setId(id);
        dog.setName("New Name");
        assertTrue(repo.update(dog));
        assertEquals("New Name", repo.findById(id).get().getName());
    }

    /**
     * @test null ile update false döndürmeli.
     */
    @Test
    void testUpdateNull() {
        assertFalse(repo.update(null));
    }

    // ── delete ───────────────────────────────────────────────────────

    /**
     * @test Kayıt silinebilmeli.
     */
    @Test
    void testDelete() {
        int id = repo.save(new Dog(0, "ToDelete", LocalDate.now(), 1));
        assertTrue(repo.delete(id));
        assertFalse(repo.findById(id).isPresent());
    }

    /**
     * @test Olmayan ID ile delete false döndürmeli.
     */
    @Test
    void testDeleteNonExistent() {
        assertFalse(repo.delete(9999));
    }

    // ── count / deleteAll ─────────────────────────────────────────────

    /**
     * @test count doğru sayıyı döndürmeli.
     */
    @Test
    void testCount() {
        repo.save(new Dog(0, "A", LocalDate.now(), 1));
        repo.save(new Cat(0, "B", LocalDate.now(), 1));
        assertEquals(2, repo.count());
    }

    /**
     * @test deleteAll tüm kayıtları silmeli.
     */
    @Test
    void testDeleteAll() {
        repo.save(new Dog(0, "A", LocalDate.now(), 1));
        repo.deleteAll();
        assertEquals(0, repo.count());
    }

    // ── findByOwnerId ─────────────────────────────────────────────────

    /**
     * @test findByOwnerId doğru sahibin petlerini döndürmeli.
     */
    @Test
    void testFindByOwnerId() {
        repo.save(new Dog(0, "OwnerPet", LocalDate.now(), 42));
        repo.save(new Cat(0, "OtherPet", LocalDate.now(), 99));
        List<Pet> ownerPets = repo.findByOwnerId(42);
        assertEquals(1, ownerPets.size());
        assertEquals("OwnerPet", ownerPets.get(0).getName());
    }

    // ── getConnection / close ─────────────────────────────────────────

    /**
     * @test getConnection null olmamalı.
     */
    @Test
    void testGetConnection() {
        assertNotNull(repo.getConnection());
    }

    /**
     * @test findById olmayan ID için empty döndürmeli.
     */
    @Test
    void testFindByIdNotFound() {
        assertTrue(repo.findById(999).isEmpty());
    }
}
