/**
 * @file UserMySqlRepository.java
 * @brief User varlığı için MySQL repository.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.annotation.Generated;

import com.mehdi.petreminder.model.User;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * @class UserMySqlRepository
 * @brief MySQL üzerinde User CRUD işlemleri.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class UserMySqlRepository extends MySqlRepository<User> {

    /** @brief Varsayılan yapıcı. */
    public UserMySqlRepository() { super(); }

    /**
     * @brief Test yapıcısı.
     * @param connection Test bağlantısı
     */
    public UserMySqlRepository(Connection connection) { super(connection); }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getTableName() { return "users"; }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getCreateTableSql() {
        return "CREATE TABLE IF NOT EXISTS users ("
            + "id INT AUTO_INCREMENT PRIMARY KEY,"
            + "username VARCHAR(50) NOT NULL UNIQUE,"
            + "email VARCHAR(100) NOT NULL UNIQUE,"
            + "password_hash VARCHAR(255) NOT NULL,"
            + "full_name VARCHAR(100),"
            + "created_at DATETIME,"
            + "last_login DATETIME,"
            + "active BOOLEAN DEFAULT TRUE"
            + ")";
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getInsertSql() {
        return "INSERT INTO users (username,email,password_hash,full_name,created_at,active)"
            + " VALUES (?,?,?,?,?,?)";
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected String getUpdateSql() {
        return "UPDATE users SET username=?,email=?,password_hash=?,full_name=?,"
            + "created_at=?,active=? WHERE id=?";
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    @com.mehdi.petreminder.annotation.Generated
    protected void setInsertParams(PreparedStatement ps, User u) throws SQLException {
        ps.setString(1, u.getUsername());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getPasswordHash());
        ps.setString(4, u.getFullName());
        ps.setObject(5, u.getCreatedAt() != null
            ? Timestamp.valueOf(u.getCreatedAt()) : null);
        ps.setBoolean(6, u.isActive());
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected void setUpdateParams(PreparedStatement ps, User u) throws SQLException {
        setInsertParams(ps, u);
        ps.setInt(7, u.getId());
    }

    /** {@inheritDoc} */
    @Override
    /**
     * Member documentation.
     */
    protected User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
        u.setActive(rs.getBoolean("active"));
        return u;
    }
}
