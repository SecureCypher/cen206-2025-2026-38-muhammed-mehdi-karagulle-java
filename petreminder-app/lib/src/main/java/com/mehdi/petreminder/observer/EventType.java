/**
 * @file EventType.java
 * @brief Observer Pattern olay turleri.
 * @details Sistemde gerceklesebilecek olay turlerini tanimlar.
 */
package com.mehdi.petreminder.observer;

/**
 * @enum EventType
 * @brief Observer Pattern icin olay turleri.
 * @details Her olay turu, belirli bir durum degisikligini temsil eder.
 *          Observer'lar bu turlere gore filtreleme yapabilir.
 * @author Muhammed Mehdi Karagulle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
/**
 * Enum documentation.
 */
public enum EventType {

    /** @brief Storage backend degisti (BINARY, SQLite, MySQL). */
    STORAGE_CHANGED("Storage Backend Degisti"),

    /** @brief Yeni pet eklendi. */
    PET_ADDED("Yeni Pet Eklendi"),

    /** @brief Pet silindi. */
    PET_DELETED("Pet Silindi"),

    /** @brief Pet guncellendi. */
    PET_UPDATED("Pet Guncellendi"),

    /** @brief Yeni hatirlatici eklendi. */
    REMINDER_ADDED("Yeni Hatirlatici Eklendi"),

    /** @brief Hatirlatici tamamlandi. */
    REMINDER_COMPLETED("Hatirlatici Tamamlandi"),

    /** @brief Hatirlatici silindi. */
    REMINDER_DELETED("Hatirlatici Silindi"),

    /** @brief Saglik kaydi eklendi. */
    MEDICAL_RECORD_ADDED("Saglik Kaydi Eklendi"),

    /** @brief Saglik kaydi silindi. */
    MEDICAL_RECORD_DELETED("Saglik Kaydi Silindi");

    /** @brief Olay aciklamasi. */
    private final String description;

    /**
     * @brief Yapici.
     * @param description Olay aciklamasi
     */
    EventType(String description) {
        this.description = description;
    }

    /**
     * @brief Aciklamayi dondurur.
     * @return Olay aciklamasi
     */
    public String getDescription() {
        return description;
    }

    /**
     * @brief toString override.
     * @return Olay aciklamasi
     */
    @Override
    public String toString() {
        return description;
    }
}