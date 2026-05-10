/**
 * @file MedicalRecordSqliteRepository.java
 * @brief MedicalRecord için SQLite repository.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.annotation.Generated;

import com.mehdi.petreminder.model.MedicalRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * @class MedicalRecordSqliteRepository
 * @brief SQLite üzerinde MedicalRecord CRUD.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class MedicalRecordSqliteRepository extends SqliteRepository<MedicalRecord> {

    /** @brief Varsayılan yapıcı. */
    public MedicalRecordSqliteRepository() { super(); }

    /**
     * @brief Test yapıcısı.
     * @param connection Test bağlantısı
     */
    public MedicalRecordSqliteRepository(Connection connection) { super(connection); }

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
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "pet_id INTEGER NOT NULL,"
            + "pet_name TEXT,"
            + "record_date TEXT NOT NULL,"
            + "record_type TEXT NOT NULL,"
            + "diagnosis TEXT,"
            + "treatment TEXT,"
            + "veterinarian_name TEXT,"
            + "cost REAL DEFAULT 0,"
            + "next_check_date TEXT,"
            + "notes TEXT,"
            + "vaccine_name TEXT"
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
        ps.setString(3, m.getRecordDate() != null ? m.getRecordDate().toString() : null);
        ps.setString(4, m.getRecordType());
        ps.setString(5, m.getDiagnosis());
        ps.setString(6, m.getTreatment());
        ps.setString(7, m.getVeterinarianName());
        ps.setDouble(8, m.getCost());
        ps.setString(9, m.getNextCheckDate() != null ? m.getNextCheckDate().toString() : null);
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
        String rd = rs.getString("record_date");
        if (rd != null) m.setRecordDate(LocalDate.parse(rd));
        m.setRecordType(rs.getString("record_type"));
        m.setDiagnosis(rs.getString("diagnosis"));
        m.setTreatment(rs.getString("treatment"));
        m.setVeterinarianName(rs.getString("veterinarian_name"));
        m.setCost(rs.getDouble("cost"));
        String ncd = rs.getString("next_check_date");
        if (ncd != null) m.setNextCheckDate(LocalDate.parse(ncd));
        m.setNotes(rs.getString("notes"));
        m.setVaccineName(rs.getString("vaccine_name"));
        return m;
    }

    /**
     * @brief PetId'ye göre sağlık kayıtlarını getirir.
     * @param petId Pet ID
     * @return MedicalRecord listesi
     */
    public List<MedicalRecord> findByPetId(int petId) {
        List<MedicalRecord> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM medical_records WHERE pet_id = ?")) {
            ps.setInt(1, petId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RepositoryException("findByPetId hatası", e);
        }
        return list;
    }
}
