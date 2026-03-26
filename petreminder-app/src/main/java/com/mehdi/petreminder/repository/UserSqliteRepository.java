/**
 * @file UserSqliteRepository.java
 * @brief User varlığı için SQLite repository.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.annotation.Generated;

import com.mehdi.petreminder.model.User;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * @class UserSqliteRepository
 * @brief SQLite üzerinde User CRUD işlemleri.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class UserSqliteRepository extends SqliteRepository<User> {

    /** @brief Varsayılan yapıcı. */
    public UserSqliteRepository() { super(); }

    /**
     * @brief Test yapıcısı.
     * @param connection Test bağlantısı
     */
    public UserSqliteRepository(Connection connection) { super(connection); }

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
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "username TEXT NOT NULL UNIQUE,"
            + "email TEXT NOT NULL UNIQUE,"
            + "password_hash TEXT NOT NULL,"
            + "full_name TEXT,"
            + "created_at TEXT,"
            + "last_login TEXT,"
            + "active INTEGER DEFAULT 1"
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
        ps.setString(5, u.getCreatedAt() != null ? u.getCreatedAt().toString() : null);
        ps.setInt(6, u.isActive() ? 1 : 0);
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
    @com.mehdi.petreminder.annotation.Generated
    protected User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        String createdAt = rs.getString("created_at");
        if (createdAt != null) u.setCreatedAt(LocalDateTime.parse(createdAt));
        u.setActive(rs.getInt("active") == 1);
        return u;
    }

    /**
     * @brief Username ile kullanıcı arar.
     * @param username Kullanıcı adı
     * @return User veya null
     */
    public User findByUsername(String username) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM users WHERE username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            throw new RepositoryException("findByUsername hatası", e);
        }
        return null;
    }
}
