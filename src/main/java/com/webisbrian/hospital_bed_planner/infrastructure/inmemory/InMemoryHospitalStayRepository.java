package com.webisbrian.hospital_bed_planner.infrastructure.inmemory;

import com.webisbrian.hospital_bed_planner.domain.model.HospitalStay;
import com.webisbrian.hospital_bed_planner.domain.repository.HospitalStayRepository;

import java.time.LocalDate;
import java.util.*;

/**
 * Implémentation en mémoire de {@link HospitalStayRepository} pour les tests.
 * Stocke les séjours dans une HashMap indexée par identifiant.
 */
public class InMemoryHospitalStayRepository implements HospitalStayRepository {

    private final Map<String, HospitalStay> storage = new HashMap<>();

    @Override
    public HospitalStay save(HospitalStay hospitalStay) {
        if (hospitalStay == null) {
            throw new IllegalArgumentException("HospitalStay cannot be null");
        }
        if (hospitalStay.getId() == null || hospitalStay.getId().isBlank()) {
            throw new IllegalArgumentException("HospitalStay id cannot be null or blank");
        }

        storage.put(hospitalStay.getId(), hospitalStay);
        return hospitalStay;
    }

    @Override
    public Optional<HospitalStay> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<HospitalStay> findAllByPatientId(String patientId) {
        return storage.values().stream()
                .filter(hospitalStay -> Objects.equals(hospitalStay.getPatientId(), patientId))
                .toList();
    }

    @Override
    public List<HospitalStay> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public List<HospitalStay> findActiveStaysOn(LocalDate date) {
        return storage.values().stream()
                .filter(stay -> isActiveOn(stay, date))
                .toList();
    }

    private boolean isActiveOn(HospitalStay stay, LocalDate date) {
        boolean admitted = !stay.getAdmissionDate().isAfter(date);
        LocalDate discharge = stay.getDischargeDateEffective();
        boolean notDischargedYet = (discharge == null || !discharge.isBefore(date));
        return admitted && notDischargedYet;
    }
}
