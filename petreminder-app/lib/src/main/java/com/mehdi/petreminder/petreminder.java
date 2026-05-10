/**
 * @file petreminder.java
 * @brief Pet Care Reminder System kütüphane yardımcı sınıfı.
 * @details Template'deki petreminder.java'nın projeye uyarlanmış hali.
 *          Bu sınıf utility / facade görevi görür.
 *          OOP Encapsulation: tüm yardımcı metodlar bu sınıfta toplanmıştır.
 */
package com.mehdi.petreminder;


import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @class petreminder
 * @brief Pet Care Reminder System yardımcı/facade sınıfı.
 * @details PDF gereksinimi: Separate library (lib) and application (app) layers.
 *          Bu sınıf lib katmanının facade'ı olarak görev yapar.
 *          OOP Encapsulation: tüm alanlar private.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 * @date 2026-03-27
 */
public class petreminder {

    /**
     * @brief Sınıf logger'ı.
     */
    private static final Logger logger =
        (Logger) LoggerFactory.getLogger(petreminder.class);

    /**
     * @brief Tarih-saat formatı (gösterim için).
     */
    private static final DateTimeFormatter DISPLAY_FORMAT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * @brief Varsayılan yapıcı.
     */
    public petreminder() {
        logger.debug("petreminder yardimci sinifi olusturuldu.");
    }

    /**
     * @brief Mevcut tarih-saati biçimli string olarak döndürür.
     * @return "dd/MM/yyyy HH:mm" formatında şimdiki zaman
     */
    public String getCurrentTimeFormatted() {
        return LocalDateTime.now().format(DISPLAY_FORMAT);
    }

    /**
     * @brief Verilen string'in boş veya null olup olmadığını kontrol eder.
     * @param value Kontrol edilecek string
     * @return null veya boş ise true
     */
    public boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
