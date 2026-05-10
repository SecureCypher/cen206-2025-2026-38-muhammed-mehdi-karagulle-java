/**
 * @file User.java
 * @brief Kullanıcı modeli sınıfı.
 * @details KVKK uyumu: şifre hash olarak saklanır, plain-text asla.
 */
package com.mehdi.petreminder.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @class User
 * @brief Uygulamayı kullanan evcil hayvan sahibi.
 * @details OOP Encapsulation: tüm alanlar private.
 *          KVKK: şifre hash'lenir, kişisel veriler korunur.
 *          Serializable: BinaryRepository için.
 * @author Muhammed Mehdi Karagülle, İbrahim Demirci, Zümre Uykun
 * @version 1.0
 */
public class User implements Serializable {

    /** @brief Serializable versiyon sabiti. */
    private static final long serialVersionUID = 1L;

    /** @brief Benzersiz kullanıcı ID. */
    private int id;

    /** @brief Kullanıcı adı (benzersiz). */
    private String username;

    /** @brief E-posta adresi. */
    private String email;

    /** @brief Şifre hash'i (plain-text ASLA saklanmaz — KVKK). */
    private String passwordHash;

    /** @brief Kullanıcının tam adı. */
    private String fullName;

    /** @brief Kayıt tarihi. */
    private LocalDateTime createdAt;

    /** @brief Son giriş tarihi. */
    private LocalDateTime lastLogin;

    /** @brief Kullanıcı aktif mi. */
    private boolean active;

    /** @brief Varsayılan yapıcı. */
    public User() {}

    /**
     * @brief Temel parametreli yapıcı.
     * @param id           Kullanıcı ID
     * @param username     Kullanıcı adı
     * @param email        E-posta
     * @param passwordHash Şifre hash'i
     * @param fullName     Tam ad
     */
    public User(int id, String username, String email,
                String passwordHash, String fullName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    /** @brief ID getter. @return Kullanıcı ID */
    public int getId() { return id; }

    /** @brief ID setter. @param id Kullanıcı ID */
    public void setId(int id) { this.id = id; }

    /** @brief Kullanıcı adı getter. @return Kullanıcı adı */
    public String getUsername() { return username; }

    /** @brief Kullanıcı adı setter. @param username Kullanıcı adı */
    public void setUsername(String username) { this.username = username; }

    /** @brief E-posta getter. @return E-posta */
    public String getEmail() { return email; }

    /** @brief E-posta setter. @param email E-posta */
    public void setEmail(String email) { this.email = email; }

    /** @brief Şifre hash getter. @return Şifre hash'i */
    public String getPasswordHash() { return passwordHash; }

    /** @brief Şifre hash setter. @param passwordHash Şifre hash'i */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /** @brief Tam ad getter. @return Tam ad */
    public String getFullName() { return fullName; }

    /** @brief Tam ad setter. @param fullName Tam ad */
    public void setFullName(String fullName) { this.fullName = fullName; }

    /** @brief Kayıt tarihi getter. @return Kayıt tarihi */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /** @brief Kayıt tarihi setter. @param createdAt Kayıt tarihi */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /** @brief Son giriş getter. @return Son giriş */
    public LocalDateTime getLastLogin() { return lastLogin; }

    /** @brief Son giriş setter. @param lastLogin Son giriş */
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    /** @brief Aktif mi getter. @return Aktif mi */
    public boolean isActive() { return active; }

    /** @brief Aktif setter. @param active Aktif mi */
    public void setActive(boolean active) { this.active = active; }

    /**
     * @brief String temsili — şifre hash'i GİZLENİR (KVKK).
     * @return Kullanıcı bilgisi, şifre hariç
     */
    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', email='%s', active=%b}",
            id, username, email, active);
    }

    /**
     * @brief Eşitlik kontrolü — ID bazlı.
     * @param obj Karşılaştırılan nesne
     * @return ID'ler eşitse true
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof User)) return false;
        User other = (User) obj;
        return this.id == other.id;
    }

    /**
     * @brief Hash kodu.
     * @return ID'ye göre hash
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
