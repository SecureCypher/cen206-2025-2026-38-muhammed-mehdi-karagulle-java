/**
 * @file GroomingReminder.java
 * @brief Tıraş/bakım hatırlatıcısı sınıfı.
 */
package com.mehdi.petreminder.model;

import java.time.LocalDateTime;

/**
 * @class GroomingReminder
 * @brief Evcil hayvan tıraş ve bakım hatırlatıcısı.
 * @details OOP Inheritance: Reminder'dan miras alır.
 * @author Muhammed Mehdi Karagülle, İbrahim Demirci, Zümre Uykun
 * @version 1.0
 */
public class GroomingReminder extends Reminder {

    /** @brief Serializable versiyon sabiti. */
    private static final long serialVersionUID = 1L;

    /** @brief Bakım türü (tıraş, banyo, tırnak kesimi, diş fırçalama). */
    private String groomingType;

    /** @brief Profesyonel groomer randevusu mu. */
    private boolean professional;

    /** @brief Groomer / veteriner adı. */
    private String groomerName;

    /** @brief Varsayılan yapıcı. */
    public GroomingReminder() { super(); }

    /**
     * @brief Temel parametreli yapıcı.
     * @param id           ID
     * @param petId        Pet ID
     * @param petName      Pet adı
     * @param scheduledTime Zaman
     * @param groomingType Bakım türü
     * @param professional Profesyonel mi
     */
    public GroomingReminder(int id, int petId, String petName,
                            LocalDateTime scheduledTime,
                            String groomingType, boolean professional) {
        super(id, petId, petName, scheduledTime,
              "Bakim: " + groomingType, "DUSUK", false, "");
        this.groomingType = groomingType;
        this.professional = professional;
        this.groomerName = "";
    }

    /**
     * @brief Tam parametreli yapıcı.
     * @param id                ID
     * @param petId             Pet ID
     * @param petName           Pet adı
     * @param scheduledTime     Zaman
     * @param description       Açıklama
     * @param priority          Öncelik
     * @param recurring         Tekrarlıyor mu
     * @param recurrencePattern Tekrar deseni
     * @param groomingType      Bakım türü
     * @param professional      Profesyonel mi
     * @param groomerName       Groomer adı
     */
    public GroomingReminder(int id, int petId, String petName,
                            LocalDateTime scheduledTime, String description,
                            String priority, boolean recurring,
                            String recurrencePattern, String groomingType,
                            boolean professional, String groomerName) {
        super(id, petId, petName, scheduledTime, description,
              priority, recurring, recurrencePattern);
        this.groomingType = groomingType;
        this.professional = professional;
        this.groomerName = groomerName;
    }

    /** @brief Bakım türü getter. @return Bakım türü */
    public String getGroomingType() { return groomingType; }

    /** @brief Bakım türü setter. @param groomingType Bakım türü */
    public void setGroomingType(String groomingType) { this.groomingType = groomingType; }

    /** @brief Profesyonel mi getter. @return Profesyonel mi */
    public boolean isProfessional() { return professional; }

    /** @brief Profesyonel setter. @param professional Profesyonel mi */
    public void setProfessional(boolean professional) { this.professional = professional; }

    /** @brief Groomer adı getter. @return Groomer adı */
    public String getGroomerName() { return groomerName; }

    /** @brief Groomer adı setter. @param groomerName Groomer adı */
    public void setGroomerName(String groomerName) { this.groomerName = groomerName; }

    /** @brief Hatırlatıcı türü. @return "Bakim" */
    @Override
    public String getReminderType() { return "Bakim"; }

    /**
     * @brief Bakım hatırlatıcısını doğrular.
     * @return Bakım türü boş değilse true
     */
    @Override
    public boolean validate() {
        return groomingType != null && !groomingType.trim().isEmpty();
    }

    /**
     * @brief Özet bilgi.
     * @return "Bakım: Tıraş (Profesyonel)"
     */
    @Override
    public String getSummary() {
        return String.format("Bakim: %s%s%s",
            groomingType,
            professional ? " (Profesyonel)" : "",
            groomerName != null && !groomerName.isEmpty() ? " - " + groomerName : "");
    }

    /** @brief String temsili. @return Bakım hatırlatıcısı bilgisi */
    @Override
    public String toString() {
        return String.format("GroomingReminder{id=%d, pet='%s', type='%s', prof=%b}",
            getId(), getPetName(), groomingType, professional);
    }
}
