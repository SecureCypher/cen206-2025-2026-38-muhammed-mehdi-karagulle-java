package com.mehdi.petreminder;

import com.mehdi.petreminder.config.StorageConfig;
import com.mehdi.petreminder.config.StorageType;
import com.mehdi.petreminder.model.Dog;
import com.mehdi.petreminder.model.Pet;
import com.mehdi.petreminder.repository.IRepository;
import com.mehdi.petreminder.repository.RepositoryFactory;

import java.time.LocalDate;
import java.util.List;

public class StorageTest {
    public static void main(String[] args) {
        System.out.println("====== PETREMINDER STORAGE TEST ======\n");

        testStorage(StorageType.BINARY);
        testStorage(StorageType.SQLITE);
        testStorage(StorageType.MYSQL);

        System.out.println("====== TESTS COMPLETED ======");
    }

    private static void testStorage(StorageType type) {
        System.out.println(">>> Testing Storage Type: " + type.name());
        try {
            StorageConfig.setActiveBackend(type);
            IRepository<Pet> repo = RepositoryFactory.createPetRepository();
            
            Dog testDog = new Dog(0, "TestDog_" + type.name(), LocalDate.now(), 1);
            testDog.setBreed("Golden Retriever");
            
            repo.save(testDog);
            System.out.println("Successfully saved Pet to " + type.name() + " repository.");
            
            List<Pet> pets = repo.findAll();
            System.out.println("Total pets in " + type.name() + ": " + pets.size());
            
            System.out.println("Data location explanation:");
            if (type == StorageType.BINARY) {
                System.out.println("  -> Saved as Java serialized objects to: " + StorageConfig.getBinaryDirectory() + "/pets.bin");
            } else if (type == StorageType.SQLITE) {
                System.out.println("  -> Saved as relational rows to local SQLite DB: " + StorageConfig.getSqliteFilePath());
            } else if (type == StorageType.MYSQL) {
                System.out.println("  -> Saved via JDBC to MySQL Docker DB: " + StorageConfig.getMysqlUrl());
            }
            
            System.out.println("----------------------------------------\n");
        } catch (Exception e) {
            System.err.println("Test failed for " + type.name() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
