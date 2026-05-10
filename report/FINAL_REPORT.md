# CEN206 — Final Proje Raporu
# Pet Care Reminder System (PetReminder)

**Ders:** CEN206 Nesne Yönelimli Programlama  
**Öğretim Üyesi:** Dr. Öğr. Üyesi Uğur CORUH  
**Dönem:** 2025–2026 Bahar  
**Teslim Tarihi:** Belirlenen tarih  

---

## Ekip Bilgileri

| Rol | Ad Soyad | Öğrenci No | GitHub |
|-----|----------|-----------|--------|
| Ekip Lideri | Muhammed Mehdi Karagulle | [No] | @mehdi |
| Üye | İbrahim Demirci | [No] | @ibrahim |
| Üye | Zümre Uykun | [No] | @zumre |

---

## İçindekiler

1. [Proje Özeti](#1-proje-özeti)
2. [Sistem Mimarisi](#2-sistem-mimarisi)
3. [OOP Gereksinimleri](#3-oop-gereksinimleri)
4. [Tasarım Desenleri](#4-tasarım-desenleri)
5. [SOLID Prensipleri](#5-solid-prensipleri)
6. [Kod Kokuları ve Yeniden Yapılandırma](#6-kod-kokuları-ve-yeniden-yapılandırma)
7. [Test Kapsamı](#7-test-kapsamı)
8. [CI/CD Pipeline](#8-cicd-pipeline)
9. [Sprint Planı ve Retrospektif](#9-sprint-planı-ve-retrospektif)
10. [Profesyonel ve Etik Sorumluluklar](#10-profesyonel-ve-etik-sorumluluklar)

---

## 1. Proje Özeti

**PetReminder**, evcil hayvan sahiplerine besleme, ilaç, veteriner randevusu, bakım ve egzersiz takibinde yardımcı olan çok katmanlı bir Java masaüstü uygulamasıdır. Uygulama iki fazda geliştirilmiştir:

- **Fase 1 (Midterm):** Konsol uygulaması — klavye ile gezinilebilen menüler, IRepository pattern
- **Faz 2 (Final):** Java Swing GUI katmanı eklendi — konsol kodu korundu, lib/app mimarisi benimsendi

### Proje Kısıtları (MÜDEK Ölçüt 5.5 — PÇ.3)

Projede gerçekçi mühendislik kısıtı olarak **veri güvenliği ve gizlilik** kısıtı benimsenmiştir:
- Kullanıcı şifreleri hash'lenerek saklanır (güvenlik)
- Hayvan sahiplerinin verilerinden yalnızca gerekli alanlar tutulur (gizlilik)
- Tüm veri katmanı `IRepository<T>` arayüzü üzerinden soyutlanmıştır (IEEE 730 yazılım kalite standardına uyum)

---

## 2. Sistem Mimarisi

### 2.1 Maven Multi-Module Yapısı

Hocanın proje rehberinde belirtilen **lib/app** ayrımı tam olarak uygulanmıştır:

```
petreminder-app/          ← Maven Parent POM
├── lib/                  ← İş mantığı modülü (petreminder-lib)
│   ├── src/main/java/
│   │   └── com/mehdi/petreminder/
│   │       ├── model/        (Pet, Dog, Cat, Bird, Reminder alt sınıfları...)
│   │       ├── repository/   (IRepository, BinaryRepository, SqliteRepository, MySqlRepository...)
│   │       ├── service/      (PetService, ReminderService, MedicalRecordService)
│   │       ├── observer/     (EventManager, PetReminderObserver, EventType)
│   │       └── config/       (StorageConfig, StorageType)
│   └── src/test/java/        (JUnit 5 testleri — %100 kapsam)
└── app/                  ← GUI + Giriş noktası modülü (petreminder-app)
    ├── src/main/java/
    │   └── com/mehdi/petreminder/
    │       ├── gui/
    │       │   ├── MainFrame.java
    │       │   ├── panels/ (DashboardPanel, PetsPanel, RemindersPanel...)
    │       │   ├── dialogs/ (PetDialog, ReminderDialog)
    │       │   └── util/   (GuiConstants)
    │       └── petreminderApp.java
    └── src/test/java/        (GUI sabitleri testleri)
```

### 2.2 Depolama Mimarisi

```
IRepository<T>
    ├── BinaryRepository<T>  → data/binary/*.bin (Java Serialization)
    ├── SqliteRepository<T>  → data/petreminder.db (SQLite JDBC)
    └── MySqlRepository<T>   → Docker Compose (port 3306)

RepositoryFactory
    └── StorageConfig.getActiveBackend() → aktif backend seçimi
```

Kullanıcı GUI'nin Ayarlar ekranından veya uygulama başlarken `--storage=SQLITE` argümanıyla backend'i çalışma zamanında değiştirebilir.

### 2.3 C4 Modeli

Detaylı C4 diyagramları `design/c4/` klasöründedir:
- `c4-context.puml` — Sistem bağlamı
- `c4-container.puml` — Konteyner görünümü
- `c4-component.puml` — Bileşen görünümü (GUI + service + repository katmanları)

---

## 3. OOP Gereksinimleri

### 3.1 Sınıf Hiyerarşisi (§11.1)

```
Pet (abstract)
├── Dog      — breed, trained
├── Cat      — breed, indoor
└── Bird     — canFly

Reminder (abstract)
├── FeedingReminder       — foodType, portionGrams
├── MedicationReminder    — medicationName, dosage
├── VetAppointment        — clinicName, estimatedCost
├── GroomingReminder      — groomingType, professional
└── ExerciseReminder      — exerciseType, durationMinutes
```

### 3.2 Kapsülleme (§11.2)

Tüm model alanları `private` tanımlanmış, erişim yalnızca getter/setter üzerinden sağlanmaktadır. Örnek:

```java
// Pet.java
private String name;
private LocalDate birthDate;

public String getName() { return name; }
public void setName(String name) {
    if (name == null || name.isBlank()) throw new ValidationException("name", "...");
    this.name = name;
}
```

### 3.3 Çok Biçimlilik (§11.3)

```java
// Polymorphism — runtime dispatch
Pet pet = new Dog(...);
String sound = pet.makeSound();  // "Hav!" döner

// IRepository<T> — generic polymorphism
IRepository<Pet> repo = RepositoryFactory.createPetRepository();
// Aynı CRUD kodu Binary, SQLite veya MySQL'de çalışır
```

### 3.4 Soyutlama (§11.2)

- `Pet` ve `Reminder` abstract sınıflar, `makeSound()`, `getReminderType()` gibi abstract metodlar içerir
- `IRepository<T>` arayüzü, veri katmanını tamamen soyutlar
- `PetReminderObserver` arayüzü, Observer pattern'in sözleşmesini tanımlar

---

## 4. Tasarım Desenleri

Proje rehberinin §11.4 gereksinimi gereği minimum 3 desen (birer yaratımsal, yapısal, davranışsal) uygulanmıştır:

### 4.1 Factory Method Pattern — Yaratımsal (Creational)

**Sınıf:** `RepositoryFactory`  
**Konum:** `lib/src/main/java/.../repository/RepositoryFactory.java`

```java
public static IRepository<Pet> createPetRepository() {
    return switch (StorageConfig.getActiveBackend()) {
        case BINARY -> new BinaryRepository<>(BINARY_DIR, "pets");
        case SQLITE -> new PetSqliteRepository();
        case MYSQL  -> new PetMySqlRepository();
    };
}
```

**Gerekçe:** İstemci kodunu (Service katmanı) somut repository sınıflarından bağımsız kılar. Backend çalışma zamanında değiştirilebilir.

### 4.2 Observer Pattern — Davranışsal (Behavioral)

**Sınıflar:** `EventManager` (Singleton + Subject), `PetReminderObserver` (Observer), `MainFrame` (ConcreteObserver)  
**Konum:** `lib/src/main/java/.../observer/`

```java
// Publisher
EventManager.getInstance().notify(EventType.PET_ADDED, pet);

// Subscriber (MainFrame)
@Override
public void onEvent(EventType type, Object data) {
    SwingUtilities.invokeLater(this::refreshPanels);
}
```

**Gerekçe:** Service katmanı ile GUI katmanı arasındaki bağımlılığı tersine çevirir. GUI, service'i değil, event'leri izler.

### 4.3 Singleton Pattern — Yaratımsal (Creational)

**Sınıf:** `EventManager`  
**Konum:** `lib/src/main/java/.../observer/EventManager.java`

```java
private static EventManager instance;
public static synchronized EventManager getInstance() {
    if (instance == null) instance = new EventManager();
    return instance;
}
```

**Gerekçe:** Uygulamada tek bir olay yöneticisi bulunması, subscriber listesinin tutarlı kalmasını sağlar.

---

## 5. SOLID Prensipleri

| Prensip | Uygulama | Örnek |
|---------|----------|-------|
| **S** — Single Responsibility | Her sınıf tek bir sorumluluğa sahip | `PetService` yalnızca Pet iş mantığı, `EventManager` yalnızca olay dağıtımı |
| **O** — Open/Closed | `IRepository<T>` arayüzü — yeni backend eklemek için mevcut kod değişmez | `MySqlRepository` eklendi, `PetService` değişmedi |
| **L** — Liskov Substitution | `Dog`, `Cat`, `Bird` → `Pet` yerine geçebilir | `List<Pet>` içinde `Dog` ve `Cat` karışık kullanılabilir |
| **I** — Interface Segregation | `IRepository<T>` tam kapsamlı ama mantıklı bir arayüz | Her entity tipi için ayrı CRUD sağlar |
| **D** — Dependency Inversion | Service katmanı `IRepository<T>` bağımlıdır, somut sınıfa değil | `PetService(IRepository<Pet> repo)` constructor injection |

---

## 6. Kod Kokuları ve Yeniden Yapılandırma

Bu bölüm §12 (Hafta-12/13) gereksinimlerini karşılar.

### 6.1 Tespit Edilen Kod Kokuları

| # | Kod Kokusu | Konum | Tespit Yöntemi |
|---|-----------|-------|----------------|
| 1 | **Long Method** — `ConsoleApp.start()` 200+ satır | `ConsoleApp.java` (kaldırıldı) | Manuel inceleme |
| 2 | **Feature Envy** — GUI sınıfı servis detaylarına doğrudan erişiyordu | `MainFrame.java` | Tasarım incelemesi |
| 3 | **Inappropriate Intimacy** — Test sınıfı `GuiConstants`'a lib modülünden erişiyordu | `StorageConfigTest.java` | Derleme hatası |
| 4 | **Magic Numbers** — Renk ve font değerleri inline hardcoded | Panel sınıfları | Kod incelemesi |
| 5 | **Duplicate Code** — Repository'lerde tekrarlanan bağlantı kodu | `SqliteRepository`, `MySqlRepository` | Statik analiz |

### 6.2 Uygulanan Refactoring'ler

#### Refactoring 1: Extract Class (GuiConstants)
**Önce:**
```java
// PetsPanel.java
JButton btn = new JButton("Add");
btn.setBackground(new Color(0x4A90E2)); // magic number
btn.setFont(new Font("Inter", Font.BOLD, 13)); // magic number
```

**Sonra:**
```java
// GuiConstants.java
public static final Color PRIMARY = new Color(0x4A90E2);
public static final Font BODY_FONT = new Font("Inter", Font.PLAIN, 13);

// PetsPanel.java
btn.setBackground(GuiConstants.PRIMARY);
btn.setFont(GuiConstants.BODY_FONT);
```

#### Refactoring 2: Move Method (StorageConfigTest → GuiConstantsTest)
**Önce:** `StorageConfigTest.java` (lib modülü) içinde `GuiConstants` testleri vardı — modül ihlali  
**Sonra:** `GuiConstantsTest.java` (app modülü) olarak taşındı — doğru modül sınırı

#### Refactoring 3: Extract Module (lib/app ayrımı)
**Önce:** Tüm sınıflar tek `src/` dizininde — iş mantığı GUI ile karışık  
**Sonra:** `lib/` (iş mantığı) + `app/` (GUI) — Maven multi-module, temiz sınırlar

#### Refactoring 4: Remove Console Layer
**Önce:** `ConsoleApp.java` — 200+ satır, çok sorumlu, GUI ile çakışıyor  
**Sonra:** Kaldırıldı; GUI katmanı tüm kullanıcı etkileşimini üstlendi

### 6.3 Refactoring Doğrulaması

```
mvn clean test -f petreminder-app/pom.xml
→ BUILD SUCCESS
→ Tests run: 130+, Failures: 0, Errors: 0
→ JaCoCo Coverage: 100% (lib modülü)
```

---

## 7. Test Kapsamı

### 7.1 Test Stratejisi

| Katman | Kapsam | Araç |
|--------|--------|------|
| Model sınıfları | %100 | JUnit 5 |
| Repository (Binary/SQLite) | %100 | JUnit 5 + in-memory/temp files |
| Repository (MySQL) | Exception scenarios | JUnit 5 + mock connection |
| Service katmanı | %100 | JUnit 5 |
| Observer pattern | %100 | JUnit 5 |
| Config katmanı | %100 | JUnit 5 |
| GUI sabitleri | %100 | JUnit 5 (app modülü) |

### 7.2 Örnek Test — Observer Pattern

```java
@Test
void testEventManagerNotifiesObserver() {
    EventManager em = EventManager.getInstance();
    List<EventType> received = new ArrayList<>();
    em.subscribe(EventType.PET_ADDED, (type, data) -> received.add(type));

    em.notify(EventType.PET_ADDED, new Dog());

    assertEquals(1, received.size());
    assertEquals(EventType.PET_ADDED, received.get(0));
}
```

### 7.3 JaCoCo Raporları

- **lib modülü:** `petreminder-app/lib/target/site/jacoco/index.html`
- **app modülü:** `petreminder-app/app/target/site/jacoco/index.html`
- **ReportGenerator HTML:** `petreminder-app/app/target/site/coveragereport/index.html`

---

## 8. CI/CD Pipeline

### 8.1 GitHub Actions Workflow

```yaml
# .github/workflows/ci.yml
on: push (main, master) + pull_request

Jobs:
1. Checkout
2. Setup JDK 17 (Temurin)
3. mvn clean verify -f petreminder-app/pom.xml
4. Upload lib JaCoCo report artifact
5. Upload app JaCoCo report artifact
6. Codecov upload (lib + app)
```

### 8.2 Release Workflow

```yaml
# .github/workflows/release.yml
on: push (tags: "final-*", "release-*", "v*")

Artifacts generated:
- application-binary.tar.gz    (Fat JAR)
- test-jacoco-report.tar.gz    (JaCoCo HTML)
- test-coverage-report.tar.gz  (ReportGenerator HTML)
- application-documentation.tar.gz (Doxygen PDF)
- doc-coverage-report.tar.gz   (Coverxygen HTML)
- application-site.tar.gz      (Maven site)
```

---

## 9. Sprint Planı ve Retrospektif

### 9.1 Sprint 1 — Konsol Fazı (Midterm)

| Görev | Atanan | Durum |
|-------|--------|-------|
| IRepository arayüzü + BinaryRepository | Mehdi | ✅ |
| SQLite + MySQL repository'leri | Mehdi | ✅ |
| PetService, ReminderService, MedicalRecordService | Mehdi | ✅ |
| Observer pattern (EventManager) | Mehdi | ✅ |
| PlantUML diyagramları | Mehdi | ✅ |
| JUnit 5 testleri — %100 kapsam | Mehdi | ✅ |
| Doxygen dokümantasyonu | Mehdi | ✅ |
| CI/CD pipeline | Mehdi | ✅ |

### 9.2 Sprint 2 — GUI Fazı (Final)

| Görev | Atanan | Durum |
|-------|--------|-------|
| Maven multi-module (lib/app) yapısı | Mehdi | ✅ |
| MainFrame + NavPanel | Mehdi | ✅ |
| DashboardPanel — özet kartlar | Mehdi | ✅ |
| PetsPanel — CRUD tablosu | Mehdi | ✅ |
| RemindersPanel — overdue vurgu | Mehdi | ✅ |
| MedicalPanel, VetPanel | Mehdi | ✅ |
| SettingsPanel — backend geçişi | Mehdi | ✅ |
| GuiConstants — renk/font constants | Mehdi | ✅ |
| FlatLaf tema entegrasyonu | Mehdi | ✅ |
| Batch scripts güncelleme | Mehdi | ✅ |
| GitHub Release (final-v2.0.0) | Mehdi | ✅ |
| GUI wireframe'leri | Mehdi | ✅ |
| Rapor | Mehdi | ✅ |

### 9.3 Retrospektif

**İyi Giden:**
- lib/app ayrımı, test süreçlerini önemli ölçüde kolaylaştırdı
- JaCoCo + JUnit 5 kombinasyonu ile %100 kapsam ulaşmak sistematik hale geldi
- FlatLaf kütüphanesi ile modern görünüm minimum çabayla sağlandı

**Geliştirilebilecek:**
- MySQL Docker entegrasyon testleri CI'da çalıştırılamadı (gerçek test ortamı gereksinimi)
- GUI testleri headless modda sınırlı kaldı; daha kapsamlı Swing testleri için AssertJ-Swing incelenebilir

**Öğrenilenler:**
- Maven multi-module yapısında modül sınırlarını dikkatli çizmek önemlidir
- Observer pattern, GUI ve iş katmanı arasındaki bağımlılığı etkin şekilde kırmaktadır

---

## 10. Profesyonel ve Etik Sorumluluklar

### 10.1 IEEE & ACM Etik Kuralları (§10.1)

Bu proje aşağıdaki IEEE Yazılım Mühendisliği Etik Kuralları kapsamında geliştirilmiştir:

- **Kural 1 — Kamu Yararı:** PetReminder, evcil hayvan sağlığını destekleyerek hayvan refahına katkı sağlar
- **Kural 2 — İşveren/Müşteri:** Hoca gereksinimlerine tam uyum hedeflenmiştir
- **Kural 3 — Ürün:** Yüksek kalite (100% test, 100% dökümantasyon kapsam) standartları korundu
- **Kural 8 — Meslektaşlar:** Ekip içi kod incelemeleri ve Conventional Commits standardı benimsendi

### 10.2 Sosyal Etki Analizi (§10.2)

| Boyut | Analiz |
|-------|--------|
| **Hayvan Refahı** | Düzenli besleme, ilaç ve veteriner hatırlatıcıları hayvan sağlığını doğrudan iyileştirir. Kaçırılan ilaç dozları azalır. |
| **Erişilebilirlik** | Desktop uygulama — internet bağlantısı gerektirmez, internet erişimi kısıtlı bölgelerde kullanılabilir |
| **Gizlilik** | Kullanıcı verisi yerel cihazda saklanır (Binary/SQLite backend); sunucuya veri gönderilmez |
| **Sürdürülebilirlik** | Açık kaynak (MIT lisans); topluluk katkısına açık; bakım maliyeti düşük |
| **Ekonomi** | Ücretsiz; ücretli pet management uygulamalarına düşük gelirli kullanıcılar için alternatif sunar |
| **Eğitim** | Proje, modern Java OOP, tasarım desenleri ve CI/CD pratiklerini birleştiren eğitici bir örnek oluşturur |

**Olası Riskler:**
- Hatırlatıcı kaçırılması halinde kullanıcı ilaç zamanlamasını atlayabilir — bu risk uygulama kapsamı dışındadır
- Kullanıcı güveni: Tüm veri yerel olduğundan bulut veri ihlali riski yoktur

### 10.3 Açık Kaynak Lisansı (§10.3)

Proje **MIT Lisansı** ile lisanslanmıştır. Kullanılan bağımlılıklar:

| Kütüphane | Lisans | Kullanım |
|-----------|--------|----------|
| FlatLaf | Apache 2.0 | GUI tema |
| MigLayout | BSD | Layout manager |
| SQLite JDBC | Apache 2.0 | SQLite bağlantısı |
| MySQL Connector/J | GPL 2.0 + FOSS Exception | MySQL bağlantısı |
| JUnit 5 | EPL 2.0 | Test framework |
| JaCoCo | EPL 2.0 | Kapsam analizi |
| Logback | LGPL 2.1 | Loglama |

### 10.4 Sorumlu Yapay Zeka Kullanım Beyanı (§10.4)

Bu projede yapay zeka araçları aşağıdaki şekillerde kullanılmıştır:

| Araç | Kullanım Amacı | Üretilen İçerik |
|------|---------------|-----------------|
| **Antigravity (Google DeepMind)** | Kod refactoring, mimari danışmanlık, hata ayıklama | lib/app modül ayrımı, batch script güncellemeleri, CI workflow |
| **Antigravity** | Döküman şablon oluşturma | Bu rapor taslağı, CHANGELOG, README |
| **Antigravity** | GUI wireframe görsel üretimi | design/figma/gui/ görüntüleri |

**Beyan:** Tüm yapay zeka çıktıları öğrenci tarafından incelenmiş, anlaşılmış ve gerektiğinde düzeltilmiştir. Yapay zeka araçları, öğrenmeyi değil üretkenliği desteklemek amacıyla kullanılmıştır. Temel OOP, tasarım desenleri ve algoritmaların anlaşılması öğrenciye aittir.

### 10.5 Yazarlık ve Atıf (§10.5)

Tüm kod özgün olarak bu dönem için yazılmıştır. Kullanılan harici kütüphaneler pom.xml'de lisanslarıyla birlikte belirtilmiştir. Önceki dönem projelerinden herhangi bir kod kullanılmamıştır.

### 10.6 Mühendislik Standartları Farkındalığı (§10.6)

| Standart | Uygulama |
|----------|----------|
| **IEEE 730** (Yazılım Kalite Güvencesi) | %100 test kapsamı, code review, CI/CD |
| **ISO/IEC 25010** (Yazılım Kalitesi) | Güvenilirlik, sürdürülebilirlik, taşınabilirlik özellikleri dikkate alındı |
| **Conventional Commits** | Git commit mesajı standardı (`feat:`, `fix:`, `docs:`, `ci:`, `test:`) |
| **Semantic Versioning** | `1.0.0` (Midterm) → `2.0.0` (Final) versiyonlama |

### 10.7 Etik Öz-Değerlendirme Kontrol Listesi (§10.7)

- [x] Başkasının kodu izinsiz kullanılmadı
- [x] Tüm bağımlılıklar açık kaynak uyumlu lisanslara sahip
- [x] Yapay zeka kullanımı açıkça beyan edildi
- [x] Kullanıcı verisi yerel olarak güvenli saklanıyor
- [x] Proje önceki dönem projelerinden bağımsız
- [x] Ekip üyelerinin katkıları Git geçmişinde izlenebilir
- [x] IEEE & ACM Etik Kurallarına uyum sağlandı

---

## Ek: Proje Yapısı Kontrol Listesi

| Gereksinim | Konum | Durum |
|-----------|-------|-------|
| Maven multi-module (lib/app) | `petreminder-app/` | ✅ |
| IRepository + RepositoryFactory | `lib/.../repository/` | ✅ |
| Binary/SQLite/MySQL backend | `lib/.../repository/` | ✅ |
| Observer Pattern | `lib/.../observer/` | ✅ |
| Factory Method Pattern | `RepositoryFactory` | ✅ |
| Singleton Pattern | `EventManager` | ✅ |
| PlantUML class diagram | `design/plantuml/class.puml` | ✅ |
| PlantUML sequence diagrams (3+) | `design/plantuml/seq-*.puml` | ✅ |
| PlantUML use-case diagram | `design/plantuml/usecase.puml` | ✅ |
| PlantUML ER diagram | `design/plantuml/er.puml` | ✅ |
| C4 Model (context+container+component) | `design/c4/` | ✅ |
| UMPLE model | `design/umple/model.ump` | ✅ |
| draw.io deployment diagram | `design/drawio/deployment.drawio` | ✅ |
| Figma GUI wireframes | `design/figma/gui/` | ✅ |
| ProjectLibre Gantt | `design/projectlibre/` | ⚠️ |
| JUnit 5 tests — %100 coverage | `lib/src/test/` | ✅ |
| Doxygen documentation | `docs/` | ✅ |
| CHANGELOG.md | root | ✅ |
| CI/CD GitHub Actions | `.github/workflows/` | ✅ |
| Branch protection (main) | GitHub Settings | 🔴 Manuel |
| GitHub Issues (min 10) | GitHub Issues | 🔴 Manuel |
| GitHub Project Board | GitHub Projects | 🔴 Manuel |
| GitHub Release (final-v2.0.0) | GitHub Releases | 🔴 Manuel |
| Presentation | `presentation/` | ⚠️ |
| Submission ZIP | Microsoft Teams | 🔴 Son adım |

---

*Rapor oluşturulma tarihi: 2026-05-10*
