package com.webisbrian.hospital_bed_planner.domain.repository;

import com.webisbrian.hospital_bed_planner.domain.model.Patient;

import java.util.List;
import java.util.Optional;

/**
 * Port de persistance pour l'entité {@link Patient}.
 * Fournit les opérations nécessaires au domaine pour enregistrer,
 * rechercher, lister et supprimer des patients.
 */
public interface PatientRepository {

    Patient save(Patient patient);

    Optional<Patient> findById(String id);

    boolean existsById(String id);

    List<Patient> findAll();

    void deleteById(String id);
}
