# Pet Care Reminder System - Kapsamlı Teknik Rapor

## 1. Proje Genel Görünümü
Bu proje, evcil hayvan sahiplerinin hayvanlarının temel ihtiyaçlarını (beslenme, ilaç, bakım, egzersiz) ve veteriner randevularını takip etmelerini sağlayan, **Katmanlı Mimari (Layered Architecture)** prensiplerine göre geliştirilmiş bir Java uygulamasıdır. Hem Grafiksel Kullanıcı Arayüzü (GUI) hem de Komut Satırı Arayüzü (CLI / Console) barındırmaktadır.

### Temel Özellikler
- **Evcil Hayvan Yönetimi (CRUD):** Köpek, Kedi ve Kuş türlerine özel kalıtım (inheritance) yapısıyla hayvan profillerinin yönetimi.
- **Hatırlatıcı Sistemi:** Beslenme, İlaç, Bakım, Egzersiz ve Veteriner randevuları için zamanlanmış görev planlama.
- **Tıbbi Kayıt Takibi:** Her hayvana özel geçmiş sağlık kayıtları ve teşhislerin saklanması.
- **Çoklu Veri Kalıcılığı (Persistence):** Aynı anda SQLite, MySQL ve Binary File (Serileştirme) olmak üzere üç farklı veritabanı altyapısının desteklenmesi ve Strategy Design Pattern ile çalışma zamanında değiştirilebilmesi.

## 2. Mimari ve Katmanlar (Layered Architecture)
Proje, bağımlılıkları minimize etmek ve test edilebilirliği artırmak için Katmanlı Mimari Modelini kullanmaktadır.
- **Model (Veri Sınıfları):** Temel varlıklar (Pet, Reminder, MedicalRecord). Getter/Setter metotlarını ve iş zekasını (polimorfizm) içerir.
- **Repository (Veri Erişim Katmanı):** `IRepository` arayüzü sayesinde veri tabanına erişim sağlanır. İş mantığından bağımsızdır, sadece CRUD (Create, Read, Update, Delete) operasyonlarını yapar.
- **Service (İş Mantığı Katmanı):** Kullanıcıdan gelen istekleri alır, verileri doğrular (validation), ardından `Repository` katmanına iletir. Aynı şekilde veri tabanından gelen veriyi işleyip arayüze (GUI/Console) yansıtır.
- **View/UI (Arayüz Katmanı):** Swing (GUI) ve Java Console olarak iki farklı görünüm seçeneği sunar. Asla doğrudan veritabanı kodları içermez, sadece `Service` katmanıyla iletişim kurar.

## 3. Kullanılan Tasarım Kalıpları (Design Patterns)
1. **Repository Pattern:** Veri erişiminin arayüzler (`IPetRepository`, `IReminderRepository`) üzerinden soyutlanması sağlandı. Böylece kodun MySQL mi yoksa SQLite mı kullandığı servislerin umurunda değildir, sadece metotları (`add()`, `getAll()`) çağırır.
2. **Strategy Pattern:** `DatabaseConfig` içerisinde oluşturulan Repository türleri çalışma anında seçilerek farklı depolama stratejileri uygulanmaktadır.
3. **Singleton Pattern:** Veritabanı bağlantı havuzlarının (`DatabaseConfig`, `AppConfig`) tüm uygulama genelinde yalnızca bir örneğinin kullanılmasını sağlamak için oluşturulmuştur.
4. **Observer Pattern (Olay Güdümlü Tasarım):** Service katmanı içinde dinleyiciler ayarlanmıştır. Örneğin bir evcil hayvan eklendiğinde (`PetAddedEvent`), diğer servisler uyarılarak işlem yapılabilir (bağlılık hissi olmadan haberleşme).
5. **Template Method Pattern:** Hatırlatıcıların (Reminder) veya dosya okuma işlemlerinin (Binary DB) ortak adımları üst sınıfta (`abstract class`), değişen kısımları alt sınıflarda belirtilmiştir.
6. **Polymorphism Desteği:** `Pet` üst sınıfından türeyen `Dog`, `Cat`, `Bird` ile `Reminder` sınıfından türeyen tipler (örneğin `VetAppointment`) polimorfik olarak işlenmektedir.

## 4. Paket ve Sınıf Yapısı (Package Structure)
```
com.mehdi.petreminder
├── config          # Veritabanı yapılandırması (AppConfig, DatabaseConfig)
├── exception       # Özel Hata Sınıfları (ServiceException, DatabaseException)
├── model           # Veri Modelleri (Pet, Reminder, Cat, Dog, Bird, vb.)
├── repository      # Veritabanı Katmanı
│   ├── interfaces  # IRepository, IPetRepository...
│   ├── impl        # Uygulamalar (PetRepositorySQLite, PetRepositoryBinary...)
├── service         # İş Mantığı Katmanı (PetService, ReminderService)
└── ui              # Arayüzler
    ├── gui         # Java Swing Ekranları (PetPanels, MainWindow)
    └── console     # Komut Satırı Uygulaması (ConsoleApp)
```

## 5. Test ve Kalite Güvencesi
- **Birim Testleri (Unit Tests):** JUnit 5 ve Mockito (Mocking) araçları kullanılarak 600'den fazla (>%97 code coverage) test yazılmıştır. Her model, servis ve repository metodu test edilmiştir.
- **Sürekli Entegrasyon (CI):** `.github/workflows/ci.yml` aracılığıyla her GitHub *push* işleminde otomatik olarak testler çalışır.
- **Dokümantasyon:** Tüm sınıf, metot ve değişkenlerin üzerine Javadoc standartlarına uygun açıklamalar eklenmiş ve proje genelinde %98.4 Doxygen kapsamına ulaşılmıştır.

## 6. Proje Savunması (Hoca Soruları için Hazırlık)
**Soru 1: Arayüzden veritabanına veri nasıl ulaşıyor?**
Cevap: Kullanıcı `ConsoleApp` veya `GUI` üzerinden veriyi girer. Arayüz bu veriyi alıp `PetService.addPet()` metoduna gönderir. `PetService` gerekli doğrulama kontrollerini yapar (isim boş mu, tarih geçerli mi). Her şey yolundaysa projede ayarlanmış olan Repository'nin (örneğin `PetRepositorySQLite`) `save()` metodunu çağırır. 

**Soru 2: Polimorfizmi nerede kullandın?**
Cevap: Evcil hayvanlarda `Pet` soyut (abstract) sınıfını oluşturdum ve `Dog`, `Cat`, `Bird` sınıfları ondan kalıtım aldı. Veritabanından veriyi çekerken sadece `List<Pet>` döndürüyorum ama döngüyle ekrana yazdırırken Java polimorfizm sayesinde hangisinin köpek hangisinin kedi olduğunu bilip kendine özel özellikleri (`isTrained`, `isIndoor`) otomatik getiriyor. Aynı mantık Hatırlatıcıların özel türü olan `VetAppointment` için de kullanıldı.

**Soru 3: Birden fazla veritabanı (Çoklu Kalıcılık) nasıl çalışıyor?**
Cevap: `AppConfig` üzerinden seçilen ayara göre, `DatabaseConfig` uygun *Repository* fabrikasını ayağa kaldırır. Uygulama SQLite modundaysa JDBC ile SQL sorguları çalışır, Binary modundaysa `ObjectOutputStream` ile nesneler "data.bin" gibi dosyalara serileştirilerek kaydedilir. Kod değişikliğine gerek olmadan konfigürasyonla sistemin veritabanı değişir.
