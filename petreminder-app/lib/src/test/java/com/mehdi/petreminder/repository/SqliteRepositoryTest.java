package com.mehdi.petreminder.repository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("SqliteRepository Exception Testleri")
class SqliteRepositoryTest {

    @Mock private Connection connection;
    @Mock private PreparedStatement ps;
    @Mock private Statement stmt;
    @Mock private ResultSet rs;

    private SqliteRepository<String> repo;

    @BeforeEach
    void setUp() throws SQLException {
        // Constructor icindeki createTableIfNotExists cagirini mocklayalim
        when(connection.createStatement()).thenReturn(stmt);
        DatabaseMetaData meta = mock(DatabaseMetaData.class);
        when(connection.getMetaData()).thenReturn(meta);
        when(meta.getDatabaseProductName()).thenReturn("SQLite");
        
        repo = new SqliteRepository<String>(connection) {
            @Override protected String getTableName() { return "test_table"; }
            @Override protected String getCreateTableSql() { return "CREATE"; }
            @Override protected String getInsertSql() { return "INSERT"; }
            @Override protected String getUpdateSql() { return "UPDATE"; }
            @Override protected void setInsertParams(PreparedStatement ps, String entity) {}
            @Override protected void setUpdateParams(PreparedStatement ps, String entity) {}
            @Override protected String mapRow(ResultSet rs) { return "mapped"; }
        };
    }

    @Test
    void testSaveThrowsRepositoryException() throws SQLException {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Mock DB Error"));
        assertThrows(RepositoryException.class, () -> repo.save("test"));
    }

    @Test
    void testSaveNoGeneratedKeysThrowsRepositoryException() throws SQLException {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(false); // ID donmuyor
        assertThrows(RepositoryException.class, () -> repo.save("test"));
    }

