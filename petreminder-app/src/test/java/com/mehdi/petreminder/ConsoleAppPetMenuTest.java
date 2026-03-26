/**
 * @file ConsoleAppPetMenuTest.java
 * @brief ConsoleApp — Pet menüsü ve girdi okuma testleri.
 * @details addNewPet (Dog/Cat/Bird), editPet, deletePet,
 *          listAllPets, handlePetsMenuChoice tüm branch'leri,
 *          readInput / readIntInput / readDoubleInput / readDate* testleri.
 */
package com.mehdi.petreminder;

import com.mehdi.petreminder.config.StorageConfig;
import com.mehdi.petreminder.config.StorageType;
import com.mehdi.petreminder.model.*;
import com.mehdi.petreminder.service.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class ConsoleAppPetMenuTest
 * @brief Pet menüsü akış testleri — Dog/Cat/Bird ekleme, düzenleme, silme.
 * @details Her test metodu tek bir davranışı doğrular.
 * @author Muhammed Mehdi Karagülle, Ibrahim Demirci, Zumre Uykun
 * @version 1.0
 */
class ConsoleAppPetMenuTest {

    /** @brief Pet servisi. */
    private PetService petService;

    /** @brief Orijinal stdout. */
    private PrintStream originalOut;

    /**
     * @brief Her testten önce SQLite backend, servis başlatılır.
     */
    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        StorageConfig.setActiveBackend(StorageType.SQLITE);
        try {
            petService = new PetService();
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

    // ── Yardımcı ─────────────────────────────────────────────────────

    /**
     * @brief Test köpeği ekler ve ID'sini döndürür.
     * @param name Pet adı
     * @return Eklenen pet'in ID'si
     */
    private int addTestDog(String name) {
        try {
            Dog dog = new Dog(0, name, LocalDate.of(2020, 1, 1), 1);
            dog.setBreed("Labrador");
            dog.setWeight(5.0);
            Pet added = petService.addPet(dog);
            return added.getId();
        } catch (Exception e) {
            return 1;
        }
    }

    // ── addNewPet branch'leri ─────────────────────────────────────────

    /**
     * @test addNewPet — species=1 (Dog) tam başarı yolu.
     */
    @Test
    void testAddNewPetDogFlow() {
        String input = "1\nTestDog\n2020-01-15\nMale\n5.0\nLabrador\nfalse\n";
        ConsoleApp app = new ConsoleApp(new Scanner(input));
        assertDoesNotThrow(app::addNewPet);
    }

    /**
     * @test addNewPet — species=2 (Cat) tam başarı yolu.
     */
    @Test
    void testAddNewPetCatFlow() {
        String input = "2\nTestCat\n2021-05-01\nFemale\n3.5\nPersian\ntrue\n";
        ConsoleApp app = new ConsoleApp(new Scanner(input));
        assertDoesNotThrow(app::addNewPet);
    }

    /**
     * @test addNewPet — species=3 (Bird) tam başarı yolu.
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
     * @test addNewPet — geçersiz doğum tarihi → null → erken dönüş.
     */
    @Test
    void testAddNewPetInvalidDate() {
        String input = "1\nMyPet\nnot-a-date\n";
        ConsoleApp app = new ConsoleApp(new Scanner(input));
        assertDoesNotThrow(app::addNewPet);
    }

    /**
     * @test addNewPet — ağırlık <= 0 → erken dönüş.
     */
    @Test
    void testAddNewPetZeroWeight() {
        String input = "1\nMyPet2\n2020-01-01\nMale\n0\n";
        ConsoleApp app = new ConsoleApp(new Scanner(input));
        assertDoesNotThrow(app::addNewPet);
    }

    /**
     * @test addNewPet — geçersiz species (9) → default case.
     */
    @Test
    void testAddNewPetInvalidSpecies() {
        String input = "9\nMyPet3\n2020-01-01\nMale\n5.0\nNone\n";
        ConsoleApp app = new ConsoleApp(new Scanner(input));
        assertDoesNotThrow(app::addNewPet);
    }

    // ── editPet branch'leri ───────────────────────────────────────────

    /**
     * @test editPet — ağırlık ve not güncelleme başarı yolu.
     */
    @Test
    void testEditPetSuccessPath() {
        int petId = addTestDog("EditMe");
        String input = petId + "\n6.5\nUpdated notes\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).editPet());
    }

    /**
     * @test editPet — boş ağırlık ve not → değişiklik olmaz.
     */
    @Test
    void testEditPetEmptyWeightAndNotes() {
        int petId = addTestDog("EditEmpty");
        String input = petId + "\n\n\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).editPet());
    }

    /**
     * @test editPet — id=0 → erken dönüş.
     */
    @Test
    void testEditPetCancelWithZeroId() {
        String input = "0\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).editPet());
    }

    // ── deletePet branch'leri  ────────────────────────────────────────

    /**
     * @test deletePet — 'y' ile silme onayı.
     */
    @Test
    void testDeletePetConfirmYes() {
        int petId = addTestDog("DeleteMe");
        String input = petId + "\ny\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).deletePet());
    }

    /**
     * @test deletePet — 'n' ile iptal.
     */
    @Test
    void testDeletePetConfirmNo() {
        int petId = addTestDog("DontDelete");
        String input = petId + "\nn\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).deletePet());
    }

    /**
     * @test deletePet — id=0 → erken dönüş.
     */
    @Test
    void testDeletePetCancelWithZeroId() {
        String input = "0\n";
        assertDoesNotThrow(() -> new ConsoleApp(new Scanner(input)).deletePet());
    }

    // ── listAllPets branch'leri ───────────────────────────────────────

    /**
     * @test listAllPets — boş liste durumu.
     */
    @Test
    void testListAllPetsEmpty() {
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(app::listAllPets);
    }

    /**
     * @test listAllPets — veri olan durum.
     */
    @Test
    void testListAllPetsWithData() {
        addTestDog("ListPet");
        ConsoleApp app = new ConsoleApp();
        assertDoesNotThrow(app::listAllPets);
    }

    // ── handlePetsMenuChoice tüm branch'ler ───────────────────────────

    /**
     * @test handlePetsMenuChoice — "1" listAllPets → false döner.
     */
    @Test
    void testHandlePetsMenuChoice1ListAll() {
        assertFalse(new ConsoleApp().handlePetsMenuChoice("1"));
    }

    /**
     * @test handlePetsMenuChoice — "2" addNewPet (geçersiz species → erken çıkar).
     */
    @Test
    void testHandlePetsMenuChoice2AddPet() {
        ConsoleApp app = new ConsoleApp(new Scanner("9\nPetName\n2020-01-01\nMale\n5.0\n"));
        assertFalse(app.handlePetsMenuChoice("2"));
    }

    /**
     * @test handlePetsMenuChoice — "3" editPet (id=0 iptal).
     */
    @Test
    void testHandlePetsMenuChoice3EditPet() {
        assertFalse(new ConsoleApp(new Scanner("0\n")).handlePetsMenuChoice("3"));
    }

    /**
     * @test handlePetsMenuChoice — "4" deletePet (id=0 iptal).
     */
    @Test
    void testHandlePetsMenuChoice4DeletePet() {
        assertFalse(new ConsoleApp(new Scanner("0\n")).handlePetsMenuChoice("4"));
    }

    /**
     * @test handlePetsMenuChoice — "0" → geri, true döner.
     */
    @Test
    void testHandlePetsMenuChoice0Back() {
        assertTrue(new ConsoleApp().handlePetsMenuChoice("0"));
    }

    /**
     * @test handlePetsMenuChoice — null parametresi → false.
     */
    @Test
    void testHandlePetsMenuChoiceNull() {
        assertFalse(new ConsoleApp().handlePetsMenuChoice(null));
    }

    /**
     * @test handlePetsMenuChoice — geçersiz seçenek → false.
     */
    @Test
    void testHandlePetsMenuChoiceDefault() {
        assertFalse(new ConsoleApp().handlePetsMenuChoice("X"));
    }

    // ── Girdi okuma metodları ─────────────────────────────────────────

    /**
     * @test readInput — scanner boş → "" döner.
     */
    @Test
    void testReadInputScannerEmpty() {
        assertEquals("", new ConsoleApp(new Scanner("")).readInput());
    }

    /**
     * @test readIntInput — geçerli sayı → 42.
     */
    @Test
    void testReadIntInputValid() {
        assertEquals(42, new ConsoleApp(new Scanner("42\n")).readIntInput());
    }

    /**
     * @test readIntInput — boş girdi → 0.
     */
    @Test
    void testReadIntInputEmpty() {
        assertEquals(0, new ConsoleApp(new Scanner("\n")).readIntInput());
    }

    /**
     * @test readIntInput — harf girdi → -1 (NumberFormatException).
     */
    @Test
    void testReadIntInputInvalid() {
        assertEquals(-1, new ConsoleApp(new Scanner("abc\n")).readIntInput());
    }

    /**
     * @test readDoubleInput — geçerli ondalık → 3.14.
     */
    @Test
    void testReadDoubleInputValid() {
        assertEquals(3.14, new ConsoleApp(new Scanner("3.14\n")).readDoubleInput(), 0.001);
    }

    /**
     * @test readDoubleInput — boş girdi → 0.0.
     */
    @Test
    void testReadDoubleInputEmpty() {
        assertEquals(0.0, new ConsoleApp(new Scanner("\n")).readDoubleInput());
    }

    /**
     * @test readDoubleInput — harf girdi → -1.0.
     */
    @Test
    void testReadDoubleInputInvalid() {
        assertEquals(-1.0, new ConsoleApp(new Scanner("xyz\n")).readDoubleInput());
    }

    /**
     * @test readDateInput — geçerli tarih → null değil.
     */
    @Test
    void testReadDateInputValid() {
        assertNotNull(new ConsoleApp(new Scanner("2025-01-15\n")).readDateInput());
    }

    /**
     * @test readDateInput — geçersiz format → null.
     */
    @Test
    void testReadDateInputInvalid() {
        assertNull(new ConsoleApp(new Scanner("not-a-date\n")).readDateInput());
    }

    /**
     * @test readDateTimeInput — geçerli tarih-saat → null değil.
     */
    @Test
    void testReadDateTimeInputValid() {
        assertNotNull(new ConsoleApp(new Scanner("2025-01-15 10:30\n")).readDateTimeInput());
    }

    /**
     * @test readDateTimeInput — geçersiz format → null.
     */
    @Test
    void testReadDateTimeInputInvalid() {
        assertNull(new ConsoleApp(new Scanner("not-a-datetime\n")).readDateTimeInput());
    }
}
