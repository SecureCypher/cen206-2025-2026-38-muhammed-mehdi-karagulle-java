/**
 * @file IRepository.java
 * @brief Generic repository interface — PDF LO.6 zorunluluğu.
 * @details Tüm storage backend'leri bu interface'i implement eder.
 *          OOP Abstraction: implementasyon detayları gizlenir.
 */
package com.mehdi.petreminder.repository;

import java.util.List;
import java.util.Optional;

/**
 * @interface IRepository
 * @brief Generic CRUD repository arayüzü.
 * @details PDF zorunluluğu: IRepository&lt;T&gt; interface with RepositoryFactory.
 *          OOP Abstraction: Binary / SQLite / MySQL aynı interface üzerinden kullanılır.
 * @param <T>  Varlık türü (Pet, Reminder, User, MedicalRecord)
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public interface IRepository<T> {

    /**
     * @brief Yeni bir varlık kaydeder.
     * @param entity Kaydedilecek varlık
     * @return Oluşturulan varlığın ID'si
     * @throws RepositoryException Kayıt başarısız olursa
     */
    int save(T entity);

    /**
     * @brief ID'ye göre varlık getirir.
     * @param id Varlık ID'si
     * @return Varlık (bulunamazsa empty Optional)
     * @throws RepositoryException Okuma başarısız olursa
     */
    Optional<T> findById(int id);

    /**
     * @brief Tüm varlıkları listeler.
     * @return Varlık listesi (boş olabilir)
     * @throws RepositoryException Okuma başarısız olursa
     */
    List<T> findAll();

    /**
     * @brief Mevcut varlığı günceller.
     * @param entity Güncellenecek varlık
     * @return Güncelleme başarılıysa true
     * @throws RepositoryException Güncelleme başarısız olursa
     */
    boolean update(T entity);

    /**
     * @brief ID'ye göre varlık siler.
     * @param id Silinecek varlığın ID'si
     * @return Silme başarılıysa true
     * @throws RepositoryException Silme başarısız olursa
     */
    boolean delete(int id);

    /**
     * @brief Tüm varlıkları siler.
     * @throws RepositoryException Silme başarısız olursa
     */
    void deleteAll();

    /**
     * @brief Varlık sayısını döndürür.
     * @return Toplam kayıt sayısı
     */
    int count();

    /**
     * @brief Repository bağlantısını/kaynaklarını kapatır.
     */
    void close();
}
