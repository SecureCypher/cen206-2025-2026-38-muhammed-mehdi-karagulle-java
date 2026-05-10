/**
 * @file SqliteRepository.java
 * @brief SQLite JDBC repository — org.xerial:sqlite-jdbc.
 * @details PDF LO.6: SQLite storage backend.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.config.StorageConfig;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import java.sql.*;
import java.util.*;

/**
 * @class SqliteRepository
 * @brief SQLite tabanlı abstract repository temel sınıfı.
 * @details Alt sınıflar (PetSqliteRepository vb.) bu sınıfı extend eder
 *          ve mapRow / getTableName / getInsertSql / getUpdateSql metodlarını implemente eder.
 *          Template Method Pattern kullanılmıştır.
 * @param <T> Varlık türü
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public abstract class SqliteRepository<T> implements IRepository<T> {

    /** @brief Logger. */
    protected static final Logger logger =
        (Logger) LoggerFactory.getLogger(SqliteRepository.class);

    /** @brief SQLite JDBC bağlantısı. */
    protected Connection connection;

    /**
     * @brief SQLite bağlantısını açar ve tabloyu oluşturur.
     * @throws RepositoryException Bağlantı başarısız olursa
     */
    public SqliteRepository() {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + StorageConfig.getSqliteFilePath();
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(true);
            createTableIfNotExists();
            logger.info("SQLite bağlantısı açıldı: {}", StorageConfig.getSqliteFilePath());
        } catch (Exception e) {
            throw new RepositoryException("SQLite bağlantı hatası", e);
        }
    }

    /**
     * @brief Test amaçlı yapıcı — dışarıdan bağlantı verilir.
     * @param connection Test bağlantısı (H2 veya SQLite in-memory)
     */
    public SqliteRepository(Connection connection) {
        this.connection = connection;
        try {
            createTableIfNotExists();
        } catch (Exception e) {
            throw new RepositoryException("Tablo oluşturma hatası", e);
        }
    }

    /**
     * @brief Tablo adını döndürür.
     * @return SQL tablo adı
     */
    protected abstract String getTableName();

    /**
     * @brief Tablo oluşturma SQL'ini döndürür.
     * @return CREATE TABLE IF NOT EXISTS SQL
     */
    protected abstract String getCreateTableSql();

    /**
     * @brief INSERT SQL'ini döndürür (ID hariç).
     * @return INSERT INTO sql
     */
    protected abstract String getInsertSql();

    /**
     * @brief UPDATE SQL'ini döndürür.
     * @return UPDATE sql
     */
    protected abstract String getUpdateSql();

    /**
     * @brief PreparedStatement'a INSERT parametrelerini set eder.
     * @param ps     PreparedStatement
     * @param entity Varlık
     * @throws SQLException SQL hatası
     */
    protected abstract void setInsertParams(PreparedStatement ps, T entity)
        throws SQLException;

    /**
     * @brief PreparedStatement'a UPDATE parametrelerini set eder.
     * @param ps     PreparedStatement
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
    protected void createTableIfNotExists() {
        try (Statement st = connection.createStatement()) {
            String sql = getCreateTableSql();
            // H2 does not support SQLite's AUTOINCREMENT; use AUTO_INCREMENT instead
            String dbProduct = connection.getMetaData().getDatabaseProductName();
            if ("H2".equalsIgnoreCase(dbProduct)) {
                sql = sql.replace("AUTOINCREMENT", "AUTO_INCREMENT");
            }
            st.execute(sql);
        } catch (SQLException e) {
            throw new RepositoryException("Tablo oluşturma hatası", e);
        }
    }

    /**
     * {@inheritDoc}
     */
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
                logger.debug("SQLite kayıt: id={}", id);
                return id;
            }
            throw new RepositoryException("ID üretilemedi");
        } catch (SQLException e) {
            throw new RepositoryException("SQLite save hatası", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> findById(int id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("SQLite findById hatası", e);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName();
        List<T> list = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("SQLite findAll hatası", e);
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(T entity) {
        if (entity == null) return false;
        try (PreparedStatement ps = connection.prepareStatement(getUpdateSql())) {
            setUpdateParams(ps, entity);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("SQLite update hatası", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("SQLite delete hatası", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        try (Statement st = connection.createStatement()) {
            st.execute("DELETE FROM " + getTableName());
        } catch (SQLException e) {
            throw new RepositoryException("SQLite deleteAll hatası", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM " + getTableName();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RepositoryException("SQLite count hatası", e);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("SQLite bağlantısı kapatıldı.");
            }
        } catch (SQLException e) {
            logger.warn("SQLite kapatma hatası: {}", e.getMessage());
        }
    }

    /**
     * @brief Bağlantıyı döndürür (test için).
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
    protected void setEntityId(T entity, int id) {
        try {
            var method = entity.getClass().getMethod("setId", int.class);
            method.invoke(entity, id);
        } catch (Exception e) {
            logger.warn("setId çağrılamadı: {}", e.getMessage());
        }
    }
}
