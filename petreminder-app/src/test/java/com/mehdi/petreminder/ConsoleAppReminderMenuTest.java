/**
 * @file ConsoleAppReminderMenuTest.java
 * @brief ConsoleApp — Reminder, VetAppointment ve MedicalRecord menüsü testleri.
 * @details addNewReminder (1-4 tipler + hata yolları), markReminderCompleted,
 *          deleteReminder, addVetAppointment, addMedicalRecord, listler,
 *          handleRemindersMenuChoice / handleVetMenuChoice / handleMedicalMenuChoice.
 */
package com.mehdi.petreminder;

import com.mehdi.petreminder.config.StorageConfig;
import com.mehdi.petreminder.config.StorageType;
import com.mehdi.petreminder.model.*;
import com.mehdi.petreminder.service.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class ConsoleAppReminderMenuTest
 * @brief Reminder, Veteriner ve Tıbbi Kayıt menüsü davranış testleri.
 * @details Her test metodu tek bir işlevi veya branch'i doğrular.
 * @author Muhammed Mehdi Karagülle, Ibrahim Demirci, Zumre Uykun
 * @version 1.0
 */
class ConsoleAppReminderMenuTest {

    /** @brief Pet servisi. */
    private PetService petService;

    /** @brief Reminder servisi. */
    private ReminderService reminderService;

    /** @brief Orijinal stdout. */
    private PrintStream originalOut;

    /**
     * @brief Her testten önce SQLite backend, servisler başlatılır.
     */
    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        StorageConfig.setActiveBackend(StorageType.SQLITE);
        try {
            petService = new PetService();
            reminderService = new ReminderService();
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * @brief Her testten sonra stdout ve config sıfırlanır.
     */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        StorageConfig.reset();
    }

    // ── Yardımcılar ───────────────────────────────────────────────────

    /**
     * @brief Test köpeği ekler.
     * @param name Pet adı
     * @return Pet ID
     */
    private int addTestDog(String name) {
        try {
            Dog dog = new Dog(0, name, LocalDate.of(2020, 1, 1), 1);
            dog.setBreed("Labrador");
            dog.setWeight(5.0);
            return petService.addPet(dog).getId();
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * @brief Test FeedingReminder ekler.
     * @param petId Pet ID
     * @return Reminder ID
     */
    private int addTestReminder(int petId) {
        try {
            FeedingReminder fr = new FeedingReminder(
                0, petId, "TestPet", LocalDateTime.now().plusDays(1), "Test", 100.0);
            return reminderService.addReminder(fr).getId();
        } catch (Exception e) {
            return 1;
        }
    }

    // ── addNewReminder tipler ─────────────────────────────────────────

    /**
     * @test addNewReminder — type=1 FeedingReminder başarı yolu.
     */
    @Test
    void testAddNewReminderType1Feeding() {
        int petId = addTestDog("FeedMe");
        String input = petId + "\n1\nFeed description\n2030-12-01 10:00\nDry Food\n150.0\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addNewReminder());
    }

    /**
     * @test addNewReminder — type=2 MedicationReminder başarı yolu.
     */
    @Test
    void testAddNewReminderType2Medication() {
        int petId = addTestDog("MedMe");
        String input = petId + "\n2\nMed description\n2030-12-01 10:00\nVitamin C\n5.0\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addNewReminder());
    }

    /**
     * @test addNewReminder — type=3 GroomingReminder başarı yolu.
     */
    @Test
    void testAddNewReminderType3Grooming() {
        int petId = addTestDog("GroomMe");
        String input = petId + "\n3\nGroom description\n2030-12-01 10:00\ntrue\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addNewReminder());
    }

    /**
     * @test addNewReminder — type=4 ExerciseReminder başarı yolu.
     */
    @Test
    void testAddNewReminderType4Exercise() {
        int petId = addTestDog("ExerciseMe");
        String input = petId + "\n4\nExercise description\n2030-12-01 10:00\n30.0\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addNewReminder());
    }

    /**
     * @test addNewReminder — boş açıklama → erken dönüş.
     */
    @Test
    void testAddNewReminderEmptyDescription() {
        int petId = addTestDog("EmptyDescPet");
        String input = petId + "\n1\n\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addNewReminder());
    }

    /**
     * @test addNewReminder — geçersiz type (9) → default case.
     */
    @Test
    void testAddNewReminderInvalidType() {
        int petId = addTestDog("DefaultTypePet");
        String input = petId + "\n9\nSome desc\n2030-12-01 10:00\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addNewReminder());
    }

    /**
     * @test addNewReminder — petId=0 → erken dönüş.
     */
    @Test
    void testAddNewReminderZeroPetId() {
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner("0\n")).addNewReminder());
    }

