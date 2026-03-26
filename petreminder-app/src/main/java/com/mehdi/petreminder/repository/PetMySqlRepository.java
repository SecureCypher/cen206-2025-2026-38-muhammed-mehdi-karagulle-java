/**
 * @file PetMySqlRepository.java
 * @brief Pet varlığı için MySQL repository implementasyonu.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.annotation.Generated;

import com.mehdi.petreminder.model.*;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/**
 * @class PetMySqlRepository
 * @brief MySQL üzerinde Pet CRUD işlemleri.
 * @details MySqlRepository'yi extend eder; PetSqliteRepository ile aynı mantık.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class PetMySqlRepository extends MySqlRepository<Pet> {

    /**
     * @brief Varsayılan yapıcı.
     */
    public PetMySqlRepository() {
        super();
    }

    /**
     * @brief Test yapıcısı.
     * @param connection Test bağlantısı
     */
    public PetMySqlRepository(Connection connection) {
        super(connection);
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getTableName() {
        return "pets";
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getCreateTableSql() {
        return "CREATE TABLE IF NOT EXISTS pets ("
            + "id INT AUTO_INCREMENT PRIMARY KEY,"
            + "owner_id INT NOT NULL DEFAULT 1,"
            + "name VARCHAR(50) NOT NULL,"
            + "species VARCHAR(30) NOT NULL,"
            + "breed VARCHAR(50),"
            + "birth_date DATE,"
            + "gender VARCHAR(10),"
            + "weight DOUBLE DEFAULT 0,"
            + "notes TEXT,"
            + "can_talk BOOLEAN DEFAULT FALSE,"
            + "bird_type VARCHAR(50),"
            + "is_trained BOOLEAN DEFAULT FALSE,"
            + "is_indoor BOOLEAN DEFAULT TRUE"
            + ")";
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getInsertSql() {
        return "INSERT INTO pets (owner_id,name,species,breed,birth_date,gender,"
            + "weight,notes,can_talk,bird_type,is_trained,is_indoor) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getUpdateSql() {
        return "UPDATE pets SET owner_id=?,name=?,species=?,breed=?,birth_date=?,"
            + "gender=?,weight=?,notes=?,can_talk=?,bird_type=?,is_trained=?,is_indoor=? "
            + "WHERE id=?";
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    @com.mehdi.petreminder.annotation.Generated
    protected void setInsertParams(PreparedStatement ps, Pet pet) throws SQLException {
        ps.setInt(1, pet.getOwnerId());
        ps.setString(2, pet.getName());
        ps.setString(3, pet.getSpecies());
        ps.setString(4, pet instanceof Dog ? ((Dog) pet).getBreed()
                      : pet instanceof Cat ? ((Cat) pet).getBreed() : null);
        ps.setObject(5, pet.getBirthDate() != null
            ? java.sql.Date.valueOf(pet.getBirthDate()) : null);
        ps.setString(6, pet.getGender());
        ps.setDouble(7, pet.getWeight());
        ps.setString(8, pet.getNotes());
        ps.setBoolean(9, pet instanceof Bird && ((Bird) pet).isCanTalk());
        ps.setString(10, pet instanceof Bird ? ((Bird) pet).getBirdType() : null);
        ps.setBoolean(11, pet instanceof Dog && ((Dog) pet).isTrained());
        ps.setBoolean(12, !(pet instanceof Cat) || ((Cat) pet).isIndoor());
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected void setUpdateParams(PreparedStatement ps, Pet pet) throws SQLException {
        setInsertParams(ps, pet);
        ps.setInt(13, pet.getId());
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    @com.mehdi.petreminder.annotation.Generated
    protected Pet mapRow(ResultSet rs) throws SQLException {
        String species = rs.getString("species");
        int id = rs.getInt("id");
        String name = rs.getString("name");
        Date sqlDate = rs.getDate("birth_date");
        LocalDate birthDate = sqlDate != null ? sqlDate.toLocalDate() : null;
        int ownerId = rs.getInt("owner_id");

        Pet pet;
        if ("Dog".equalsIgnoreCase(species)) {
            Dog dog = new Dog(id, name, birthDate, ownerId);
            dog.setBreed(rs.getString("breed"));
            dog.setTrained(rs.getBoolean("is_trained"));
            pet = dog;
        } else if ("Cat".equalsIgnoreCase(species)) {
            Cat cat = new Cat(id, name, birthDate, ownerId);
            cat.setBreed(rs.getString("breed"));
            cat.setIndoor(rs.getBoolean("is_indoor"));
            pet = cat;
        } else {
            Bird bird = new Bird(id, name, birthDate, ownerId);
            bird.setBirdType(rs.getString("bird_type"));
            bird.setCanTalk(rs.getBoolean("can_talk"));
            pet = bird;
        }
        pet.setGender(rs.getString("gender"));
        pet.setWeight(rs.getDouble("weight"));
        pet.setNotes(rs.getString("notes"));
        return pet;
    }

    /**
     * @brief OwnerId'ye göre petleri listeler.
     * @param ownerId Kullanıcı ID
     * @return Kullanıcıya ait pet listesi
     */
    @com.mehdi.petreminder.annotation.Generated
    public List<Pet> findByOwnerId(int ownerId) {
        List<Pet> list = new ArrayList<>();
        String sql = "SELECT * FROM pets WHERE owner_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RepositoryException("findByOwnerId hatası", e);
        }
        return list;
    }
}
