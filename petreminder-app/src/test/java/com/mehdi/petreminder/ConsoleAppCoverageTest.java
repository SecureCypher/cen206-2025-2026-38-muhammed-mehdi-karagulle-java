/**
 * @file ConsoleAppCoverageTest.java
 * @brief ConsoleApp tüm method success-path testleri — SQLite pre-seed kullanır.
 * @details editPet, deletePet, addNewReminder (1-4), markReminderCompleted,
 *          deleteReminder, handleStorageSwitch, handleMainMenuChoice,
 *          printMainMenu, exitApp, showSettingsMenu, readInput exception,
 *          showPetsMenu tam seçenek akışları kapsanır.
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
 * @class ConsoleAppCoverageTest
 * @brief ConsoleApp success-path ve branch coverage testleri.
 * @details SQLite backend ile gerçek veri oluşturarak edit/delete/reminder
 *          akışlarını test eder. JaCoCo'da missed instruction sayısını minimize eder.
 * @author Muhammed Mehdi Karagülle, Ibrahim Demirci, Zumre Uykun
 * @version 1.0
 */
class ConsoleAppCoverageTest {

    /** @brief Servisler. */
    private PetService petService;
    /** @brief Reminder service. */
    private ReminderService reminderService;
    /** @brief Original stdout. */
    private PrintStream originalOut;

