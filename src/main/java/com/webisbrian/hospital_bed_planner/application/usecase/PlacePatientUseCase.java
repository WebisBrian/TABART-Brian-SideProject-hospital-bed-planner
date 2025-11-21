package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.application.usecase.port.PlacePatientUseCasePort;
import com.webisbrian.hospital_bed_planner.domain.model.Bed;
import com.webisbrian.hospital_bed_planner.domain.model.HospitalStay;
import com.webisbrian.hospital_bed_planner.domain.model.StayType;
import com.webisbrian.hospital_bed_planner.domain.service.PlacementService;
import com.webisbrian.hospital_bed_planner.domain.repository.HospitalStayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Use case d'application permettant de placer un patient dans un lit
 * à une date donnée, en créant un séjour d'hospitalisation.
 */
public class PlacePatientUseCase implements PlacePatientUseCasePort {

    private static final Logger logger = LoggerFactory.getLogger(PlacePatientUseCase.class);

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

        logger.info("Placing patient with id {} on date {}", patientId, admissionDate);

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
            logger.warn("Invalid stay dates: admission={}, plannedDischarge={}", admissionDate, plannedDischargeDate);
            throw new IllegalArgumentException("Planned discharge date cannot be before admission date");
        }

        // 1. Demander au service de placement un lit pour ce patient et cette date
        Optional<Bed> suggestedBed = placementService.suggestBedForPatient(patientId, admissionDate);

        // 2. Si aucun lit disponible, on retourne Optional.empty()
        if (suggestedBed.isEmpty()) {
            logger.warn("No bed available for patient with id {} on date {}", patientId, admissionDate);
            return Optional.empty();
        }

        Bed bed = suggestedBed.get();

        // 3. Créer le séjour (sortie effective inconnue au moment du placement)
        HospitalStay stay = new HospitalStay(
                stayId,
                patientId,
                bed.getId(),
                stayType,
                admissionDate,
                plannedDischargeDate,
                null
        );

        hospitalStayRepository.save(stay);

        logger.info("Patient placed with id {} on bed {}", patientId, bed.getId());
        return Optional.of(stay);
    }
}
