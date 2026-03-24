/**
 * @file ConsoleApp.java
 * @brief Console application main loop.
 */
package com.mehdi.petreminder;

import com.mehdi.petreminder.config.StorageConfig;
import com.mehdi.petreminder.config.StorageType;
import com.mehdi.petreminder.model.*;
import com.mehdi.petreminder.service.MedicalRecordService;
import com.mehdi.petreminder.service.PetService;
import com.mehdi.petreminder.service.ReminderService;
import com.mehdi.petreminder.service.ServiceException;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * @class ConsoleApp
 * @brief Console application main loop and menu manager.
 * @details Fully functional CRUD CLI mode connecting to Service layer.
 */
public class ConsoleApp {

    /** @brief Logger for ConsoleApp. */
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ConsoleApp.class);
    
    /** @brief Console input scanner. */
    private final Scanner scanner;
    
    /** @brief Execution flag. */
    private boolean running;

    /** @brief PetService instance. */
    private PetService petService;
    
    /** @brief ReminderService instance. */
    private ReminderService reminderService;
    
    /** @brief MedicalRecordService instance. */
    private MedicalRecordService medicalRecordService;

    /** @brief Interactive Terminal for menus. */
    private Terminal terminal;

    /**
     * @brief ConsoleApp method.
     */
    public ConsoleApp() {
        this.scanner = new Scanner(System.in);
        this.running = false;
        initTerminal();
        initServices();
    }

    /**
     * @brief ConsoleApp method.
     */
    public ConsoleApp(Scanner scanner) {
        this.scanner = scanner;
        this.running = false;
        initServices();
    }

    private void initTerminal() {
        if (System.console() != null) {
            try {
                this.terminal = TerminalBuilder.builder().system(true).build();
            } catch (Exception e) {
                this.terminal = null;
            }
        }
    }

    /**
     * @brief initServices method.
     */
    private void initServices() {
        try {
            this.petService = new PetService();
            this.reminderService = new ReminderService();
            this.medicalRecordService = new MedicalRecordService();
        } catch (Exception e) {
            logger.error("Failed to initialize services Component: {}", e.getMessage());
        }
    }

    /**
     * @brief start method.
     */
    public void start() {
        this.running = true;
        logger.info("ConsoleApp started.");
        showMainMenu();
    }

    /**
     * @brief showMainMenu method.
     */
    public void showMainMenu() {
        while (running) {
            String choice = selectMenuOption(
                "\n==========================================\n   PET CARE REMINDER SYSTEM - MAIN MENU  \n==========================================",
                new String[]{"Pets", "Reminders", "Vet Appointments", "Medical Records", "Settings (Storage: " + StorageConfig.getActiveBackend().getDisplayName() + ")", "Exit"},
                new String[]{"1", "2", "3", "4", "5", "0"}
            );
            handleMainMenuChoice(choice);
        }
    }

    /**
     * @brief printMainMenu method.
     */
    public void printMainMenu() {
        System.out.println("\n==========================================");
        System.out.println("   PET CARE REMINDER SYSTEM - MAIN MENU  ");
        System.out.println("==========================================");
        System.out.println("  [1] Pets");
        System.out.println("  [2] Reminders");
        System.out.println("  [3] Vet Appointments");
        System.out.println("  [4] Medical Records");
        System.out.println("  [5] Settings");
        System.out.println("  [0] Exit");
        System.out.println("==========================================");
        System.out.print("Your choice: ");
    }

    /**
     * @brief handleMainMenuChoice method.
     */
    public void handleMainMenuChoice(String choice) {
        if (choice == null) return;
        switch (choice.trim()) {
            case "1": showPetsMenu(); break;
            case "2": showRemindersMenu(); break;
            case "3": showVetMenu(); break;
            case "4": showMedicalMenu(); break;
            case "5": showSettingsMenu(); initServices(); break; // re-init on storage change
            case "0": exitApp(); break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    /**
     * @brief selectMenuOption method.
     */
    private String selectMenuOption(String title, String[] options, String[] returnValues) {
        if (terminal == null) {
            System.out.println(title);
            for (int i = 0; i < options.length; i++) {
                System.out.println("  [" + returnValues[i] + "] " + options[i]);
            }
            System.out.print("Your choice: ");
            return readInput();
        }

        try {
            terminal.enterRawMode();
            int selectedIndex = 0;
            boolean firstDraw = true;
            
            while (running) {
                if (!firstDraw) {
                    System.out.print("\033[" + (options.length + 2) + "A");
                }
                firstDraw = false;
                
                System.out.println(title);
                for (int i = 0; i < options.length; i++) {
                    if (i == selectedIndex) {
                        System.out.println("\033[2K\r > [" + returnValues[i] + "] " + options[i]);
                    } else {
                        System.out.println("\033[2K\r   [" + returnValues[i] + "] " + options[i]);
                    }
                }
                System.out.print("\033[2K\r (Up/Down/Tab to navigate, Enter to select)");
                System.out.flush();

                int c = terminal.reader().read();
                if (c == 9) { // Tab
                    selectedIndex = (selectedIndex + 1) % options.length;
                } else if (c == 13 || c == 10) { // Enter
                    System.out.println();
                    terminal.echo(true); // Restore echo before returning
                    return returnValues[selectedIndex];
                } else if (c == 27) { // ESC sequence (Arrow keys)
                    int next1 = terminal.reader().read(100);
                    int next2 = terminal.reader().read(100);
                    if (next1 == 91) { // '['
                        if (next2 == 65) { // Up
                            selectedIndex = (selectedIndex - 1 + options.length) % options.length;
                        } else if (next2 == 66) { // Down
                            selectedIndex = (selectedIndex + 1) % options.length;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // fallback gracefully
        } finally {
            try { terminal.echo(true); } catch(Exception ignored) {}
        }
        
        System.out.println(title);
        for (int i = 0; i < options.length; i++) {
            System.out.println("  [" + returnValues[i] + "] " + options[i]);
        }
        System.out.print("Your choice: ");
        return readInput();
    }

    // ==========================================
    // PETS MENU
    // ==========================================
    /**
     * @brief showPetsMenu method.
     */
    public void showPetsMenu() {
        while (running) {
            String choice = selectMenuOption(
                "\n--- PETS ---",
                new String[]{"List All Pets", "Add New Pet", "Edit Pet", "Delete Pet", "Back to Main Menu"},
                new String[]{"1", "2", "3", "4", "0"}
            );

            switch (choice) {
                case "1": listAllPets(); break;
                case "2": addNewPet(); break;
                case "3": editPet(); break;
                case "4": deletePet(); break;
                case "0": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * @brief listAllPets method.
     */
    private void listAllPets() {
        System.out.println("\n--- LIST OF PETS ---");
        try {
            List<Pet> pets = petService.getAllPets();
            if (pets.isEmpty()) {
                System.out.println("No pets found. Please add a pet first.");
                return;
            }
            for (Pet p : pets) {
                System.out.println(String.format("ID: %d | Name: %s | Species: %s | Age: %s",
                    p.getId(), p.getName(), p.getSpecies(), p.getAgeString()));
            }
        } catch (Exception e) {
            System.out.println("Error listing pets: " + e.getMessage());
        }
    }

    /**
     * @brief addNewPet method.
     */
    private void addNewPet() {
        System.out.println("\n--- ADD NEW PET ---");
        
        System.out.print("Species (1: Dog, 2: Cat, 3: Bird): ");
        String typeChoice = readInput();
        
        System.out.print("Name: ");
        String name = readInput();
        if (name.isEmpty()) {
            System.out.println("Error: Name cannot be empty.");
            return;
        }

        System.out.print("Birth Date (YYYY-MM-DD): ");
        LocalDate birthDate = readDateInput();
        if (birthDate == null) return;

        System.out.print("Gender (Male/Female): ");
        String gender = readInput();

        System.out.print("Weight (kg): ");
        double weight = readDoubleInput();
        if (weight <= 0) return;

        System.out.print("Breed: ");
        String breed = readInput();

        Pet newPet = null;
        try {
            if ("1".equals(typeChoice)) {
                Dog dog = new Dog(0, name, birthDate, 1);
                dog.setGender(gender);
                dog.setWeight(weight);
                dog.setBreed(breed);
                System.out.print("Is trained? (true/false): ");
                dog.setTrained(Boolean.parseBoolean(readInput()));
                newPet = dog;
            } else if ("2".equals(typeChoice)) {
                Cat cat = new Cat(0, name, birthDate, 1);
                cat.setGender(gender);
                cat.setWeight(weight);
                cat.setBreed(breed);
                System.out.print("Is indoor? (true/false): ");
                cat.setIndoor(Boolean.parseBoolean(readInput()));
                newPet = cat;
            } else if ("3".equals(typeChoice)) {
                Bird bird = new Bird(0, name, birthDate, 1);
                bird.setGender(gender);
                bird.setWeight(weight);
                bird.setBirdType(breed);
                System.out.print("Can talk? (true/false): ");
                bird.setCanTalk(Boolean.parseBoolean(readInput()));
                newPet = bird;
            } else {
                System.out.println("Invalid species choice.");
                return;
            }

            petService.addPet(newPet);
            System.out.println("Pet '" + name + "' added successfully.");
        } catch (ServiceException e) {
            System.out.println("Failed to add pet: " + e.getMessage());
        }
    }

    /**
     * @brief editPet method.
     */
    private void editPet() {
        listAllPets();
        System.out.print("\nEnter ID of pet to edit (or 0 to cancel): ");
        int id = readIntInput();
        if (id <= 0) return;

        try {
            Pet pet = petService.getPetById(id);
            System.out.println("Editing Pet: " + pet.getName());
            
            System.out.print("New Weight (" + pet.getWeight() + "): ");
            String weightStr = readInput();
            if (!weightStr.isEmpty()) {
                pet.setWeight(Double.parseDouble(weightStr));
            }

            System.out.print("New Notes: ");
            String notes = readInput();
            if (!notes.isEmpty()) {
                pet.setNotes(notes);
            }

            petService.updatePet(pet);
            System.out.println("Pet updated successfully.");
        } catch (ServiceException | NumberFormatException e) {
            System.out.println("Failed to update pet: " + e.getMessage());
        }
    }

    /**
     * @brief deletePet method.
     */
    private void deletePet() {
        listAllPets();
        System.out.print("\nEnter ID of pet to delete (or 0 to cancel): ");
        int id = readIntInput();
        if (id <= 0) return;

        try {
            Pet p = petService.getPetById(id);
            System.out.print("Are you sure you want to delete '" + p.getName() + "'? (y/n): ");
            if ("y".equalsIgnoreCase(readInput())) {
                petService.deletePet(id);
                System.out.println("Pet deleted successfully.");
            }
        } catch (ServiceException e) {
            System.out.println("Failed to delete pet: " + e.getMessage());
        }
    }

    // ==========================================
    // REMINDERS MENU
    // ==========================================
    /**
     * @brief showRemindersMenu method.
     */
    public void showRemindersMenu() {
        while (running) {
            String choice = selectMenuOption(
                "\n--- REMINDERS ---",
                new String[]{"List All Pending Reminders", "Add New Reminder", "Mark Reminder as Completed", "Delete Reminder", "Back to Main Menu"},
                new String[]{"1", "2", "3", "4", "0"}
            );

            switch (choice) {
                case "1": listPendingReminders(); break;
                case "2": addNewReminder(); break;
                case "3": markReminderCompleted(); break;
                case "4": deleteReminder(); break;
                case "0": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * @brief listPendingReminders method.
     */
    private void listPendingReminders() {
        System.out.println("\n--- PENDING REMINDERS ---");
        try {
            List<Reminder> reminders = reminderService.getPendingReminders();
            if (reminders.isEmpty()) {
                System.out.println("No pending reminders found.");
                return;
            }
            for (Reminder r : reminders) {
                System.out.println(String.format("ID: %d | Pet ID: %d | Time: %s | Desc: %s",
                    r.getId(), r.getPetId(), r.getScheduledTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), r.getDescription()));
            }
        } catch (Exception e) {
            System.out.println("Error listing reminders: " + e.getMessage());
        }
    }

    /**
     * @brief addNewReminder method.
     */
    private void addNewReminder() {
        System.out.println("\n--- ADD NEW REMINDER ---");
        System.out.print("Enter Pet ID for this reminder: ");
        int petId = readIntInput();
        if (petId <= 0) return;

        // Verify pet exists
        try {
            petService.getPetById(petId);
        } catch (ServiceException e) {
            System.out.println("Error: Pet not found.");
            return;
        }

        System.out.print("Reminder Type (1: Feeding, 2: Medication, 3: Grooming, 4: Exercise): ");
        String typeChoice = readInput();

        System.out.print("Description: ");
        String desc = readInput();
        if (desc.isEmpty()) {
            System.out.println("Error: Description cannot be empty.");
            return;
        }

        System.out.print("Date and Time (YYYY-MM-DD HH:MM): ");
        LocalDateTime time = readDateTimeInput();
        if (time == null) return;

        Reminder newReminder = null;
        try {
            switch (typeChoice) {
                case "1":
                    FeedingReminder fr = new FeedingReminder(0, petId, "Unknown", time, "Unknown", 0);
                    fr.setDescription(desc);
                    System.out.print("Food Type: ");
                    fr.setFoodType(readInput());
                    System.out.print("Portion (grams): ");
                    fr.setPortionGrams((int)readDoubleInput());
                    newReminder = fr;
                    break;
                case "2":
                    MedicationReminder mr = new MedicationReminder(0, petId, "Unknown", time, "Unknown", 0.0, "Unknown");
                    mr.setDescription(desc);
                    System.out.print("Medication Name: ");
                    mr.setMedicationName(readInput());
                    System.out.print("Dosage: ");
                    mr.setDosage(readDoubleInput());
                    newReminder = mr;
                    break;
                case "3":
                    GroomingReminder gr = new GroomingReminder(0, petId, "Unknown", time, "Unknown", false);
                    gr.setDescription(desc);
                    System.out.print("Professional grooming? (true/false): ");
                    gr.setProfessional(Boolean.parseBoolean(readInput()));
                    newReminder = gr;
                    break;
                case "4":
                    ExerciseReminder er = new ExerciseReminder(0, petId, "Unknown", time, "Unknown", 0);
                    er.setDescription(desc);
                    System.out.print("Duration (minutes): ");
                    er.setDurationMinutes((int)readDoubleInput());
                    newReminder = er;
                    break;
                default:
                    System.out.println("Invalid type choice.");
                    return;
            }

            reminderService.addReminder(newReminder);
            System.out.println("Reminder added successfully.");
        } catch (ServiceException e) {
            System.out.println("Failed to add reminder: " + e.getMessage());
        }
    }

    /**
     * @brief markReminderCompleted method.
     */
    private void markReminderCompleted() {
        listPendingReminders();
        System.out.print("\nEnter ID of reminder to complete (or 0 to cancel): ");
        int id = readIntInput();
        if (id <= 0) return;

        try {
            reminderService.markCompleted(id);
            System.out.println("Reminder marked as completed.");
        } catch (ServiceException e) {
            System.out.println("Failed to update reminder: " + e.getMessage());
        }
    }

    /**
     * @brief deleteReminder method.
     */
    private void deleteReminder() {
        listPendingReminders();
        System.out.print("\nEnter ID of reminder to delete (or 0 to cancel): ");
        int id = readIntInput();
        if (id <= 0) return;

        try {
            reminderService.deleteReminder(id);
            System.out.println("Reminder deleted successfully.");
        } catch (ServiceException e) {
            System.out.println("Failed to delete reminder: " + e.getMessage());
        }
    }

    // ==========================================
    // VET APPOINTMENTS MENU
    // ==========================================
    /**
     * @brief showVetMenu method.
     */
    public void showVetMenu() {
        while (running) {
            String choice = selectMenuOption(
                "\n--- VET APPOINTMENTS ---",
                new String[]{"List Vet Appointments", "Add New Appointment", "Back to Main Menu"},
                new String[]{"1", "2", "0"}
            );

            switch (choice) {
                case "1": listVetAppointments(); break;
                case "2": addVetAppointment(); break;
                case "0": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * @brief listVetAppointments method.
     */
    private void listVetAppointments() {
        System.out.println("\n--- VET APPOINTMENTS ---");
        try {
            List<Reminder> reminders = reminderService.getPendingReminders();
            boolean found = false;
            for (Reminder r : reminders) {
                if (r instanceof VetAppointment) {
                    VetAppointment va = (VetAppointment) r;
                    System.out.println(String.format("ID: %d | Pet ID: %d | Time: %s | Clinic: %s | Reason: %s",
                        va.getId(), va.getPetId(), va.getScheduledTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 
                        va.getClinicName(), va.getReason()));
                    found = true;
                }
            }
            if (!found) System.out.println("No pending vet appointments found.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * @brief addVetAppointment method.
     */
    private void addVetAppointment() {
        System.out.println("\n--- ADD VET APPOINTMENT ---");
        System.out.print("Enter Pet ID: ");
        int petId = readIntInput();
        if (petId <= 0) return;

        System.out.print("Description: ");
        String desc = readInput();

        System.out.print("Date and Time (YYYY-MM-DD HH:MM): ");
        LocalDateTime time = readDateTimeInput();
        if (time == null) return;

        System.out.print("Clinic Name: ");
        String clinic = readInput();

        System.out.print("Reason: ");
        String reason = readInput();

        try {
            VetAppointment va = new VetAppointment(0, petId, "Unknown", time, "Unknown", clinic, reason);
            va.setDescription(desc);
            reminderService.addReminder(va);
            System.out.println("Vet appointment scheduling successful.");
        } catch (ServiceException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    // ==========================================
    // MEDICAL RECORDS MENU
    // ==========================================
    /**
     * @brief showMedicalMenu method.
     */
    public void showMedicalMenu() {
        while (running) {
            String choice = selectMenuOption(
                "\n--- MEDICAL RECORDS ---",
                new String[]{"List Records", "Add New Record", "Back to Main Menu"},
                new String[]{"1", "2", "0"}
            );

            switch (choice) {
                case "1": listMedicalRecords(); break;
                case "2": addMedicalRecord(); break;
                case "0": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * @brief listMedicalRecords method.
     */
    private void listMedicalRecords() {
        System.out.println("\n--- MEDICAL RECORDS ---");
        System.out.print("Enter Pet ID (or 0 for all): ");
        int petId = readIntInput();
        try {
            List<MedicalRecord> records = (petId > 0) ? 
                medicalRecordService.getRecordsByPetId(petId) : medicalRecordService.getAllRecords();
                
            if (records.isEmpty()) {
                System.out.println("No records found.");
                return;
            }
            
            for (MedicalRecord m : records) {
                System.out.println(String.format("ID: %d | Pet ID: %d | Date: %s | Diagnosis: %s | Treatment: %s",
                    m.getId(), m.getPetId(), m.getRecordDate(), m.getDiagnosis(), m.getTreatment()));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * @brief addMedicalRecord method.
     */
    private void addMedicalRecord() {
        System.out.println("\n--- ADD MEDICAL RECORD ---");
        System.out.print("Enter Pet ID: ");
        int petId = readIntInput();
        if (petId <= 0) return;

        System.out.print("Date (YYYY-MM-DD): ");
        LocalDate date = readDateInput();
        if (date == null) return;

        System.out.print("Diagnosis: ");
        String diagnosis = readInput();

        System.out.print("Treatment: ");
        String treatment = readInput();

        try {
            MedicalRecord record = new MedicalRecord(0, petId, "Unknown", date, "Checkup", diagnosis, "Unknown");
            record.setTreatment(treatment);
            medicalRecordService.addRecord(record);
            System.out.println("Medical record added successfully.");
        } catch (ServiceException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    // ==========================================
    // SETTINGS MENU
    // ==========================================
    /**
     * @brief showSettingsMenu method.
     */
    public void showSettingsMenu() {
        String choice = selectMenuOption(
            "\n--- SETTINGS ---\nCurrent Storage: " + StorageConfig.getActiveBackend().getDisplayName(),
            new String[]{"Binary File I/O (.bin)", "SQLite (.db)", "MySQL (Docker required)", "Back"},
            new String[]{"1", "2", "3", "0"}
        );
        handleStorageSwitch(choice);
    }

    /**
     * @brief handleStorageSwitch method.
     */
    public void handleStorageSwitch(String choice) {
        if (choice == null) return;
        switch (choice.trim()) {
            case "1":
                StorageConfig.setActiveBackend(StorageType.BINARY);
                System.out.println("Storage switched to: Binary File I/O");
                break;
            case "2":
                StorageConfig.setActiveBackend(StorageType.SQLITE);
                System.out.println("Storage switched to: SQLite");
                break;
            case "3":
                StorageConfig.setActiveBackend(StorageType.MYSQL);
                System.out.println("Storage switched to: MySQL");
                System.out.println("NOTE: MySQL requires Docker Compose to be running.");
                break;
            case "0":
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // ==========================================
    // UTILITIES
    // ==========================================
    /**
     * @brief exitApp method.
     */
    public void exitApp() {
        running = false;
        System.out.println("\nClosing Pet Care Reminder System...");
        System.out.println("Goodbye!");
    }

    /**
     * @brief readInput method.
     */
    public String readInput() {
        try {
            if (scanner.hasNextLine()) {
                return scanner.nextLine().trim();
            }
        } catch (Exception e) {
            logger.error("Input reading error: {}", e.getMessage());
        }
        return "";
    }
    
    // Test helper
    /**
     * @brief setScannerSource method.
     */
    protected void setScannerSource(String input) {
        // Only used in tests via reflection
    }

    /**
     * @brief readIntInput method.
     */
    private int readIntInput() {
        try {
            String input = readInput();
            return input.isEmpty() ? 0 : Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid number.");
            return -1;
        }
    }

    /**
     * @brief readDoubleInput method.
     */
    private double readDoubleInput() {
        try {
            String input = readInput();
            return input.isEmpty() ? 0.0 : Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid decimal number.");
            return -1.0;
        }
    }

    /**
     * @brief readDateInput method.
     */
    private LocalDate readDateInput() {
        String input = readInput();
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date format. Please use YYYY-MM-DD.");
            return null;
        }
    }

    /**
     * @brief readDateTimeInput method.
     */
    private LocalDateTime readDateTimeInput() {
        String input = readInput();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return LocalDateTime.parse(input, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid format. Please use YYYY-MM-DD HH:MM");
            return null;
        }
    }

    /**
     * @brief isRunning getter.
     * @return boolean running state
     */
    public boolean isRunning() { return running; }
    
    /**
     * @brief setRunning setter.
     * @param running running state
     */
    public void setRunning(boolean running) { this.running = running; }
}
