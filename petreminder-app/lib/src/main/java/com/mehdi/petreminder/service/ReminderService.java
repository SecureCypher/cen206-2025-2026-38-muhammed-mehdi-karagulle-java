/**
 * @file ReminderService.java
 * @brief Reminder iş mantığı servis katmanı.
 * @details Observer Pattern: CRUD işlemlerinde EventManager'a bildirim gönderir.
 */
package com.mehdi.petreminder.service;

import com.mehdi.petreminder.model.Reminder;
import com.mehdi.petreminder.observer.EventManager;
import com.mehdi.petreminder.observer.EventType;
import com.mehdi.petreminder.repository.IRepository;
import com.mehdi.petreminder.repository.RepositoryFactory;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @class ReminderService
 * @brief Reminder CRUD ve iş kuralları.
 * @details Observer Pattern: Reminder eklendiğinde, tamamlandığında veya
 *          silindiğinde EventManager üzerinden kayıtlı observer'lara
 *          bildirim gönderilir. Böylece Dashboard gibi bileşenler
 *          otomatik güncellenir.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class ReminderService {

    /** @brief Logger. */
    private static final Logger logger =
        (Logger) LoggerFactory.getLogger(ReminderService.class);

    /** @brief Reminder repository. */
    private final IRepository<Reminder> repository;

    /**
     * @brief Varsayılan yapıcı.
     */
    public ReminderService() {
        this.repository = RepositoryFactory.createReminderRepository();
    }

    /**
     * @brief Test yapıcısı.
     * @param repository Test repository
     */
    public ReminderService(IRepository<Reminder> repository) {
        this.repository = repository;
    }

    /**
     * @brief Yeni hatırlatıcı ekler.
     * @param reminder Eklenecek Reminder
     * @return Oluşturulan Reminder
     * @throws ServiceException Doğrulama başarısız
     */
    public Reminder addReminder(Reminder reminder) {
        validateReminder(reminder);
        int id = repository.save(reminder);
        logger.info("Reminder eklendi: id={}, type={}", id, reminder.getReminderType());
        // Observer Pattern: Reminder eklendi bildirimi
        EventManager.getInstance().notify(EventType.REMINDER_ADDED, reminder);
        return reminder;
    }

    /**
     * @brief ID ile hatırlatıcı getirir.
     * @param id Reminder ID
     * @return Reminder nesnesi
     */
    public Reminder getReminderById(int id) {
        return repository.findById(id)
            .orElseThrow(() -> new ServiceException("Reminder bulunamadı: id=" + id));
    }

    /**
     * @brief Tüm hatırlatıcıları listeler.
     * @return Reminder listesi
     */
    public List<Reminder> getAllReminders() {
        return repository.findAll();
    }

    /**
     * @brief Bekleyen (tamamlanmamış) hatırlatıcıları getirir.
     * @return Pending reminder listesi
     */
    public List<Reminder> getPendingReminders() {
        return repository.findAll().stream()
            .filter(r -> !r.isCompleted())
            .sorted(Comparator.comparing(r -> r.getScheduledTime() != null
                ? r.getScheduledTime() : LocalDateTime.MAX))
            .collect(Collectors.toList());
    }

    /**
     * @brief Gecikmiş hatırlatıcıları getirir.
     * @return Overdue reminder listesi
     */
    public List<Reminder> getOverdueReminders() {
        return repository.findAll().stream()
            .filter(Reminder::isOverdue)
            .collect(Collectors.toList());
    }

    /**
     * @brief Pet'e ait hatırlatıcıları getirir.
     * @param petId Pet ID
     * @return Reminder listesi
     */
    public List<Reminder> getRemindersByPetId(int petId) {
        return repository.findAll().stream()
            .filter(r -> r.getPetId() == petId)
            .collect(Collectors.toList());
    }

    /**
     * @brief Hatırlatıcıyı tamamlandı olarak işaretler.
     * @param id Reminder ID
     */
    public void markCompleted(int id) {
        Reminder r = getReminderById(id);
        r.setCompleted(true);
        repository.update(r);
        logger.info("Reminder tamamlandı: id={}", id);
        // Observer Pattern: Reminder tamamlandı bildirimi
        EventManager.getInstance().notify(EventType.REMINDER_COMPLETED, r);
    }

    /**
     * @brief Hatırlatıcı günceller.
     * @param reminder Güncellenecek Reminder
     */
    public void updateReminder(Reminder reminder) {
        validateReminder(reminder);
        boolean updated = repository.update(reminder);
        if (!updated) {
            throw new ServiceException("Reminder güncellenemedi: id=" + reminder.getId());
        }
    }

    /**
     * @brief Hatırlatıcı siler.
     * @param id Silinecek ID
     */
    public void deleteReminder(int id) {
        boolean deleted = repository.delete(id);
        if (!deleted) {
            throw new ServiceException("Reminder silinemedi: id=" + id);
        }
        logger.info("Reminder silindi: id={}", id);
        // Observer Pattern: Reminder silindi bildirimi
        EventManager.getInstance().notify(EventType.REMINDER_DELETED, id);
    }

    /**
     * @brief Tüm hatırlatıcıları siler.
     */
    public void deleteAllReminders() {
        repository.deleteAll();
    }

    /**
     * @brief Toplam hatırlatıcı sayısı.
     * @return Sayı
     */
    public int getReminderCount() {
        return repository.count();
    }

    /**
     * @brief Repository kapatır.
     */
    public void close() {
        repository.close();
    }

    /**
     * @brief Reminder doğrulama.
     * @param r Doğrulanacak Reminder
     */
    private void validateReminder(Reminder r) {
        if (r == null) throw new ServiceException("Reminder null olamaz");
        if (r.getPetId() <= 0) throw new ServiceException("Geçerli bir pet seçin");
        if (r.getScheduledTime() == null) throw new ServiceException("Zaman boş olamaz");
        if (!r.validate()) throw new ServiceException("Reminder verisi geçersiz");
    }
}