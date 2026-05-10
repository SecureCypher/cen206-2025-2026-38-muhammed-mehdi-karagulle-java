/**
 * @file VetAppointment.java
 * @brief Veteriner randevu hatırlatıcısı sınıfı.
 */
package com.mehdi.petreminder.model;

import java.time.LocalDateTime;

/**
 * @class VetAppointment
 * @brief Veteriner randevusu — Reminder'dan türetilmiş somut sınıf.
 * @details OOP Inheritance: Reminder'dan miras alır.
 *          OOP Polymorphism: getReminderType(), validate(), getSummary() override.
 * @author Muhammed Mehdi Karagülle, İbrahim Demirci, Zümre Uykun
 * @version 1.0
 */
public class VetAppointment extends Reminder {

    /** @brief Serializable versiyon sabiti. */
    private static final long serialVersionUID = 1L;

    /** @brief Veteriner adı. */
    private String veterinarianName;

    /** @brief Klinik/Hastane adı. */
    private String clinicName;

    /** @brief Muayene sebebi (rutin, hastalık, aşı, kontrol). */
    private String reason;

    /** @brief Tahmini maliyet (TL). */
    private double estimatedCost;

    /** @brief Randevu onaylandı mı. */
    private boolean confirmed;

    /** @brief Klinik telefon numarası. */
    private String clinicPhone;

    /** @brief Varsayılan yapıcı. */
    public VetAppointment() { super(); }

    /**
     * @brief Temel parametreli yapıcı.
     * @param id               ID
     * @param petId            Pet ID
     * @param petName          Pet adı
     * @param scheduledTime    Randevu zamanı
     * @param veterinarianName Veteriner adı
     * @param clinicName       Klinik adı
     * @param reason           Muayene sebebi
     */
    public VetAppointment(int id, int petId, String petName,
                          LocalDateTime scheduledTime,
                          String veterinarianName, String clinicName,
                          String reason) {
        super(id, petId, petName, scheduledTime,
              "Veteriner: " + reason, "YUKSEK", false, "");
        this.veterinarianName = veterinarianName;
        this.clinicName = clinicName;
        this.reason = reason;
        this.estimatedCost = 0.0;
        this.confirmed = false;
        this.clinicPhone = "";
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
     * @param veterinarianName  Veteriner adı
     * @param clinicName        Klinik adı
     * @param reason            Muayene sebebi
     * @param estimatedCost     Tahmini maliyet
     * @param confirmed         Onaylandı mı
     * @param clinicPhone       Klinik telefonu
     */
    public VetAppointment(int id, int petId, String petName,
                          LocalDateTime scheduledTime, String description,
                          String priority, boolean recurring,
                          String recurrencePattern, String veterinarianName,
                          String clinicName, String reason,
                          double estimatedCost, boolean confirmed,
                          String clinicPhone) {
        super(id, petId, petName, scheduledTime, description,
              priority, recurring, recurrencePattern);
        this.veterinarianName = veterinarianName;
        this.clinicName = clinicName;
        this.reason = reason;
        this.estimatedCost = estimatedCost;
        this.confirmed = confirmed;
        this.clinicPhone = clinicPhone;
    }

    /** @brief Veteriner adı getter. @return Veteriner adı */
    public String getVeterinarianName() { return veterinarianName; }

    /** @brief Veteriner adı setter. @param veterinarianName Veteriner adı */
    public void setVeterinarianName(String veterinarianName) {
        this.veterinarianName = veterinarianName;
    }

    /** @brief Klinik adı getter. @return Klinik adı */
    public String getClinicName() { return clinicName; }

    /** @brief Klinik adı setter. @param clinicName Klinik adı */
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }

    /** @brief Muayene sebebi getter. @return Muayene sebebi */
    public String getReason() { return reason; }

    /** @brief Muayene sebebi setter. @param reason Muayene sebebi */
    public void setReason(String reason) { this.reason = reason; }

    /** @brief Tahmini maliyet getter. @return Tahmini maliyet (TL) */
    public double getEstimatedCost() { return estimatedCost; }

    /** @brief Tahmini maliyet setter. @param estimatedCost Tahmini maliyet */
    public void setEstimatedCost(double estimatedCost) { this.estimatedCost = estimatedCost; }

    /** @brief Onay durumu getter. @return Onaylandı mı */
    public boolean isConfirmed() { return confirmed; }

    /** @brief Onay durumu setter. @param confirmed Onaylandı mı */
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }

    /** @brief Klinik telefonu getter. @return Telefon */
    public String getClinicPhone() { return clinicPhone; }

    /** @brief Klinik telefonu setter. @param clinicPhone Telefon */
    public void setClinicPhone(String clinicPhone) { this.clinicPhone = clinicPhone; }

    /** @brief Hatırlatıcı türü. @return "Veteriner" */
    @Override
    public String getReminderType() { return "Veteriner"; }

    /**
     * @brief Veteriner randevusunu doğrular.
     * @return Veteriner ve klinik bilgileri dolu ise true
     */
    @Override
    public boolean validate() {
        return veterinarianName != null && !veterinarianName.trim().isEmpty()
            && clinicName != null && !clinicName.trim().isEmpty()
            && reason != null && !reason.trim().isEmpty();
    }

    /**
     * @brief Özet bilgi.
     * @return "Veteriner: Dr.Ali - Rize Vet Kliniği (Rutin Muayene)"
     */
    @Override
    public String getSummary() {
        return String.format("Veteriner: %s - %s (%s)%s",
            veterinarianName, clinicName, reason,
            confirmed ? " [Onaylandi]" : " [Bekliyor]");
    }

    /** @brief String temsili. @return VetAppointment bilgisi */
    @Override
    public String toString() {
        return String.format(
            "VetAppointment{id=%d, pet='%s', vet='%s', clinic='%s', reason='%s'}",
            getId(), getPetName(), veterinarianName, clinicName, reason);
    }
}
