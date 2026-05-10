/**
 * @file ReminderMySqlRepository.java
 * @brief Reminder varlığı için MySQL repository.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.annotation.Generated;

import com.mehdi.petreminder.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @class ReminderMySqlRepository
 * @brief MySQL üzerinde Reminder CRUD işlemleri.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class ReminderMySqlRepository extends MySqlRepository<Reminder> {

    /**
     * @brief Varsayılan yapıcı.
     */
    public ReminderMySqlRepository() {
        super();
    }

    /**
     * @brief Test yapıcısı.
     * @param connection Test bağlantısı
     */
    public ReminderMySqlRepository(Connection connection) {
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
            + "id INT AUTO_INCREMENT PRIMARY KEY,"
            + "pet_id INT NOT NULL,"
            + "pet_name VARCHAR(50),"
            + "reminder_type VARCHAR(30) NOT NULL,"
            + "scheduled_time DATETIME NOT NULL,"
            + "completed BOOLEAN DEFAULT FALSE,"
            + "description TEXT,"
            + "priority VARCHAR(10) DEFAULT 'ORTA',"
            + "recurring BOOLEAN DEFAULT FALSE,"
            + "recurrence_pattern VARCHAR(30),"
            + "food_type VARCHAR(50),"
            + "portion_grams DOUBLE,"
            + "includes_water_change BOOLEAN DEFAULT FALSE,"
            + "medication_name VARCHAR(100),"
            + "dosage DOUBLE,"
            + "dosage_unit VARCHAR(20),"
            + "prescribed BOOLEAN DEFAULT FALSE,"
            + "exercise_type VARCHAR(50),"
            + "duration_minutes INT,"
            + "distance_km DOUBLE,"
            + "grooming_type VARCHAR(50),"
            + "professional BOOLEAN DEFAULT FALSE,"
            + "groomer_name VARCHAR(100),"
            + "veterinarian_name VARCHAR(100),"
            + "clinic_name VARCHAR(100),"
            + "reason VARCHAR(100),"
            + "estimated_cost DOUBLE,"
            + "confirmed BOOLEAN DEFAULT FALSE,"
            + "clinic_phone VARCHAR(20)"
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
        ps.setObject(4, r.getScheduledTime() != null
            ? Timestamp.valueOf(r.getScheduledTime()) : null);
        ps.setBoolean(5, r.isCompleted());
        ps.setString(6, r.getDescription());
        ps.setString(7, r.getPriority());
        ps.setBoolean(8, r.isRecurring());
        ps.setString(9, r.getRecurrencePattern());
        ps.setString(10, r instanceof FeedingReminder ? ((FeedingReminder) r).getFoodType() : null);
        ps.setObject(11, r instanceof FeedingReminder ? ((FeedingReminder) r).getPortionGrams() : null);
        ps.setBoolean(12, r instanceof FeedingReminder && ((FeedingReminder) r).isIncludesWaterChange());
        ps.setString(13, r instanceof MedicationReminder ? ((MedicationReminder) r).getMedicationName() : null);
        ps.setObject(14, r instanceof MedicationReminder ? ((MedicationReminder) r).getDosage() : null);
        ps.setString(15, r instanceof MedicationReminder ? ((MedicationReminder) r).getDosageUnit() : null);
        ps.setBoolean(16, r instanceof MedicationReminder && ((MedicationReminder) r).isPrescribed());
        ps.setString(17, r instanceof ExerciseReminder ? ((ExerciseReminder) r).getExerciseType() : null);
        ps.setObject(18, r instanceof ExerciseReminder ? ((ExerciseReminder) r).getDurationMinutes() : null);
        ps.setObject(19, r instanceof ExerciseReminder ? ((ExerciseReminder) r).getDistanceKm() : null);
        ps.setString(20, r instanceof GroomingReminder ? ((GroomingReminder) r).getGroomingType() : null);
        ps.setBoolean(21, r instanceof GroomingReminder && ((GroomingReminder) r).isProfessional());
        ps.setString(22, r instanceof GroomingReminder ? ((GroomingReminder) r).getGroomerName() : null);
        ps.setString(23, r instanceof VetAppointment ? ((VetAppointment) r).getVeterinarianName() : null);
        ps.setString(24, r instanceof VetAppointment ? ((VetAppointment) r).getClinicName() : null);
        ps.setString(25, r instanceof VetAppointment ? ((VetAppointment) r).getReason() : null);
        ps.setObject(26, r instanceof VetAppointment ? ((VetAppointment) r).getEstimatedCost() : null);
        ps.setBoolean(27, r instanceof VetAppointment && ((VetAppointment) r).isConfirmed());
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
        Timestamp ts = rs.getTimestamp("scheduled_time");
        LocalDateTime scheduledTime = ts != null ? ts.toLocalDateTime() : null;
        String description = rs.getString("description");
        String priority = rs.getString("priority");
        boolean recurring = rs.getBoolean("recurring");
        String recurrencePattern = rs.getString("recurrence_pattern");
        boolean completed = rs.getBoolean("completed");

        Reminder reminder;
        switch (type != null ? type : "") {
            case "Besleme":
                reminder = new FeedingReminder(id, petId, petName, scheduledTime,
                    description, priority, recurring, recurrencePattern,
                    rs.getString("food_type"), rs.getDouble("portion_grams"),
                    rs.getBoolean("includes_water_change"));
                break;
            case "Ilac":
                reminder = new MedicationReminder(id, petId, petName, scheduledTime,
                    description, priority, recurring, recurrencePattern,
                    rs.getString("medication_name"), rs.getDouble("dosage"),
                    rs.getString("dosage_unit"), rs.getBoolean("prescribed"), null);
                break;
            case "Egzersiz":
                reminder = new ExerciseReminder(id, petId, petName, scheduledTime,
                    description, priority, recurring, recurrencePattern,
                    rs.getString("exercise_type"), rs.getInt("duration_minutes"),
                    rs.getDouble("distance_km"));
                break;
            case "Bakim":
                reminder = new GroomingReminder(id, petId, petName, scheduledTime,
                    description, priority, recurring, recurrencePattern,
                    rs.getString("grooming_type"), rs.getBoolean("professional"),
                    rs.getString("groomer_name"));
                break;
            case "Veteriner":
                reminder = new VetAppointment(id, petId, petName, scheduledTime,
                    description, priority, recurring, recurrencePattern,
                    rs.getString("veterinarian_name"), rs.getString("clinic_name"),
                    rs.getString("reason"), rs.getDouble("estimated_cost"),
                    rs.getBoolean("confirmed"), rs.getString("clinic_phone"));
                break;
            default:
                reminder = new FeedingReminder(id, petId, petName, scheduledTime, "Mama", 100);
        }
        reminder.setCompleted(completed);
        return reminder;
    }
}
