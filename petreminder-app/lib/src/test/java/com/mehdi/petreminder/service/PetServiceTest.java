/**
 * @file PetServiceTest.java
 * @brief PetService JUnit5 testleri — mock repository ile.
 */
package com.mehdi.petreminder.service;

import com.mehdi.petreminder.model.*;
import com.mehdi.petreminder.repository.IRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @class PetServiceTest
 * @brief PetService iş mantığı testleri.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
@DisplayName("PetService Testleri")
class PetServiceTest {

    @Mock
    private IRepository<Pet> mockRepo;

    private PetService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PetService(mockRepo);
    }

    @Test @DisplayName("addPet: geçerli pet kaydedilir")
    void testAddPet() {
        Dog dog = new Dog(0, "Rex", LocalDate.of(2020, 1, 1), 1);
        when(mockRepo.save(dog)).thenReturn(1);
        Pet result = service.addPet(dog);
        assertNotNull(result);
        verify(mockRepo, times(1)).save(dog);
    }

    @Test @DisplayName("addPet: null fırlatır ServiceException")
    void testAddPetNull() {
        assertThrows(ServiceException.class, () -> service.addPet(null));
    }

    @Test @DisplayName("addPet: boş ad fırlatır ServiceException")
    void testAddPetEmptyName() {
        Dog dog = new Dog(0, "", null, 1);
        assertThrows(ServiceException.class, () -> service.addPet(dog));
    }

    @Test @DisplayName("addPet: boşluklu ad fırlatır ServiceException")
    void testAddPetBlankName() {
        Dog dog = new Dog(0, "   ", null, 1);
        assertThrows(ServiceException.class, () -> service.addPet(dog));
    }

    @Test @DisplayName("addPet: gelecek doğum tarihi fırlatır")
    void testAddPetFutureBirthDate() {
        Dog dog = new Dog(0, "Rex", LocalDate.now().plusDays(1), 1);
        assertThrows(ServiceException.class, () -> service.addPet(dog));
    }

    @Test @DisplayName("getPetById: mevcut pet döner")
    void testGetPetById() {
        Dog dog = new Dog(1, "Rex", null, 1);
        when(mockRepo.findById(1)).thenReturn(Optional.of(dog));
        Pet found = service.getPetById(1);
        assertEquals("Rex", found.getName());
    }

    @Test @DisplayName("getPetById: olmayan ID fırlatır")
    void testGetPetByIdNotFound() {
        when(mockRepo.findById(99)).thenReturn(Optional.empty());
        assertThrows(ServiceException.class, () -> service.getPetById(99));
    }

    @Test @DisplayName("getAllPets: liste döner")
    void testGetAllPets() {
        List<Pet> list = List.of(
            new Dog(1, "Rex", null, 1),
            new Cat(2, "Boncuk", null, 1)
        );
        when(mockRepo.findAll()).thenReturn(list);
        assertEquals(2, service.getAllPets().size());
    }

    @Test @DisplayName("updatePet: güncelleme çağrılır")
    void testUpdatePet() {
        Dog dog = new Dog(1, "Rex", null, 1);
        when(mockRepo.update(dog)).thenReturn(true);
        assertDoesNotThrow(() -> service.updatePet(dog));
        verify(mockRepo).update(dog);
    }

    @Test @DisplayName("updatePet: güncelleme başarısız fırlatır")
    void testUpdatePetFails() {
        Dog dog = new Dog(1, "Rex", null, 1);
        when(mockRepo.update(dog)).thenReturn(false);
        assertThrows(ServiceException.class, () -> service.updatePet(dog));
    }

    @Test @DisplayName("deletePet: silme çağrılır")
    void testDeletePet() {
        when(mockRepo.delete(1)).thenReturn(true);
        assertDoesNotThrow(() -> service.deletePet(1));
        verify(mockRepo).delete(1);
    }

    @Test @DisplayName("deletePet: bulunamayan fırlatır")
    void testDeletePetNotFound() {
        when(mockRepo.delete(99)).thenReturn(false);
        assertThrows(ServiceException.class, () -> service.deletePet(99));
    }

    @Test @DisplayName("deleteAllPets: çağrılır")
    void testDeleteAllPets() {
        assertDoesNotThrow(() -> service.deleteAllPets());
        verify(mockRepo).deleteAll();
    }

    @Test @DisplayName("getPetCount: sayı döner")
    void testGetPetCount() {
        when(mockRepo.count()).thenReturn(5);
        assertEquals(5, service.getPetCount());
    }

    @Test @DisplayName("searchByName: filtreler")
    void testSearchByName() {
        List<Pet> all = List.of(
            new Dog(1, "Rex", null, 1),
            new Cat(2, "Boncuk", null, 1),
            new Dog(3, "Rexor", null, 1)
        );
        when(mockRepo.findAll()).thenReturn(all);
        List<Pet> result = service.searchByName("rex");
        assertEquals(2, result.size());
    }

    @Test @DisplayName("searchByName: boş sorgu tümünü döner")
    void testSearchByNameEmpty() {
        List<Pet> all = List.of(new Dog(1, "Rex", null, 1));
        when(mockRepo.findAll()).thenReturn(all);
        assertEquals(1, service.searchByName("").size());
        assertEquals(1, service.searchByName(null).size());
    }

    @Test @DisplayName("filterBySpecies: Dog filtreler")
    void testFilterBySpecies() {
        List<Pet> all = List.of(
            new Dog(1, "Rex", null, 1),
            new Cat(2, "Boncuk", null, 1)
        );
        when(mockRepo.findAll()).thenReturn(all);
        List<Pet> dogs = service.filterBySpecies("Kopek");
        assertEquals(1, dogs.size());
        assertInstanceOf(Dog.class, dogs.get(0));
    }

    @Test @DisplayName("filterBySpecies: boş tümünü döner")
    void testFilterBySpeciesEmpty() {
        List<Pet> all = List.of(new Dog(1, "Rex", null, 1));
        when(mockRepo.findAll()).thenReturn(all);
        assertEquals(1, service.filterBySpecies("").size());
    }

    @Test @DisplayName("close: repo kapatılır")
    void testClose() {
        assertDoesNotThrow(() -> service.close());
        verify(mockRepo).close();
    }

    // Varsayılan yapıcı coverage
    @Test @DisplayName("Varsayılan yapıcı")
    void testDefaultConstructor() {
        assertDoesNotThrow(() -> new PetService());
    }

    // filterBySpecies null sorgu — tüm listeyi döndürmeli
    @Test @DisplayName("filterBySpecies: null tümünü döner")
    void testFilterBySpeciesNull() {
        List<Pet> all = List.of(new Dog(1, "Rex", null, 1));
        when(mockRepo.findAll()).thenReturn(all);
        assertEquals(1, service.filterBySpecies(null).size());
    }

    // addPet null birthDate ile geçerli — exception atmamalı
    @Test @DisplayName("addPet: null birthDate başarılı")
    void testAddPetNullBirthDate() {
        Dog dog = new Dog(0, "Rex", null, 1);
        when(mockRepo.save(dog)).thenReturn(1);
        Pet result = service.addPet(dog);
        assertNotNull(result);
    }

    // validatePet name null — getName()==null branch coverage
    @Test @DisplayName("addPet: null name fırlatır ServiceException")
    void testAddPetNullName() {
        Dog dog = new Dog(0, null, null, 1);
        assertThrows(ServiceException.class, () -> service.addPet(dog));
    }

    // filterBySpecies sadece whitespace — trim().isEmpty() branch coverage
    @Test @DisplayName("filterBySpecies: sadece boşluk tümünü döner")
    void testFilterBySpeciesWhitespace() {
        List<Pet> all = List.of(new Dog(1, "Rex", null, 1));
        when(mockRepo.findAll()).thenReturn(all);
        assertEquals(1, service.filterBySpecies("   ").size());
    }

    // searchByName sadece whitespace — trim().isEmpty() branch coverage
    @Test @DisplayName("searchByName: sadece boşluk tümünü döner")
    void testSearchByNameWhitespace() {
        List<Pet> all = List.of(new Dog(1, "Rex", null, 1));
        when(mockRepo.findAll()).thenReturn(all);
        assertEquals(1, service.searchByName("   ").size());
    }
}
