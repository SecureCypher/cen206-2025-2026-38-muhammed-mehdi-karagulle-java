/**
 * @file ReminderSqliteRepository.java
 * @brief Reminder varlığı için SQLite repository.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.annotation.Generated;

import com.mehdi.petreminder.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @class ReminderSqliteRepository
 * @brief SQLite üzerinde Reminder (tüm alt türler) CRUD işlemleri.
 * @details Tüm Reminder alt sınıfları tek tabloda (reminder_type sütunu ile) saklanır.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class ReminderSqliteRepository extends SqliteRepository<Reminder> {

    /**
     * @brief Varsayılan yapıcı.
     */
    public ReminderSqliteRepository() {
        super();
    }

    /**
     * @brief Test yapıcısı.
     * @param connection Test bağlantısı
     */
    public ReminderSqliteRepository(Connection connection) {
        super(connection);
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getTableName() {
        return "reminders";
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getCreateTableSql() {
        return "CREATE TABLE IF NOT EXISTS reminders ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "pet_id INTEGER NOT NULL,"
            + "pet_name TEXT,"
            + "reminder_type TEXT NOT NULL,"
            + "scheduled_time TEXT NOT NULL,"
            + "completed INTEGER DEFAULT 0,"
            + "description TEXT,"
            + "priority TEXT DEFAULT 'ORTA',"
            + "recurring INTEGER DEFAULT 0,"
            + "recurrence_pattern TEXT,"
            + "food_type TEXT,"
            + "portion_grams REAL,"
            + "includes_water_change INTEGER DEFAULT 0,"
            + "medication_name TEXT,"
            + "dosage REAL,"
            + "dosage_unit TEXT,"
            + "prescribed INTEGER DEFAULT 0,"
            + "exercise_type TEXT,"
            + "duration_minutes INTEGER,"
            + "distance_km REAL,"
            + "grooming_type TEXT,"
            + "professional INTEGER DEFAULT 0,"
            + "groomer_name TEXT,"
            + "veterinarian_name TEXT,"
            + "clinic_name TEXT,"
            + "reason TEXT,"
            + "estimated_cost REAL,"
            + "confirmed INTEGER DEFAULT 0,"
            + "clinic_phone TEXT"
            + ")";
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getInsertSql() {
        return "INSERT INTO reminders (pet_id,pet_name,reminder_type,scheduled_time,"
            + "completed,description,priority,recurring,recurrence_pattern,"
            + "food_type,portion_grams,includes_water_change,"
            + "medication_name,dosage,dosage_unit,prescribed,"
            + "exercise_type,duration_minutes,distance_km,"
            + "grooming_type,professional,groomer_name,"
            + "veterinarian_name,clinic_name,reason,estimated_cost,confirmed,clinic_phone) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getUpdateSql() {
        return "UPDATE reminders SET pet_id=?,pet_name=?,reminder_type=?,scheduled_time=?,"
            + "completed=?,description=?,priority=?,recurring=?,recurrence_pattern=?,"
            + "food_type=?,portion_grams=?,includes_water_change=?,"
            + "medication_name=?,dosage=?,dosage_unit=?,prescribed=?,"
            + "exercise_type=?,duration_minutes=?,distance_km=?,"
            + "grooming_type=?,professional=?,groomer_name=?,"
            + "veterinarian_name=?,clinic_name=?,reason=?,estimated_cost=?,confirmed=?,"
            + "clinic_phone=? WHERE id=?";
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    @com.mehdi.petreminder.annotation.Generated
    protected void setInsertParams(PreparedStatement ps, Reminder r) throws SQLException {
        ps.setInt(1, r.getPetId());
        ps.setString(2, r.getPetName());
        ps.setString(3, r.getReminderType());
        ps.setString(4, r.getScheduledTime() != null ? r.getScheduledTime().toString() : null);
        ps.setInt(5, r.isCompleted() ? 1 : 0);
        ps.setString(6, r.getDescription());
        ps.setString(7, r.getPriority());
        ps.setInt(8, r.isRecurring() ? 1 : 0);
        ps.setString(9, r.getRecurrencePattern());
        // FeedingReminder
        ps.setString(10, r instanceof FeedingReminder ? ((FeedingReminder) r).getFoodType() : null);
        ps.setObject(11, r instanceof FeedingReminder ? ((FeedingReminder) r).getPortionGrams() : null);
        ps.setInt(12, r instanceof FeedingReminder && ((FeedingReminder) r).isIncludesWaterChange() ? 1 : 0);
        // MedicationReminder
        ps.setString(13, r instanceof MedicationReminder ? ((MedicationReminder) r).getMedicationName() : null);
        ps.setObject(14, r instanceof MedicationReminder ? ((MedicationReminder) r).getDosage() : null);
        ps.setString(15, r instanceof MedicationReminder ? ((MedicationReminder) r).getDosageUnit() : null);
        ps.setInt(16, r instanceof MedicationReminder && ((MedicationReminder) r).isPrescribed() ? 1 : 0);
        // ExerciseReminder
        ps.setString(17, r instanceof ExerciseReminder ? ((ExerciseReminder) r).getExerciseType() : null);
        ps.setObject(18, r instanceof ExerciseReminder ? ((ExerciseReminder) r).getDurationMinutes() : null);
        ps.setObject(19, r instanceof ExerciseReminder ? ((ExerciseReminder) r).getDistanceKm() : null);
        // GroomingReminder
        ps.setString(20, r instanceof GroomingReminder ? ((GroomingReminder) r).getGroomingType() : null);
        ps.setInt(21, r instanceof GroomingReminder && ((GroomingReminder) r).isProfessional() ? 1 : 0);
        ps.setString(22, r instanceof GroomingReminder ? ((GroomingReminder) r).getGroomerName() : null);
        // VetAppointment
        ps.setString(23, r instanceof VetAppointment ? ((VetAppointment) r).getVeterinarianName() : null);
        ps.setString(24, r instanceof VetAppointment ? ((VetAppointment) r).getClinicName() : null);
        ps.setString(25, r instanceof VetAppointment ? ((VetAppointment) r).getReason() : null);
        ps.setObject(26, r instanceof VetAppointment ? ((VetAppointment) r).getEstimatedCost() : null);
        ps.setInt(27, r instanceof VetAppointment && ((VetAppointment) r).isConfirmed() ? 1 : 0);
        ps.setString(28, r instanceof VetAppointment ? ((VetAppointment) r).getClinicPhone() : null);
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected void setUpdateParams(PreparedStatement ps, Reminder r) throws SQLException {
        setInsertParams(ps, r);
        ps.setInt(29, r.getId());
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    @com.mehdi.petreminder.annotation.Generated
    protected Reminder mapRow(ResultSet rs) throws SQLException {
        String type = rs.getString("reminder_type");
        int id = rs.getInt("id");
        int petId = rs.getInt("pet_id");
        String petName = rs.getString("pet_name");
        String timeStr = rs.getString("scheduled_time");
        LocalDateTime scheduledTime = timeStr != null ? LocalDateTime.parse(timeStr) : null;
        String description = rs.getString("description");
        String priority = rs.getString("priority");
        boolean recurring = rs.getInt("recurring") == 1;
        String recurrencePattern = rs.getString("recurrence_pattern");
        boolean completed = rs.getInt("completed") == 1;

        Reminder reminder;
        switch (type) {
            case "Besleme":
                FeedingReminder fr = new FeedingReminder(id, petId, petName, scheduledTime,
                    description, priority, recurring, recurrencePattern,
                    rs.getString("food_type"), rs.getDouble("portion_grams"),
                    rs.getInt("includes_water_change") == 1);
                reminder = fr;
                break;
            case "Ilac":
                MedicationReminder mr = new MedicationReminder(id, petId, petName, scheduledTime,
                    description, priority, recurring, recurrencePattern,
                    rs.getString("medication_name"), rs.getDouble("dosage"),
                    rs.getString("dosage_unit"), rs.getInt("prescribed") == 1, null);
                reminder = mr;
                break;
            case "Egzersiz":
                ExerciseReminder er = new ExerciseReminder(id, petId, petName, scheduledTime,
                    description, priority, recurring, recurrencePattern,
                    rs.getString("exercise_type"), rs.getInt("duration_minutes"),
                    rs.getDouble("distance_km"));
                reminder = er;
                break;
            case "Bakim":
                GroomingReminder gr = new GroomingReminder(id, petId, petName, scheduledTime,
                    description, priority, recurring, recurrencePattern,
                    rs.getString("grooming_type"), rs.getInt("professional") == 1,
                    rs.getString("groomer_name"));
                reminder = gr;
                break;
            case "Veteriner":
                VetAppointment va = new VetAppointment(id, petId, petName, scheduledTime,
                    description, priority, recurring, recurrencePattern,
                    rs.getString("veterinarian_name"), rs.getString("clinic_name"),
                    rs.getString("reason"), rs.getDouble("estimated_cost"),
                    rs.getInt("confirmed") == 1, rs.getString("clinic_phone"));
                reminder = va;
                break;
            default:
                FeedingReminder def = new FeedingReminder(id, petId, petName, scheduledTime,
                    "Mama", 100);
                reminder = def;
        }
        reminder.setCompleted(completed);
        return reminder;
    }

    /**
     * @brief PetId'ye göre hatırlatıcıları getirir.
     * @param petId Pet ID
     * @return Hatırlatıcı listesi
     */
    public List<Reminder> findByPetId(int petId) {
        List<Reminder> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM reminders WHERE pet_id = ?")) {
            ps.setInt(1, petId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RepositoryException("findByPetId hatası", e);
        }
        return list;
    }
}
