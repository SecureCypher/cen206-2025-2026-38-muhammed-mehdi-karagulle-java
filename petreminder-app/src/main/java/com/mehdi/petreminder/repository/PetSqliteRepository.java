/**
 * @file PetSqliteRepository.java
 * @brief Pet varlığı için SQLite repository implementasyonu.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.annotation.Generated;

import com.mehdi.petreminder.model.*;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * @class PetSqliteRepository
 * @brief SQLite üzerinde Pet CRUD işlemleri.
 * @details SqliteRepository'yi extend eder; Template Method Pattern.
 *          Dog / Cat / Bird tür ayrımı species sütununa göre yapılır.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class PetSqliteRepository extends SqliteRepository<Pet> {

    /** @brief Logger. */
    private static final Logger log =
        (Logger) LoggerFactory.getLogger(PetSqliteRepository.class);

    /**
     * @brief Varsayılan yapıcı — gerçek SQLite.
     */
    public PetSqliteRepository() {
        super();
    }

    /**
     * @brief Test yapıcısı.
     * @param connection Test bağlantısı
     */
    public PetSqliteRepository(Connection connection) {
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
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "owner_id INTEGER NOT NULL DEFAULT 1,"
            + "name TEXT NOT NULL,"
            + "species TEXT NOT NULL,"
            + "breed TEXT,"
            + "birth_date TEXT,"
            + "gender TEXT,"
            + "weight REAL DEFAULT 0,"
            + "notes TEXT,"
            + "can_talk INTEGER DEFAULT 0,"
            + "bird_type TEXT,"
            + "is_trained INTEGER DEFAULT 0,"
            + "is_indoor INTEGER DEFAULT 1"
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
    protected void setInsertParams(PreparedStatement ps, Pet pet) throws SQLException {
        ps.setInt(1, pet.getOwnerId());
        ps.setString(2, pet.getName());
        ps.setString(3, pet.getSpecies());
        ps.setString(4, getBreed(pet));
        ps.setString(5, pet.getBirthDate() != null ? pet.getBirthDate().toString() : null);
        ps.setString(6, pet.getGender());
        ps.setDouble(7, pet.getWeight());
        ps.setString(8, pet.getNotes());
        ps.setInt(9, (pet instanceof Bird && ((Bird) pet).isCanTalk()) ? 1 : 0);
        ps.setString(10, pet instanceof Bird ? ((Bird) pet).getBirdType() : null);
        ps.setInt(11, (pet instanceof Dog && ((Dog) pet).isTrained()) ? 1 : 0);
        ps.setInt(12, (pet instanceof Cat && ((Cat) pet).isIndoor()) ? 1 : 0);
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
    protected Pet mapRow(ResultSet rs) throws SQLException {
        String species = rs.getString("species");
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String birthDateStr = rs.getString("birth_date");
        LocalDate birthDate = birthDateStr != null ? LocalDate.parse(birthDateStr) : null;
        int ownerId = rs.getInt("owner_id");

        Pet pet;
        if ("Dog".equalsIgnoreCase(species) || "Kopek".equalsIgnoreCase(species)) {
            Dog dog = new Dog(id, name, birthDate, ownerId);
            dog.setBreed(rs.getString("breed"));
            dog.setTrained(rs.getInt("is_trained") == 1);
            pet = dog;
        } else if ("Cat".equalsIgnoreCase(species) || "Kedi".equalsIgnoreCase(species)) {
            Cat cat = new Cat(id, name, birthDate, ownerId);
            cat.setBreed(rs.getString("breed"));
            cat.setIndoor(rs.getInt("is_indoor") == 1);
            pet = cat;
        } else {
            Bird bird = new Bird(id, name, birthDate, ownerId);
            bird.setBirdType(rs.getString("bird_type"));
            bird.setCanTalk(rs.getInt("can_talk") == 1);
            pet = bird;
        }
        pet.setGender(rs.getString("gender"));
        pet.setWeight(rs.getDouble("weight"));
        pet.setNotes(rs.getString("notes"));
        return pet;
    }

    /**
     * @brief Pet'in breed bilgisini döndürür (polimorfik).
     * @param pet Pet varlığı
     * @return Irk bilgisi
     */
    private String getBreed(Pet pet) {
        if (pet instanceof Dog) return ((Dog) pet).getBreed();
        if (pet instanceof Cat) return ((Cat) pet).getBreed();
        return null;
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
