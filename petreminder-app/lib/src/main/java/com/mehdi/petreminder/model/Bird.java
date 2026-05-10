/**
 * @file Bird.java
 * @brief Kus turunu temsil eden somut sinif.
 */
/**
 * Member documentation.
 */
package com.mehdi.petreminder.model;

import java.time.LocalDate;

/**
 * @class Bird
 * @brief Kus evcil hayvan sinifi.
 * @details OOP Inheritance: Pet sinifından miras alir.
 *          OOP Polymorphism: makeSound() ve getIconName() override edilir.
 * @author Muhammed Mehdi Karagulle, Ibrahim Demirci, Zumre Uykun
 * @version 1.0
 */
public class Bird extends Pet {

    /** @brief Serializable versiyon sabiti. */
    private static final long serialVersionUID = 1L;

    /** @brief Kus turu (Muhabbet kusu, Papagan, vb.). */
    private String birdType;

    /** @brief Konusabiliyor mu. */
    private boolean canTalk;

    /** @brief Varsayilan yapici. */
    public Bird() { super(); }

    /**
     * @brief Temel bilgilerle yapici.
     * @param id Kimlik
     * @param name Isim
     * @param birthDate Dogum tarihi
     * @param ownerId Sahip ID
     */
    public Bird(int id, String name, LocalDate birthDate, int ownerId) {
        super(id, name, "Kus", birthDate, ownerId, "Erkek", 0.0, "");
        this.birdType = "Muhabbet Kusu";
        this.canTalk = false;
    }

    /**
     * @brief Tam parametreli yapici.
     * @param id Kimlik
     * @param name Isim
     * @param birthDate Dogum tarihi
     * @param ownerId Sahip ID
     * @param gender Cinsiyet
     * @param weight Agirlik
     * @param notes Notlar
     * @param birdType Kus turu
     * @param canTalk Konusabiliyor mu
     */
    public Bird(int id, String name, LocalDate birthDate, int ownerId,
                String gender, double weight, String notes,
                String birdType, boolean canTalk) {
        super(id, name, "Kus", birthDate, ownerId, gender, weight, notes);
        this.birdType = birdType;
        this.canTalk = canTalk;
    }

    /** @brief Kus turu getter. @return Kus turu */
    public String getBirdType() { return birdType; }

    /** @brief Kus turu setter. @param birdType Kus turu */
    public void setBirdType(String birdType) { this.birdType = birdType; }

    /** @brief Konusma getter. @return Konusabiliyor mu */
    public boolean isCanTalk() { return canTalk; }

    /** @brief Konusma setter. @param canTalk Konusabiliyor mu */
    public void setCanTalk(boolean canTalk) { this.canTalk = canTalk; }

    /**
     * @brief Kusa ozgu ses.
     * @return "Cik Cik!" veya "Merhaba!" (konusabiliyorsa)
     */
    @Override
    public String makeSound() {
        return canTalk ? "Merhaba!" : "Cik Cik!";
    }

    /**
     * @brief GUI ikon adi.
     * @return "bird"
     */
    @Override
    public String getIconName() { return "bird"; }

    /**
     * @brief String temsili.
     * @return Kus bilgilerini iceren string
     */
    @Override
    public String toString() {
        return String.format("Bird{id=%d, name='%s', type='%s', age=%s, canTalk=%b}",
                getId(), getName(), birdType, getAgeString(), canTalk);
    }
}
