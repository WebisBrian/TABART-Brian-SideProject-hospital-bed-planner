package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.domain.model.HospitalStay;
import com.webisbrian.hospital_bed_planner.domain.model.StayType;
import com.webisbrian.hospital_bed_planner.domain.repository.HospitalStayRepository;
import com.webisbrian.hospital_bed_planner.domain.repository.PatientRepository;
import com.webisbrian.hospital_bed_planner.domain.repository.BedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * Use case permettant de créer un séjour d'hospitalisation
 * en spécifiant explicitement le lit (pas de placement automatique).
 */
public class CreateStayUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateStayUseCase.class);

    private final HospitalStayRepository hospitalStayRepository;
    private final PatientRepository patientRepository;
    private final BedRepository bedRepository;

    public CreateStayUseCase(HospitalStayRepository hospitalStayRepository,
                             PatientRepository patientRepository,
                             BedRepository bedRepository) {
        this.hospitalStayRepository = hospitalStayRepository;
        this.patientRepository = patientRepository;
        this.bedRepository = bedRepository;
    }

    /**
     * Crée un séjour avec un lit choisi explicitement.
     * <p>
     * Règles :
     * - stayId, patientId, bedId, admissionDate, stayType obligatoires.
     * - patientId doit exister.
     * - bedId doit exister.
     * - dischargeDatePlanned doit être >= admissionDate si renseignée.
     * - Ce use case NE VÉRIFIE PAS que le lit est libre (cas d'usage volontairement manuel).
     */
    public HospitalStay createStay(String stayId,
                                   String patientId,
                                   String bedId,
                                   LocalDate admissionDate,
                                   LocalDate dischargeDatePlanned,
                                   StayType stayType) {

        logger.info("Creating stay with id{}", stayId);

        if (stayId == null || stayId.isBlank())
            throw new IllegalArgumentException("Stay id cannot be null or blank");

        if (patientId == null || patientId.isBlank())
            throw new IllegalArgumentException("Patient id cannot be null or blank");

        if (bedId == null || bedId.isBlank())
            throw new IllegalArgumentException("Bed id cannot be null or blank");

        if (admissionDate == null)
            throw new IllegalArgumentException("Admission date cannot be null");

        if (stayType == null)
            throw new IllegalArgumentException("Stay type cannot be null");

        if (dischargeDatePlanned != null && dischargeDatePlanned.isBefore(admissionDate)) {
            logger.warn("Invalid stay dates: admission={}, plannedDischarge={}", admissionDate, dischargeDatePlanned);
            throw new IllegalArgumentException("Planned discharge date cannot be before admission date");
        }

        if (patientRepository.findById(patientId).isEmpty()) {
            logger.warn("Attempt to create stay with non-existing patientId={}", patientId);
            throw new IllegalArgumentException("Patient with id " + patientId + " does not exist");
        }

        if (bedRepository.findById(bedId).isEmpty()) {
            logger.warn("Attempt to create stay with non-existing bedId={}", bedId);
            throw new IllegalArgumentException("Bed with id " + bedId + " does not exist");
        }

        HospitalStay stay = new HospitalStay(
                stayId,
                patientId,
                bedId,
                stayType,
                admissionDate,
                dischargeDatePlanned,
                null // date de sortie effective
        );

        hospitalStayRepository.save(stay);

        logger.info("Stay created with id {}", stayId);
        return stay;
    }
}
