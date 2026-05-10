/**
 * @file ExerciseReminder.java
 * @brief Egzersiz hatırlatıcısı sınıfı.
 */
package com.mehdi.petreminder.model;

import java.time.LocalDateTime;

/**
 * @class ExerciseReminder
 * @brief Evcil hayvan egzersiz hatırlatıcısı.
 * @details OOP Inheritance: Reminder'dan miras alır.
 * @author Muhammed Mehdi Karagülle, İbrahim Demirci, Zümre Uykun
 * @version 1.0
 */
public class ExerciseReminder extends Reminder {

    /** @brief Serializable versiyon sabiti. */
    private static final long serialVersionUID = 1L;

    /** @brief Egzersiz türü (yürüyüş, oyun, koşu). */
    private String exerciseType;

    /** @brief Süre (dakika). */
    private int durationMinutes;

    /** @brief Hedef mesafe (km) — isteğe bağlı. */
    private double distanceKm;

    /** @brief Varsayılan yapıcı. */
    public ExerciseReminder() { super(); }

    /**
     * @brief Temel parametreli yapıcı.
     * @param id              ID
     * @param petId           Pet ID
     * @param petName         Pet adı
     * @param scheduledTime   Zaman
     * @param exerciseType    Egzersiz türü
     * @param durationMinutes Süre (dk)
     */
    public ExerciseReminder(int id, int petId, String petName,
                            LocalDateTime scheduledTime,
                            String exerciseType, int durationMinutes) {
        super(id, petId, petName, scheduledTime,
              "Egzersiz: " + exerciseType, "ORTA", false, "");
        this.exerciseType = exerciseType;
        this.durationMinutes = durationMinutes;
        this.distanceKm = 0.0;
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
     * @param exerciseType      Egzersiz türü
     * @param durationMinutes   Süre
     * @param distanceKm        Mesafe (km)
     */
    public ExerciseReminder(int id, int petId, String petName,
                            LocalDateTime scheduledTime, String description,
                            String priority, boolean recurring,
                            String recurrencePattern, String exerciseType,
                            int durationMinutes, double distanceKm) {
        super(id, petId, petName, scheduledTime, description,
              priority, recurring, recurrencePattern);
        this.exerciseType = exerciseType;
        this.durationMinutes = durationMinutes;
        this.distanceKm = distanceKm;
    }

    /** @brief Egzersiz türü getter. @return Egzersiz türü */
    public String getExerciseType() { return exerciseType; }

    /** @brief Egzersiz türü setter. @param exerciseType Egzersiz türü */
    public void setExerciseType(String exerciseType) { this.exerciseType = exerciseType; }

    /** @brief Süre getter. @return Süre (dk) */
    public int getDurationMinutes() { return durationMinutes; }

    /** @brief Süre setter. @param durationMinutes Süre (dk) */
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    /** @brief Mesafe getter. @return Mesafe (km) */
    public double getDistanceKm() { return distanceKm; }

    /** @brief Mesafe setter. @param distanceKm Mesafe (km) */
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    /** @brief Hatırlatıcı türü. @return "Egzersiz" */
    @Override
    public String getReminderType() { return "Egzersiz"; }

    /**
     * @brief Egzersiz hatırlatıcısını doğrular.
     * @return Egzersiz türü ve süre geçerliyse true
     */
    @Override
    public boolean validate() {
        return exerciseType != null && !exerciseType.trim().isEmpty()
            && durationMinutes > 0;
    }

    /**
     * @brief Özet bilgi.
     * @return "Egzersiz: Yürüyüş 30dk"
     */
    @Override
    public String getSummary() {
        String s = String.format("Egzersiz: %s %d dk", exerciseType, durationMinutes);
        if (distanceKm > 0) s += String.format(" (%.1f km)", distanceKm);
        return s;
    }

    /** @brief String temsili. @return Egzersiz hatırlatıcısı bilgisi */
    @Override
    public String toString() {
        return String.format("ExerciseReminder{id=%d, pet='%s', type='%s', %ddk}",
            getId(), getPetName(), exerciseType, durationMinutes);
    }
}
