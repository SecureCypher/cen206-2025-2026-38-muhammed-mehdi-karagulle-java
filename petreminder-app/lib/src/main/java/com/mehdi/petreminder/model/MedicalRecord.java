/**
 * @file MedicalRecord.java
 * @brief Evcil hayvan sağlık kaydı modeli.
 * @details Composition ilişkisi: MedicalRecord Pet'e ait.
 */
package com.mehdi.petreminder.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @class MedicalRecord
 * @brief Evcil hayvanın sağlık kaydı.
 * @details OOP Composition: Pet nesnesiyle composition ilişkisi.
 *          OOP Encapsulation: tüm alanlar private.
 *          Serializable: BinaryRepository için.
 * @author Muhammed Mehdi Karagülle, İbrahim Demirci, Zümre Uykun
 * @version 1.0
 */
public class MedicalRecord implements Serializable {

    /** @brief Serializable versiyon sabiti. */
    private static final long serialVersionUID = 1L;

    /** @brief Tarih formatı. */
    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** @brief Kayıt ID. */
    private int id;

    /** @brief İlgili pet ID. */
    private int petId;

    /** @brief İlgili pet adı. */
    private String petName;

    /** @brief Kayıt tarihi. */
    private LocalDate recordDate;

    /**
     * @brief Kayıt türü.
     * @details ASII, AMELIYAT, KONTROL, HASTALIK, KAS_YAPIMI
     */
    private String recordType;

    /** @brief Teşhis veya işlem açıklaması. */
    private String diagnosis;

    /** @brief Tedavi detayı. */
    private String treatment;

    /** @brief Uygulayan veteriner. */
    private String veterinarianName;

    /** @brief Maliyet (TL). */
    private double cost;

    /** @brief Sonraki kontrol tarihi. */
    private LocalDate nextCheckDate;

    /** @brief Ek notlar. */
    private String notes;

    /** @brief Aşı adı — recordType==ASII ise dolu. */
    private String vaccineName;

    /** @brief Varsayılan yapıcı. */
    public MedicalRecord() {}

    /**
     * @brief Temel parametreli yapıcı.
     * @param id               Kayıt ID
     * @param petId            Pet ID
     * @param petName          Pet adı
     * @param recordDate       Tarih
     * @param recordType       Tür
     * @param diagnosis        Teşhis
     * @param veterinarianName Veteriner adı
     */
    public MedicalRecord(int id, int petId, String petName,
                         LocalDate recordDate, String recordType,
                         String diagnosis, String veterinarianName) {
        this.id = id;
        this.petId = petId;
        this.petName = petName;
        this.recordDate = recordDate;
        this.recordType = recordType;
        this.diagnosis = diagnosis;
        this.veterinarianName = veterinarianName;
        this.treatment = "";
        this.cost = 0.0;
        this.notes = "";
        this.vaccineName = "";
    }

    /** @brief ID getter. @return Kayıt ID */
    public int getId() { return id; }

    /** @brief ID setter. @param id Kayıt ID */
    public void setId(int id) { this.id = id; }

    /** @brief Pet ID getter. @return Pet ID */
    public int getPetId() { return petId; }

    /** @brief Pet ID setter. @param petId Pet ID */
    public void setPetId(int petId) { this.petId = petId; }

    /** @brief Pet adı getter. @return Pet adı */
    public String getPetName() { return petName; }

    /** @brief Pet adı setter. @param petName Pet adı */
    public void setPetName(String petName) { this.petName = petName; }

    /** @brief Kayıt tarihi getter. @return Kayıt tarihi */
    public LocalDate getRecordDate() { return recordDate; }

    /** @brief Kayıt tarihi setter. @param recordDate Kayıt tarihi */
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    /** @brief Kayıt türü getter. @return Kayıt türü */
    public String getRecordType() { return recordType; }

    /** @brief Kayıt türü setter. @param recordType Kayıt türü */
    public void setRecordType(String recordType) { this.recordType = recordType; }

    /** @brief Teşhis getter. @return Teşhis */
    public String getDiagnosis() { return diagnosis; }

    /** @brief Teşhis setter. @param diagnosis Teşhis */
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    /** @brief Tedavi getter. @return Tedavi */
    public String getTreatment() { return treatment; }

    /** @brief Tedavi setter. @param treatment Tedavi */
    public void setTreatment(String treatment) { this.treatment = treatment; }

    /** @brief Veteriner getter. @return Veteriner */
    public String getVeterinarianName() { return veterinarianName; }

    /** @brief Veteriner setter. @param veterinarianName Veteriner */
    public void setVeterinarianName(String veterinarianName) {
        this.veterinarianName = veterinarianName;
    }

    /** @brief Maliyet getter. @return Maliyet (TL) */
    public double getCost() { return cost; }

    /** @brief Maliyet setter. @param cost Maliyet */
    public void setCost(double cost) { this.cost = cost; }

    /** @brief Sonraki kontrol getter. @return Sonraki kontrol tarihi */
    public LocalDate getNextCheckDate() { return nextCheckDate; }

    /** @brief Sonraki kontrol setter. @param nextCheckDate Tarih */
    public void setNextCheckDate(LocalDate nextCheckDate) {
        this.nextCheckDate = nextCheckDate;
    }

    /** @brief Notlar getter. @return Notlar */
    public String getNotes() { return notes; }

    /** @brief Notlar setter. @param notes Notlar */
    public void setNotes(String notes) { this.notes = notes; }

    /** @brief Aşı adı getter. @return Aşı adı */
    public String getVaccineName() { return vaccineName; }

    /** @brief Aşı adı setter. @param vaccineName Aşı adı */
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }

    /**
     * @brief Biçimli tarih döndürür.
     * @return dd/MM/yyyy formatında tarih
     */
    public String getFormattedDate() {
        if (recordDate == null) return "Belirsiz";
        return recordDate.format(DATE_FMT);
    }

    /**
     * @brief String temsili.
     * @return Sağlık kaydı özeti
     */
    @Override
    public String toString() {
        return String.format("MedicalRecord{id=%d, pet='%s', type='%s', date='%s'}",
            id, petName, recordType, getFormattedDate());
    }

    /**
     * @brief Eşitlik kontrolü.
     * @param obj Karşılaştırılan nesne
     * @return ID'ler eşitse true
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof MedicalRecord)) return false;
        MedicalRecord other = (MedicalRecord) obj;
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
