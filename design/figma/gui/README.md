# GUI Wireframes — PetReminder Final Phase

Bu klasör, PetReminder uygulamasının Final Faz GUI ekranlarını belgeleyen wireframe görsellerini içerir.
Tasarımlar, Swing tabanlı uygulamanın fiili ekranlarını yansıtmaktadır.

## Ekranlar

| Dosya | Ekran | Açıklama |
|-------|-------|----------|
| `01-dashboard.png` | Dashboard | Özet kartlar, bekleyen hatırlatıcılar |
| `02-pets-panel.png` | Pet Yönetimi | Pet listesi tablosu, CRUD butonları |
| `03-reminders-panel.png` | Hatırlatıcılar | Tür filtresi, gecikmiş vurgu |
| `04-medical-records.png` | Tıbbi Kayıtlar | Aşı/muayene/cerrahi kayıtları |
| `05-add-pet-dialog.png` | Pet Ekle Dialogu | Modal form — tüm alanlar |
| `06-settings-panel.png` | Ayarlar | Depolama seçimi (Binary/SQLite/MySQL) |

## Tasarım Kararları

- **Navigasyon**: Sol kenar çubuğu (NavPanel), 200px genişlik, koyu mavi arka plan
- **Aktif öğe vurgusu**: Mavi kenarlık + açık arka plan
- **Gecikmiş öğeler**: Kırmızı satır vurgusu (RemindersPanel)
- **Depolama seçimi**: Çalışma zamanında geçiş (RepositoryFactory pattern)
- **Teknoloji**: Java Swing + FlatLaf (IntelliJ teması) + MigLayout

## İlgili Kaynak Kodlar

- `petreminder-app/app/src/main/java/com/mehdi/petreminder/gui/MainFrame.java`
- `petreminder-app/app/src/main/java/com/mehdi/petreminder/gui/panels/`
- `petreminder-app/app/src/main/java/com/mehdi/petreminder/gui/util/GuiConstants.java`
