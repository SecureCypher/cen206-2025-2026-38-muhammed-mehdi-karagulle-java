/**
 * @file ServiceException.java
 * @brief Servis katmanı özel istisna sınıfı.
 */
package com.mehdi.petreminder.service;

/**
 * @class ServiceException
 * @brief Servis katmanında fırlatılan unchecked istisna.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class ServiceException extends RuntimeException {

    /** @brief serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * @brief Mesajlı yapıcı.
     * @param message Hata mesajı
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * @brief Mesaj ve neden'li yapıcı.
     * @param message Hata mesajı
     * @param cause   Neden
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
