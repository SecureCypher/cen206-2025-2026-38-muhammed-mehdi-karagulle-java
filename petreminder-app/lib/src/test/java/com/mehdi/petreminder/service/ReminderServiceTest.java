/**
 * @file ReminderServiceTest.java
 * @brief ReminderService JUnit5 testleri.
 */
package com.mehdi.petreminder.service;

import com.mehdi.petreminder.model.*;
import com.mehdi.petreminder.repository.IRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @class ReminderServiceTest
 * @brief ReminderService iş mantığı testleri.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
@DisplayName("ReminderService Testleri")
class ReminderServiceTest {

    @Mock
    private IRepository<Reminder> mockRepo;

    private ReminderService service;

    private static final LocalDateTime FUTURE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime PAST   = LocalDateTime.now().minusDays(1);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ReminderService(mockRepo);
    }

    @Test @DisplayName("addReminder: geçerli reminder kaydedilir")
    void testAddReminder() {
        FeedingReminder fr = new FeedingReminder(0, 1, "Rex", FUTURE, "Mama", 100);
        when(mockRepo.save(fr)).thenReturn(1);
        Reminder result = service.addReminder(fr);
        assertNotNull(result);
        verify(mockRepo).save(fr);
    }

    @Test @DisplayName("addReminder: null fırlatır")
    void testAddReminderNull() {
        assertThrows(ServiceException.class, () -> service.addReminder(null));
    }

    @Test @DisplayName("addReminder: petId <= 0 fırlatır")
    void testAddReminderInvalidPetId() {
        FeedingReminder fr = new FeedingReminder(0, 0, "Rex", FUTURE, "Mama", 100);
        assertThrows(ServiceException.class, () -> service.addReminder(fr));
    }

    @Test @DisplayName("addReminder: null zaman fırlatır")
    void testAddReminderNullTime() {
        FeedingReminder fr = new FeedingReminder(0, 1, "Rex", null, "Mama", 100);
        assertThrows(ServiceException.class, () -> service.addReminder(fr));
    }

    @Test @DisplayName("getReminderById: mevcut döner")
    void testGetReminderById() {
        FeedingReminder fr = new FeedingReminder(1, 1, "Rex", FUTURE, "Mama", 100);
        when(mockRepo.findById(1)).thenReturn(Optional.of(fr));
        assertEquals("Besleme", service.getReminderById(1).getReminderType());
    }

    @Test @DisplayName("getReminderById: olmayan fırlatır")
    void testGetReminderByIdNotFound() {
        when(mockRepo.findById(99)).thenReturn(Optional.empty());
        assertThrows(ServiceException.class, () -> service.getReminderById(99));
    }

    @Test @DisplayName("getAllReminders: liste döner")
    void testGetAllReminders() {
        when(mockRepo.findAll()).thenReturn(List.of(
            new FeedingReminder(1, 1, "Rex", FUTURE, "Mama", 100),
            new VetAppointment(2, 1, "Rex", FUTURE, "Dr.", "Klinik", "Kontrol")
        ));
        assertEquals(2, service.getAllReminders().size());
    }

    @Test @DisplayName("getPendingReminders: sadece tamamlanmamışlar")
    void testGetPendingReminders() {
        FeedingReminder completed = new FeedingReminder(1, 1, "Rex", FUTURE, "Mama", 100);
        completed.setCompleted(true);
        FeedingReminder pending = new FeedingReminder(2, 1, "Rex", FUTURE, "Mama", 100);
        when(mockRepo.findAll()).thenReturn(List.of(completed, pending));
        List<Reminder> result = service.getPendingReminders();
        assertEquals(1, result.size());
        assertFalse(result.get(0).isCompleted());
    }

    @Test @DisplayName("getOverdueReminders: gecikmiş")
    void testGetOverdueReminders() {
        FeedingReminder overdue = new FeedingReminder(1, 1, "Rex", PAST, "Mama", 100);
        FeedingReminder ok     = new FeedingReminder(2, 1, "Rex", FUTURE, "Mama", 100);
        when(mockRepo.findAll()).thenReturn(List.of(overdue, ok));
        List<Reminder> result = service.getOverdueReminders();
        assertEquals(1, result.size());
    }

    @Test @DisplayName("getRemindersByPetId: filtreler")
    void testGetRemindersByPetId() {
        when(mockRepo.findAll()).thenReturn(List.of(
            new FeedingReminder(1, 1, "Rex", FUTURE, "Mama", 100),
            new FeedingReminder(2, 2, "Boncuk", FUTURE, "Mama", 100)
        ));
        assertEquals(1, service.getRemindersByPetId(1).size());
    }

    @Test @DisplayName("markCompleted: completed=true set edilir")
    void testMarkCompleted() {
        FeedingReminder fr = new FeedingReminder(1, 1, "Rex", FUTURE, "Mama", 100);
        when(mockRepo.findById(1)).thenReturn(Optional.of(fr));
        when(mockRepo.update(fr)).thenReturn(true);
        service.markCompleted(1);
        assertTrue(fr.isCompleted());
        verify(mockRepo).update(fr);
    }

    @Test @DisplayName("updateReminder: güncelleme çağrılır")
    void testUpdateReminder() {
        FeedingReminder fr = new FeedingReminder(1, 1, "Rex", FUTURE, "Mama", 100);
        when(mockRepo.update(fr)).thenReturn(true);
        assertDoesNotThrow(() -> service.updateReminder(fr));
    }

    @Test @DisplayName("updateReminder: güncelleme başarısız fırlatır")
    void testUpdateReminderFails() {
        FeedingReminder fr = new FeedingReminder(1, 1, "Rex", FUTURE, "Mama", 100);
        when(mockRepo.update(fr)).thenReturn(false);
        assertThrows(ServiceException.class, () -> service.updateReminder(fr));
    }

    @Test @DisplayName("deleteReminder: silme çağrılır")
    void testDeleteReminder() {
        when(mockRepo.delete(1)).thenReturn(true);
        assertDoesNotThrow(() -> service.deleteReminder(1));
        verify(mockRepo).delete(1);
    }

    @Test @DisplayName("deleteReminder: bulunamayan fırlatır")
    void testDeleteReminderNotFound() {
        when(mockRepo.delete(99)).thenReturn(false);
        assertThrows(ServiceException.class, () -> service.deleteReminder(99));
    }

    @Test @DisplayName("deleteAllReminders: çağrılır")
    void testDeleteAll() {
        assertDoesNotThrow(() -> service.deleteAllReminders());
        verify(mockRepo).deleteAll();
    }

    @Test @DisplayName("getReminderCount: döner")
    void testCount() {
        when(mockRepo.count()).thenReturn(7);
        assertEquals(7, service.getReminderCount());
    }

    @Test @DisplayName("close: çağrılır")
    void testClose() {
        assertDoesNotThrow(() -> service.close());
        verify(mockRepo).close();
    }

    @Test @DisplayName("Varsayılan yapıcı")
    void testDefaultConstructor() {
        assertDoesNotThrow(() -> new ReminderService());
    }

    @Test @DisplayName("validateReminder: validate false dönerse fırlatır")
    void testValidateReminderFalse() {
        FeedingReminder fr = new FeedingReminder();
        fr.setPetId(1);
        fr.setScheduledTime(FUTURE);
        // validate() false donecek cunku foodType bos vb.
        assertThrows(ServiceException.class, () -> service.addReminder(fr));
    }

    @Test @DisplayName("markCompleted: bulunamayan exception fırlatır")
    void testMarkCompletedNotFound() {
        when(mockRepo.findById(99)).thenReturn(Optional.empty());
        assertThrows(ServiceException.class, () -> service.markCompleted(99));
    }

    @Test @DisplayName("getPendingReminders: scheduledTime null olan")
    void testGetPendingRemindersNullTime() {
        FeedingReminder r1 = new FeedingReminder(1, 1, "Rex", null, "Mama", 100);
        FeedingReminder r2 = new FeedingReminder(2, 1, "Rex", FUTURE, "Mama", 100);
        when(mockRepo.findAll()).thenReturn(List.of(r1, r2));
        List<Reminder> list = service.getPendingReminders();
        assertEquals(2, list.size());
        assertEquals(2, list.get(0).getId()); // FUTURE has valid time, sorts before MAX
    }
}
