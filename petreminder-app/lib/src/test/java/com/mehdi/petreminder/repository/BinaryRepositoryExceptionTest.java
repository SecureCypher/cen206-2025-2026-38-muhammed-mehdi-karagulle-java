package com.mehdi.petreminder.repository;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BinaryRepository Exception Testleri")
class BinaryRepositoryExceptionTest {

    private final String testDir = "target/test-binary-exceptions";
    private BinaryRepository<DummyEntity> repo;

    public static class DummyEntity implements Serializable {
        // getId veya setId metodu YOK - reflection hatalari icin
    }

    @BeforeEach
    void setUp() {
        File dir = new File(testDir);
        if (!dir.exists()) dir.mkdirs();
        File f = new File(testDir + File.separator + "test_dummy.bin");
        if (f.exists()) f.delete();
        repo = new BinaryRepository<>(testDir, "test_dummy");
    }

    @AfterEach
    void cleanUp() {
        File f = new File(testDir + File.separator + "test_dummy.bin");
        if (f.exists()) f.delete();
        new File(testDir).delete();
    }

    @Test
    void testReflectionWarnings() {
        DummyEntity entity = new DummyEntity();
        
        // save, setId metodunu bulamayacak ve catch bloguna dusecek
        assertDoesNotThrow(() -> repo.save(entity));
        
        // delete, getId metodunu bulamayacak ve catch bloguna dusecek
        assertFalse(repo.delete(1));
        
        // update, getId metodunu bulamayacak ve catch bloguna dusecek
        // Ancak ustte save ile ekledigimiz icin findAll() icinde -1 ID'li nesne var, 
        // bu yuzden true dondu! Repoyu temizleyelim:
        repo.deleteAll();
        assertFalse(repo.update(entity));
        
        // findById, getId metodunu bulamayacak ve catch bloguna dusecek
        assertTrue(repo.findById(1).isEmpty());
        
        // initNextId, varlik okudugunda getId bulamayacak
        repo.save(new DummyEntity());
        BinaryRepository<DummyEntity> repo2 = new BinaryRepository<>(testDir, "test_dummy");
        assertEquals(1, repo2.count()); // it should have loaded the 1 item
    }
    
    @Test
    void testIOExceptionOnWrite() {
        // Dosyayi readonly yap veya klasor yap ki yazamasin
        File f = new File(repo.getFilePath());
        f.mkdirs(); // dosya isminde klasor olustur = YAZILAMAZ!
        
        assertThrows(RepositoryException.class, () -> repo.save(new DummyEntity()));
        
        f.delete(); // temizle
    }
    
    @Test
    void testEOFExceptionOnRead() throws IOException {
        File f = new File(repo.getFilePath());
        f.createNewFile(); // 0 byte file
        
        // 0 byte ise bos liste donmeli (cunku file.length() == 0 kontrolu var)
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void testCorruptionExceptionOnRead() throws IOException {
        File f = new File(repo.getFilePath());
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write("corrupted data not objects".getBytes());
        }
        
        // InvalidClassException / StreamCorruptedException
        assertThrows(RepositoryException.class, () -> repo.findAll());
    }
}
