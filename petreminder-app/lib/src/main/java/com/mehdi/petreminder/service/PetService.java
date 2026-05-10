/**
 * @file PetService.java
 * @brief Pet iş mantığı servis katmanı.
 * @details Repository üzerinde iş kuralları uygular.
 *          Observer Pattern: CRUD işlemlerinde EventManager'a bildirim gönderir.
 */
package com.mehdi.petreminder.service;

import com.mehdi.petreminder.model.*;
import com.mehdi.petreminder.observer.EventManager;
import com.mehdi.petreminder.observer.EventType;
import com.mehdi.petreminder.repository.IRepository;
import com.mehdi.petreminder.repository.RepositoryFactory;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @class PetService
 * @brief Pet CRUD iş mantığı katmanı.
 * @details OOP Composition: IRepository bağımlılığı constructor injection ile.
 *          PDF zorunluluğu: Service layer — business logic ayrılır.
 *          Observer Pattern: Her CRUD işleminde EventManager üzerinden
 *          kayıtlı observer'lara bildirim gönderilir.
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class PetService {

    /** @brief Logger. */
    private static final Logger logger =
        (Logger) LoggerFactory.getLogger(PetService.class);

    /** @brief Pet repository. */
    private final IRepository<Pet> repository;

    /**
     * @brief Varsayılan yapıcı — RepositoryFactory kullanır.
     */
    public PetService() {
        this.repository = RepositoryFactory.createPetRepository();
    }

    /**
     * @brief Test yapıcısı — dependency injection.
     * @param repository Mock veya test repository'si
     */
    public PetService(IRepository<Pet> repository) {
        this.repository = repository;
    }

    /**
     * @brief Yeni pet ekler.
     * @param pet Eklenecek Pet nesnesi
     * @return Oluşturulan Pet (ID set edilmiş)
     * @throws ServiceException Doğrulama başarısız olursa
     */
    public Pet addPet(Pet pet) {
        validatePet(pet);
        int id = repository.save(pet);
        logger.info("Pet eklendi: id={}, name={}", id, pet.getName());
        // Observer Pattern: Pet eklendi bildirimi
        EventManager.getInstance().notify(EventType.PET_ADDED, pet);
        return pet;
    }

    /**
     * @brief ID ile pet getirir.
     * @param id Pet ID
     * @return Pet nesnesi
     * @throws ServiceException Bulunamazsa
     */
    public Pet getPetById(int id) {
        return repository.findById(id)
            .orElseThrow(() -> new ServiceException("Pet bulunamadı: id=" + id));
    }

    /**
     * @brief Tüm petleri listeler.
     * @return Pet listesi
     */
    public List<Pet> getAllPets() {
        return repository.findAll();
    }

    /**
     * @brief Pet günceller.
     * @param pet Güncellenecek Pet
     * @throws ServiceException Doğrulama başarısız veya bulunamazsa
     */
    public void updatePet(Pet pet) {
        validatePet(pet);
        boolean updated = repository.update(pet);
        if (!updated) {
            throw new ServiceException("Pet güncellenemedi: id=" + pet.getId());
        }
        logger.info("Pet güncellendi: id={}", pet.getId());
        // Observer Pattern: Pet güncellendi bildirimi
        EventManager.getInstance().notify(EventType.PET_UPDATED, pet);
    }

    /**
     * @brief Pet siler.
     * @param id Silinecek Pet ID
     * @throws ServiceException Bulunamazsa
     */
    public void deletePet(int id) {
        boolean deleted = repository.delete(id);
        if (!deleted) {
            throw new ServiceException("Pet silinemedi: id=" + id);
        }
        logger.info("Pet silindi: id={}", id);
        // Observer Pattern: Pet silindi bildirimi
        EventManager.getInstance().notify(EventType.PET_DELETED, id);
    }

    /**
     * @brief Tüm petleri siler.
     */
    public void deleteAllPets() {
        repository.deleteAll();
    }

    /**
     * @brief Toplam pet sayısını döndürür.
     * @return Sayı
     */
    public int getPetCount() {
        return repository.count();
    }

    /**
     * @brief Ada göre pet arar (büyük/küçük harf duyarsız).
     * @param name Aranacak ad
     * @return Eşleşen Pet listesi
     */
    public List<Pet> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) return getAllPets();
        String lower = name.toLowerCase().trim();
        return repository.findAll().stream()
            .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(lower))
            .collect(Collectors.toList());
    }

    /**
     * @brief Türe göre filtreler (Dog, Cat, Bird).
     * @param species Tür adı
     * @return Filtrelenmiş liste
     */
    public List<Pet> filterBySpecies(String species) {
        if (species == null || species.trim().isEmpty()) return getAllPets();
        return repository.findAll().stream()
            .filter(p -> p.getSpecies() != null
                && p.getSpecies().equalsIgnoreCase(species.trim()))
            .collect(Collectors.toList());
    }

    /**
     * @brief Repository kapatır.
     */
    public void close() {
        repository.close();
    }

    /**
     * @brief Pet doğrulama kuralları.
     * @param pet Doğrulanacak Pet
     * @throws ServiceException Kural ihlali
     */
    private void validatePet(Pet pet) {
        if (pet == null) throw new ServiceException("Pet null olamaz");
        if (pet.getName() == null || pet.getName().trim().isEmpty()) {
            throw new ServiceException("Pet adı boş olamaz");
        }
        if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(LocalDate.now())) {
            throw new ServiceException("Doğum tarihi gelecekte olamaz");
        }
    }
}