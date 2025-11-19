package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.domain.model.HospitalStay;
import com.webisbrian.hospital_bed_planner.domain.repository.HospitalStayRepository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Use case d'application permettant d'enregistrer la sortie effective
 * d'un patient pour un séjour donné.
 */
public class DischargePatientUseCase {

    private final HospitalStayRepository hospitalStayRepository;

    public DischargePatientUseCase(HospitalStayRepository hospitalStayRepository) {
        this.hospitalStayRepository = hospitalStayRepository;
    }

    /**
     * Enregistre la date de sortie effective d'un séjour d'hospitalisation.
     *
     * Règles :
     * - stayId ne doit pas être null ou vide.
     * - dischargeDate ne doit pas être null.
     * - Le séjour doit exister.
     * - Le séjour ne doit pas déjà être clôturé (date de sortie effective déjà renseignée).
     * - La date de sortie effective ne peut pas être avant la date d'admission.
     *
     * La méthode retourne le séjour mis à jour.
     *
     * @param stayId        identifiant du séjour à clôturer
     * @param dischargeDate date de sortie effective
     * @return le séjour mis à jour avec la date de sortie effective
     * @throws IllegalArgumentException si les paramètres sont invalides,
     *                                  si le séjour n'existe pas
     *                                  ou si le séjour est déjà clôturé
     */
    public HospitalStay discharge(String stayId, LocalDate dischargeDate) {
        // 1. Validation des paramètres
        if (stayId == null || stayId.isBlank()) {
            throw new IllegalArgumentException("Stay id cannot be null or blank");
        }
        if (dischargeDate == null) {
            throw new IllegalArgumentException("Discharge date cannot be null");
        }

        // 2. Recherche du séjour
        Optional<HospitalStay> optionalStay = hospitalStayRepository.findById(stayId);
        if (optionalStay.isEmpty()) {
            throw new IllegalArgumentException("Hospital stay with id " + stayId + " does not exist");
        }

        HospitalStay existingStay = optionalStay.get();

        // 3. Vérifier qu'il n'est pas déjà clôturé
        if (existingStay.getDischargeDateEffective() != null) {
            throw new IllegalArgumentException("Hospital stay with id " + stayId + " is already discharged");
        }

        // 4. Vérifier la cohérence de la date de sortie
        if (dischargeDate.isBefore(existingStay.getAdmissionDate())) {
            throw new IllegalArgumentException("Discharge date cannot be before admission date");
        }

        // 5. Créer une nouvelle instance immuable avec la date de sortie effective renseignée
        HospitalStay updatedStay = new HospitalStay(
                existingStay.getId(),
                existingStay.getPatientId(),
                existingStay.getBedId(),
                existingStay.getStayType(),
                existingStay.getAdmissionDate(),
                existingStay.getDischargeDatePlanned(),
                dischargeDate
        );

        // 6. Persister le séjour mis à jour
        hospitalStayRepository.save(updatedStay);

        // 7. Retourner le séjour mis à jour
        return updatedStay;
    }
}
