package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.domain.model.HospitalStay;
import com.webisbrian.hospital_bed_planner.domain.model.StayType;
import com.webisbrian.hospital_bed_planner.domain.repository.HospitalStayRepository;
import com.webisbrian.hospital_bed_planner.domain.repository.PatientRepository;
import com.webisbrian.hospital_bed_planner.domain.repository.BedRepository;

import java.time.LocalDate;

/**
 * Use case permettant de créer un séjour d'hospitalisation
 * en spécifiant explicitement le lit (pas de placement automatique).
 */
public class CreateStayUseCase {

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
     *
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

        // 1. Validations de base
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

        if (dischargeDatePlanned != null && dischargeDatePlanned.isBefore(admissionDate))
            throw new IllegalArgumentException("Planned discharge date cannot be before admission date");

        // 2. Vérifier existence patient
        if (patientRepository.findById(patientId).isEmpty())
            throw new IllegalArgumentException("Patient with id " + patientId + " does not exist");

        // 3. Vérifier existence lit
        if (bedRepository.findById(bedId).isEmpty())
            throw new IllegalArgumentException("Bed with id " + bedId + " does not exist");

        // 4. Création du séjour
        HospitalStay stay = new HospitalStay(
                stayId,
                patientId,
                bedId,
                stayType,
                admissionDate,
                dischargeDatePlanned,
                null // date de sortie effective
        );

        // 5. Persistance
        hospitalStayRepository.save(stay);

        return stay;
    }
}