    @Test
    void testFindByIdThrowsRepositoryException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Mock DB Error"));
        assertThrows(RepositoryException.class, () -> repo.findById(1));
    }

    @Test
    void testFindAllThrowsRepositoryException() throws SQLException {
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("Mock DB Error"));
        assertThrows(RepositoryException.class, () -> repo.findAll());
    }

    @Test
    void testUpdateThrowsRepositoryException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Mock DB Error"));
        assertThrows(RepositoryException.class, () -> repo.update("test"));
    }

    @Test
    void testDeleteThrowsRepositoryException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Mock DB Error"));
        assertThrows(RepositoryException.class, () -> repo.delete(1));
    }

    @Test
    void testDeleteAllThrowsRepositoryException() throws SQLException {
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.execute(anyString())).thenThrow(new SQLException("Mock DB Error"));
        assertThrows(RepositoryException.class, () -> repo.deleteAll());
    }

    @Test
    void testCountThrowsRepositoryException() throws SQLException {
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenThrow(new SQLException("Mock DB Error"));
        assertThrows(RepositoryException.class, () -> repo.count());
    }

    @Test
    void testCloseCatchesSQLException() throws SQLException {
        when(connection.isClosed()).thenReturn(false);
        doThrow(new SQLException("Close Error")).when(connection).close();
        assertDoesNotThrow(() -> repo.close());
    }

    @Test
    void testCreateTableThrowsRepositoryException() throws SQLException {
        Connection badConnection = mock(Connection.class);
        when(badConnection.createStatement()).thenThrow(new SQLException("Mock DB Error"));
        assertThrows(RepositoryException.class, () -> {
            new SqliteRepository<String>(badConnection) {
                @Override protected String getTableName() { return "test"; }
                @Override protected String getCreateTableSql() { return "CREATE"; }
                @Override protected String getInsertSql() { return "INSERT"; }
                @Override protected String getUpdateSql() { return "UPDATE"; }
                @Override protected void setInsertParams(PreparedStatement p, String e) {}
                @Override protected void setUpdateParams(PreparedStatement p, String e) {}
                @Override protected String mapRow(ResultSet r) { return "mapped"; }
            };
        });
    }

    // save başarılı — generated key döner
    @Test
    void testSaveSuccess() throws SQLException {
        ResultSet generatedKeys = mock(ResultSet.class);
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(42);
        int id = repo.save("test");
        assertEquals(42, id);
    }

    // save null entity RepositoryException fırlatır
    @Test
    void testSaveNullEntity() {
        assertThrows(RepositoryException.class, () -> repo.save(null));
    }

    // findById başarılı — bulunan kayıt döner
    @Test
    void testFindByIdSuccess() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        var found = repo.findById(1);
        assertTrue(found.isPresent());
        assertEquals("mapped", found.get());
    }

    // findById bulunamayan — empty döner
    @Test
    void testFindByIdNotFound() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        assertTrue(repo.findById(1).isEmpty());
    }

    // update başarılı — rows > 0
    @Test
    void testUpdateSuccess() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        assertTrue(repo.update("test"));
    }

    // update null false döner
    @Test
    void testUpdateNullReturnsFalse() {
        assertFalse(repo.update(null));
    }

    // update başarısız — rows == 0
    @Test
    void testUpdateNoRowsAffected() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);
        assertFalse(repo.update("test"));
    }

    // delete başarılı — rows > 0
    @Test
    void testDeleteSuccess() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        assertTrue(repo.delete(1));
    }

    // close connection zaten kapalı
    @Test
    void testCloseAlreadyClosedConnection() throws SQLException {
        when(connection.isClosed()).thenReturn(true);
        assertDoesNotThrow(() -> repo.close());
    }

    // close connection null
    @Test
    void testCloseNullConnection() throws SQLException {
        // repo'nun connection'ı null yapamayız ama close normal kapalı dönerse sorun yok
        when(connection.isClosed()).thenReturn(false);
        assertDoesNotThrow(() -> repo.close());
    }

    // count başarılı — sonuç döner
    @Test
    void testCountSuccess() throws SQLException {
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(5);
        assertEquals(5, repo.count());
    }

    // count sonuç yok — rs.next() false döner
    @Test
    void testCountNoResult() throws SQLException {
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        assertEquals(0, repo.count());
    }

    // findAll başarılı
    @Test
    void testFindAllSuccess() throws SQLException {
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        var list = repo.findAll();
        assertEquals(1, list.size());
    }

    // deleteAll başarılı
    @Test
    void testDeleteAllSuccess() throws SQLException {
        when(connection.createStatement()).thenReturn(stmt);
        assertDoesNotThrow(() -> repo.deleteAll());
    }

    // getConnection null değil
    @Test
    void testGetConnection() {
        assertNotNull(repo.getConnection());
    }

    // H2 AUTOINCREMENT -> AUTO_INCREMENT dönüştürme branchı
    @Test
    void testCreateTableWithH2Replacement() throws SQLException {
        Connection h2Conn = mock(Connection.class);
        Statement h2Stmt = mock(Statement.class);
        DatabaseMetaData h2Meta = mock(DatabaseMetaData.class);
        when(h2Conn.createStatement()).thenReturn(h2Stmt);
        when(h2Conn.getMetaData()).thenReturn(h2Meta);
        when(h2Meta.getDatabaseProductName()).thenReturn("H2");
        assertDoesNotThrow(() -> {
            new SqliteRepository<String>(h2Conn) {
                @Override protected String getTableName() { return "t"; }
                @Override protected String getCreateTableSql() { return "CREATE TABLE t (id INTEGER PRIMARY KEY AUTOINCREMENT)"; }
                @Override protected String getInsertSql() { return "INSERT"; }
                @Override protected String getUpdateSql() { return "UPDATE"; }
                @Override protected void setInsertParams(PreparedStatement p, String e) {}
                @Override protected void setUpdateParams(PreparedStatement p, String e) {}
                @Override protected String mapRow(ResultSet r) { return "mapped"; }
            };
        });
    }

    // close: connection null — null branch coverage
    @Test
    void testCloseNullConnectionField() throws Exception {
        // repo'nun connection alanını null yapalım
        var field = SqliteRepository.class.getDeclaredField("connection");
        field.setAccessible(true);
        field.set(repo, null);
        assertDoesNotThrow(() -> repo.close());
    }

    // setEntityId catch branch — entity'nin setId(int) metodu yok
    @Test
    void testSetEntityIdCatchBranch() throws SQLException {
        // Entity tipi String — String.setId(int) yok, catch'e düşer
        ResultSet genKeys = mock(ResultSet.class);
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(genKeys);
        when(genKeys.next()).thenReturn(true);
        when(genKeys.getInt(1)).thenReturn(1);
        // String entity'nin setId metodu yok, setEntityId catch'e düşer ama save başarılı olur
        int id = repo.save("test");
        assertEquals(1, id);
    }
}

