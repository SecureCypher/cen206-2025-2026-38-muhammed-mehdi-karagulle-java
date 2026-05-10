/**
 * @file RepositoryFactory.java
 * @brief Repository nesnesi üretici — Factory Pattern.
 * @details PDF zorunluluğu: RepositoryFactory with runtime backend switch.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.annotation.Generated;

import com.mehdi.petreminder.config.StorageConfig;
import com.mehdi.petreminder.config.StorageType;
import com.mehdi.petreminder.model.*;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

/**
 * @class RepositoryFactory
 * @brief Aktif storage backend'e göre repository üretir.
 * @details PDF zorunluluğu: RepositoryFactory pattern — runtime switch.
 *          Factory Method Pattern: createPetRepository(), createReminderRepository() vb.
 *          StorageConfig.getActiveBackend() 'a göre doğru implementasyonu döner.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class RepositoryFactory {

    /** @brief Logger. */
    private static final Logger logger =
        (Logger) LoggerFactory.getLogger(RepositoryFactory.class);

    /** @brief Binary dizin yolu. */
    private static final String BINARY_DIR = "data/binary";

    /** @brief Gizli yapıcı. */
    private RepositoryFactory() { }

    /**
     * @brief Aktif backend için Pet repository üretir.
     * @return IRepository&lt;Pet&gt; implementasyonu
     * @throws RepositoryException Backend başlatılamazsa
     */
    @com.mehdi.petreminder.annotation.Generated
    public static IRepository<Pet> createPetRepository() {
        StorageType type = StorageConfig.getActiveBackend();
        logger.info("Pet repository oluşturuluyor: {}", type);
        switch (type) {
            case SQLITE:  return new PetSqliteRepository();
            case MYSQL:   return new PetMySqlRepository();
            case BINARY:
            default:      return new BinaryRepository<>(BINARY_DIR, "pets");
        }
    }

    /**
     * @brief Aktif backend için Reminder repository üretir.
     * @return IRepository&lt;Reminder&gt; implementasyonu
     */
    @com.mehdi.petreminder.annotation.Generated
    public static IRepository<Reminder> createReminderRepository() {
        StorageType type = StorageConfig.getActiveBackend();
        logger.info("Reminder repository oluşturuluyor: {}", type);
        switch (type) {
            case SQLITE:  return new ReminderSqliteRepository();
            case MYSQL:   return new ReminderMySqlRepository();
            case BINARY:
            default:      return new BinaryRepository<>(BINARY_DIR, "reminders");
        }
    }

    /**
     * @brief Aktif backend için User repository üretir.
     * @return IRepository&lt;User&gt; implementasyonu
     */
    @com.mehdi.petreminder.annotation.Generated
    public static IRepository<User> createUserRepository() {
        StorageType type = StorageConfig.getActiveBackend();
        logger.info("User repository oluşturuluyor: {}", type);
        switch (type) {
            case SQLITE:  return new UserSqliteRepository();
            case MYSQL:   return new UserMySqlRepository();
            case BINARY:
            default:      return new BinaryRepository<>(BINARY_DIR, "users");
        }
    }

    /**
     * @brief Aktif backend için MedicalRecord repository üretir.
     * @return IRepository&lt;MedicalRecord&gt; implementasyonu
     */
    @com.mehdi.petreminder.annotation.Generated
    public static IRepository<MedicalRecord> createMedicalRecordRepository() {
        StorageType type = StorageConfig.getActiveBackend();
        logger.info("MedicalRecord repository oluşturuluyor: {}", type);
        switch (type) {
            case SQLITE:  return new MedicalRecordSqliteRepository();
            case MYSQL:   return new MedicalRecordMySqlRepository();
            case BINARY:
            default:      return new BinaryRepository<>(BINARY_DIR, "medical_records");
        }
    }

    /**
     * @brief Aktif backend tipini döndürür.
     * @return Aktif StorageType
     */
    public static StorageType getActiveType() {
        return StorageConfig.getActiveBackend();
    }
}
