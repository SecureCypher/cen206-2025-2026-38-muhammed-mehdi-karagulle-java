/**
 * @file PetModelTest.java
 * @brief Pet, Dog, Cat, Bird model siniflari icin JUnit 5 test dosyasi.
 * @details Tum getter/setter, makeSound, getAge, equals, hashCode,
 *          toString metodlarini test eder. JaCoCo %100 coverage hedeflenir.
 */
package com.mehdi.petreminder.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.time.LocalDate;

/**
 * @class PetModelTest
 * @brief Pet hiyerarsisi icin birim test sinifi.
 * @author Muhammed Mehdi Karagulle, Ibrahim Demirci, Zumre Uykun
 */
class PetModelTest {

    private Dog dog;
    private Cat cat;
    private Bird bird;

    /** @brief Her testten once calisir. */
    @BeforeEach
    void setUp() {
        dog  = new Dog(1, "Karamel", LocalDate.of(2021, 5, 10), 1);
        cat  = new Cat(2, "Pamuk",   LocalDate.of(2020, 3, 15), 1);
        bird = new Bird(3, "Cici",   LocalDate.of(2022, 7, 20), 1);
    }

    // ── Dog Testleri ──────────────────────────────────────────────────

    @Test public void testDogDefaultConstructor() {
        Dog d = new Dog();
        assertNotNull(d);
    }

    @Test public void testDogGetId() { assertEquals(1, dog.getId()); }

    @Test public void testDogSetId() { dog.setId(99); assertEquals(99, dog.getId()); }

    @Test public void testDogGetName() { assertEquals("Karamel", dog.getName()); }

    @Test public void testDogSetName() { dog.setName("Boncuk"); assertEquals("Boncuk", dog.getName()); }

    @Test public void testDogGetSpecies() { assertEquals("Kopek", dog.getSpecies()); }

    @Test public void testDogSetSpecies() { dog.setSpecies("Kedi"); assertEquals("Kedi", dog.getSpecies()); }

    @Test public void testDogGetOwnerId() { assertEquals(1, dog.getOwnerId()); }

    @Test public void testDogSetOwnerId() { dog.setOwnerId(5); assertEquals(5, dog.getOwnerId()); }

    @Test public void testDogGetGender() { assertEquals("Erkek", dog.getGender()); }

    @Test public void testDogSetGender() { dog.setGender("Disi"); assertEquals("Disi", dog.getGender()); }

    @Test public void testDogGetWeight() { assertEquals(0.0, dog.getWeight(), 0.001); }

    @Test public void testDogSetWeight() { dog.setWeight(12.5); assertEquals(12.5, dog.getWeight(), 0.001); }

    @Test public void testDogGetNotes() { assertNotNull(dog.getNotes()); }

    @Test public void testDogSetNotes() { dog.setNotes("Cok oyuncu"); assertEquals("Cok oyuncu", dog.getNotes()); }

    @Test public void testDogGetBirthDate() { assertEquals(LocalDate.of(2021, 5, 10), dog.getBirthDate()); }

    @Test public void testDogSetBirthDate() {
        dog.setBirthDate(LocalDate.of(2019, 1, 1));
        assertEquals(LocalDate.of(2019, 1, 1), dog.getBirthDate());
    }

    @Test public void testDogMakeSound() { assertEquals("Hav! Hav!", dog.makeSound()); }

    @Test public void testDogGetIconName() { assertEquals("dog", dog.getIconName()); }

    @Test public void testDogGetAge() { assertTrue(dog.getAge() >= 0); }

    @Test public void testDogGetAgeString() { assertNotNull(dog.getAgeString()); }

    @Test public void testDogAgeNullBirthDate() {
        dog.setBirthDate(null);
        assertEquals(-1, dog.getAge());
        assertEquals("Bilinmiyor", dog.getAgeString());
    }

    @Test public void testDogGetBreed() { assertEquals("Karisik", dog.getBreed()); }

    @Test public void testDogSetBreed() { dog.setBreed("Golden"); assertEquals("Golden", dog.getBreed()); }

    @Test public void testDogIsTrained() { assertFalse(dog.isTrained()); }

    @Test public void testDogSetTrained() { dog.setTrained(true); assertTrue(dog.isTrained()); }

    @Test public void testDogToString() { assertTrue(dog.toString().contains("Karamel")); }

    @Test public void testDogFullConstructor() {
        Dog d = new Dog(10, "Max", LocalDate.of(2020, 1, 1), 2,
                "Erkek", 25.0, "Buyuk kopek", "Labrador", true);
        assertEquals("Labrador", d.getBreed());
        assertTrue(d.isTrained());
        assertEquals(25.0, d.getWeight(), 0.001);
    }