    /**
     * @test addNewReminder — var olmayan petId → ServiceException yakalanır.
     */
    @Test
    void testAddNewReminderPetNotFound() {
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner("99999\n")).addNewReminder());
    }

    // ── markReminderCompleted branch'leri ─────────────────────────────

    /**
     * @test markReminderCompleted — gerçek reminder tamamlandı yolu.
     */
    @Test
    void testMarkReminderCompletedSuccess() {
        int petId = addTestDog("MarkMePet");
        int remId = addTestReminder(petId);
        assertDoesNotThrow(
            () -> new ConsoleApp(new Scanner(remId + "\n")).markReminderCompleted());
    }

    /**
     * @test markReminderCompleted — olmayan ID → ServiceException yakalanır.
     */
    @Test
    void testMarkReminderCompletedNotFound() {
        assertDoesNotThrow(
            () -> new ConsoleApp(new Scanner("99999\n")).markReminderCompleted());
    }

    /**
     * @test markReminderCompleted — id=0 → erken dönüş.
     */
    @Test
    void testMarkReminderCompletedCancelZeroId() {
        assertDoesNotThrow(
            () -> new ConsoleApp(new Scanner("0\n")).markReminderCompleted());
    }

    // ── deleteReminder branch'leri ────────────────────────────────────

    /**
     * @test deleteReminder — gerçek reminder silme başarı yolu.
     */
    @Test
    void testDeleteReminderSuccess() {
        int petId = addTestDog("DelReminderPet");
        int remId = addTestReminder(petId);
        assertDoesNotThrow(
            () -> new ConsoleApp(new Scanner(remId + "\n")).deleteReminder());
    }

    /**
     * @test deleteReminder — olmayan ID → ServiceException yakalanır.
     */
    @Test
    void testDeleteReminderNotFound() {
        assertDoesNotThrow(
            () -> new ConsoleApp(new Scanner("99999\n")).deleteReminder());
    }

    /**
     * @test listPendingReminders — boş liste.
     */
    @Test
    void testListPendingRemindersEmpty() {
        assertDoesNotThrow(new ConsoleApp()::listPendingReminders);
    }

    /**
     * @test listPendingReminders — veri dolu durum.
     */
    @Test
    void testListPendingRemindersWithData() {
        int petId = addTestDog("ReminderListPet");
        addTestReminder(petId);
        assertDoesNotThrow(new ConsoleApp()::listPendingReminders);
    }

    // ── VetAppointment branch'leri ────────────────────────────────────

    /**
     * @test addVetAppointment — başarı yolu.
     */
    @Test
    void testAddVetAppointmentSuccess() {
        int petId = addTestDog("VetSuccessPet");
        String input = petId + "\nAnnual checkup\n2030-06-15 14:00\nCity Vet\nVaccination\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addVetAppointment());
    }

    /**
     * @test addVetAppointment — petId=0 → erken dönüş.
     */
    @Test
    void testAddVetAppointmentZeroPetId() {
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner("0\n")).addVetAppointment());
    }

    /**
     * @test addVetAppointment — geçersiz saat formatı → null → erken dönüş.
     */
    @Test
    void testAddVetAppointmentInvalidTime() {
        int petId = addTestDog("VetBadTime");
        String input = petId + "\nDesc\nbad-time\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addVetAppointment());
    }

    /**
     * @test listVetAppointments — boş durum.
     */
    @Test
    void testListVetAppointmentsEmpty() {
        assertDoesNotThrow(new ConsoleApp()::listVetAppointments);
    }

    /**
     * @test listVetAppointments — içinde VetAppointment olan durum.
     */
    @Test
    void testListVetAppointmentsWithData() {
        int petId = addTestDog("VetListPet");
        try {
            VetAppointment va = new VetAppointment(
                0, petId, "VetListPet",
                LocalDateTime.now().plusDays(1),
                "Checkup", "City Clinic", "Annual");
            reminderService.addReminder(va);
        } catch (Exception ignored) { }
        assertDoesNotThrow(new ConsoleApp()::listVetAppointments);
    }

    // ── MedicalRecord branch'leri ─────────────────────────────────────

    /**
     * @test addMedicalRecord — başarı yolu.
     */
    @Test
    void testAddMedicalRecordSuccess() {
        int petId = addTestDog("MedRecPet");
        String input = petId + "\n2025-05-20\nFlu\nRest\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addMedicalRecord());
    }

    /**
     * @test listMedicalRecords — petId=0 (hepsi).
     */
    @Test
    void testListMedicalRecordsAll() {
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner("0\n")).listMedicalRecords());
    }

    /**
     * @test listMedicalRecords — belirli petId.
     */
    @Test
    void testListMedicalRecordsForPet() {
        int petId = addTestDog("MedListPet");
        assertDoesNotThrow(
            () -> new ConsoleApp(new Scanner(petId + "\n")).listMedicalRecords());
    }

    // ── handleRemindersMenuChoice tüm branch'ler ──────────────────────

    /**
     * @test handleRemindersMenuChoice — "1" listele → false.
     */
    @Test
    void testHandleRemindersMenuChoice1List() {
        assertFalse(new ConsoleApp().handleRemindersMenuChoice("1"));
    }

    /**
     * @test handleRemindersMenuChoice — "2" ekle (petId=0 iptal) → false.
     */
    @Test
    void testHandleRemindersMenuChoice2Add() {
        assertFalse(new ConsoleApp(new Scanner("0\n")).handleRemindersMenuChoice("2"));
    }

    /**
     * @test handleRemindersMenuChoice — "3" tamamla (id=0 iptal) → false.
     */
    @Test
    void testHandleRemindersMenuChoice3Mark() {
        assertFalse(new ConsoleApp(new Scanner("0\n")).handleRemindersMenuChoice("3"));
    }

    /**
     * @test handleRemindersMenuChoice — "4" sil (id=0 iptal) → false.
     */
    @Test
    void testHandleRemindersMenuChoice4Delete() {
        assertFalse(new ConsoleApp(new Scanner("0\n")).handleRemindersMenuChoice("4"));
    }

    /**
     * @test handleRemindersMenuChoice — "0" geri → true.
     */
    @Test
    void testHandleRemindersMenuChoice0Back() {
        assertTrue(new ConsoleApp().handleRemindersMenuChoice("0"));
    }

    /**
     * @test handleRemindersMenuChoice — null → false.
     */
    @Test
    void testHandleRemindersMenuChoiceNull() {
        assertFalse(new ConsoleApp().handleRemindersMenuChoice(null));
    }

    /**
     * @test handleRemindersMenuChoice — geçersiz → false.
     */
    @Test
    void testHandleRemindersMenuChoiceDefault() {
        assertFalse(new ConsoleApp().handleRemindersMenuChoice("Z"));
    }

    // ── handleVetMenuChoice tüm branch'ler ───────────────────────────

    /**
     * @test handleVetMenuChoice — "1" listele → false.
     */
    @Test
    void testHandleVetMenuChoice1List() {
        assertFalse(new ConsoleApp().handleVetMenuChoice("1"));
    }

    /**
     * @test handleVetMenuChoice — "2" ekle (id=0 iptal) → false.
     */
    @Test
    void testHandleVetMenuChoice2Add() {
        assertFalse(new ConsoleApp(new Scanner("0\n")).handleVetMenuChoice("2"));
    }

    /**
     * @test handleVetMenuChoice — "0" geri → true.
     */
    @Test
    void testHandleVetMenuChoice0Back() {
        assertTrue(new ConsoleApp().handleVetMenuChoice("0"));
    }

    /**
     * @test handleVetMenuChoice — geçersiz → false.
     */
    @Test
    void testHandleVetMenuChoiceDefault() {
        assertFalse(new ConsoleApp().handleVetMenuChoice("Q"));
    }

    // ── handleMedicalMenuChoice tüm branch'ler ────────────────────────

    /**
     * @test handleMedicalMenuChoice — "1" listele (petId=0) → false.
     */
    @Test
    void testHandleMedicalMenuChoice1List() {
        assertFalse(new ConsoleApp(new Scanner("0\n")).handleMedicalMenuChoice("1"));
    }

    /**
     * @test handleMedicalMenuChoice — "2" ekle (petId=0 iptal) → false.
     */
    @Test
    void testHandleMedicalMenuChoice2Add() {
        assertFalse(new ConsoleApp(new Scanner("0\n")).handleMedicalMenuChoice("2"));
    }

    /**
     * @test handleMedicalMenuChoice — "0" geri → true.
     */
    @Test
    void testHandleMedicalMenuChoice0Back() {
        assertTrue(new ConsoleApp().handleMedicalMenuChoice("0"));
    }

    /**
     * @test handleMedicalMenuChoice — geçersiz → false.
     */
    @Test
    void testHandleMedicalMenuChoiceDefault() {
        assertFalse(new ConsoleApp().handleMedicalMenuChoice("W"));
    }
}
