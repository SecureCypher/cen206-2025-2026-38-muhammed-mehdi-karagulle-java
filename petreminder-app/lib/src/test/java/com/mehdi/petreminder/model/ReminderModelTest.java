/**
 * @file ReminderModelTest.java
 * @brief Reminder hiyerarşisi + User + MedicalRecord için JUnit 5 testleri.
 * @details %100 JaCoCo coverage hedefler.
 *          PDF zorunluluğu: JUnit5, @Test, @BeforeEach kullanımı.
 */
package com.mehdi.petreminder.model;

import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @class ReminderModelTest
 * @brief Reminder, FeedingReminder, MedicationReminder,
 *        ExerciseReminder, GroomingReminder, VetAppointment,
 *        User, MedicalRecord test sınıfı.
 * @author Muhammed Mehdi Karagülle, İbrahim Demirci, Zümre Uykun
 */
class ReminderModelTest {

    private FeedingReminder feeding;
    private MedicationReminder medication;
    private ExerciseReminder exercise;
    private GroomingReminder grooming;
    private VetAppointment vet;
    private User user;
    private MedicalRecord record;
    private LocalDateTime now;

    /** @brief Her testten önce nesneler hazırlanır. */
    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().plusDays(1);

        feeding = new FeedingReminder(1, 10, "Karamel", now, "Kuru Mama", 150.0);
        medication = new MedicationReminder(2, 10, "Karamel", now, "Antibiyotik", 250.0, "mg");
        exercise = new ExerciseReminder(3, 10, "Karamel", now, "Yuruyus", 30);
        grooming = new GroomingReminder(4, 10, "Karamel", now, "Tiras", false);
        vet = new VetAppointment(5, 10, "Karamel", now, "Dr. Ali", "Rize Vet", "Rutin");
        user = new User(1, "mehdi", "mehdi@test.com", "hash123", "Mehdi K");
        record = new MedicalRecord(1, 10, "Karamel",
            LocalDate.now(), "ASII", "Kuduz asisi", "Dr. Ali");
    }

    // ── FeedingReminder ───────────────────────────────────────────────

    @Test void testFeedingDefaultConstructor() { assertNotNull(new FeedingReminder()); }

    @Test void testFeedingGetId() { assertEquals(1, feeding.getId()); }

    @Test void testFeedingSetId() { feeding.setId(99); assertEquals(99, feeding.getId()); }

    @Test void testFeedingGetPetId() { assertEquals(10, feeding.getPetId()); }

    @Test void testFeedingSetPetId() { feeding.setPetId(20); assertEquals(20, feeding.getPetId()); }

    @Test void testFeedingGetPetName() { assertEquals("Karamel", feeding.getPetName()); }

    @Test void testFeedingSetPetName() { feeding.setPetName("Boncuk"); assertEquals("Boncuk", feeding.getPetName()); }

    @Test void testFeedingGetScheduledTime() { assertNotNull(feeding.getScheduledTime()); }

    @Test void testFeedingSetScheduledTime() {
        LocalDateTime t = LocalDateTime.now();
        feeding.setScheduledTime(t);
        assertEquals(t, feeding.getScheduledTime());
    }

    @Test void testFeedingIsCompleted() { assertFalse(feeding.isCompleted()); }

    @Test void testFeedingSetCompleted() { feeding.setCompleted(true); assertTrue(feeding.isCompleted()); }

    @Test void testFeedingGetDescription() { assertNotNull(feeding.getDescription()); }

    @Test void testFeedingSetDescription() {
        feeding.setDescription("Sabah beslemesi");
        assertEquals("Sabah beslemesi", feeding.getDescription());
    }

    @Test void testFeedingGetPriority() { assertEquals("ORTA", feeding.getPriority()); }

    @Test void testFeedingSetPriority() { feeding.setPriority("YUKSEK"); assertEquals("YUKSEK", feeding.getPriority()); }

    @Test void testFeedingIsRecurring() { assertFalse(feeding.isRecurring()); }

    @Test void testFeedingSetRecurring() { feeding.setRecurring(true); assertTrue(feeding.isRecurring()); }

    @Test void testFeedingGetRecurrencePattern() { assertNotNull(feeding.getRecurrencePattern()); }

    @Test void testFeedingSetRecurrencePattern() {
        feeding.setRecurrencePattern("Gunluk");
        assertEquals("Gunluk", feeding.getRecurrencePattern());
    }

    @Test void testFeedingGetFoodType() { assertEquals("Kuru Mama", feeding.getFoodType()); }

    @Test void testFeedingSetFoodType() { feeding.setFoodType("Yas Mama"); assertEquals("Yas Mama", feeding.getFoodType()); }

    @Test void testFeedingGetPortion() { assertEquals(150.0, feeding.getPortionGrams(), 0.001); }

    @Test void testFeedingSetPortion() { feeding.setPortionGrams(200.0); assertEquals(200.0, feeding.getPortionGrams(), 0.001); }

    @Test void testFeedingIsWaterChange() { assertFalse(feeding.isIncludesWaterChange()); }

    @Test void testFeedingSetWaterChange() { feeding.setIncludesWaterChange(true); assertTrue(feeding.isIncludesWaterChange()); }

    @Test void testFeedingGetReminderType() { assertEquals("Besleme", feeding.getReminderType()); }

    @Test void testFeedingValidateTrue() { assertTrue(feeding.validate()); }

    @Test void testFeedingValidateFalseEmptyFood() {
        feeding.setFoodType("");
        assertFalse(feeding.validate());
    }

    @Test void testFeedingValidateFalseNullFood() {
        feeding.setFoodType(null);
        assertFalse(feeding.validate());
    }

    @Test void testFeedingValidateFalseZeroPortion() {
        feeding.setPortionGrams(0);
        assertFalse(feeding.validate());
    }

    @Test void testFeedingGetSummaryNoWater() {
        assertTrue(feeding.getSummary().contains("Besleme"));
    }

    @Test void testFeedingGetSummaryWithWater() {
        feeding.setIncludesWaterChange(true);
        assertTrue(feeding.getSummary().contains("Su"));
    }

    @Test void testFeedingToString() { assertTrue(feeding.toString().contains("Karamel")); }

    @Test void testFeedingGetFormattedTime() { assertNotNull(feeding.getFormattedTime()); }

    @Test void testFeedingGetFormattedTimeNull() {
        feeding.setScheduledTime(null);
        assertEquals("Belirsiz", feeding.getFormattedTime());
    }

    @Test void testFeedingIsOverdueFuture() { assertFalse(feeding.isOverdue()); }

    @Test void testFeedingIsOverduePast() {
        feeding.setScheduledTime(LocalDateTime.now().minusDays(1));
        assertTrue(feeding.isOverdue());
    }

    @Test void testFeedingIsOverdueCompleted() {
        feeding.setScheduledTime(LocalDateTime.now().minusDays(1));
        feeding.setCompleted(true);
        assertFalse(feeding.isOverdue());
    }

    @Test void testFeedingIsOverdueNull() {
        feeding.setScheduledTime(null);
        assertFalse(feeding.isOverdue());
    }

    @Test void testFeedingEquals() {
        FeedingReminder f2 = new FeedingReminder(1, 5, "Baska", now, "Yas", 50);
        assertEquals(feeding, f2);
    }

    @Test void testFeedingEqualsNull() { assertNotEquals(feeding, null); }

    @Test void testFeedingEqualsSelf() { assertEquals(feeding, feeding); }

    @Test void testFeedingEqualsOtherType() { assertNotEquals(feeding, "string"); }

    @Test void testFeedingNotEquals() {
        FeedingReminder f2 = new FeedingReminder(99, 5, "Baska", now, "Yas", 50);
        assertNotEquals(feeding, f2);
    }

    @Test void testFeedingHashCode() {
        FeedingReminder f2 = new FeedingReminder(1, 5, "Baska", now, "Yas", 50);
        assertEquals(feeding.hashCode(), f2.hashCode());
    }

    @Test void testFeedingFullConstructor() {
        FeedingReminder f = new FeedingReminder(10, 1, "Max", now, "Aciklama",
            "YUKSEK", true, "Gunluk", "Ev Yemegi", 300.0, true);
        assertEquals("Ev Yemegi", f.getFoodType());
        assertTrue(f.isIncludesWaterChange());
    }

    // ── MedicationReminder ────────────────────────────────────────────

    @Test void testMedDefaultConstructor() { assertNotNull(new MedicationReminder()); }

    @Test void testMedGetReminderType() { assertEquals("Ilac", medication.getReminderType()); }

    @Test void testMedGetMedicationName() { assertEquals("Antibiyotik", medication.getMedicationName()); }

    @Test void testMedSetMedicationName() {
        medication.setMedicationName("Vitamin");
        assertEquals("Vitamin", medication.getMedicationName());
    }

    @Test void testMedGetDosage() { assertEquals(250.0, medication.getDosage(), 0.001); }

    @Test void testMedSetDosage() { medication.setDosage(500.0); assertEquals(500.0, medication.getDosage(), 0.001); }

    @Test void testMedGetDosageUnit() { assertEquals("mg", medication.getDosageUnit()); }

    @Test void testMedSetDosageUnit() { medication.setDosageUnit("ml"); assertEquals("ml", medication.getDosageUnit()); }

    @Test void testMedIsPrescribed() { assertFalse(medication.isPrescribed()); }

    @Test void testMedSetPrescribed() { medication.setPrescribed(true); assertTrue(medication.isPrescribed()); }

    @Test void testMedGetVetName() { assertNotNull(medication.getVeterinarianName()); }

    @Test void testMedSetVetName() {
        medication.setVeterinarianName("Dr. Ahmet");
        assertEquals("Dr. Ahmet", medication.getVeterinarianName());
    }

    @Test void testMedValidateTrue() { assertTrue(medication.validate()); }

    @Test void testMedValidateFalseNullName() { medication.setMedicationName(null); assertFalse(medication.validate()); }

    @Test void testMedValidateFalseEmptyName() { medication.setMedicationName(""); assertFalse(medication.validate()); }

    @Test void testMedValidateFalseZeroDose() { medication.setDosage(0); assertFalse(medication.validate()); }

    @Test void testMedValidateFalseNullUnit() { medication.setDosageUnit(null); assertFalse(medication.validate()); }

    @Test void testMedValidateFalseEmptyUnit() { medication.setDosageUnit(""); assertFalse(medication.validate()); }

    @Test void testMedGetSummaryNotPrescribed() { assertFalse(medication.getSummary().contains("Receteli")); }

    @Test void testMedGetSummaryPrescribed() {
        medication.setPrescribed(true);
        assertTrue(medication.getSummary().contains("Receteli"));
    }

    @Test void testMedToString() { assertTrue(medication.toString().contains("Antibiyotik")); }

    @Test void testMedFullConstructor() {
        MedicationReminder m = new MedicationReminder(20, 1, "Max", now,
            "Aciklama", "YUKSEK", true, "Gunluk",
            "Probiyotik", 100.0, "ml", true, "Dr. Ayse");
        assertTrue(m.isPrescribed());
        assertEquals("Dr. Ayse", m.getVeterinarianName());
    }

    // ── ExerciseReminder ──────────────────────────────────────────────

    @Test void testExDefaultConstructor() { assertNotNull(new ExerciseReminder()); }

    @Test void testExGetReminderType() { assertEquals("Egzersiz", exercise.getReminderType()); }

    @Test void testExGetExerciseType() { assertEquals("Yuruyus", exercise.getExerciseType()); }

    @Test void testExSetExerciseType() { exercise.setExerciseType("Kos"); assertEquals("Kos", exercise.getExerciseType()); }

    @Test void testExGetDuration() { assertEquals(30, exercise.getDurationMinutes()); }

    @Test void testExSetDuration() { exercise.setDurationMinutes(60); assertEquals(60, exercise.getDurationMinutes()); }

    @Test void testExGetDistance() { assertEquals(0.0, exercise.getDistanceKm(), 0.001); }

    @Test void testExSetDistance() { exercise.setDistanceKm(5.0); assertEquals(5.0, exercise.getDistanceKm(), 0.001); }

    @Test void testExValidateTrue() { assertTrue(exercise.validate()); }

    @Test void testExValidateFalseEmpty() { exercise.setExerciseType(""); assertFalse(exercise.validate()); }

    @Test void testExValidateFalseNull() { exercise.setExerciseType(null); assertFalse(exercise.validate()); }

    @Test void testExValidateFalseZeroDuration() { exercise.setDurationMinutes(0); assertFalse(exercise.validate()); }

    @Test void testExGetSummaryNoDistance() { assertFalse(exercise.getSummary().contains("km")); }

    @Test void testExGetSummaryWithDistance() {
        exercise.setDistanceKm(3.0);
        assertTrue(exercise.getSummary().contains("km"));
    }

    @Test void testExToString() { assertTrue(exercise.toString().contains("Yuruyus")); }

    @Test void testExFullConstructor() {
        ExerciseReminder e = new ExerciseReminder(30, 1, "Max", now,
            "Aciklama", "ORTA", true, "Gunluk", "Oyun", 45, 2.5);
        assertEquals(2.5, e.getDistanceKm(), 0.001);
    }

    // ── GroomingReminder ──────────────────────────────────────────────

    @Test void testGrDefaultConstructor() { assertNotNull(new GroomingReminder()); }

    @Test void testGrGetReminderType() { assertEquals("Bakim", grooming.getReminderType()); }

    @Test void testGrGetGroomingType() { assertEquals("Tiras", grooming.getGroomingType()); }

    @Test void testGrSetGroomingType() { grooming.setGroomingType("Banyo"); assertEquals("Banyo", grooming.getGroomingType()); }

    @Test void testGrIsProfessional() { assertFalse(grooming.isProfessional()); }

    @Test void testGrSetProfessional() { grooming.setProfessional(true); assertTrue(grooming.isProfessional()); }

    @Test void testGrGetGroomerName() { assertNotNull(grooming.getGroomerName()); }

    @Test void testGrSetGroomerName() { grooming.setGroomerName("Ali Usta"); assertEquals("Ali Usta", grooming.getGroomerName()); }

    @Test void testGrValidateTrue() { assertTrue(grooming.validate()); }

    @Test void testGrValidateFalseEmpty() { grooming.setGroomingType(""); assertFalse(grooming.validate()); }

    @Test void testGrValidateFalseNull() { grooming.setGroomingType(null); assertFalse(grooming.validate()); }

    @Test void testGrGetSummaryBasic() { assertFalse(grooming.getSummary().contains("Profesyonel")); }

    @Test void testGrGetSummaryProfessional() {
        grooming.setProfessional(true);
        assertTrue(grooming.getSummary().contains("Profesyonel"));
    }

    @Test void testGrGetSummaryWithGroomer() {
        grooming.setGroomerName("Ali");
        assertTrue(grooming.getSummary().contains("Ali"));
    }

    // Groomer null ise getSummary groomer bilgisi içermemeli — branch coverage
    @Test void testGrGetSummaryNullGroomer() {
        grooming.setGroomerName(null);
        String summary = grooming.getSummary();
        assertFalse(summary.contains(" - "));
    }

    @Test void testGrToString() { assertTrue(grooming.toString().contains("Tiras")); }

    @Test void testGrFullConstructor() {
        GroomingReminder g = new GroomingReminder(40, 1, "Max", now,
            "Aciklama", "DUSUK", false, "", "Tirnak", true, "Mehmet Usta");
        assertTrue(g.isProfessional());
        assertEquals("Mehmet Usta", g.getGroomerName());
    }

    // ── VetAppointment ────────────────────────────────────────────────

    @Test void testVetDefaultConstructor() { assertNotNull(new VetAppointment()); }

    @Test void testVetGetReminderType() { assertEquals("Veteriner", vet.getReminderType()); }

    @Test void testVetGetVetName() { assertEquals("Dr. Ali", vet.getVeterinarianName()); }

    @Test void testVetSetVetName() { vet.setVeterinarianName("Dr. Ayse"); assertEquals("Dr. Ayse", vet.getVeterinarianName()); }

    @Test void testVetGetClinicName() { assertEquals("Rize Vet", vet.getClinicName()); }

    @Test void testVetSetClinicName() { vet.setClinicName("Merkez Klinik"); assertEquals("Merkez Klinik", vet.getClinicName()); }

    @Test void testVetGetReason() { assertEquals("Rutin", vet.getReason()); }

    @Test void testVetSetReason() { vet.setReason("Asi"); assertEquals("Asi", vet.getReason()); }

    @Test void testVetGetCost() { assertEquals(0.0, vet.getEstimatedCost(), 0.001); }

    @Test void testVetSetCost() { vet.setEstimatedCost(500.0); assertEquals(500.0, vet.getEstimatedCost(), 0.001); }

    @Test void testVetIsConfirmed() { assertFalse(vet.isConfirmed()); }

    @Test void testVetSetConfirmed() { vet.setConfirmed(true); assertTrue(vet.isConfirmed()); }

    @Test void testVetGetPhone() { assertNotNull(vet.getClinicPhone()); }

    @Test void testVetSetPhone() { vet.setClinicPhone("0464-123-4567"); assertEquals("0464-123-4567", vet.getClinicPhone()); }

    @Test void testVetValidateTrue() { assertTrue(vet.validate()); }

    @Test void testVetValidateFalseNullVet() { vet.setVeterinarianName(null); assertFalse(vet.validate()); }

    @Test void testVetValidateFalseEmptyVet() { vet.setVeterinarianName(""); assertFalse(vet.validate()); }

    @Test void testVetValidateFalseNullClinic() { vet.setClinicName(null); assertFalse(vet.validate()); }

    @Test void testVetValidateFalseEmptyClinic() { vet.setClinicName(""); assertFalse(vet.validate()); }

    @Test void testVetValidateFalseNullReason() { vet.setReason(null); assertFalse(vet.validate()); }

    @Test void testVetValidateFalseEmptyReason() { vet.setReason(""); assertFalse(vet.validate()); }

    @Test void testVetGetSummaryUnconfirmed() { assertTrue(vet.getSummary().contains("Bekliyor")); }

    @Test void testVetGetSummaryConfirmed() {
        vet.setConfirmed(true);
        assertTrue(vet.getSummary().contains("Onaylandi"));
    }

    @Test void testVetToString() { assertTrue(vet.toString().contains("Dr. Ali")); }

    @Test void testVetEquals() {
        VetAppointment v2 = new VetAppointment(5, 2, "Baska", now, "Dr. X", "Klinik", "Kontrol");
        assertEquals(vet, v2);
    }

    @Test void testVetFullConstructor() {
        VetAppointment v = new VetAppointment(50, 1, "Max", now, "Aciklama",
            "YUKSEK", false, "", "Dr. Zeynep", "Trabzon Vet",
            "Ameliyat", 2000.0, true, "0462-111-2222");
        assertTrue(v.isConfirmed());
        assertEquals(2000.0, v.getEstimatedCost(), 0.001);
    }

    // ── User ──────────────────────────────────────────────────────────

    @Test void testUserDefaultConstructor() { assertNotNull(new User()); }

    @Test void testUserGetId() { assertEquals(1, user.getId()); }

    @Test void testUserSetId() { user.setId(99); assertEquals(99, user.getId()); }

    @Test void testUserGetUsername() { assertEquals("mehdi", user.getUsername()); }

    @Test void testUserSetUsername() { user.setUsername("ibrahim"); assertEquals("ibrahim", user.getUsername()); }

    @Test void testUserGetEmail() { assertEquals("mehdi@test.com", user.getEmail()); }

    @Test void testUserSetEmail() { user.setEmail("x@y.com"); assertEquals("x@y.com", user.getEmail()); }

    @Test void testUserGetPasswordHash() { assertEquals("hash123", user.getPasswordHash()); }

    @Test void testUserSetPasswordHash() { user.setPasswordHash("newhash"); assertEquals("newhash", user.getPasswordHash()); }

    @Test void testUserGetFullName() { assertEquals("Mehdi K", user.getFullName()); }

    @Test void testUserSetFullName() { user.setFullName("Ibrahim D"); assertEquals("Ibrahim D", user.getFullName()); }

    @Test void testUserIsActive() { assertTrue(user.isActive()); }

    @Test void testUserSetActive() { user.setActive(false); assertFalse(user.isActive()); }

    @Test void testUserGetCreatedAt() { assertNotNull(user.getCreatedAt()); }

    @Test void testUserSetCreatedAt() {
        LocalDateTime t = LocalDateTime.now();
        user.setCreatedAt(t);
        assertEquals(t, user.getCreatedAt());
    }

    @Test void testUserGetLastLogin() { assertNull(user.getLastLogin()); }

    @Test void testUserSetLastLogin() {
        LocalDateTime t = LocalDateTime.now();
        user.setLastLogin(t);
        assertEquals(t, user.getLastLogin());
    }

    @Test void testUserToStringHidesPassword() {
        String s = user.toString();
        assertFalse(s.contains("hash123")); // KVKK: şifre gizlenmeli
        assertTrue(s.contains("mehdi"));
    }

    @Test void testUserEquals() {
        User u2 = new User(1, "baska", "b@c.com", "hash", "B");
        assertEquals(user, u2);
    }

    @Test void testUserEqualsNull() { assertNotEquals(user, null); }

    @Test void testUserEqualsSelf() { assertEquals(user, user); }

    @Test void testUserEqualsOtherType() { assertNotEquals(user, "string"); }

    @Test void testUserNotEquals() {
        User u2 = new User(99, "baska", "b@c.com", "hash", "B");
        assertNotEquals(user, u2);
    }

    @Test void testUserHashCode() {
        User u2 = new User(1, "baska", "b@c.com", "hash", "B");
        assertEquals(user.hashCode(), u2.hashCode());
    }

    // ── MedicalRecord ─────────────────────────────────────────────────

    @Test void testRecordDefaultConstructor() { assertNotNull(new MedicalRecord()); }

    @Test void testRecordGetId() { assertEquals(1, record.getId()); }

    @Test void testRecordSetId() { record.setId(99); assertEquals(99, record.getId()); }

    @Test void testRecordGetPetId() { assertEquals(10, record.getPetId()); }

    @Test void testRecordSetPetId() { record.setPetId(20); assertEquals(20, record.getPetId()); }

    @Test void testRecordGetPetName() { assertEquals("Karamel", record.getPetName()); }

    @Test void testRecordSetPetName() { record.setPetName("Max"); assertEquals("Max", record.getPetName()); }

    @Test void testRecordGetRecordDate() { assertNotNull(record.getRecordDate()); }

    @Test void testRecordSetRecordDate() {
        LocalDate d = LocalDate.of(2025, 1, 1);
        record.setRecordDate(d);
        assertEquals(d, record.getRecordDate());
    }

    @Test void testRecordGetRecordType() { assertEquals("ASII", record.getRecordType()); }

    @Test void testRecordSetRecordType() { record.setRecordType("AMELIYAT"); assertEquals("AMELIYAT", record.getRecordType()); }

    @Test void testRecordGetDiagnosis() { assertEquals("Kuduz asisi", record.getDiagnosis()); }

    @Test void testRecordSetDiagnosis() { record.setDiagnosis("Gribe yakalandi"); assertEquals("Gribe yakalandi", record.getDiagnosis()); }

    @Test void testRecordGetTreatment() { assertNotNull(record.getTreatment()); }

    @Test void testRecordSetTreatment() { record.setTreatment("Antibiyotik"); assertEquals("Antibiyotik", record.getTreatment()); }

    @Test void testRecordGetVetName() { assertEquals("Dr. Ali", record.getVeterinarianName()); }

    @Test void testRecordSetVetName() { record.setVeterinarianName("Dr. Zeynep"); assertEquals("Dr. Zeynep", record.getVeterinarianName()); }

    @Test void testRecordGetCost() { assertEquals(0.0, record.getCost(), 0.001); }

    @Test void testRecordSetCost() { record.setCost(350.0); assertEquals(350.0, record.getCost(), 0.001); }

    @Test void testRecordGetNextCheck() { assertNull(record.getNextCheckDate()); }

    @Test void testRecordSetNextCheck() {
        LocalDate d = LocalDate.now().plusMonths(3);
        record.setNextCheckDate(d);
        assertEquals(d, record.getNextCheckDate());
    }

    @Test void testRecordGetNotes() { assertNotNull(record.getNotes()); }

    @Test void testRecordSetNotes() { record.setNotes("Reaksiyon yok"); assertEquals("Reaksiyon yok", record.getNotes()); }

    @Test void testRecordGetVaccineName() { assertNotNull(record.getVaccineName()); }

    @Test void testRecordSetVaccineName() { record.setVaccineName("Kuduz"); assertEquals("Kuduz", record.getVaccineName()); }

    @Test void testRecordGetFormattedDate() { assertNotNull(record.getFormattedDate()); }

    @Test void testRecordGetFormattedDateNull() {
        record.setRecordDate(null);
        assertEquals("Belirsiz", record.getFormattedDate());
    }

    @Test void testRecordToString() { assertTrue(record.toString().contains("Karamel")); }

    @Test void testRecordEquals() {
        MedicalRecord r2 = new MedicalRecord(1, 5, "Baska", LocalDate.now(), "KONTROL", "x", "Dr. X");
        assertEquals(record, r2);
    }

    @Test void testRecordEqualsNull() { assertNotEquals(record, null); }

    @Test void testRecordEqualsSelf() { assertEquals(record, record); }

    @Test void testRecordEqualsOtherType() { assertNotEquals(record, "string"); }

    @Test void testRecordNotEquals() {
        MedicalRecord r2 = new MedicalRecord(99, 5, "Baska", LocalDate.now(), "KONTROL", "x", "Dr. X");
        assertNotEquals(record, r2);
    }

    @Test void testRecordHashCode() {
        MedicalRecord r2 = new MedicalRecord(1, 5, "Baska", LocalDate.now(), "KONTROL", "x", "Dr. X");
        assertEquals(record.hashCode(), r2.hashCode());
    }
}
