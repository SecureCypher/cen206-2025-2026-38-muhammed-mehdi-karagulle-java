package com.mehdi.petreminder.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class BaseModelTest {

    private static class DummyPet extends Pet {
        public DummyPet() { super(); }
        public DummyPet(int a, String b, String c, LocalDate d, int e, String f, double g, String h) {
            super(a, b, c, d, e, f, g, h);
        }
        @Override public String makeSound() { return "dummy"; }
        @Override public String getIconName() { return "dummy.png"; }
    }

    private static class DummyReminder extends Reminder {
        public DummyReminder() { super(); }
        public DummyReminder(int a, int b, String c, LocalDateTime d, String e, String f, boolean g, String h) {
            super(a, b, c, d, e, f, g, h);
        }
        @Override public String getReminderType() { return "dummy"; }
        @Override public boolean validate() { return true; }
        @Override public String getSummary() { return "dummy"; }
    }

    @Test
    void testDummyPet() {
        DummyPet p1 = new DummyPet();
        p1.setId(1);
        p1.setName("Test");
        p1.setSpecies("Test");
        p1.setBirthDate(LocalDate.now());
        p1.setOwnerId(1);
        p1.setGender("Test");
        p1.setWeight(1.0);
        p1.setNotes("Test");

        assertEquals(1, p1.getId());
        assertEquals("Test", p1.getName());
        assertEquals("Test", p1.getSpecies());
        assertNotNull(p1.getBirthDate());
        assertEquals(1, p1.getOwnerId());
        assertEquals("Test", p1.getGender());
        assertEquals(1.0, p1.getWeight());
        assertEquals("Test", p1.getNotes());
        assertEquals("dummy", p1.makeSound());
        assertEquals("dummy.png", p1.getIconName());
        assertTrue(p1.getAge() >= 0);
        assertNotNull(p1.getAgeString());
        assertEquals(p1, p1);
        assertNotEquals(p1, null);
        assertNotEquals(p1, new Object());

        DummyPet p2 = new DummyPet(1, "Test", "Test", LocalDate.now(), 1, "Test", 1.0, "Test");
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotNull(p2.toString());
        
        DummyPet p3 = new DummyPet();
        p3.setId(3);
        assertNotEquals(p1, p3);
    }

    @Test
    void testDummyReminder() {
        DummyReminder r1 = new DummyReminder();
        r1.setId(1);
        r1.setPetId(2);
        r1.setPetName("Test");
        r1.setScheduledTime(LocalDateTime.now());
        r1.setCompleted(true);
        r1.setDescription("Test");
        r1.setPriority("Test");
        r1.setRecurring(true);
        r1.setRecurrencePattern("Test");

        assertEquals(1, r1.getId());
        assertEquals(2, r1.getPetId());
        assertEquals("Test", r1.getPetName());
        assertNotNull(r1.getScheduledTime());
        assertTrue(r1.isCompleted());
        assertEquals("Test", r1.getDescription());
        assertEquals("Test", r1.getPriority());
        assertTrue(r1.isRecurring());
        assertEquals("Test", r1.getRecurrencePattern());
        assertEquals("dummy", r1.getReminderType());
        assertTrue(r1.validate());
        assertEquals("dummy", r1.getSummary());
        assertNotNull(r1.getFormattedTime());
        assertFalse(r1.isOverdue()); // currently completed = true
        assertEquals(r1, r1);
        assertNotEquals(r1, null);
        assertNotEquals(r1, new Object());

        DummyReminder r2 = new DummyReminder(1, 2, "Test", LocalDateTime.now(), "Test", "Test", true, "Test");
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotNull(r2.toString());

        DummyReminder r3 = new DummyReminder();
        r3.setId(3);
        assertNotEquals(r1, r3);
    }
}
