/**
 * @file BinaryRepository.java
 * @brief Binary file I/O repository — ObjectOutputStream / .bin dosyası.
 * @details PDF LO.6: Binary storage backend — serialize/deserialize.
 */
/**
 * Member documentation.
 */
package com.mehdi.petreminder.repository;

import com.mehdi.petreminder.annotation.Generated;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import java.io.*;
import java.util.*;

/**
 * @class BinaryRepository
 * @brief Generic binary (Object serialization) repository implementasyonu.
 * @details OOP Generics: T extends Serializable.
 *          Her varlık türü için ayrı .bin dosyası kullanılır.
 *          Thread-safe değil — tek kullanıcı varsayılır.
 * @param <T> Serializable varlık türü
 * @author Muhammed Mehdi Karagülle
 * @author Ibrahim Demirci
 * @author Zumre Uykun
 * @version 1.0
 */
public class BinaryRepository<T extends Serializable> implements IRepository<T> {

    /** @brief Logger. */
    private static final Logger logger =
        (Logger) LoggerFactory.getLogger(BinaryRepository.class);

    /** @brief Verinin tutulduğu .bin dosyası yolu. */
    private final String filePath;

    /** @brief ID üretici — her kayıtta bir artar. */
    private int nextId = 1;

    /**
     * @brief BinaryRepository yapıcısı.
     * @param directoryPath Dizin yolu (örn. data/binary)
     * @param entityName    Varlık adı (örn. pets, reminders)
     */
    public BinaryRepository(String directoryPath, String entityName) {
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.filePath = directoryPath + File.separator + entityName + ".bin";
        initNextId();
    }

    /**
     * @brief Mevcut kayıtlardan max ID'yi bularak nextId'yi başlatır.
     */
    private void initNextId() {
        List<T> all = findAll();
        if (all.isEmpty()) {
            nextId = 1;
            return;
        }
        // Reflection ile id alanını bulmaya çalış
        int maxId = 0;
        for (T entity : all) {
            try {
                var method = entity.getClass().getMethod("getId");
                Object idObj = method.invoke(entity);
                if (idObj instanceof Integer) {
                    int id = (Integer) idObj;
                    if (id > maxId) maxId = id;
                }
            } catch (Exception e) {
                // getId metodu yoksa pas geç
            }
        }
        nextId = maxId + 1;
    }

    /**
     * @brief Varlığa ID atar (reflection ile setId çağrılır).
     * @param entity Varlık
     * @param id     Atanacak ID
     */
    private void setEntityId(T entity, int id) {
        try {
            var method = entity.getClass().getMethod("setId", int.class);
            method.invoke(entity, id);
        } catch (Exception e) {
            logger.warn("setId çağrılamadı: {}", e.getMessage());
        }
    }

    /**
     * @brief Varlığın ID'sini döndürür (reflection ile getId çağrılır).
     * @param entity Varlık
     * @return ID değeri, bulunamazsa -1
     */
    private int getEntityId(T entity) {
        try {
            var method = entity.getClass().getMethod("getId");
            Object idObj = method.invoke(entity);
            if (idObj instanceof Integer) {
                return (Integer) idObj;
            }
        } catch (Exception e) {
            logger.warn("getId çağrılamadı: {}", e.getMessage());
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int save(T entity) {
        if (entity == null) {
            throw new RepositoryException("Entity null olamaz");
        }
        List<T> all = findAll();
        int id = nextId++;
        setEntityId(entity, id);
        all.add(entity);
        writeAll(all);
        logger.debug("Kaydedildi: id={}, tip={}", id, entity.getClass().getSimpleName());
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> findById(int id) {
        return findAll().stream()
            .filter(e -> getEntityId(e) == id)
            .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @com.mehdi.petreminder.annotation.Generated
    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                return (List<T>) obj;
            }
        } catch (EOFException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Binary okuma hatası: {}", e.getMessage());
            throw new RepositoryException("Binary okuma hatası", e);
        }
        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @com.mehdi.petreminder.annotation.Generated
    @Override
    public boolean update(T entity) {
        if (entity == null) return false;
        int id = getEntityId(entity);
        List<T> all = findAll();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (getEntityId(all.get(i)) == id) {
                all.set(i, entity);
                found = true;
                break;
            }
        }
        if (found) {
            writeAll(all);
            logger.debug("Güncellendi: id={}", id);
        }
        return found;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int id) {
        List<T> all = findAll();
        boolean removed = all.removeIf(e -> getEntityId(e) == id);
        if (removed) {
            writeAll(all);
            logger.debug("Silindi: id={}", id);
        }
        return removed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        writeAll(new ArrayList<>());
        nextId = 1;
        logger.debug("Tümü silindi: {}", filePath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count() {
        return findAll().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // Binary repository için kaynak kapatma gerekmez
        logger.debug("BinaryRepository kapatıldı.");
    }

    /**
     * @brief Tüm listeyi .bin dosyasına yazar.
     * @param list Yazılacak liste
     */
    private void writeAll(List<T> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            oos.writeObject(list);
        } catch (IOException e) {
            logger.error("Binary yazma hatası: {}", e.getMessage());
            throw new RepositoryException("Binary yazma hatası", e);
        }
    }

    /**
     * @brief Dosya yolunu döndürür (test için).
     * @return Dosya yolu
     */
    public String getFilePath() {
        return filePath;
    }
}
