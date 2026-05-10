/**
 * @file RepositoryException.java
 * @brief Repository katmanı için özel istisna sınıfı.
 */
package com.mehdi.petreminder.repository;

/**
 * @class RepositoryException
 * @brief Repository işlemlerinde fırlatılan unchecked istisna.
 * @details OOP: RuntimeException türetilmiş özel istisna.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class RepositoryException extends RuntimeException {

    /** @brief Serializable UID. */
    private static final long serialVersionUID = 1L;

    /**
     * @brief Mesajlı yapıcı.
     * @param message Hata mesajı
     */
    public RepositoryException(String message) {
        super(message);
    }

    /**
     * @brief Mesaj ve neden'li yapıcı.
     * @param message Hata mesajı
     * @param cause   Altta yatan istisna
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
