/**
 * @file MedicationReminder.java
 * @brief İlaç hatırlatıcısı sınıfı.
 */
package com.mehdi.petreminder.model;

import java.time.LocalDateTime;

/**
 * @class MedicationReminder
 * @brief Evcil hayvan ilaç hatırlatıcısı.
 * @details OOP Inheritance: Reminder'dan miras alır.
 * @author Muhammed Mehdi Karagülle, İbrahim Demirci, Zümre Uykun
 * @version 1.0
 */
public class MedicationReminder extends Reminder {

    /** @brief Serializable versiyon sabiti. */
    private static final long serialVersionUID = 1L;

    /** @brief İlaç adı. */
    private String medicationName;

    /** @brief Doz miktarı (mg veya ml). */
    private double dosage;

    /** @brief Doz birimi (mg, ml, tablet). */
    private String dosageUnit;

    /** @brief Reçeteli mi. */
    private boolean prescribed;

    /** @brief Veteriner adı. */
    private String veterinarianName;

    /** @brief Varsayılan yapıcı. */
    public MedicationReminder() { super(); }

    /**
     * @brief Temel parametreli yapıcı.
     * @param id             ID
     * @param petId          Pet ID
     * @param petName        Pet adı
     * @param scheduledTime  Zaman
     * @param medicationName İlaç adı
     * @param dosage         Doz
     * @param dosageUnit     Birim
     */
    public MedicationReminder(int id, int petId, String petName,
                              LocalDateTime scheduledTime,
                              String medicationName, double dosage,
                              String dosageUnit) {
        super(id, petId, petName, scheduledTime,
              "İlaç: " + medicationName, "YUKSEK", false, "");
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.dosageUnit = dosageUnit;
        this.prescribed = false;
        this.veterinarianName = "";
    }

    /**
     * @brief Tam parametreli yapıcı.
     * @param id               ID
     * @param petId            Pet ID
     * @param petName          Pet adı
     * @param scheduledTime    Zaman
     * @param description      Açıklama
     * @param priority         Öncelik
     * @param recurring        Tekrarlıyor mu
     * @param recurrencePattern Tekrar deseni
     * @param medicationName   İlaç adı
     * @param dosage           Doz
     * @param dosageUnit       Birim
     * @param prescribed       Reçeteli mi
     * @param veterinarianName Veteriner adı
     */
    public MedicationReminder(int id, int petId, String petName,
                              LocalDateTime scheduledTime, String description,
                              String priority, boolean recurring,
                              String recurrencePattern, String medicationName,
                              double dosage, String dosageUnit,
                              boolean prescribed, String veterinarianName) {
        super(id, petId, petName, scheduledTime, description,
              priority, recurring, recurrencePattern);
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.dosageUnit = dosageUnit;
        this.prescribed = prescribed;
        this.veterinarianName = veterinarianName;
    }

    /** @brief İlaç adı getter. @return İlaç adı */
    public String getMedicationName() { return medicationName; }

    /** @brief İlaç adı setter. @param medicationName İlaç adı */
    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    /** @brief Doz getter. @return Doz */
    public double getDosage() { return dosage; }

    /** @brief Doz setter. @param dosage Doz */
    public void setDosage(double dosage) { this.dosage = dosage; }

    /** @brief Birim getter. @return Birim */
    public String getDosageUnit() { return dosageUnit; }

    /** @brief Birim setter. @param dosageUnit Birim */
    public void setDosageUnit(String dosageUnit) { this.dosageUnit = dosageUnit; }

    /** @brief Reçeteli mi getter. @return Reçeteli mi */
    public boolean isPrescribed() { return prescribed; }

    /** @brief Reçeteli setter. @param prescribed Reçeteli mi */
    public void setPrescribed(boolean prescribed) { this.prescribed = prescribed; }

    /** @brief Veteriner adı getter. @return Veteriner adı */
    public String getVeterinarianName() { return veterinarianName; }

    /** @brief Veteriner adı setter. @param veterinarianName Veteriner adı */
    public void setVeterinarianName(String veterinarianName) {
        this.veterinarianName = veterinarianName;
    }

    /** @brief Hatırlatıcı türü. @return "Ilac" */
    @Override
    public String getReminderType() { return "Ilac"; }

    /**
     * @brief İlaç hatırlatıcısını doğrular.
     * @return İlaç adı ve doz geçerliyse true
     */
    @Override
    public boolean validate() {
        return medicationName != null && !medicationName.trim().isEmpty()
            && dosage > 0
            && dosageUnit != null && !dosageUnit.trim().isEmpty();
    }

    /**
     * @brief Özet bilgi.
     * @return "İlaç: Antibiyotik 250mg"
     */
    @Override
    public String getSummary() {
        return String.format("Ilac: %s %.1f%s%s",
            medicationName, dosage, dosageUnit,
            prescribed ? " (Receteli)" : "");
    }

    /**
     * @brief String temsili.
     * @return İlaç hatırlatıcısı bilgisi
     */
    @Override
    public String toString() {
        return String.format("MedicationReminder{id=%d, pet='%s', med='%s', dose=%.1f%s}",
            getId(), getPetName(), medicationName, dosage, dosageUnit);
    }
}
