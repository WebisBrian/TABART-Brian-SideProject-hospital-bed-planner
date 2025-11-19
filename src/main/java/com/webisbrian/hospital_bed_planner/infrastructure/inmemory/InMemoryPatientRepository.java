package com.webisbrian.hospital_bed_planner.infrastructure.inmemory;

import com.webisbrian.hospital_bed_planner.domain.model.Patient;
import com.webisbrian.hospital_bed_planner.domain.repository.PatientRepository;

import java.util.*;

/**
 * Implémentation en mémoire de {@link PatientRepository} pour les tests.
 * Stocke les patients dans une HashMap indexée par identifiant.
 */
public class InMemoryPatientRepository implements PatientRepository {

    private final Map<String, Patient> storage = new HashMap<>();

    @Override
    public Patient save(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        if (patient.getId() == null || patient.getId().isBlank()) {
            throw new IllegalArgumentException("Patient id cannot be null or blank");
        }

        storage.put(patient.getId(), patient);
        return patient;
    }

    @Override
    public Optional<Patient> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    @Override
    public List<Patient> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(String id) {
        storage.remove(id);
    }
}
