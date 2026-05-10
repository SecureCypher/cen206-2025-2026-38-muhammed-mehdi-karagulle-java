/**
 * @file MedicalRecordServiceTest.java
 * @brief MedicalRecordService JUnit5 testleri.
 */
package com.mehdi.petreminder.service;

import com.mehdi.petreminder.model.MedicalRecord;
import com.mehdi.petreminder.repository.IRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @class MedicalRecordServiceTest
 * @brief MedicalRecordService iş mantığı testleri.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
@DisplayName("MedicalRecordService Testleri")
class MedicalRecordServiceTest {

    @Mock
    private IRepository<MedicalRecord> mockRepo;

    private MedicalRecordService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new MedicalRecordService(mockRepo);
    }

    private MedicalRecord makeRecord(int id) {
        MedicalRecord r = new MedicalRecord();
        r.setId(id);
        r.setPetId(1);
        r.setPetName("Rex");
        r.setRecordDate(LocalDate.now());
        r.setRecordType("KONTROL");
        r.setVeterinarianName("Dr.Ali");
        return r;
    }

    @Test @DisplayName("addRecord: kaydedilir")
    void testAddRecord() {
        MedicalRecord r = makeRecord(0);
        when(mockRepo.save(r)).thenReturn(1);
        assertNotNull(service.addRecord(r));
        verify(mockRepo).save(r);
    }

    @Test @DisplayName("addRecord: null fırlatır")
    void testAddNull() {
        assertThrows(ServiceException.class, () -> service.addRecord(null));
    }

    @Test @DisplayName("addRecord: petId <= 0 fırlatır")
    void testAddInvalidPetId() {
        MedicalRecord r = makeRecord(0);
        r.setPetId(0);
        assertThrows(ServiceException.class, () -> service.addRecord(r));
    }

    @Test @DisplayName("addRecord: null tarih fırlatır")
    void testAddNullDate() {
        MedicalRecord r = makeRecord(0);
        r.setRecordDate(null);
        assertThrows(ServiceException.class, () -> service.addRecord(r));
    }

    @Test @DisplayName("addRecord: boş recordType fırlatır")
    void testAddEmptyType() {
        MedicalRecord r = makeRecord(0);
        r.setRecordType("");
        assertThrows(ServiceException.class, () -> service.addRecord(r));
    }

    @Test @DisplayName("getRecordById: mevcut döner")
    void testGetById() {
        MedicalRecord r = makeRecord(1);
        when(mockRepo.findById(1)).thenReturn(Optional.of(r));
        assertEquals(1, service.getRecordById(1).getId());
    }

    @Test @DisplayName("getRecordById: olmayan fırlatır")
    void testGetByIdNotFound() {
        when(mockRepo.findById(99)).thenReturn(Optional.empty());
        assertThrows(ServiceException.class, () -> service.getRecordById(99));
    }

    @Test @DisplayName("getAllRecords: liste döner")
    void testGetAll() {
        when(mockRepo.findAll()).thenReturn(List.of(makeRecord(1), makeRecord(2)));
        assertEquals(2, service.getAllRecords().size());
    }

    @Test @DisplayName("getRecordsByPetId: filtreler ve sıralar")
    void testGetByPetId() {
        MedicalRecord r1 = makeRecord(1);
        r1.setRecordDate(LocalDate.of(2025, 1, 1));
        MedicalRecord r2 = makeRecord(2);
        r2.setRecordDate(LocalDate.of(2025, 6, 1));
        MedicalRecord r3 = makeRecord(3);
        r3.setPetId(2);
        when(mockRepo.findAll()).thenReturn(List.of(r1, r2, r3));
        List<MedicalRecord> list = service.getRecordsByPetId(1);
        assertEquals(2, list.size());
        // En yeni önce
        assertEquals(LocalDate.of(2025, 6, 1), list.get(0).getRecordDate());
    }

    @Test @DisplayName("updateRecord: güncelleme çağrılır")
    void testUpdate() {
        MedicalRecord r = makeRecord(1);
        when(mockRepo.update(r)).thenReturn(true);
        assertDoesNotThrow(() -> service.updateRecord(r));
    }

    @Test @DisplayName("updateRecord: başarısız fırlatır")
    void testUpdateFails() {
        MedicalRecord r = makeRecord(1);
        when(mockRepo.update(r)).thenReturn(false);
        assertThrows(ServiceException.class, () -> service.updateRecord(r));
    }

    @Test @DisplayName("deleteRecord: silinir")
    void testDelete() {
        when(mockRepo.delete(1)).thenReturn(true);
        assertDoesNotThrow(() -> service.deleteRecord(1));
    }

    @Test @DisplayName("deleteRecord: bulunamayan fırlatır")
    void testDeleteNotFound() {
        when(mockRepo.delete(99)).thenReturn(false);
        assertThrows(ServiceException.class, () -> service.deleteRecord(99));
    }

    @Test @DisplayName("deleteAllRecords: çağrılır")
    void testDeleteAll() {
        assertDoesNotThrow(() -> service.deleteAllRecords());
        verify(mockRepo).deleteAll();
    }

    @Test @DisplayName("getRecordCount: döner")
    void testCount() {
        when(mockRepo.count()).thenReturn(3);
        assertEquals(3, service.getRecordCount());
    }

    @Test @DisplayName("close: çağrılır")
    void testClose() {
        assertDoesNotThrow(() -> service.close());
        verify(mockRepo).close();
    }

    // Varsayılan yapıcı coverage
    @Test @DisplayName("Varsayılan yapıcı")
    void testDefaultConstructor() {
        assertDoesNotThrow(() -> new MedicalRecordService());
    }

    // addRecord: null recordType fırlatır — eksik branch coverage
    @Test @DisplayName("addRecord: null recordType fırlatır")
    void testAddNullType() {
        MedicalRecord r = makeRecord(0);
        r.setRecordType(null);
        assertThrows(ServiceException.class, () -> service.addRecord(r));
    }
}
