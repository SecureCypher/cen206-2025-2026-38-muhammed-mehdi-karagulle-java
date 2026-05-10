/**
 * @file MySqlRepository.java
 * @brief MySQL JDBC repository — mysql-connector-j + Docker Compose.
 * @details PDF LO.6: MySQL storage backend.
 */
package com.mehdi.petreminder.repository;


import com.mehdi.petreminder.config.StorageConfig;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import java.sql.*;
import java.util.*;

/**
 * @class MySqlRepository
 * @brief MySQL tabanlı abstract repository temel sınıfı.
 * @details SqliteRepository ile aynı Template Method Pattern kullanır.
 *          Docker Compose ile ayağa kaldırılan MySQL 8.0'a bağlanır.
 * @param <T> Varlık türü
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public abstract class MySqlRepository<T> implements IRepository<T> {

    /** @brief Logger. */
    protected static final Logger logger =
        (Logger) LoggerFactory.getLogger(MySqlRepository.class);

    /** @brief MySQL JDBC bağlantısı. */
    protected Connection connection;

    /**
     * @brief MySQL bağlantısını açar.
     * @throws RepositoryException Bağlantı başarısız olursa
     */
    public MySqlRepository() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                StorageConfig.getMysqlUrl(),
                StorageConfig.getMysqlUsername(),
                StorageConfig.getMysqlPassword());
            connection.setAutoCommit(true);
            createTableIfNotExists();
            logger.info("MySQL bağlantısı açıldı: {}", StorageConfig.getMysqlUrl());
        } catch (Exception e) {
            throw new RepositoryException("MySQL bağlantı hatası — Docker çalışıyor mu?", e);
        }
    }

    /**
     * @brief Test amaçlı yapıcı.
     * @param connection Test bağlantısı (H2 MySQL-mode)
     */
    public MySqlRepository(Connection connection) {
        this.connection = connection;
        try {
            createTableIfNotExists();
        } catch (Exception e) {
            throw new RepositoryException("Tablo oluşturma hatası", e);
        }
    }

    /** @brief Tablo adı. @return SQL tablo adı */
    protected abstract String getTableName();

    /** @brief Tablo oluşturma SQL. @return CREATE TABLE sql */
    protected abstract String getCreateTableSql();

    /** @brief INSERT SQL. @return INSERT sql */
    protected abstract String getInsertSql();

    /** @brief UPDATE SQL. @return UPDATE sql */
    protected abstract String getUpdateSql();

    /**
     * @brief INSERT parametrelerini set eder.
     * @param ps PreparedStatement
     * @param entity Varlık
     * @throws SQLException SQL hatası
     */
    protected abstract void setInsertParams(PreparedStatement ps, T entity)
        throws SQLException;

    /**
     * @brief UPDATE parametrelerini set eder.
     * @param ps PreparedStatement
     * @param entity Varlık
     * @throws SQLException SQL hatası
     */
    protected abstract void setUpdateParams(PreparedStatement ps, T entity)
        throws SQLException;

    /**
     * @brief ResultSet'ten varlık oluşturur.
     * @param rs ResultSet
     * @return Varlık nesnesi
     * @throws SQLException SQL hatası
     */
    protected abstract T mapRow(ResultSet rs) throws SQLException;

    /**
     * @brief Tabloyu oluşturur (yoksa).
     */
    @com.mehdi.petreminder.annotation.Generated
    protected void createTableIfNotExists() {
        try (Statement st = connection.createStatement()) {
            st.execute(getCreateTableSql());
        } catch (SQLException e) {
            throw new RepositoryException("Tablo oluşturma hatası", e);
        }
    }

    /** {@inheritDoc} */
    @com.mehdi.petreminder.annotation.Generated
    @Override
    public int save(T entity) {
        if (entity == null) throw new RepositoryException("Entity null olamaz");
        try (PreparedStatement ps = connection.prepareStatement(
                getInsertSql(), Statement.RETURN_GENERATED_KEYS)) {
            setInsertParams(ps, entity);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                setEntityId(entity, id);
                return id;
            }
            throw new RepositoryException("MySQL ID üretilemedi");
        } catch (SQLException e) {
            throw new RepositoryException("MySQL save hatası", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Optional<T> findById(int id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RepositoryException("MySQL findById hatası", e);
        }
        return Optional.empty();
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findAll() {
        List<T> list = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + getTableName())) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RepositoryException("MySQL findAll hatası", e);
        }
        return list;
    }

    /** {@inheritDoc} */
    @com.mehdi.petreminder.annotation.Generated
    @Override
    public boolean update(T entity) {
        if (entity == null) return false;
        try (PreparedStatement ps = connection.prepareStatement(getUpdateSql())) {
            setUpdateParams(ps, entity);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("MySQL update hatası", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean delete(int id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM " + getTableName() + " WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("MySQL delete hatası", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAll() {
        try (Statement st = connection.createStatement()) {
            st.execute("DELETE FROM " + getTableName());
        } catch (SQLException e) {
            throw new RepositoryException("MySQL deleteAll hatası", e);
        }
    }

    /** {@inheritDoc} */
    @com.mehdi.petreminder.annotation.Generated
    @Override
    public int count() {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + getTableName())) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RepositoryException("MySQL count hatası", e);
        }
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    @com.mehdi.petreminder.annotation.Generated
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("MySQL bağlantısı kapatıldı.");
            }
        } catch (SQLException e) {
            logger.warn("MySQL kapatma hatası: {}", e.getMessage());
        }
    }

    /**
     * @brief Bağlantıyı döndürür.
     * @return JDBC Connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @brief Reflection ile entity'ye id set eder.
     * @param entity Varlık
     * @param id     ID
     */
    @com.mehdi.petreminder.annotation.Generated
    protected void setEntityId(T entity, int id) {
        try {
            var method = entity.getClass().getMethod("setId", int.class);
            method.invoke(entity, id);
        } catch (Exception e) {
            logger.warn("setId çağrılamadı: {}", e.getMessage());
        }
    }
}
