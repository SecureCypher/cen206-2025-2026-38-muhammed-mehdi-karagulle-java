# Changelog
All notable changes to the Pet Care Reminder System will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Ongoing improvements and documentation updates

---

## [2.0.0] - 2026-05-10

### Added
- **GUI Layer (Swing):**
  - Full desktop GUI using Java Swing with FlatLaf light theme and MigLayout
  - `MainFrame` with tabbed navigation: Pets, Reminders, Medical Records, Vet Appointments, Settings
  - `PetsPanel`: CRUD table with add/edit/delete dialogs
  - `RemindersPanel`: Reminder scheduling with overdue detection
  - `MedicalRecordPanel`: Medical history per pet
  - `VetPanel`: Vet appointment management
  - `SettingsPanel`: Runtime storage backend switching (Binary / SQLite / MySQL)
- **Maven Multi-Module Architecture:**
  - Separated into `lib/` module (business logic) and `app/` module (GUI + entry point)
  - Parent `pom.xml` coordinates both modules
- **CI/CD Enhancements:**
  - JaCoCo HTML test coverage report uploaded as artifact
  - Coverage badge integration via GitHub Actions
- **Design Documents (Final Phase):**
  - GUI wireframes added to `design/figma/gui/`
  - C4 Component diagram updated with GUI layer
  - PlantUML class diagram updated with GUI classes
- **Code Smell Refactoring:**
  - Identified and refactored ≥ 3 code smells (documented in report)

### Changed
- `petreminderApp.java` — `main()` now launches GUI directly (console layer removed)
- Architecture strictly follows MVC: View (gui/) ↔ Controller (service/) ↔ Model (model/)
- All design patterns documented with intent, code location, and justification in report

### Removed
- Console application layer (`ConsoleApp.java`) — project is GUI-only for Final
- Template math utility methods (`add`, `subtract`, `multiply`, `divide`) — unrelated to domain

---

## [1.0.0] - 2026-03-27

### Added
- **Core Models:**
  - Polymorphic hierarchy for `Pet` (`Dog`, `Cat`, `Bird`).
  - Polymorphic hierarchy for `Reminder` (`FeedingReminder`, `MedicationReminder`, `VetAppointment`, `ExerciseReminder`, `GroomingReminder`).
- **Storage — IRepository Pattern:**
  - `IRepository<T>` interface: `save`, `findById`, `findAll`, `update`, `delete`
  - `BinaryRepository<T>` — Java Serializable + binary file I/O
  - `SqliteRepository<T>` — SQLite via JDBC (Template Method pattern)
  - `MySqlRepository<T>` — MySQL via Docker Compose
  - `RepositoryFactory` — runtime backend switching via `--storage=` argument
- **Observer Pattern:**
  - `EventManager` — notifies listeners on CRUD operations (`PET_ADDED`, `REMINDER_COMPLETED`, etc.)
- **Unit Testing:**
  - Comprehensive JUnit 5 test suite — 100% instruction and branch coverage (JaCoCo)
- **Documentation:**
  - Javadoc/Doxygen documentation coverage 100%
  - Class, Use-Case, ER, Sequence, Activity, State Diagrams in `design/plantuml/`
  - C4 Model (Context, Container, Component) in `design/c4/`
  - UMPLE model in `design/umple/`
  - Figma console wireframes in `design/figma/console/`
  - draw.io Deployment Diagram in `design/drawio/`
  - ProjectLibre Gantt chart in `design/projectlibre/`
- **CI/CD:**
  - GitHub Actions CI Pipeline (`.github/workflows/ci.yml`)
  - GitHub Actions Release Pipeline (`.github/workflows/release.yml`)

### Fixed
- Addressed file resource leaks (e.g. `FileInputStream`) during testing in `BinaryRepository.java`
- Corrected reflection errors around ID mappings during fallback edge cases
- Ensured thread safety in `EventManager` singleton with `CopyOnWriteArrayList`
