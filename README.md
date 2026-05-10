# PetReminder Application

[![Java CI with Maven](https://github.com/SecureCypher/cen206-2025-2026-38-muhammed-mehdi-karagulle-java/actions/workflows/ci.yml/badge.svg)](https://github.com/SecureCypher/cen206-2025-2026-38-muhammed-mehdi-karagulle-java/actions/workflows/ci.yml)
[![Release](https://github.com/SecureCypher/cen206-2025-2026-38-muhammed-mehdi-karagulle-java/actions/workflows/release.yml/badge.svg)](https://github.com/SecureCypher/cen206-2025-2026-38-muhammed-mehdi-karagulle-java/actions/workflows/release.yml)

## Overview
PetReminder is a comprehensive Java-based desktop application designed to help pet owners manage their pets' care routines, medical records, and veterinary appointments. 
Built using modern Java technologies including Swing (with FlatLaf), JDBC (SQLite/MySQL), and JUnit 5, it ensures robust performance, excellent test coverage, and a user-friendly interface.

## Features
- **Pet Management**: Add, view, edit, and delete pet profiles (Cats, Dogs, Birds, etc.).
- **Reminders**: Schedule feeding, medication, exercise, and grooming routines.
- **Medical Records**: Keep track of vaccinations, surgeries, and treatments.
- **Vet Appointments**: Log and schedule upcoming visits to the veterinarian.
- **Data Persistence**: Supports SQLite for local storage and MySQL for distributed usage.

## Architecture

The project follows a **lib/app modular architecture** as required by the project guide:

| Module | Path | Description |
|--------|------|-------------|
| **lib** | `petreminder-app/lib/` | Business logic: model, service, repository, observer, config |
| **app** | `petreminder-app/app/` | Entry point + GUI (Swing panels) |
| **test** | `*/src/test/java/` | Unit tests for both modules |

- **Language**: Java 11/17
- **UI Framework**: Java Swing with FlatLaf Light Theme and MigLayout
- **Database**: SQLite (default), MySQL (Docker), Binary, H2 (Testing)
- **Storage**: IRepository pattern with runtime backend switching
- **Design Patterns**: Observer, Factory Method, Template Method (min. 3 categories)
- **Testing**: JUnit 5, Mockito — 100% instruction coverage
- **Documentation**: Doxygen/Javadoc — 100% coverage
- **Build Tool**: Apache Maven (multi-module)
- **CI/CD**: GitHub Actions

## How to Run
Use the provided batch scripts in the repository root:
1. `4-install-required-apps.bat` - Install required local dependencies such as Doxygen, Lcov, ReportGenerator.
2. `7-build-app.bat` - Compiles the project, runs tests, and generates the fat JAR along with documentation (Doxygen) and coverage reports.
3. `8-run-app.bat` - Launches the PetReminder desktop application standalone.
4. `9-run-webpage.bat` - Starts a local web server (Maven Site) to view this documentation and Doxygen HTML outputs (port 9000).

## Documentation
The source code is thoroughly documented using Javadoc. To view the Doxygen documentation, build the project using `7-build-app.bat` and run `9-run-webpage.bat`. Then navigate to `http://localhost:9000/doxygen/html/index.html`.

## Team Members
Developed by students at Recep Tayyip Erdogan University (RTEU):
- Ibrahim Demirci
- Muhammed Mehdi Karagulle
- Zumre Uykun

## License
This project is licensed under the **GNU Affero General Public License v3.0 (AGPL-3.0)**. 
See the [LICENSE](../LICENSE) file in the root directory for more details.
