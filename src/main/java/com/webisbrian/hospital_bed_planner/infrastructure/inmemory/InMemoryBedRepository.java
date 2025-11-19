package com.webisbrian.hospital_bed_planner.infrastructure.inmemory;

import com.webisbrian.hospital_bed_planner.domain.model.Bed;
import com.webisbrian.hospital_bed_planner.domain.model.BedStatus;
import com.webisbrian.hospital_bed_planner.domain.repository.BedRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implémentation en mémoire de {@link BedRepository} pour les tests.
 * Stocke les lits dans une HashMap indexée par identifiant.
 */
public class InMemoryBedRepository implements BedRepository {

    private final Map<String, Bed> storage = new HashMap<>();

    @Override
    public Bed save(Bed bed) {
        if (bed == null) {
            throw new IllegalArgumentException("Bed cannot be null");
        }
        if (bed.getId() == null || bed.getId().isBlank()) {
            throw new IllegalArgumentException("Bed id cannot be null or blank");
        }

        storage.put(bed.getId(), bed);
        return bed;
    }

    @Override
    public Optional<Bed> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Bed> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public List<Bed> findByStatus(BedStatus status) {
        return storage.values().stream()
                .filter(bed -> bed.getStatus() == status)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        storage.remove(id);
    }

}
