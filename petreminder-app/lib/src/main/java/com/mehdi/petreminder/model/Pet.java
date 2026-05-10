/**
 * @file Pet.java
 * @brief Tum evcil hayvan turleri icin soyut temel sinif.
 * @details Encapsulation ve abstraction prensiplerini uygular.
 *          Tum pet-specific alt siniflar bu siniftan turetilir.
 */
package com.mehdi.petreminder.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;

/**
 * @class Pet
 * @brief Evcil hayvanin temel soyut sinifi.
 * @details OOP Abstraction: abstract class ile somutlastirilamaz.
 *          OOP Encapsulation: tum alanlar private, getter/setter ile erisim.
 *          Serializable: BinaryRepository icin zorunlu.
 * @author Muhammed Mehdi Karagulle, Ibrahim Demirci, Zumre Uykun
 * @version 1.0
 */
public abstract class Pet implements Serializable {

    /** @brief Serializable icin versiyon sabiti. */
    private static final long serialVersionUID = 1L;

    /** @brief Benzersiz evcil hayvan kimlik numarasi. */
    private int id;

    /** @brief Evcil hayvanin ismi. */
    private String name;

    /** @brief Evcil hayvanin turu (Dog, Cat, Bird). */
    private String species;

    /** @brief Evcil hayvanin dogum tarihi. */
    private LocalDate birthDate;

    /** @brief Evcil hayvana sahip olan kullanicinin ID'si. */
    private int ownerId;

    /** @brief Evcil hayvanin cinsi (erkek/disi). */
    private String gender;

    /** @brief Evcil hayvanin agirligi (kg). */
    private double weight;

    /** @brief Evcil hayvan hakkinda notlar. */
    private String notes;

    /**
     * @brief Varsayilan yapici metod.
     * @details Serialization icin gereklidir.
     */
    protected Pet() {}

    /**
     * @brief Tam parametreli yapici metod.
     * @param id      Benzersiz kimlik numarasi
     * @param name    Evcil hayvanin ismi
     * @param species Hayvan turu
     * @param birthDate Dogum tarihi
     * @param ownerId Sahip kullanici ID'si
     * @param gender  Cinsiyet (Erkek/Disi)
     * @param weight  Agirlik (kg)
     * @param notes   Ek notlar
     */
    protected Pet(int id, String name, String species, LocalDate birthDate,
                  int ownerId, String gender, double weight, String notes) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.birthDate = birthDate;
        this.ownerId = ownerId;
        this.gender = gender;
        this.weight = weight;
        this.notes = notes;
    }

    // ── Getter / Setter ──────────────────────────────────────────────

    /** @brief ID getter. @return Evcil hayvan ID'si */
    public int getId() { return id; }

    /** @brief ID setter. @param id Yeni ID degeri */
    public void setId(int id) { this.id = id; }

    /** @brief Isim getter. @return Evcil hayvan ismi */
    public String getName() { return name; }

    /** @brief Isim setter. @param name Yeni isim */
    public void setName(String name) { this.name = name; }

    /** @brief Tur getter. @return Hayvan turu */
    public String getSpecies() { return species; }

    /** @brief Tur setter. @param species Hayvan turu */
    public void setSpecies(String species) { this.species = species; }

    /** @brief Dogum tarihi getter. @return Dogum tarihi */
    public LocalDate getBirthDate() { return birthDate; }

    /** @brief Dogum tarihi setter. @param birthDate Dogum tarihi */
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    /** @brief Sahip ID getter. @return Sahip kullanici ID'si */
    public int getOwnerId() { return ownerId; }

    /** @brief Sahip ID setter. @param ownerId Sahip kullanici ID'si */
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    /** @brief Cinsiyet getter. @return Cinsiyet */
    public String getGender() { return gender; }

    /** @brief Cinsiyet setter. @param gender Cinsiyet */
    public void setGender(String gender) { this.gender = gender; }

    /** @brief Agirlik getter. @return Agirlik (kg) */
    public double getWeight() { return weight; }

    /** @brief Agirlik setter. @param weight Agirlik (kg) */
    public void setWeight(double weight) { this.weight = weight; }

    /** @brief Notlar getter. @return Notlar */
    public String getNotes() { return notes; }

    /** @brief Notlar setter. @param notes Notlar */
    public void setNotes(String notes) { this.notes = notes; }

    // ── Abstract Metodlar (Polymorphism) ─────────────────────────────

    /**
     * @brief Hayvana ozgu ses dondurur.
     * @details OOP Polymorphism: her alt sinif farkli implementasyon saglar.
     * @return Hayvan sesi string'i
     */
    public abstract String makeSound();

    /**
     * @brief Hayvana ozgu ikon adi dondurur (GUI icin).
     * @return Ikon adi (ornk. "dog.png")
     */
    public abstract String getIconName();

    /**
     * @brief Yas hesaplar.
     * @details Bugun ile dogum tarihi arasindaki farki yil cinsinden dondurur.
     * @return Yas (yil), dogum tarihi null ise -1
     */
    public int getAge() {
        if (birthDate == null) return -1;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * @brief Yas bilgisini formatlı string olarak dondurur.
     * @return "2 yil 3 ay" formatinda yas
     */
    public String getAgeString() {
        if (birthDate == null) return "Bilinmiyor";
        Period p = Period.between(birthDate, LocalDate.now());
        return p.getYears() + " yil " + p.getMonths() + " ay";
    }

    /**
     * @brief Nesneyi string olarak temsil eder.
     * @return Formatli pet bilgisi
     */
    @Override
    public String toString() {
        return String.format("Pet{id=%d, name='%s', species='%s', age=%s}",
                id, name, species, getAgeString());
    }

    /**
     * @brief Iki Pet nesnesinin esitligini karsilastirir.
     * @param obj Karsilastirilacak nesne
     * @return ID'ler esitse true
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Pet)) return false;
        Pet other = (Pet) obj;
        return this.id == other.id;
    }

    /**
     * @brief Hash kodu hesaplar.
     * @return ID'ye gore hash kodu
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
