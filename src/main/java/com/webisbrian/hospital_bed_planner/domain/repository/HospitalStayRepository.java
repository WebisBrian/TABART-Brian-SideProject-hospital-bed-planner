package com.webisbrian.hospital_bed_planner.domain.repository;

import com.webisbrian.hospital_bed_planner.domain.model.HospitalStay;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Port de persistance pour l'entité {@link HospitalStay}.
 * Fournit les opérations nécessaires au domaine pour gérer les séjours d'hospitalisation.
 *
 * Un séjour est considéré actif à une date donnée si :
 * - la date d'admission est antérieure ou égale à cette date,
 * - et la date de sortie effective est nulle ou postérieure à cette date.
 */
public interface HospitalStayRepository {

    HospitalStay save(HospitalStay hospitalStay);

    Optional<HospitalStay> findById(String id);

    List<HospitalStay> findAllByPatientId(String patientId);

    List<HospitalStay> findAll();

    List<HospitalStay> findActiveStaysOn(LocalDate date);
}
