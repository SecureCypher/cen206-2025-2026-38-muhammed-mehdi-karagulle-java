/**
 * @file Reminder.java
 * @brief Tüm hatırlatıcı türleri için soyut temel sınıf.
 * @details OOP Abstraction ve Polymorphism temel sınıfı.
 *          Serializable: BinaryRepository için zorunlu.
 */
package com.mehdi.petreminder.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @class Reminder
 * @brief Evcil hayvan bakım hatırlatıcısının soyut temel sınıfı.
 * @details OOP Abstraction: abstract class ile somutlaştırılamaz.
 *          OOP Polymorphism: validate() her alt sınıfta farklı davranır.
 *          OOP Encapsulation: tüm alanlar private.
 * @author Muhammed Mehdi Karagülle, İbrahim Demirci, Zümre Uykun
 * @version 1.0
 */
public abstract class Reminder implements Serializable {

    /** @brief Serializable versiyon sabiti. */
    private static final long serialVersionUID = 1L;

    /** @brief Tarih saat formatı. */
    protected static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** @brief Benzersiz hatırlatıcı ID. */
    private int id;

    /** @brief İlgili evcil hayvanın ID'si. */
    private int petId;

    /** @brief İlgili evcil hayvanın adı (görüntüleme için). */
    private String petName;

    /** @brief Hatırlatıcının planlandığı tarih-saat. */
    private LocalDateTime scheduledTime;

    /** @brief Hatırlatıcının tamamlanıp tamamlanmadığı. */
    private boolean completed;

    /** @brief Hatırlatıcı açıklaması. */
    private String description;

    /** @brief Hatırlatıcı önceliği (DUSUK, ORTA, YUKSEK). */
    private String priority;

    /** @brief Tekrar eden hatırlatıcı mı. */
    private boolean recurring;

    /** @brief Tekrar sıklığı (günlük, haftalık, aylık). */
    private String recurrencePattern;

    /** @brief Varsayılan yapıcı — Serialization için. */
    protected Reminder() {}

    /**
     * @brief Tam parametreli yapıcı.
     * @param id                Benzersiz ID
     * @param petId             Pet ID
     * @param petName           Pet adı
     * @param scheduledTime     Planlanan zaman
     * @param description       Açıklama
     * @param priority          Öncelik (DUSUK/ORTA/YUKSEK)
     * @param recurring         Tekrarlıyor mu
     * @param recurrencePattern Tekrar deseni
     */
    protected Reminder(int id, int petId, String petName,
                       LocalDateTime scheduledTime, String description,
                       String priority, boolean recurring, String recurrencePattern) {
        this.id = id;
        this.petId = petId;
        this.petName = petName;
        this.scheduledTime = scheduledTime;
        this.completed = false;
        this.description = description;
        this.priority = priority;
        this.recurring = recurring;
        this.recurrencePattern = recurrencePattern;
    }

    // ── Getters / Setters ─────────────────────────────────────────────

    /** @brief ID getter. @return Hatırlatıcı ID */
    public int getId() { return id; }

    /** @brief ID setter. @param id Yeni ID */
    public void setId(int id) { this.id = id; }

    /** @brief Pet ID getter. @return Pet ID */
    public int getPetId() { return petId; }

    /** @brief Pet ID setter. @param petId Pet ID */
    public void setPetId(int petId) { this.petId = petId; }

    /** @brief Pet adı getter. @return Pet adı */
    public String getPetName() { return petName; }

    /** @brief Pet adı setter. @param petName Pet adı */
    public void setPetName(String petName) { this.petName = petName; }

    /** @brief Planlanan zaman getter. @return Planlanan zaman */
    public LocalDateTime getScheduledTime() { return scheduledTime; }

    /** @brief Planlanan zaman setter. @param scheduledTime Planlanan zaman */
    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    /** @brief Tamamlandı mı getter. @return true ise tamamlandı */
    public boolean isCompleted() { return completed; }

    /** @brief Tamamlandı setter. @param completed Durum */
    public void setCompleted(boolean completed) { this.completed = completed; }

    /** @brief Açıklama getter. @return Açıklama */
    public String getDescription() { return description; }

    /** @brief Açıklama setter. @param description Açıklama */
    public void setDescription(String description) { this.description = description; }

    /** @brief Öncelik getter. @return Öncelik */
    public String getPriority() { return priority; }

    /** @brief Öncelik setter. @param priority Öncelik */
    public void setPriority(String priority) { this.priority = priority; }

    /** @brief Tekrarlıyor mu getter. @return Tekrarlıyor mu */
    public boolean isRecurring() { return recurring; }

    /** @brief Tekrarlama setter. @param recurring Tekrarlıyor mu */
    public void setRecurring(boolean recurring) { this.recurring = recurring; }

    /** @brief Tekrar deseni getter. @return Tekrar deseni */
    public String getRecurrencePattern() { return recurrencePattern; }

    /** @brief Tekrar deseni setter. @param recurrencePattern Tekrar deseni */
    public void setRecurrencePattern(String recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
    }

    // ── Abstract Metodlar (Polymorphism) ──────────────────────────────

    /**
     * @brief Hatırlatıcının türünü döndürür.
     * @details OOP Polymorphism: her alt sınıf farklı tür döndürür.
     * @return Hatırlatıcı türü string'i
     */
    public abstract String getReminderType();

    /**
     * @brief Hatırlatıcının geçerliliğini doğrular.
     * @details OOP Polymorphism: her alt sınıf kendi kuralını uygular.
     * @return Geçerliyse true
     */
    public abstract boolean validate();

    /**
     * @brief Hatırlatıcının özet bilgisini döndürür (GUI'de gösterilir).
     * @return Özet string
     */
    public abstract String getSummary();

    // ── Yardımcı Metodlar ────────────────────────────────────────────

    /**
     * @brief Planlanan zamanı biçimli string olarak döndürür.
     * @return "dd/MM/yyyy HH:mm" formatında zaman
     */
    public String getFormattedTime() {
        if (scheduledTime == null) return "Belirsiz";
        return scheduledTime.format(FORMATTER);
    }

    /**
     * @brief Hatırlatıcının geçmiş mi olduğunu kontrol eder.
     * @return Planlan zaman geçmişse ve tamamlanmamışsa true
     */
    public boolean isOverdue() {
        if (scheduledTime == null || completed) return false;
        return LocalDateTime.now().isAfter(scheduledTime);
    }

    /**
     * @brief String temsili.
     * @return Hatırlatıcı bilgilerini içeren string
     */
    @Override
    public String toString() {
        return String.format("Reminder{id=%d, type='%s', pet='%s', time='%s', done=%b}",
            id, getReminderType(), petName, getFormattedTime(), completed);
    }

    /**
     * @brief Eşitlik kontrolü — ID bazlı.
     * @param obj Karşılaştırılan nesne
     * @return ID'ler eşitse true
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Reminder)) return false;
        Reminder other = (Reminder) obj;
        return this.id == other.id;
    }

    /**
     * @brief Hash kodu.
     * @return ID'ye göre hash
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
