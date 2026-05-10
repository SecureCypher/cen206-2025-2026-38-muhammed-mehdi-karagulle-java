/**
 * @file PetReminderObserver.java
 * @brief Observer Design Pattern — gozlemci arayuzu.
 * @details PDF zorunlulugu: Minimum 3 design pattern.
 *          Observer Pattern: Durum degisikliklerinde otomatik bildirim.
 *          Factory ✅, Template Method ✅, Observer ✅
 */
package com.mehdi.petreminder.observer;

/**
 * @interface PetReminderObserver
 * @brief Gozlemci arayuzu — durum degisikliklerini dinler.
 * @details Observer Design Pattern: Bu interface'i implement eden
 *          siniflar, Subject'teki degisikliklerden haberdar olur.
 *          Ornek: Storage degistiginde servisler guncellenir,
 *                 Reminder eklendiginde Dashboard yenilenir.
 * @author Muhammed Mehdi Karagulle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public interface PetReminderObserver {

    /**
     * @brief Olay gerceklestiginde cagrilan metod.
     * @details Subject durumu degistiginde tum kayitli observer'lara
     *          bu metod uzerinden bildirim gonderilir.
     * @param event Olay turu (ornegin STORAGE_CHANGED, REMINDER_ADDED)
     * @param data  Olayla ilgili veri (ornegin yeni StorageType, eklenen Reminder)
     */
    void onEvent(EventType event, Object data);
}