    @Test public void testDogEquals() {
        Dog d2 = new Dog(1, "Baska", LocalDate.now(), 2);
        assertEquals(dog, d2); // ayni ID
    }

    @Test public void testDogEqualsNull() { assertNotEquals(dog, null); }

    @Test public void testDogEqualsSelf() { assertEquals(dog, dog); }

    @Test public void testDogEqualsOtherType() { assertNotEquals(dog, "string"); }

    @Test public void testDogNotEquals() {
        Dog d2 = new Dog(2, "Baska", LocalDate.now(), 2);
        assertNotEquals(dog, d2);
    }

    @Test public void testDogHashCode() {
        Dog d2 = new Dog(1, "Baska", LocalDate.now(), 2);
        assertEquals(dog.hashCode(), d2.hashCode());
    }

    // ── Cat Testleri ──────────────────────────────────────────────────

    @Test public void testCatDefaultConstructor() { assertNotNull(new Cat()); }

    @Test public void testCatGetId() { assertEquals(2, cat.getId()); }

    @Test public void testCatGetName() { assertEquals("Pamuk", cat.getName()); }

    @Test public void testCatGetSpecies() { assertEquals("Kedi", cat.getSpecies()); }

    @Test public void testCatMakeSound() { assertEquals("Miyav!", cat.makeSound()); }

    @Test public void testCatGetIconName() { assertEquals("cat", cat.getIconName()); }

    @Test public void testCatGetBreed() { assertEquals("Karisik", cat.getBreed()); }

    @Test public void testCatSetBreed() { cat.setBreed("Persian"); assertEquals("Persian", cat.getBreed()); }

    @Test public void testCatIsIndoor() { assertTrue(cat.isIndoor()); }

    @Test public void testCatSetIndoor() { cat.setIndoor(false); assertFalse(cat.isIndoor()); }

    @Test public void testCatToString() { assertTrue(cat.toString().contains("Pamuk")); }

    @Test public void testCatFullConstructor() {
        Cat c = new Cat(20, "Luna", LocalDate.of(2021, 6, 1), 3,
                "Disi", 4.5, "Lazy cat", "Siamese", false);
        assertEquals("Siamese", c.getBreed());
        assertFalse(c.isIndoor());
    }

    @Test public void testCatEquals() {
        Cat c2 = new Cat(2, "Baska", LocalDate.now(), 3);
        assertEquals(cat, c2);
    }

    // ── Bird Testleri ─────────────────────────────────────────────────

    @Test public void testBirdDefaultConstructor() { assertNotNull(new Bird()); }

    @Test public void testBirdGetId() { assertEquals(3, bird.getId()); }

    @Test public void testBirdGetName() { assertEquals("Cici", bird.getName()); }

    @Test public void testBirdGetSpecies() { assertEquals("Kus", bird.getSpecies()); }

    @Test public void testBirdMakeSoundSilent() { assertEquals("Cik Cik!", bird.makeSound()); }

    @Test public void testBirdMakeSoundTalking() {
        bird.setCanTalk(true);
        assertEquals("Merhaba!", bird.makeSound());
    }

    @Test public void testBirdGetIconName() { assertEquals("bird", bird.getIconName()); }

    @Test public void testBirdGetBirdType() { assertEquals("Muhabbet Kusu", bird.getBirdType()); }

    @Test public void testBirdSetBirdType() { bird.setBirdType("Papagan"); assertEquals("Papagan", bird.getBirdType()); }

    @Test public void testBirdIsCanTalk() { assertFalse(bird.isCanTalk()); }

    @Test public void testBirdSetCanTalk() { bird.setCanTalk(true); assertTrue(bird.isCanTalk()); }

    @Test public void testBirdToString() { assertTrue(bird.toString().contains("Cici")); }

    @Test public void testBirdFullConstructor() {
        Bird b = new Bird(30, "Rio", LocalDate.of(2023, 2, 14), 4,
                "Erkek", 0.3, "Konusuyor", "Papagan", true);
        assertTrue(b.isCanTalk());
        assertEquals("Papagan", b.getBirdType());
    }

    @Test public void testBirdEquals() {
        Bird b2 = new Bird(3, "Baska", LocalDate.now(), 2);
        assertEquals(bird, b2);
    }

    @Test public void testBirdGetAge() { assertTrue(bird.getAge() >= 0); }
}
