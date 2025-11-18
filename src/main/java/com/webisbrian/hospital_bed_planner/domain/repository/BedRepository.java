package com.webisbrian.hospital_bed_planner.domain.repository;

import com.webisbrian.hospital_bed_planner.domain.model.Bed;
import com.webisbrian.hospital_bed_planner.domain.model.BedStatus;

import java.util.List;
import java.util.Optional;

/**
 * Port de persistance pour l'entité {@link Bed}.
 * Fournit les opérations nécessaires au domaine pour enregistrer,
 * rechercher, lister et supprimer des lits.
 */
public interface BedRepository {

    Bed save(Bed bed);

    Optional<Bed> findById(String id);

    List<Bed> findAll();

    List<Bed> findByStatus(BedStatus status);

    void deleteById(String id);
}
