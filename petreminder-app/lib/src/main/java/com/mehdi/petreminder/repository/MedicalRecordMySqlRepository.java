/**
 * @file MedicalRecordMySqlRepository.java
 * @brief MedicalRecord için MySQL repository.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.annotation.Generated;

import com.mehdi.petreminder.model.MedicalRecord;

import java.sql.*;
import java.time.LocalDate;

/**
 * @class MedicalRecordMySqlRepository
 * @brief MySQL üzerinde MedicalRecord CRUD.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class MedicalRecordMySqlRepository extends MySqlRepository<MedicalRecord> {

    /** @brief Varsayılan yapıcı. */
    public MedicalRecordMySqlRepository() { super(); }

    /**
     * @brief Test yapıcısı.
     * @param connection Test bağlantısı
     */
    public MedicalRecordMySqlRepository(Connection connection) { super(connection); }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getTableName() { return "medical_records"; }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getCreateTableSql() {
        return "CREATE TABLE IF NOT EXISTS medical_records ("
            + "id INT AUTO_INCREMENT PRIMARY KEY,"
            + "pet_id INT NOT NULL,"
            + "pet_name VARCHAR(50),"
            + "record_date DATE NOT NULL,"
            + "record_type VARCHAR(30) NOT NULL,"
            + "diagnosis TEXT,"
            + "treatment TEXT,"
            + "veterinarian_name VARCHAR(100),"
            + "cost DOUBLE DEFAULT 0,"
            + "next_check_date DATE,"
            + "notes TEXT,"
            + "vaccine_name VARCHAR(100)"
            + ")";
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getInsertSql() {
        return "INSERT INTO medical_records (pet_id,pet_name,record_date,record_type,"
            + "diagnosis,treatment,veterinarian_name,cost,next_check_date,notes,vaccine_name)"
            + " VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getUpdateSql() {
        return "UPDATE medical_records SET pet_id=?,pet_name=?,record_date=?,record_type=?,"
            + "diagnosis=?,treatment=?,veterinarian_name=?,cost=?,next_check_date=?,"
            + "notes=?,vaccine_name=? WHERE id=?";
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    @com.mehdi.petreminder.annotation.Generated
    protected void setInsertParams(PreparedStatement ps, MedicalRecord m) throws SQLException {
        ps.setInt(1, m.getPetId());
        ps.setString(2, m.getPetName());
        ps.setObject(3, m.getRecordDate() != null
            ? java.sql.Date.valueOf(m.getRecordDate()) : null);
        ps.setString(4, m.getRecordType());
        ps.setString(5, m.getDiagnosis());
        ps.setString(6, m.getTreatment());
        ps.setString(7, m.getVeterinarianName());
        ps.setDouble(8, m.getCost());
        ps.setObject(9, m.getNextCheckDate() != null
            ? java.sql.Date.valueOf(m.getNextCheckDate()) : null);
        ps.setString(10, m.getNotes());
        ps.setString(11, m.getVaccineName());
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected void setUpdateParams(PreparedStatement ps, MedicalRecord m) throws SQLException {
        setInsertParams(ps, m);
        ps.setInt(12, m.getId());
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected MedicalRecord mapRow(ResultSet rs) throws SQLException {
        MedicalRecord m = new MedicalRecord();
        m.setId(rs.getInt("id"));
        m.setPetId(rs.getInt("pet_id"));
        m.setPetName(rs.getString("pet_name"));
        java.sql.Date rd = rs.getDate("record_date");
        if (rd != null) m.setRecordDate(rd.toLocalDate());
        m.setRecordType(rs.getString("record_type"));
        m.setDiagnosis(rs.getString("diagnosis"));
        m.setTreatment(rs.getString("treatment"));
        m.setVeterinarianName(rs.getString("veterinarian_name"));
        m.setCost(rs.getDouble("cost"));
        java.sql.Date ncd = rs.getDate("next_check_date");
        if (ncd != null) m.setNextCheckDate(ncd.toLocalDate());
        m.setNotes(rs.getString("notes"));
        m.setVaccineName(rs.getString("vaccine_name"));
        return m;
    }
}