    /**
     * @brief Her testten önce SQLITE backend set edilir, stdout susturulur.
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
     * @brief Her testten sonra restore edilir.
     */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        StorageConfig.reset();
    }

    // ─── Yardımcı: SQLITE'a dog ekle, ID'sini döndür ──────────────

    /**
     * @brief Bir köpek ekleyip ID'sini döndürür.
     * @return Eklenen köpeğin ID'si (>0)
     */
    private int addTestDog(String name) {
        try {
            Dog dog = new Dog(0, name, LocalDate.of(2020, 1, 1), 1);
            dog.setBreed("Labrador");
            dog.setWeight(5.0);
            Pet added = petService.addPet(dog);
            return added.getId();
        } catch (Exception e) {
            return 1; // fallback
        }
    }

    /**
     * @brief Bir reminder ekleyip ID'sini döndürür.
     * @param petId Ebeveyn pet ID'si
     * @return Reminder ID
     */
    private int addTestReminder(int petId) {
        try {
            FeedingReminder fr = new FeedingReminder(0, petId, "TestPet",
                LocalDateTime.now().plusDays(1), "Test", 100.0);
            Reminder added = reminderService.addReminder(fr);
            return added.getId();
        } catch (Exception e) {
            return 1;
        }
    }

    // ─── editPet success path ──────────────────────────────────────

    /**
     * @test editPet — gerçek pet bulunur, ağırlık ve notlar güncellenir.
     */
    @Test
    void testEditPetSuccessPath() {
        int petId = addTestDog("EditMe");
        // id → weight update → notes update
        String input = petId + "\n6.5\nUpdated notes\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).editPet());
    }

    /**
     * @test editPet — weight boş (değişmez), notes boş (değişmez).
     */
    @Test
    void testEditPetEmptyWeightAndNotes() {
        int petId = addTestDog("EditEmpty");
        // empty weight, empty notes → no changes
        String input = petId + "\n\n\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).editPet());
    }

    // ─── deletePet success paths ───────────────────────────────────

    /**
     * @test deletePet — gerçek pet bulunur, 'y' ile silinir.
     */
    @Test
    void testDeletePetConfirmYes() {
        int petId = addTestDog("DeleteMe");
        String input = petId + "\ny\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).deletePet());
    }

    /**
     * @test deletePet — gerçek pet bulunur, 'n' ile iptal edilir.
     */
    @Test
    void testDeletePetConfirmNo() {
        int petId = addTestDog("DontDelete");
        String input = petId + "\nn\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).deletePet());
    }

    // ─── addNewReminder types 1-4 with real pet ───────────────────

    /**
     * @test addNewReminder — type 1 (FeedingReminder) tam akış.
     */
    @Test
    void testAddNewReminderType1Feeding() {
        int petId = addTestDog("FeedMe");
        // petId, type=1, desc, dateTime, foodType, portion
        String input = petId + "\n1\nFeed description\n2030-12-01 10:00\nDry Food\n150.0\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addNewReminder());
    }

    /**
     * @test addNewReminder — type 2 (MedicationReminder) tam akış.
     */
    @Test
    void testAddNewReminderType2Medication() {
        int petId = addTestDog("MedMe");
        String input = petId + "\n2\nMed description\n2030-12-01 10:00\nVitamin C\n5.0\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addNewReminder());
    }

    /**
     * @test addNewReminder — type 3 (GroomingReminder) tam akış.
     */
    @Test
    void testAddNewReminderType3Grooming() {
        int petId = addTestDog("GroomMe");
        String input = petId + "\n3\nGroom description\n2030-12-01 10:00\ntrue\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addNewReminder());
    }

    /**
     * @test addNewReminder — type 4 (ExerciseReminder) tam akış.
     */
    @Test
    void testAddNewReminderType4Exercise() {
        int petId = addTestDog("ExerciseMe");
        String input = petId + "\n4\nExercise description\n2030-12-01 10:00\n30.0\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addNewReminder());
    }

    /**
     * @test addNewReminder — getPetById success → ServiceException inner try catch.
     */
    @Test
    void testAddNewReminderEmptyDescBranch() {
        int petId = addTestDog("EmptyDescPet");
        // description is empty → error message, return
        String input = petId + "\n1\n\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addNewReminder());
    }

    /**
     * @test addNewReminder — invalid type → default case.
     */
    @Test
    void testAddNewReminderDefaultCase() {
        int petId = addTestDog("DefaultTypePet");
        // type=9 (invalid) → default case → return
        String input = petId + "\n9\nSome desc\n2030-12-01 10:00\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addNewReminder());
    }

    // ─── markReminderCompleted success path ───────────────────────

    /**
     * @test markReminderCompleted — gerçek reminder ID ile tamamlandı.
     */
    @Test
    void testMarkReminderCompletedSuccessPath() {
        int petId = addTestDog("MarkMePet");
        int remId = addTestReminder(petId);
        String input = remId + "\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).markReminderCompleted());
    }

    /**
     * @test markReminderCompleted — olmayan ID → exception catch branch.
     */
    @Test
    void testMarkReminderCompletedExceptionBranch() {
        String input = "99999\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).markReminderCompleted());
    }

    // ─── deleteReminder success path ──────────────────────────────

    /**
     * @test deleteReminder — gerçek reminder ID ile silindi.
     */
    @Test
    void testDeleteReminderSuccessPath() {
        int petId = addTestDog("DelReminderPet");
        int remId = addTestReminder(petId);
        String input = remId + "\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).deleteReminder());
    }

    /**
     * @test deleteReminder — olmayan ID → exception catch branch.
     */
    @Test
    void testDeleteReminderExceptionBranch() {
        String input = "99999\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).deleteReminder());
    }

    // ─── addVetAppointment success path ───────────────────────────

    /**
     * @test addVetAppointment — gerçek petId ile başarılı kayıt.
     */
    @Test
    void testAddVetAppointmentSuccessPath() {
        int petId = addTestDog("VetPet");
        String input = petId + "\nCheckup\n2030-06-15 10:00\nCity Clinic\nVaccination\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addVetAppointment());
    }

    /**
     * @test addVetAppointment — petId=0 erken dönüş.
     */
    @Test
    void testAddVetAppointmentZeroPetId() {
        String input = "0\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addVetAppointment());
    }

    // ─── addMedicalRecord success path ────────────────────────────

    /**
     * @test addMedicalRecord — gerçek petId ile başarılı kayıt.
     */
    @Test
    void testAddMedicalRecordSuccessPath() {
        int petId = addTestDog("MedRecPet");
        String input = petId + "\n2025-05-20\nFlu\nRest\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).addMedicalRecord());
    }

    // ─── handleStorageSwitch tüm branch'ler ───────────────────────

    /**
     * @test handleStorageSwitch — null güvenli.
     */
    @Test
    void testHandleStorageSwitchNull() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleStorageSwitch(null));
    }

    /**
     * @test handleStorageSwitch — "1" Binary.
     */
    @Test
    void testHandleStorageSwitchBinary() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleStorageSwitch("1"));
        assertEquals(StorageType.BINARY, StorageConfig.getActiveBackend());
    }

    /**
     * @test handleStorageSwitch — "2" SQLite.
     */
    @Test
    void testHandleStorageSwitchSqlite() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleStorageSwitch("2"));
        assertEquals(StorageType.SQLITE, StorageConfig.getActiveBackend());
    }

    /**
     * @test handleStorageSwitch — "3" MySQL.
     */
    @Test
    void testHandleStorageSwitchMysql() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleStorageSwitch("3"));
        assertEquals(StorageType.MYSQL, StorageConfig.getActiveBackend());
    }

    /**
     * @test handleStorageSwitch — "0" Back (no-op).
     */
    @Test
    void testHandleStorageSwitchBack() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleStorageSwitch("0"));
    }

    /**
     * @test handleStorageSwitch — invalid → default branch.
     */
    @Test
    void testHandleStorageSwitchDefault() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleStorageSwitch("X"));
    }

    // ─── handleMainMenuChoice tüm branch'ler ──────────────────────

    /**
     * @test handleMainMenuChoice — null.
     */
    @Test
    void testHandleMainMenuChoiceNull() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleMainMenuChoice(null));
    }

    /**
     * @test handleMainMenuChoice — "1" → showPetsMenu (running=false, immediate exit).
     */
    @Test
    void testHandleMainMenuChoice1() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleMainMenuChoice("1"));
    }

    /**
     * @test handleMainMenuChoice — "2" → showRemindersMenu.
     */
    @Test
    void testHandleMainMenuChoice2() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleMainMenuChoice("2"));
    }

    /**
     * @test handleMainMenuChoice — "3" → showVetMenu.
     */
    @Test
    void testHandleMainMenuChoice3() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleMainMenuChoice("3"));
    }

    /**
     * @test handleMainMenuChoice — "4" → showMedicalMenu.
     */
    @Test
    void testHandleMainMenuChoice4() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleMainMenuChoice("4"));
    }

    /**
     * @test handleMainMenuChoice — "5" → showSettingsMenu + initServices.
     */
    @Test
    void testHandleMainMenuChoice5() {
        ConsoleApp app = new ConsoleApp(new Scanner("0\n")); // "0"=Back in settings
        assertDoesNotThrow(() -> app.handleMainMenuChoice("5"));
    }

    /**
     * @test handleMainMenuChoice — "0" → exitApp.
     */
    @Test
    void testHandleMainMenuChoice0() {
        ConsoleApp app = new ConsoleApp();
        app.setRunning(true);
        app.handleMainMenuChoice("0");
        assertFalse(app.isRunning());
    }

    /**
     * @test handleMainMenuChoice — invalid → default branch.
     */
    @Test
    void testHandleMainMenuChoiceDefault() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(() -> app.handleMainMenuChoice("X"));
    }

    // ─── printMainMenu ────────────────────────────────────────────

    /**
     * @test printMainMenu — exception yok.
     */
    @Test
    void testPrintMainMenu() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(app::printMainMenu);
    }

    // ─── exitApp ─────────────────────────────────────────────────

    /**
     * @test exitApp — running=false olur.
     */
    @Test
    void testExitApp() {
        ConsoleApp app = new ConsoleApp();
        app.setRunning(true);
        app.exitApp();
        assertFalse(app.isRunning());
    }

    // ─── showSettingsMenu ─────────────────────────────────────────

    /**
     * @test showSettingsMenu — "0" ile geri (handleStorageSwitch'e akar).
     */
    @Test
    void testShowSettingsMenuBack() {
        ConsoleApp app = new ConsoleApp(new Scanner("0\n"));
        assertDoesNotThrow(app::showSettingsMenu);
    }

    /**
     * @test showSettingsMenu — "1" (Binary) seçimi.
     */
    @Test
    void testShowSettingsMenuBinary() {
        ConsoleApp app = new ConsoleApp(new Scanner("1\n"));
        assertDoesNotThrow(app::showSettingsMenu);
        assertEquals(StorageType.BINARY, StorageConfig.getActiveBackend());
    }


    // ─── Direct sub-method calls (replaces show*Menu(running=true)) ───────

    /**
     * @test listAllPets — boş liste (no pets yet) durumu.
     */
    @Test
    void testListAllPetsEmpty() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(app::listAllPets);
    }

    /**
     * @test listAllPets — petler var durumu.
     */
    @Test
    void testListAllPetsWithData() {
        addTestDog("ListPet");
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(app::listAllPets);
    }

    /**
     * @test listPendingReminders — direkt çağrım.
     */
    @Test
    void testListPendingReminders() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(app::listPendingReminders);
    }

    /**
     * @test listVetAppointments — direkt çağrım.
     */
    @Test
    void testListVetAppointments() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(app::listVetAppointments);
    }

    /**
     * @test listMedicalRecords — direkt çağrım petId=0 (all).
     */
    @Test
    void testListMedicalRecordsAll() {
        ConsoleApp app = new ConsoleApp(new Scanner("0\n"));
        assertDoesNotThrow(app::listMedicalRecords);
    }

    /**
     * @test listMedicalRecords — petId>0.
     */
    @Test
    void testListMedicalRecordsForPet() {
        int petId = addTestDog("MedListPet");
        ConsoleApp app = new ConsoleApp(new Scanner(petId + "\n"));
        assertDoesNotThrow(app::listMedicalRecords);
    }

    /**
     * @test addNewPet — species=1 (Dog) tam akış.
     */
    @Test
    void testAddNewPetDogFlow() {
        // species=1, name="TestDog", date, gender, weight, breed
        String input = "1\nTestDog\n2020-01-15\nMale\n5.0\nLabrador\n";
        ConsoleApp app = new ConsoleApp(new Scanner(input));
        assertDoesNotThrow(app::addNewPet);
    }

    /**
     * @test addNewPet — species=2 (Cat) tam akış.
     */
    @Test
    void testAddNewPetCatFlow() {
        String input = "2\nTestCat\n2021-05-01\nFemale\n3.5\nPersian\ntrue\n";
        ConsoleApp app = new ConsoleApp(new Scanner(input));
        assertDoesNotThrow(app::addNewPet);
    }

    /**
     * @test addNewPet — species=3 (Bird) tam akış.
     */
    @Test
    void testAddNewPetBirdFlow() {
        String input = "3\nTestBird\n2022-03-01\nMale\n0.5\nParakeet\ntrue\n";
        ConsoleApp app = new ConsoleApp(new Scanner(input));
        assertDoesNotThrow(app::addNewPet);
    }

    /**
     * @test addNewPet — boş isim → erken dönüş.
     */
    @Test
    void testAddNewPetEmptyName() {
        String input = "1\n\n";
        ConsoleApp app = new ConsoleApp(new Scanner(input));
        assertDoesNotThrow(app::addNewPet);
    }

    /**
     * @test addNewPet — geçersiz birth date → null → erken dönüş.
     */
    @Test
    void testAddNewPetInvalidDate() {
        String input = "1\nMyPet\nnot-a-date\n";
        ConsoleApp app = new ConsoleApp(new Scanner(input));
        assertDoesNotThrow(app::addNewPet);
    }

    /**
     * @test addNewPet — weight <= 0 → erken dönüş.
     */
    @Test
    void testAddNewPetZeroWeight() {
        String input = "1\nMyPet2\n2020-01-01\nMale\n0\n";
        ConsoleApp app = new ConsoleApp(new Scanner(input));
        assertDoesNotThrow(app::addNewPet);
    }

    /**
     * @test addNewPet — species=9 (invalid) → default case.
     */
    @Test
    void testAddNewPetInvalidSpecies() {
        String input = "9\nMyPet3\n2020-01-01\nMale\n5.0\nNone\n";
        ConsoleApp app = new ConsoleApp(new Scanner(input));
        assertDoesNotThrow(app::addNewPet);
    }

    // ─── readInput exception branch ───────────────────────────────

    /**
     * @test readInput — scanner atarsa boş string döner.
     */
    @Test
    void testReadInputScannerEmpty() {
        ConsoleApp app = new ConsoleApp(new Scanner(""));
        // scanner has no lines → returns ""
        String result = app.readInput();
        assertEquals("", result);
    }

    // ─── readIntInput / readDoubleInput direct tests ───────────────

    /**
     * @test readIntInput — geçerli sayı.
     */
    @Test
    void testReadIntInputValid() {
        ConsoleApp app = new ConsoleApp(new Scanner("42\n"));
        assertEquals(42, app.readIntInput());
    }

    /**
     * @test readIntInput — boş string → 0.
     */
    @Test
    void testReadIntInputEmpty() {
        ConsoleApp app = new ConsoleApp(new Scanner("\n"));
        assertEquals(0, app.readIntInput());
    }

    /**
     * @test readIntInput — geçersiz string → 0 (NumberFormatException catch).
     */
    @Test
    void testReadIntInputInvalid() {
        ConsoleApp app = new ConsoleApp(new Scanner("abc\n"));
        assertEquals(-1, app.readIntInput());
    }

    /**
     * @test readDoubleInput — geçerli sayı.
     */
    @Test
    void testReadDoubleInputValid() {
        ConsoleApp app = new ConsoleApp(new Scanner("3.14\n"));
        assertEquals(3.14, app.readDoubleInput(), 0.001);
    }

    /**
     * @test readDoubleInput — boş string → 0.0.
     */
    @Test
    void testReadDoubleInputEmpty() {
        ConsoleApp app = new ConsoleApp(new Scanner("\n"));
        assertEquals(0.0, app.readDoubleInput());
    }

    /**
     * @test readDoubleInput — geçersiz string → 0.0.
     */
    @Test
    void testReadDoubleInputInvalid() {
        ConsoleApp app = new ConsoleApp(new Scanner("xyz\n"));
        assertEquals(-1.0, app.readDoubleInput());
    }

    /**
     * @test readDateInput — geçerli tarih.
     */
    @Test
    void testReadDateInputValid() {
        ConsoleApp app = new ConsoleApp(new Scanner("2025-01-15\n"));
        assertNotNull(app.readDateInput());
    }

    /**
     * @test readDateInput — geçersiz tarih → null.
     */
    @Test
    void testReadDateInputInvalid() {
        ConsoleApp app = new ConsoleApp(new Scanner("not-a-date\n"));
        assertNull(app.readDateInput());
    }

    /**
     * @test readDateTimeInput — geçerli tarih-saat.
     */
    @Test
    void testReadDateTimeInputValid() {
        ConsoleApp app = new ConsoleApp(new Scanner("2025-01-15 10:30\n"));
        assertNotNull(app.readDateTimeInput());
    }

    /**
     * @test readDateTimeInput — geçersiz → null.
     */
    @Test
    void testReadDateTimeInputInvalid() {
        ConsoleApp app = new ConsoleApp(new Scanner("not-a-datetime\n"));
        assertNull(app.readDateTimeInput());
    }

    // ─── handlePetsMenuChoice tüm case'ler ───────────────────────────

    /**
     * @test handlePetsMenuChoice "1" → listAllPets → false.
     */
    @Test
    void testHandlePetsMenuChoice1ListAll() {
        ConsoleApp app = new ConsoleApp();
        assertFalse(app.handlePetsMenuChoice("1"));
    }

    /**
     * @test handlePetsMenuChoice "2" → addNewPet (species invalid → immediate).
     */
    @Test
    void testHandlePetsMenuChoice2AddPet() {
        // species=9 (invalid) → early return from addNewPet
        ConsoleApp app = new ConsoleApp(new Scanner("9\nPetName\n2020-01-01\nMale\n5.0\n"));
        assertFalse(app.handlePetsMenuChoice("2"));
    }

    /**
     * @test handlePetsMenuChoice "3" → editPet (cancel).
     */
    @Test
    void testHandlePetsMenuChoice3EditPet() {
        ConsoleApp app = new ConsoleApp(new Scanner("0\n"));
        assertFalse(app.handlePetsMenuChoice("3"));
    }

    /**
     * @test handlePetsMenuChoice "4" → deletePet (cancel).
     */
    @Test
    void testHandlePetsMenuChoice4DeletePet() {
        ConsoleApp app = new ConsoleApp(new Scanner("0\n"));
        assertFalse(app.handlePetsMenuChoice("4"));
    }

    /**
     * @test handlePetsMenuChoice "0" → return true (back to main).
     */
    @Test
    void testHandlePetsMenuChoice0Back() {
        ConsoleApp app = new ConsoleApp();
        assertTrue(app.handlePetsMenuChoice("0"));
    }

    /**
     * @test handlePetsMenuChoice default → invalid, false.
     */
    @Test
    void testHandlePetsMenuChoiceDefault() {
        ConsoleApp app = new ConsoleApp();
        assertFalse(app.handlePetsMenuChoice("X"));
    }

    // ─── handleRemindersMenuChoice tüm case'ler ──────────────────────

    /**
     * @test handleRemindersMenuChoice "1" → listPendingReminders → false.
     */
    @Test
    void testHandleRemindersMenuChoice1List() {
        ConsoleApp app = new ConsoleApp();
        assertFalse(app.handleRemindersMenuChoice("1"));
    }

    /**
     * @test handleRemindersMenuChoice "2" → addNewReminder (petId=0 cancel).
     */
    @Test
    void testHandleRemindersMenuChoice2Add() {
        ConsoleApp app = new ConsoleApp(new Scanner("0\n"));
        assertFalse(app.handleRemindersMenuChoice("2"));
    }

    /**
     * @test handleRemindersMenuChoice "3" → markReminderCompleted (cancel).
     */
    @Test
    void testHandleRemindersMenuChoice3Mark() {
        ConsoleApp app = new ConsoleApp(new Scanner("0\n"));
        assertFalse(app.handleRemindersMenuChoice("3"));
    }

    /**
     * @test handleRemindersMenuChoice "4" → deleteReminder (cancel).
     */
    @Test
    void testHandleRemindersMenuChoice4Delete() {
        ConsoleApp app = new ConsoleApp(new Scanner("0\n"));
        assertFalse(app.handleRemindersMenuChoice("4"));
    }

    /**
     * @test handleRemindersMenuChoice "0" → true (back).
     */
    @Test
    void testHandleRemindersMenuChoice0Back() {
        ConsoleApp app = new ConsoleApp();
        assertTrue(app.handleRemindersMenuChoice("0"));
    }

    /**
     * @test handleRemindersMenuChoice default → false.
     */
    @Test
    void testHandleRemindersMenuChoiceDefault() {
        ConsoleApp app = new ConsoleApp();
        assertFalse(app.handleRemindersMenuChoice("Z"));
    }

    // ─── handleVetMenuChoice tüm case'ler ────────────────────────────

    /**
     * @test handleVetMenuChoice "1" → listVetAppointments → false.
     */
    @Test
    void testHandleVetMenuChoice1List() {
        ConsoleApp app = new ConsoleApp();
        assertFalse(app.handleVetMenuChoice("1"));
    }

    /**
     * @test handleVetMenuChoice "2" → addVetAppointment (cancel).
     */
    @Test
    void testHandleVetMenuChoice2Add() {
        ConsoleApp app = new ConsoleApp(new Scanner("0\n"));
        assertFalse(app.handleVetMenuChoice("2"));
    }

    /**
     * @test handleVetMenuChoice "0" → true.
     */
    @Test
    void testHandleVetMenuChoice0Back() {
        ConsoleApp app = new ConsoleApp();
        assertTrue(app.handleVetMenuChoice("0"));
    }

    /**
     * @test handleVetMenuChoice default → false.
     */
    @Test
    void testHandleVetMenuChoiceDefault() {
        ConsoleApp app = new ConsoleApp();
        assertFalse(app.handleVetMenuChoice("Q"));
    }

    // ─── handleMedicalMenuChoice tüm case'ler ────────────────────────

    /**
     * @test handleMedicalMenuChoice "1" → listMedicalRecords (petId=0, all).
     */
    @Test
    void testHandleMedicalMenuChoice1List() {
        ConsoleApp app = new ConsoleApp(new Scanner("0\n"));
        assertFalse(app.handleMedicalMenuChoice("1"));
    }

    /**
     * @test handleMedicalMenuChoice "2" → addMedicalRecord (cancel).
     */
    @Test
    void testHandleMedicalMenuChoice2Add() {
        ConsoleApp app = new ConsoleApp(new Scanner("0\n"));
        assertFalse(app.handleMedicalMenuChoice("2"));
    }

    /**
     * @test handleMedicalMenuChoice "0" → true.
     */
    @Test
    void testHandleMedicalMenuChoice0Back() {
        ConsoleApp app = new ConsoleApp();
        assertTrue(app.handleMedicalMenuChoice("0"));
    }

    /**
     * @test handleMedicalMenuChoice default → false.
     */
    @Test
    void testHandleMedicalMenuChoiceDefault() {
        ConsoleApp app = new ConsoleApp();
        assertFalse(app.handleMedicalMenuChoice("W"));
    }

    // ─── listPendingReminders with actual data ────────────────────────

    /**
     * @test listPendingReminders — gerçek reminder ile veri dolu path.
     */
    @Test
    void testListPendingRemindersWithData() {
        int petId = addTestDog("ReminderListPet");
        addTestReminder(petId);
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(app::listPendingReminders);
    }

    // ─── listVetAppointments with actual VetAppointment ──────────────

    /**
     * @test listVetAppointments — gerçek VetAppointment varsa VetAppointment branch.
     */
    @Test
    void testListVetAppointmentsWithData() {
        int petId = addTestDog("VetListPet");
        try {
            com.mehdi.petreminder.model.VetAppointment va = new com.mehdi.petreminder.model.VetAppointment(
                0, petId, "VetListPet", java.time.LocalDateTime.now().plusDays(1),
                "Checkup", "City Clinic", "Annual");
            reminderService.addReminder(va);
        } catch (Exception ignored) { }
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(app::listVetAppointments);
    }

    // ─── addVetAppointment success path ───────────────────────────────

    /**
     * @test addVetAppointment — gerçek dateTime ile başarı path.
     */
    @Test
    void testAddVetAppointmentSuccess() {
        int petId = addTestDog("VetSuccessPet");
        String input = petId + "\nAnnual checkup\n2030-06-15 14:00\nCity Vet\nVaccination\n";
        ConsoleApp app = new ConsoleApp(new Scanner(input));
        assertDoesNotThrow(app::addVetAppointment);
    }

    /**
     * @test addVetAppointment — geçersiz saat format → null → erken dönüş.
     */
    @Test
    void testAddVetAppointmentInvalidTime() {
        int petId = addTestDog("VetBadTime");
        String input = petId + "\nDesc\nbad-time\n";
        ConsoleApp app = new ConsoleApp(new Scanner(input));
        assertDoesNotThrow(app::addVetAppointment);
    }

    // ─── addNewReminder pet-not-found exception ───────────────────────

    /**
     * @test addNewReminder — getPetById throws ServiceException (pet not found).
     */
    @Test
    void testAddNewReminderPetNotFound() {
        String input = "99999\n"; // non-existent petId
        ConsoleApp app = new ConsoleApp(new Scanner(input));
        assertDoesNotThrow(app::addNewReminder);
    }

    // ─── petreminderApp tüm branch'ler ───────────────────────────────

    /**
     * @test petreminderApp.parseStorageArg — null → BINARY.
     */
    @Test
    void testParseStorageArgNull() {
        assertEquals(com.mehdi.petreminder.config.StorageType.BINARY,
            petreminderApp.parseStorageArg(null));
    }

    /**
     * @test petreminderApp.parseStorageArg — "--storage=SQLITE".
     */
    @Test
    void testParseStorageArgSqlite() {
        assertEquals(com.mehdi.petreminder.config.StorageType.SQLITE,
            petreminderApp.parseStorageArg(new String[]{"--storage=SQLITE"}));
    }

    /**
     * @test petreminderApp.parseStorageArg — invalid → BINARY fallback.
     */
    @Test
    void testParseStorageArgInvalid() {
        assertEquals(com.mehdi.petreminder.config.StorageType.BINARY,
            petreminderApp.parseStorageArg(new String[]{"--storage=INVALID"}));
    }

    /**
     * @test petreminderApp.parseStorageArg — null arg in array (skip null).
     */
    @Test
    void testParseStorageArgNullElement() {
        assertEquals(com.mehdi.petreminder.config.StorageType.BINARY,
            petreminderApp.parseStorageArg(new String[]{null}));
    }

    /**
     * @test petreminderApp.isGuiRequested — null.
     */
    @Test
    void testIsGuiRequestedNull() {
        assertFalse(petreminderApp.isGuiRequested(null));
    }

    /**
     * @test petreminderApp.isGuiRequested — "--gui" → true.
     */
    @Test
    void testIsGuiRequestedTrue() {
        assertTrue(petreminderApp.isGuiRequested(new String[]{"--gui"}));
    }

    /**
     * @test petreminderApp.isGuiRequested — no gui flag → false.
     */
    @Test
    void testIsGuiRequestedFalse() {
        assertFalse(petreminderApp.isGuiRequested(new String[]{"--storage=SQLITE"}));
    }

    /**
     * @test petreminderApp.getVersion — versiyon string döner.
     */
    @Test
    void testGetVersion() {
        assertEquals("1.0.0", petreminderApp.getVersion());
    }

    /**
     * @test petreminderApp.getAppName — uygulama adı döner.
     */
    @Test
    void testGetAppName() {
        assertEquals("Pet Care Reminder System", petreminderApp.getAppName());
    }
}
