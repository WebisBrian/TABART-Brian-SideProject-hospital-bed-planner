package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.domain.model.Bed;
import com.webisbrian.hospital_bed_planner.domain.model.HospitalStay;
import com.webisbrian.hospital_bed_planner.domain.model.StayType;
import com.webisbrian.hospital_bed_planner.domain.service.PlacementService;
import com.webisbrian.hospital_bed_planner.domain.repository.HospitalStayRepository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Use case d'application permettant de placer un patient dans un lit
 * à une date donnée, en créant un séjour d'hospitalisation.
 */
public class PlacePatientUseCase {

    private final PlacementService placementService;
    private final HospitalStayRepository hospitalStayRepository;

    public PlacePatientUseCase(PlacementService placementService,
                               HospitalStayRepository hospitalStayRepository) {
        this.placementService = placementService;
        this.hospitalStayRepository = hospitalStayRepository;
    }

    /**
     * Tente de placer un patient dans un lit en créant un séjour.
     *
     * Règles :
     * - Les paramètres obligatoires (stayId, patientId, admissionDate, stayType) ne doivent pas être null/vides.
     * - Si la date de sortie prévue est renseignée, elle ne doit pas être avant la date d'admission.
     * - Le placement repose sur {@link PlacementService#suggestBedForPatient(String, LocalDate)}.
     * - Si aucun lit n'est disponible, le use case retourne {@code Optional.empty()}.
     * - Si un lit est trouvé, un {@link HospitalStay} est créé et sauvegardé, puis renvoyé.
     *
     * @param stayId               identifiant du séjour à créer
     * @param patientId            identifiant du patient à placer
     * @param admissionDate        date d'admission (début de séjour)
     * @param plannedDischargeDate date de sortie prévue (peut être null)
     * @param stayType             type de séjour (WEEK, DAY, etc.)
     * @return un séjour créé si un lit a pu être trouvé, sinon {@code Optional.empty()}
     * @throws IllegalArgumentException si les paramètres sont invalides
     *                                  ou si le patient n'existe pas (via PlacementService)
     */
    public Optional<HospitalStay> placePatient(String stayId,
                                               String patientId,
                                               LocalDate admissionDate,
                                               LocalDate plannedDischargeDate,
                                               StayType stayType) {
        // 1. Validation des paramètres
        if (stayId == null || stayId.isBlank()) {
            throw new IllegalArgumentException("Stay id cannot be null or blank");
        }
        if (patientId == null || patientId.isBlank()) {
            throw new IllegalArgumentException("Patient id cannot be null or blank");
        }
        if (admissionDate == null) {
            throw new IllegalArgumentException("Admission date cannot be null");
        }
        if (stayType == null) {
            throw new IllegalArgumentException("Stay type cannot be null");
        }
        if (plannedDischargeDate != null && plannedDischargeDate.isBefore(admissionDate)) {
            throw new IllegalArgumentException("Planned discharge date cannot be before admission date");
        }

        // 2. Demander au service de placement un lit pour ce patient et cette date
        Optional<Bed> suggestedBed = placementService.suggestBedForPatient(patientId, admissionDate);

        // 3. Si aucun lit disponible, on retourne Optional.empty()
        if (suggestedBed.isEmpty()) {
            return Optional.empty();
        }

        Bed bed = suggestedBed.get();

        // 4. Créer le séjour (sortie effective inconnue au moment du placement)
        HospitalStay stay = new HospitalStay(
                stayId,
                patientId,
                bed.getId(),
                stayType,
                admissionDate,
                plannedDischargeDate,
                null
        );

        // 5. Persister le séjour
        hospitalStayRepository.save(stay);

        // 6. Retourner le séjour créé
        return Optional.of(stay);
    }
}
