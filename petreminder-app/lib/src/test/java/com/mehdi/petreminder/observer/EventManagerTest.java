/**
 * @file EventManagerTest.java
 * @brief EventManager (Observer Pattern Subject) icin JUnit 5 testleri.
 * @details %100 coverage hedefi — subscribe, unsubscribe, notify, singleton.
 */
package com.mehdi.petreminder.observer;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @class EventManagerTest
 * @brief EventManager test sinifi.
 * @author Muhammed Mehdi Karagulle, Ibrahim Demirci, Zumre Uykun
 */
class EventManagerTest {

    /** @brief Test objesi. */
    private EventManager manager;

    @BeforeEach
    void setUp() {
        EventManager.resetInstance();
        manager = EventManager.getInstance();
        manager.clearAll();
    }

    @AfterEach
    void tearDown() {
        manager.clearAll();
        EventManager.resetInstance();
    }

    // ── Singleton testleri ────────────────────────────────────────────

    @Test void testGetInstanceNotNull() {
        assertNotNull(EventManager.getInstance());
    }

    @Test void testGetInstanceSingleton() {
        EventManager a = EventManager.getInstance();
        EventManager b = EventManager.getInstance();
        assertSame(a, b);
    }

    @Test void testResetInstanceCreatesNew() {
        EventManager old = EventManager.getInstance();
        EventManager.resetInstance();
        EventManager fresh = EventManager.getInstance();
        assertNotSame(old, fresh);
    }

    // ── subscribe testleri ────────────────────────────────────────────

    @Test void testSubscribeIncreasesCount() {
        TestObserver obs = new TestObserver();
        manager.subscribe(EventType.PET_ADDED, obs);
        assertEquals(1, manager.getObserverCount(EventType.PET_ADDED));
    }

    @Test void testSubscribeMultipleObservers() {
        TestObserver obs1 = new TestObserver();
        TestObserver obs2 = new TestObserver();
        manager.subscribe(EventType.PET_ADDED, obs1);
        manager.subscribe(EventType.PET_ADDED, obs2);
        assertEquals(2, manager.getObserverCount(EventType.PET_ADDED));
    }

    @Test void testSubscribeSameObserverTwice() {
        TestObserver obs = new TestObserver();
        manager.subscribe(EventType.PET_ADDED, obs);
        manager.subscribe(EventType.PET_ADDED, obs);
        assertEquals(1, manager.getObserverCount(EventType.PET_ADDED));
    }

    @Test void testSubscribeNullEventType() {
        TestObserver obs = new TestObserver();
        assertDoesNotThrow(() -> manager.subscribe(null, obs));
    }

    @Test void testSubscribeNullObserver() {
        assertDoesNotThrow(() -> manager.subscribe(EventType.PET_ADDED, null));
        assertEquals(0, manager.getObserverCount(EventType.PET_ADDED));
    }

    @Test void testSubscribeDifferentEvents() {
        TestObserver obs = new TestObserver();
        manager.subscribe(EventType.PET_ADDED, obs);
        manager.subscribe(EventType.REMINDER_ADDED, obs);
        assertEquals(1, manager.getObserverCount(EventType.PET_ADDED));
        assertEquals(1, manager.getObserverCount(EventType.REMINDER_ADDED));
        assertEquals(0, manager.getObserverCount(EventType.PET_DELETED));
    }

    // ── unsubscribe testleri ──────────────────────────────────────────

    @Test void testUnsubscribeRemovesObserver() {
        TestObserver obs = new TestObserver();
        manager.subscribe(EventType.PET_ADDED, obs);
        manager.unsubscribe(EventType.PET_ADDED, obs);
        assertEquals(0, manager.getObserverCount(EventType.PET_ADDED));
    }

    @Test void testUnsubscribeNonExistent() {
        TestObserver obs = new TestObserver();
        assertDoesNotThrow(() -> manager.unsubscribe(EventType.PET_ADDED, obs));
    }

    @Test void testUnsubscribeNullParams() {
        assertDoesNotThrow(() -> manager.unsubscribe(null, new TestObserver()));
        assertDoesNotThrow(() -> manager.unsubscribe(EventType.PET_ADDED, null));
    }

    @Test void testUnsubscribeAllRemovesFromAllEvents() {
        TestObserver obs = new TestObserver();
        manager.subscribe(EventType.PET_ADDED, obs);
        manager.subscribe(EventType.REMINDER_ADDED, obs);
        manager.subscribe(EventType.STORAGE_CHANGED, obs);
        manager.unsubscribeAll(obs);
        assertEquals(0, manager.getObserverCount(EventType.PET_ADDED));
        assertEquals(0, manager.getObserverCount(EventType.REMINDER_ADDED));
        assertEquals(0, manager.getObserverCount(EventType.STORAGE_CHANGED));
    }

    @Test void testUnsubscribeAllNull() {
        assertDoesNotThrow(() -> manager.unsubscribeAll(null));
    }

    // ── notify testleri ───────────────────────────────────────────────

    @Test void testNotifyCallsObserver() {
        TestObserver obs = new TestObserver();
        manager.subscribe(EventType.PET_ADDED, obs);
        manager.notify(EventType.PET_ADDED, "TestPet");
        assertEquals(1, obs.callCount);
        assertEquals(EventType.PET_ADDED, obs.lastEvent);
        assertEquals("TestPet", obs.lastData);
    }

