/**
 * @file FeedingReminder.java
 * @brief Besleme hatırlatıcısı sınıfı.
 */
package com.mehdi.petreminder.model;

import java.time.LocalDateTime;

/**
 * @class FeedingReminder
 * @brief Evcil hayvan besleme hatırlatıcısı.
 * @details OOP Inheritance: Reminder'dan miras alır.
 *          OOP Polymorphism: getReminderType(), validate(), getSummary() override.
 * @author Muhammed Mehdi Karagülle, İbrahim Demirci, Zümre Uykun
 * @version 1.0
 */
public class FeedingReminder extends Reminder {

    /** @brief Serializable versiyon sabiti. */
    private static final long serialVersionUID = 1L;

    /** @brief Besin türü (kuru mama, yaş mama, ev yemeği). */
    private String foodType;

    /** @brief Porsiyon miktarı (gram). */
    private double portionGrams;

    /** @brief Su değişimi de yapılacak mı. */
    private boolean includesWaterChange;

    /** @brief Varsayılan yapıcı. */
    public FeedingReminder() { super(); }

    /**
     * @brief Temel parametreli yapıcı.
     * @param id            ID
     * @param petId         Pet ID
     * @param petName       Pet adı
     * @param scheduledTime Zaman
     * @param foodType      Besin türü
     * @param portionGrams  Porsiyon (gram)
     */
    public FeedingReminder(int id, int petId, String petName,
                           LocalDateTime scheduledTime,
                           String foodType, double portionGrams) {
        super(id, petId, petName, scheduledTime,
              "Besleme: " + foodType, "ORTA", false, "");
        this.foodType = foodType;
        this.portionGrams = portionGrams;
        this.includesWaterChange = false;
    }

    /**
     * @brief Tam parametreli yapıcı.
     * @param id                  ID
     * @param petId               Pet ID
     * @param petName             Pet adı
     * @param scheduledTime       Zaman
     * @param description         Açıklama
     * @param priority            Öncelik
     * @param recurring           Tekrarlıyor mu
     * @param recurrencePattern   Tekrar deseni
     * @param foodType            Besin türü
     * @param portionGrams        Porsiyon (gram)
     * @param includesWaterChange Su değişimi dahil mi
     */
    public FeedingReminder(int id, int petId, String petName,
                           LocalDateTime scheduledTime, String description,
                           String priority, boolean recurring,
                           String recurrencePattern, String foodType,
                           double portionGrams, boolean includesWaterChange) {
        super(id, petId, petName, scheduledTime, description,
              priority, recurring, recurrencePattern);
        this.foodType = foodType;
        this.portionGrams = portionGrams;
        this.includesWaterChange = includesWaterChange;
    }

    /** @brief Besin türü getter. @return Besin türü */
    public String getFoodType() { return foodType; }

    /** @brief Besin türü setter. @param foodType Besin türü */
    public void setFoodType(String foodType) { this.foodType = foodType; }

    /** @brief Porsiyon getter. @return Porsiyon (gram) */
    public double getPortionGrams() { return portionGrams; }

    /** @brief Porsiyon setter. @param portionGrams Porsiyon (gram) */
    public void setPortionGrams(double portionGrams) { this.portionGrams = portionGrams; }

    /** @brief Su değişimi getter. @return Su değişimi dahil mi */
    public boolean isIncludesWaterChange() { return includesWaterChange; }

    /** @brief Su değişimi setter. @param includesWaterChange Su değişimi */
    public void setIncludesWaterChange(boolean includesWaterChange) {
        this.includesWaterChange = includesWaterChange;
    }

    /**
     * @brief Hatırlatıcı türü.
     * @return "Besleme"
     */
    @Override
    public String getReminderType() { return "Besleme"; }

    /**
     * @brief Besleme hatırlatıcısını doğrular.
     * @return foodType boş değilse ve porsiyon > 0 ise true
     */
    @Override
    public boolean validate() {
        return foodType != null && !foodType.trim().isEmpty() && portionGrams > 0;
    }

    /**
     * @brief Özet bilgi.
     * @return "Besleme: Kuru Mama (150g)"
     */
    @Override
    public String getSummary() {
        return String.format("Besleme: %s (%.0fg)%s",
            foodType, portionGrams,
            includesWaterChange ? " + Su değişimi" : "");
    }

    /**
     * @brief String temsili.
     * @return Besleme hatırlatıcısı bilgisi
     */
    @Override
    public String toString() {
        return String.format("FeedingReminder{id=%d, pet='%s', food='%s', portion=%.0fg}",
            getId(), getPetName(), foodType, portionGrams);
    }
}
