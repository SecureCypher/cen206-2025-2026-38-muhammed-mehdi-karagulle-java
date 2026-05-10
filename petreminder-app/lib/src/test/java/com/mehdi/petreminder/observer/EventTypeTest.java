/**
 * @file EventTypeTest.java
 * @brief EventType enum icin JUnit 5 testleri.
 */
package com.mehdi.petreminder.observer;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @class EventTypeTest
 * @brief EventType enum test sinifi.
 * @author Muhammed Mehdi Karagulle, Ibrahim Demirci, Zumre Uykun
 */
class EventTypeTest {

    @Test void testValuesNotEmpty() {
        EventType[] values = EventType.values();
        assertTrue(values.length > 0);
    }

    @Test void testStorageChangedExists() {
        assertNotNull(EventType.valueOf("STORAGE_CHANGED"));
    }

    @Test void testPetAddedExists() {
        assertNotNull(EventType.valueOf("PET_ADDED"));
    }

    @Test void testPetDeletedExists() {
        assertNotNull(EventType.valueOf("PET_DELETED"));
    }

    @Test void testPetUpdatedExists() {
        assertNotNull(EventType.valueOf("PET_UPDATED"));
    }

    @Test void testReminderAddedExists() {
        assertNotNull(EventType.valueOf("REMINDER_ADDED"));
    }

    @Test void testReminderCompletedExists() {
        assertNotNull(EventType.valueOf("REMINDER_COMPLETED"));
    }

    @Test void testReminderDeletedExists() {
        assertNotNull(EventType.valueOf("REMINDER_DELETED"));
    }

    @Test void testMedicalRecordAddedExists() {
        assertNotNull(EventType.valueOf("MEDICAL_RECORD_ADDED"));
    }

    @Test void testMedicalRecordDeletedExists() {
        assertNotNull(EventType.valueOf("MEDICAL_RECORD_DELETED"));
    }

    @Test void testGetDescriptionNotNull() {
        for (EventType type : EventType.values()) {
            assertNotNull(type.getDescription());
            assertFalse(type.getDescription().isEmpty());
        }
    }

    @Test void testStorageChangedDescription() {
        assertTrue(EventType.STORAGE_CHANGED.getDescription().contains("Storage"));
    }

    @Test void testToStringReturnsDescription() {
        for (EventType type : EventType.values()) {
            assertEquals(type.getDescription(), type.toString());
        }
    }

    @Test void testAllValuesCount() {
        assertEquals(9, EventType.values().length);
    }
}