    @Test void testNotifyCallsMultipleObservers() {
        TestObserver obs1 = new TestObserver();
        TestObserver obs2 = new TestObserver();
        manager.subscribe(EventType.REMINDER_ADDED, obs1);
        manager.subscribe(EventType.REMINDER_ADDED, obs2);
        manager.notify(EventType.REMINDER_ADDED, "TestReminder");
        assertEquals(1, obs1.callCount);
        assertEquals(1, obs2.callCount);
    }

    @Test void testNotifyDoesNotCallUnsubscribed() {
        TestObserver obs1 = new TestObserver();
        TestObserver obs2 = new TestObserver();
        manager.subscribe(EventType.PET_ADDED, obs1);
        manager.subscribe(EventType.PET_ADDED, obs2);
        manager.unsubscribe(EventType.PET_ADDED, obs2);
        manager.notify(EventType.PET_ADDED, null);
        assertEquals(1, obs1.callCount);
        assertEquals(0, obs2.callCount);
    }

    @Test void testNotifyOnlyTargetEvent() {
        TestObserver obs = new TestObserver();
        manager.subscribe(EventType.PET_ADDED, obs);
        manager.notify(EventType.REMINDER_ADDED, "Farkli olay");
        assertEquals(0, obs.callCount);
    }

    @Test void testNotifyWithNullData() {
        TestObserver obs = new TestObserver();
        manager.subscribe(EventType.STORAGE_CHANGED, obs);
        manager.notify(EventType.STORAGE_CHANGED, null);
        assertEquals(1, obs.callCount);
        assertNull(obs.lastData);
    }

    @Test void testNotifyNullEventType() {
        assertDoesNotThrow(() -> manager.notify(null, "data"));
    }

    @Test void testNotifyNoObservers() {
        assertDoesNotThrow(() -> manager.notify(EventType.PET_DELETED, "id=1"));
    }

    @Test void testNotifyWithExceptionInObserver() {
        PetReminderObserver badObserver = (event, data) -> {
            throw new RuntimeException("Test exception");
        };
        TestObserver goodObserver = new TestObserver();
        manager.subscribe(EventType.PET_ADDED, badObserver);
        manager.subscribe(EventType.PET_ADDED, goodObserver);
        assertDoesNotThrow(() -> manager.notify(EventType.PET_ADDED, "test"));
        assertEquals(1, goodObserver.callCount);
    }

    // ── getObserverCount testleri ─────────────────────────────────────

    @Test void testGetObserverCountZero() {
        assertEquals(0, manager.getObserverCount(EventType.PET_ADDED));
    }

    @Test void testGetObserverCountNull() {
        assertEquals(0, manager.getObserverCount(null));
    }

    // ── clearAll testleri ─────────────────────────────────────────────

    @Test void testClearAllRemovesEverything() {
        TestObserver obs = new TestObserver();
        for (EventType type : EventType.values()) {
            manager.subscribe(type, obs);
        }
        manager.clearAll();
        for (EventType type : EventType.values()) {
            assertEquals(0, manager.getObserverCount(type));
        }
    }

    // ── Entegrasyon testleri ──────────────────────────────────────────

    @Test void testFullLifecycle() {
        TestObserver obs = new TestObserver();

        // 1. subscribe
        manager.subscribe(EventType.REMINDER_COMPLETED, obs);
        assertEquals(1, manager.getObserverCount(EventType.REMINDER_COMPLETED));

        // 2. notify
        manager.notify(EventType.REMINDER_COMPLETED, "id=5");
        assertEquals(1, obs.callCount);
        assertEquals("id=5", obs.lastData);

        // 3. ikinci notify
        manager.notify(EventType.REMINDER_COMPLETED, "id=10");
        assertEquals(2, obs.callCount);
        assertEquals("id=10", obs.lastData);

        // 4. unsubscribe
        manager.unsubscribe(EventType.REMINDER_COMPLETED, obs);
        manager.notify(EventType.REMINDER_COMPLETED, "id=15");
        assertEquals(2, obs.callCount);
    }

    @Test void testMultipleEventsMultipleObservers() {
        TestObserver petObs = new TestObserver();
        TestObserver reminderObs = new TestObserver();
        TestObserver allObs = new TestObserver();

        manager.subscribe(EventType.PET_ADDED, petObs);
        manager.subscribe(EventType.PET_ADDED, allObs);
        manager.subscribe(EventType.REMINDER_ADDED, reminderObs);
        manager.subscribe(EventType.REMINDER_ADDED, allObs);

        manager.notify(EventType.PET_ADDED, "dog");
        assertEquals(1, petObs.callCount);
        assertEquals(0, reminderObs.callCount);
        assertEquals(1, allObs.callCount);

        manager.notify(EventType.REMINDER_ADDED, "feeding");
        assertEquals(1, petObs.callCount);
        assertEquals(1, reminderObs.callCount);
        assertEquals(2, allObs.callCount);
    }

    // ── Yardimci test observer sinifi ─────────────────────────────────

    /**
     * @brief Test amacli basit observer implementasyonu.
     */
    private static class TestObserver implements PetReminderObserver {
        int callCount = 0;
        EventType lastEvent = null;
        Object lastData = null;

        @Override
        public void onEvent(EventType event, Object data) {
            callCount++;
            lastEvent = event;
            lastData = data;
        }
    }
}