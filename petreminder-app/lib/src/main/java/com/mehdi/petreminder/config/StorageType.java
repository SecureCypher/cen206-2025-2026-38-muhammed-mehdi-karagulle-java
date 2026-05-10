/**
 * @file StorageType.java
 * @brief Storage backend türleri.
 */
package com.mehdi.petreminder.config;

/**
 * @enum StorageType
 * @brief Desteklenen storage backend türleri.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
/**
 * Enum documentation.
 */
public enum StorageType {

    /** @brief Binary dosya (.bin) */
    BINARY("Binary Dosya"),

    /** @brief SQLite veritabanı */
    SQLITE("SQLite"),

    /** @brief MySQL veritabanı (Docker) */
    MYSQL("MySQL (Docker)");

    /** @brief Kullanıcıya gösterilen ad. */
    private final String displayName;

    /**
     * @brief Yapıcı.
     * @param displayName Gösterim adı
     */
    StorageType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @brief Gösterim adını döndürür.
     * @return displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @brief toString override.
     * @return displayName
     */
    @Override
    public String toString() {
        return displayName;
    }
}
