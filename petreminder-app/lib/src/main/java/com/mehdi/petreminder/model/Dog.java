/**
 * @file Dog.java
 * @brief Kopek turunu temsil eden somut sinif.
 */
package com.mehdi.petreminder.model;

import java.time.LocalDate;

/**
 * @class Dog
 * @brief Kopek evcil hayvan sinifi.
 * @details OOP Inheritance: Pet sinifından miras alir.
 *          OOP Polymorphism: makeSound() ve getIconName() override edilir.
 * @author Muhammed Mehdi Karagulle, Ibrahim Demirci, Zumre Uykun
 * @version 1.0
 */
public class Dog extends Pet {

    /** @brief Serializable versiyon sabiti. */
    private static final long serialVersionUID = 1L;

    /** @brief Kopek irki (Golden, Labrador, vb.). */
    private String breed;

    /** @brief Kopek egitim durumu. */
    private boolean trained;

    /** @brief Varsayilan yapici. */
    public Dog() { super(); }

    /**
     * @brief Temel bilgilerle yapici.
     * @param id Kimlik
     * @param name Isim
     * @param birthDate Dogum tarihi
     * @param ownerId Sahip ID
     */
    public Dog(int id, String name, LocalDate birthDate, int ownerId) {
        super(id, name, "Kopek", birthDate, ownerId, "Erkek", 0.0, "");
        this.breed = "Karisik";
        this.trained = false;
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
     * @param trained Egitimli mi
     */
    public Dog(int id, String name, LocalDate birthDate, int ownerId,
               String gender, double weight, String notes,
               String breed, boolean trained) {
        super(id, name, "Kopek", birthDate, ownerId, gender, weight, notes);
        this.breed = breed;
        this.trained = trained;
    }

    /** @brief Irk getter. @return Kopek irki */
    public String getBreed() { return breed; }

    /** @brief Irk setter. @param breed Kopek irki */
    public void setBreed(String breed) { this.breed = breed; }

    /** @brief Egitim durumu getter. @return Egitimli mi */
    public boolean isTrained() { return trained; }

    /** @brief Egitim durumu setter. @param trained Egitimli mi */
    public void setTrained(boolean trained) { this.trained = trained; }

    /**
     * @brief Kopege ozgu ses.
     * @return "Hav! Hav!"
     */
    @Override
    public String makeSound() { return "Hav! Hav!"; }

    /**
     * @brief GUI ikon adi.
     * @return "dog"
     */
    @Override
    public String getIconName() { return "dog"; }

    /**
     * @brief String temsili.
     * @return Kopek bilgilerini iceren string
     */
    @Override
    public String toString() {
        return String.format("Dog{id=%d, name='%s', breed='%s', age=%s, trained=%b}",
                getId(), getName(), breed, getAgeString(), trained);
    }
}
