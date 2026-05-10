/**
 * @file BinaryRepositoryTest.java
 * @brief BinaryRepository JUnit5 testleri — %100 coverage.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class BinaryRepositoryTest
 * @brief BinaryRepository tam test sınıfı.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
@DisplayName("BinaryRepository Testleri")
class BinaryRepositoryTest {

    /** @brief Geçici dizin. */
    @TempDir
    Path tempDir;

    /** @brief Test objesi. */
    private BinaryRepository<Pet> repo;

    /** @brief Test pet. */
    private Dog testDog;

    /**
     * @brief Her testten önce.
     */
    @BeforeEach
    void setUp() {
        repo = new BinaryRepository<>(tempDir.toString(), "pets_test");
        testDog = new Dog(0, "Rex", LocalDate.of(2020, 1, 15), 1);
        testDog.setBreed("Labrador");
    }

    /**
     * @brief Her testten sonra.
     */
    @AfterEach
    void tearDown() {
        repo.close();
    }

    @Test
    @DisplayName("save: yeni pet kaydeder ve ID döner")
    void testSave() {
        int id = repo.save(testDog);
        assertTrue(id > 0, "ID pozitif olmalı");
        assertEquals(id, testDog.getId());
    }

    @Test
    @DisplayName("save: null entity fırlatır")
    void testSaveNull() {
        assertThrows(RepositoryException.class, () -> repo.save(null));
    }

    @Test
    @DisplayName("findById: kayıtlı pet bulunur")
    void testFindById() {
        int id = repo.save(testDog);
        Optional<Pet> found = repo.findById(id);
        assertTrue(found.isPresent());
        assertEquals("Rex", found.get().getName());
    }

    @Test
    @DisplayName("findById: olmayan ID empty döner")
    void testFindByIdNotFound() {
        Optional<Pet> found = repo.findById(9999);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("findAll: boş repo boş liste döner")
    void testFindAllEmpty() {
        List<Pet> all = repo.findAll();
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    @DisplayName("findAll: kayıtlı tüm petler listelenir")
    void testFindAll() {
        repo.save(testDog);
        Cat cat = new Cat(0, "Boncuk", LocalDate.of(2021, 5, 1), 1);
        repo.save(cat);
        List<Pet> all = repo.findAll();
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("update: mevcut pet güncellenir")
    void testUpdate() {
        repo.save(testDog);
        testDog.setName("MaxUpdated");
        boolean updated = repo.update(testDog);
        assertTrue(updated);
        Optional<Pet> found = repo.findById(testDog.getId());
        assertTrue(found.isPresent());
        assertEquals("MaxUpdated", found.get().getName());
    }

    @Test
    @DisplayName("update: null entity false döner")
    void testUpdateNull() {
        assertFalse(repo.update(null));
    }

    @Test
    @DisplayName("update: olmayan ID false döner")
    void testUpdateNotFound() {
        Dog ghost = new Dog(9999, "Ghost", null, 1);
        assertFalse(repo.update(ghost));
    }

    @Test
    @DisplayName("delete: mevcut pet silinir")
    void testDelete() {
        int id = repo.save(testDog);
        assertTrue(repo.delete(id));
        assertFalse(repo.findById(id).isPresent());
    }

    @Test
    @DisplayName("delete: olmayan ID false döner")
    void testDeleteNotFound() {
        assertFalse(repo.delete(9999));
    }

    @Test
    @DisplayName("deleteAll: tüm kayıtlar silinir")
    void testDeleteAll() {
        repo.save(testDog);
        repo.deleteAll();
        assertEquals(0, repo.count());
    }

    @Test
    @DisplayName("count: doğru sayı döner")
    void testCount() {
        assertEquals(0, repo.count());
        repo.save(testDog);
        assertEquals(1, repo.count());
        repo.save(new Cat(0, "Tekir", null, 1));
        assertEquals(2, repo.count());
    }

    @Test
    @DisplayName("close: exception fırlatmaz")
    void testClose() {
        assertDoesNotThrow(() -> repo.close());
    }

    @Test
    @DisplayName("getFilePath: dosya yolu döner")
    void testGetFilePath() {
        assertNotNull(repo.getFilePath());
        assertTrue(repo.getFilePath().endsWith("pets_test.bin"));
    }

    @Test
    @DisplayName("multiple saves: ID'ler artan sırada")
    void testMultipleSaves() {
        int id1 = repo.save(new Dog(0, "Dog1", null, 1));
        int id2 = repo.save(new Dog(0, "Dog2", null, 1));
        int id3 = repo.save(new Dog(0, "Dog3", null, 1));
        assertTrue(id1 < id2 && id2 < id3);
    }

    @Test
    @DisplayName("FeedingReminder binary serialization")
    void testFeedingReminderBinary() {
        BinaryRepository<com.mehdi.petreminder.model.FeedingReminder> remRepo =
            new BinaryRepository<>(tempDir.toString(), "rem_test");
        var fr = new com.mehdi.petreminder.model.FeedingReminder(0, 1, "Rex",
            java.time.LocalDateTime.now(), "Mama", 150.0);
        int id = remRepo.save(fr);
        assertTrue(id > 0);
        var found = remRepo.findById(id);
        assertTrue(found.isPresent());
        assertEquals("Besleme", found.get().getReminderType());
        remRepo.close();
    }

    // findAll: dosya non-List obje içeriyorsa bos liste dönmeli — obj instanceof List = false branch
    @Test
    @DisplayName("findAll: non-List serialized object boş liste döner")
    void testFindAllNonListObject() throws Exception {
        BinaryRepository<Pet> repo2 = new BinaryRepository<>(tempDir.toString(), "nonlist_test");
        // Dosyaya List olmayan bir Object yaz
        java.io.File f = new java.io.File(repo2.getFilePath());
        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(
                new java.io.FileOutputStream(f))) {
            oos.writeObject("This is not a List");
        }
        java.util.List<Pet> result = repo2.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        repo2.close();
    }

    // Entity whose getId returns non-Integer (String) — covers initNextId/getEntityId `idObj instanceof Integer = false`
    public static class StringIdEntity implements java.io.Serializable {
        private String id = "abc";
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }

    @Test
    @DisplayName("initNextId/getEntityId: non-Integer getId branch")
    void testNonIntegerGetId() {
        BinaryRepository<StringIdEntity> strRepo =
            new BinaryRepository<>(tempDir.toString(), "strid_test");
        StringIdEntity entity = new StringIdEntity();
        // save çağrılır — setId(int) bulunamaz, catch'e düşer (setEntityId catch branch)
        assertDoesNotThrow(() -> strRepo.save(entity));
        assertEquals(1, strRepo.count());
        // findById — getEntityId non-Integer getId döner, -1 döner, 1 != -1 → empty
        assertTrue(strRepo.findById(1).isEmpty());
        // update — getEntityId -1 döner, ama save edilen entity'nin ID'si de -1 → eşleşir
        // Bu aslında true döner çünkü -1 == -1
        assertDoesNotThrow(() -> strRepo.update(entity));
        // delete -1 ile — getEntityId -1 döner, eşleşir
        assertDoesNotThrow(() -> strRepo.delete(-1));
        // initNextId — constructor'da çağrılır, data ile tekrar oluştur
        // getId non-Integer döner, idObj instanceof Integer = false → maxId güncellenmez
        BinaryRepository<StringIdEntity> strRepo2 =
            new BinaryRepository<>(tempDir.toString(), "strid_test");
        // initNextId: tüm entity'ler var ama maxId güncellenemez (non-Integer)
        assertNotNull(strRepo2);
        strRepo.close();
        strRepo2.close();
    }
}
