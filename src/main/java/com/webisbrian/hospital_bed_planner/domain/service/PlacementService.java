package com.webisbrian.hospital_bed_planner.domain.service;

import com.webisbrian.hospital_bed_planner.domain.model.Bed;
import com.webisbrian.hospital_bed_planner.domain.model.BedStatus;
import com.webisbrian.hospital_bed_planner.domain.model.HospitalStay;
import com.webisbrian.hospital_bed_planner.domain.repository.BedRepository;
import com.webisbrian.hospital_bed_planner.domain.repository.HospitalStayRepository;
import com.webisbrian.hospital_bed_planner.domain.repository.PatientRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PlacementService {

    private final PatientRepository patientRepository;
    private final BedRepository bedRepository;
    private final HospitalStayRepository hospitalStayRepository;

    public PlacementService(PatientRepository patientRepository,
                            BedRepository bedRepository,
                            HospitalStayRepository hospitalStayRepository) {

        this.patientRepository = patientRepository;
        this.bedRepository = bedRepository;
        this.hospitalStayRepository = hospitalStayRepository;
    }

    /**
     * Propose un lit pour un patient donné à une date donnée.
     * <p>
     * Règles MVP :
     * - Le patient doit exister.
     * - Le lit doit être en statut AVAILABLE.
     * - Le lit ne doit pas être déjà occupé via un séjour actif ce jour-là.
     * - Parmi les lits éligibles, on choisit celui avec le plus petit code (ordre alphabétique).
     *
     * @param patientId identifiant du patient
     * @param date      date d'hospitalisation visée
     * @return un lit disponible correspondant aux contraintes, ou {@code Optional.empty()} si aucun.
     * @throws IllegalArgumentException si le patient n'existe pas ou si les paramètres sont invalides
     */
    public Optional<Bed> suggestBedForPatient(String patientId, LocalDate date) {
        // 1. Validation simple des paramètres
        if (patientId == null || patientId.isBlank()) {
            throw new IllegalArgumentException("Patient id cannot be null or blank");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        // 2. Vérifier que le patient existe
        if (patientRepository.findById(patientId).isEmpty()) {
            throw new IllegalArgumentException("Patient with id " + patientId + " does not exist");
        }

        // 3. Récupérer les séjours actifs à cette date
        List<HospitalStay> activeStays = hospitalStayRepository.findActiveStaysOn(date);

        // 4. En déduire la liste des lits déjà occupés
        Set<String> occupiedBedIds = activeStays.stream()
                .map(HospitalStay::getBedId)
                .collect(Collectors.toSet());

        // 5. Récupérer tous les lits et filtrer
        //      - statut AVAILABLE
        //      - lit non occupé ce jour-là

        return bedRepository.findAll().stream()
                .filter(bed -> bed.getStatus() == BedStatus.AVAILABLE)
                .filter(bed -> !occupiedBedIds.contains(bed.getId()))
                // 6. Trier par code pour avoir un comportement déterministe
                .sorted(Comparator.comparing(Bed::getCode))
                // 7. Retourner le premier lit trouvé
                .findFirst();
    }
}
