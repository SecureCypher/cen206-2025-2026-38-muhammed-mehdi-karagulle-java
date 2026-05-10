/**
 * @file MedicalRecordService.java
 * @brief Sağlık kayıtları servis katmanı.
 * @details Observer Pattern: CRUD işlemlerinde EventManager'a bildirim gönderir.
 */
/**
 * Member documentation.
 */
package com.mehdi.petreminder.service;

import com.mehdi.petreminder.model.MedicalRecord;
import com.mehdi.petreminder.observer.EventManager;
import com.mehdi.petreminder.observer.EventType;
import com.mehdi.petreminder.repository.IRepository;
import com.mehdi.petreminder.repository.RepositoryFactory;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @class MedicalRecordService
 * @brief MedicalRecord CRUD ve iş kuralları.
 * @details Observer Pattern: Kayıt eklendiğinde veya silindiğinde
 *          EventManager üzerinden bildirim gönderilir.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class MedicalRecordService {

    /** @brief Logger. */
    private static final Logger logger =
        (Logger) LoggerFactory.getLogger(MedicalRecordService.class);

    /** @brief MedicalRecord repository. */
    private final IRepository<MedicalRecord> repository;

    /**
     * @brief Varsayılan yapıcı.
     */
    public MedicalRecordService() {
        this.repository = RepositoryFactory.createMedicalRecordRepository();
    }

    /**
     * @brief Test yapıcısı.
     * @param repository Test repository
     */
    public MedicalRecordService(IRepository<MedicalRecord> repository) {
        this.repository = repository;
    }

    /**
     * @brief Kayıt ekler.
     * @param record Eklenecek MedicalRecord
     * @return Eklenen kayıt
     */
    public MedicalRecord addRecord(MedicalRecord record) {
        validateRecord(record);
        repository.save(record);
        logger.info("MedicalRecord eklendi: petId={}", record.getPetId());
        // Observer Pattern: Saglik kaydi eklendi bildirimi
        EventManager.getInstance().notify(EventType.MEDICAL_RECORD_ADDED, record);
        return record;
    }

    /**
     * @brief ID ile kayıt getirir.
     * @param id Kayıt ID
     * @return MedicalRecord
     */
    public MedicalRecord getRecordById(int id) {
        return repository.findById(id)
            .orElseThrow(() -> new ServiceException("Kayıt bulunamadı: id=" + id));
    }

    /**
     * @brief Tüm kayıtları listeler.
     * @return Liste
     */
    public List<MedicalRecord> getAllRecords() {
        return repository.findAll();
    }

    /**
     * @brief Pet'e ait kayıtları getirir.
     * @param petId Pet ID
     * @return MedicalRecord listesi
     */
    public List<MedicalRecord> getRecordsByPetId(int petId) {
        return repository.findAll().stream()
            .filter(r -> r.getPetId() == petId)
            .sorted(Comparator.comparing(
                MedicalRecord::getRecordDate,
                Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());
    }

    /**
     * @brief Kayıt günceller.
     * @param record Güncellenecek kayıt
     */
    public void updateRecord(MedicalRecord record) {
        validateRecord(record);
        boolean updated = repository.update(record);
        if (!updated) throw new ServiceException("Kayıt güncellenemedi: id=" + record.getId());
    }

    /**
     * @brief Kayıt siler.
     * @param id Kayıt ID
     */
    public void deleteRecord(int id) {
        boolean deleted = repository.delete(id);
        if (!deleted) throw new ServiceException("Kayıt silinemedi: id=" + id);
        logger.info("MedicalRecord silindi: id={}", id);
        // Observer Pattern: Saglik kaydi silindi bildirimi
        EventManager.getInstance().notify(EventType.MEDICAL_RECORD_DELETED, id);
    }

    /**
     * @brief Tüm kayıtları siler.
     */
    public void deleteAllRecords() {
        repository.deleteAll();
    }

    /**
     * @brief Toplam kayıt sayısı.
     * @return Sayı
     */
    public int getRecordCount() {
        return repository.count();
    }

    /**
     * @brief Repository kapatır.
     */
    public void close() {
        repository.close();
    }

    /**
     * @brief Doğrulama.
     * @param r Kayıt
     */
    private void validateRecord(MedicalRecord r) {
        if (r == null) throw new ServiceException("Kayıt null olamaz");
        if (r.getPetId() <= 0) throw new ServiceException("Geçerli pet ID gerekli");
        if (r.getRecordDate() == null) throw new ServiceException("Kayıt tarihi boş olamaz");
        if (r.getRecordType() == null || r.getRecordType().trim().isEmpty()) {
            throw new ServiceException("Kayıt türü boş olamaz");
        }
    }
}