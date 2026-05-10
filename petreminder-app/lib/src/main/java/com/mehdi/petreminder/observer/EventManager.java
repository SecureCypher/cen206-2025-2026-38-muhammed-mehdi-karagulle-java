/**
 * @file EventManager.java
 * @brief Observer Pattern — Subject (yayinci) sinifi.
 * @details Observer'lari kaydeder, cikarir ve bildirim gonderir.
 *          Singleton Pattern ile tek instance kullanilir.
 */
package com.mehdi.petreminder.observer;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @class EventManager
 * @brief Observer Pattern Subject — olay yonetici.
 * @details PDF zorunlulugu: Observer Design Pattern.
 *          Bu sinif "Subject" rolundedir. Observer'lari kayit altina alir
 *          ve bir olay gerceklestiginde tum kayitli observer'lara bildirim gonderir.
 *          Thread-safe: CopyOnWriteArrayList kullanilir (GUI thread guvenli).
 *          Singleton: Uygulama genelinde tek instance.
 * @author Muhammed Mehdi Karagulle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class EventManager {

    /** @brief Logger. */
    private static final Logger logger =
        (Logger) LoggerFactory.getLogger(EventManager.class);

    /** @brief Singleton instance. */
    private static EventManager instance;

    /**
     * @brief Olay turune gore kayitli observer listesi.
     * @details Her EventType icin ayri bir observer listesi tutulur.
     *          CopyOnWriteArrayList: iterator sirasinda modifikasyona izin verir.
     */
    private final Map<EventType, List<PetReminderObserver>> listeners;

    /**
     * @brief Gizli yapici — Singleton.
     * @details Tum EventType degerleri icin bos liste olusturur.
     */
    private EventManager() {
        listeners = new EnumMap<>(EventType.class);
        for (EventType type : EventType.values()) {
            listeners.put(type, new CopyOnWriteArrayList<>());
        }
        logger.debug("EventManager olusturuldu.");
    }

    /**
     * @brief Singleton instance dondurur.
     * @return EventManager tek instance
     */
    public static synchronized EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    /**
     * @brief Belirli bir olay turune observer kaydeder.
     * @param eventType Dinlenecek olay turu
     * @param observer  Kayit edilecek observer
     */
    public void subscribe(EventType eventType, PetReminderObserver observer) {
        if (eventType == null || observer == null) {
            logger.warn("subscribe: null parametre, atlaniyor.");
            return;
        }
        List<PetReminderObserver> list = listeners.get(eventType);
        if (!list.contains(observer)) {
            list.add(observer);
            logger.debug("Observer kaydedildi: {} -> {}",
                eventType, observer.getClass().getSimpleName());
        }
    }

    /**
     * @brief Observer'i belirli bir olay turundan cikarir.
     * @param eventType Olay turu
     * @param observer  Cikarilacak observer
     */
    public void unsubscribe(EventType eventType, PetReminderObserver observer) {
        if (eventType == null || observer == null) return;
        List<PetReminderObserver> list = listeners.get(eventType);
        list.remove(observer);
        logger.debug("Observer cikarildi: {} -> {}",
            eventType, observer.getClass().getSimpleName());
    }

    /**
     * @brief Tum olay turlerinden observer'i cikarir.
     * @param observer Cikarilacak observer
     */
    public void unsubscribeAll(PetReminderObserver observer) {
        if (observer == null) return;
        for (EventType type : EventType.values()) {
            listeners.get(type).remove(observer);
        }
        logger.debug("Observer tum olaylardan cikarildi: {}",
            observer.getClass().getSimpleName());
    }

    /**
     * @brief Belirli bir olay turu icin tum observer'lara bildirim gonderir.
     * @param eventType Gerceklesen olay turu
     * @param data      Olayla ilgili veri (null olabilir)
     */
    public void notify(EventType eventType, Object data) {
        if (eventType == null) return;
        List<PetReminderObserver> list = listeners.get(eventType);
        logger.info("Olay tetiklendi: {} ({} observer)",
            eventType, list.size());
        for (PetReminderObserver observer : list) {
            try {
                observer.onEvent(eventType, data);
            } catch (Exception e) {
                logger.error("Observer bildirim hatasi: {} - {}",
                    observer.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    /**
     * @brief Belirli olay turune kayitli observer sayisini dondurur.
     * @param eventType Olay turu
     * @return Observer sayisi
     */
    public int getObserverCount(EventType eventType) {
        if (eventType == null) return 0;
        return listeners.get(eventType).size();
    }

    /**
     * @brief Tum observer'lari temizler (test icin).
     */
    public void clearAll() {
        for (EventType type : EventType.values()) {
            listeners.get(type).clear();
        }
        logger.debug("Tum observer'lar temizlendi.");
    }

    /**
     * @brief Singleton instance'i sifirlar (test icin).
     */
    public static synchronized void resetInstance() {
        instance = null;
    }
}