/**
 * @file Cat.java
 * @brief Kedi turunu temsil eden somut sinif.
 */
package com.mehdi.petreminder.model;

import java.time.LocalDate;

/**
 * @class Cat
 * @brief Kedi evcil hayvan sinifi.
 * @details OOP Inheritance: Pet sinifından miras alir.
 *          OOP Polymorphism: makeSound() ve getIconName() override edilir.
 * @author Muhammed Mehdi Karagulle, Ibrahim Demirci, Zumre Uykun
 * @version 1.0
 */
public class Cat extends Pet {

    /** @brief Serializable versiyon sabiti. */
    private static final long serialVersionUID = 1L;

    /** @brief Kedi irki (Persian, Siamese, vb.). */
    private String breed;

    /** @brief Ic mekan kedisi mi. */
    private boolean indoor;

    /** @brief Varsayilan yapici. */
    public Cat() { super(); }

    /**
     * @brief Temel bilgilerle yapici.
     * @param id Kimlik
     * @param name Isim
     * @param birthDate Dogum tarihi
     * @param ownerId Sahip ID
     */
    public Cat(int id, String name, LocalDate birthDate, int ownerId) {
        super(id, name, "Kedi", birthDate, ownerId, "Disi", 0.0, "");
        this.breed = "Karisik";
        this.indoor = true;
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
     * @param breed Irk
     * @param indoor Ic mekan mi
     */
    public Cat(int id, String name, LocalDate birthDate, int ownerId,
               String gender, double weight, String notes,
               String breed, boolean indoor) {
        super(id, name, "Kedi", birthDate, ownerId, gender, weight, notes);
        this.breed = breed;
        this.indoor = indoor;
    }

    /** @brief Irk getter. @return Kedi irki */
    public String getBreed() { return breed; }

    /** @brief Irk setter. @param breed Kedi irki */
    public void setBreed(String breed) { this.breed = breed; }

    /** @brief Ic mekan getter. @return Ic mekan mi */
    public boolean isIndoor() { return indoor; }

    /** @brief Ic mekan setter. @param indoor Ic mekan mi */
    public void setIndoor(boolean indoor) { this.indoor = indoor; }

    /**
     * @brief Kediye ozgu ses.
     * @return "Miyav!"
     */
    @Override
    public String makeSound() { return "Miyav!"; }

    /**
     * @brief GUI ikon adi.
     * @return "cat"
     */
    @Override
    public String getIconName() { return "cat"; }

    /**
     * @brief String temsili.
     * @return Kedi bilgilerini iceren string
     */
    @Override
    public String toString() {
        return String.format("Cat{id=%d, name='%s', breed='%s', age=%s, indoor=%b}",
                getId(), getName(), breed, getAgeString(), indoor);
    }
}
