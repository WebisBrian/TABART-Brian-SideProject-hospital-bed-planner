package com.webisbrian.hospital_bed_planner.console;


import com.webisbrian.hospital_bed_planner.application.usecase.port.CreateStayUseCasePort;
import com.webisbrian.hospital_bed_planner.application.usecase.port.DischargePatientUseCasePort;
import com.webisbrian.hospital_bed_planner.application.usecase.port.PlacePatientUseCasePort;
import com.webisbrian.hospital_bed_planner.domain.model.HospitalStay;
import com.webisbrian.hospital_bed_planner.domain.model.StayType;
import com.webisbrian.hospital_bed_planner.domain.repository.HospitalStayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Optional;
import java.io.PrintWriter;

/**
 * Handler pour la gestion des séjours : création, placement automatique et sortie.
 *
 * Les logs évitent d'exposer des données sensibles ; seuls les identifiants et
 * messages d'erreur sont loggés. Les exceptions inattendues sont loggées en debug.
 */
public class StayMenuHandler {
    private static final Logger logger = LoggerFactory.getLogger(StayMenuHandler.class);

    private final CreateStayUseCasePort createStayUseCase;
    private final PlacePatientUseCasePort placePatientUseCase;
    private final DischargePatientUseCasePort dischargePatientUseCase;
    private final HospitalStayRepository hospitalStayRepository;
    private final ConsoleInputHandler input;
    private final PrintWriter out;

    public StayMenuHandler(CreateStayUseCasePort createStayUseCase,
                           PlacePatientUseCasePort placePatientUseCase,
                           DischargePatientUseCasePort dischargePatientUseCase,
                           HospitalStayRepository hospitalStayRepository,
                           ConsoleInputHandler input,
                           PrintWriter out) {
        this.createStayUseCase = createStayUseCase;
        this.placePatientUseCase = placePatientUseCase;
        this.dischargePatientUseCase = dischargePatientUseCase;
        this.hospitalStayRepository = hospitalStayRepository;
        this.input = input;
        this.out = out;
    }

    public void handleCreateStay() {
        out.println("--- Création d'un séjour ---");
        String stayId = input.readLine("ID du séjour");
        String patientId = input.readLine("ID patient");
        String bedId = input.readLine("ID du lit");
        LocalDate admissionDate = input.readMandatoryDate("Date d'admission");
        LocalDate dischargePlanned = input.readOptionalDate("Date de sortie prévue");
        StayType stayType;
        try {
            stayType = StayType.valueOf(input.readLine("Type de séjour (WEEK/DAY)").toUpperCase());
        } catch (Exception e) {
            out.println("Type invalide.");
            return;
        }

        try {
            logger.info("Console: creating stay id={}", stayId);
            createStayUseCase.createStay(stayId, patientId, bedId, admissionDate, dischargePlanned, stayType);
            out.println("Séjour créé avec succès !");
        } catch (IllegalArgumentException e) {
            logger.error("Erreur création séjour id={}: {}", stayId, e.getMessage());
            logger.debug("Stacktrace création séjour id=" + stayId, e);
            out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue création séjour id={}: {}", stayId, e.getMessage());
            logger.debug("Stacktrace création séjour id=" + stayId, e);
            out.println("Erreur inattendue : " + e.getMessage());
        }
    }

    public void handlePlacePatient() {
        out.println("--- Placement automatique d'un patient ---");
        String stayId = input.readLine("ID du séjour");
        String patientId = input.readLine("ID patient");
        LocalDate admissionDate = input.readMandatoryDate("Date d'admission");
        LocalDate dischargePlanned = input.readOptionalDate("Date de sortie prévue");
        StayType stayType;
        try {
            stayType = StayType.valueOf(input.readLine("Type de séjour (WEEK/DAY)").toUpperCase());
        } catch (Exception e) {
            out.println("Type de séjour invalide.");
            return;
        }

        try {
            logger.info("Console: placing patient id={} on stay id={}", patientId, stayId);
            Optional<HospitalStay> result = placePatientUseCase.placePatient(stayId, patientId, admissionDate, dischargePlanned, stayType);
            if (result.isEmpty()) {
                out.println("Aucun lit disponible pour ce patient à cette date.");
                return;
            }
            HospitalStay stay = result.get();
            out.println("Séjour créé et lit attribué avec succès !");
            out.println("  - ID séjour : " + stay.getId());
            out.println("  - ID patient : " + stay.getPatientId());
            out.println("  - ID lit : " + stay.getBedId());
            out.println("  - Type : " + stay.getStayType());
            out.println("  - Admission : " + stay.getAdmissionDate());
            out.println("  - Sortie prévue : " + stay.getDischargeDatePlanned());
        } catch (IllegalArgumentException e) {
            logger.error("Erreur placement patient id={}: {}", patientId, e.getMessage());
            logger.debug("Stacktrace placement patient id=" + patientId, e);
            out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue placement patient id={}: {}", patientId, e.getMessage());
            logger.debug("Stacktrace placement patient id=" + patientId, e);
            out.println("Erreur inattendue : " + e.getMessage());
        }
    }

    public void handleDischargePatient() {
        out.println("--- Enregistrer une sortie de patient ---");
        String stayId = input.readLine("ID du séjour");
        LocalDate dischargeDate = input.readMandatoryDate("Date de sortie effective");

        try {
            logger.info("Console: discharging stay id={}", stayId);
            HospitalStay updatedStay = dischargePatientUseCase.discharge(stayId, dischargeDate);
            out.println("Sortie enregistrée avec succès !");
            out.println("  - ID séjour : " + updatedStay.getId());
            out.println("  - ID patient : " + updatedStay.getPatientId());
            out.println("  - ID lit : " + updatedStay.getBedId());
            out.println("  - Admission : " + updatedStay.getAdmissionDate());
            out.println("  - Sortie prévue : " + updatedStay.getDischargeDatePlanned());
            out.println("  - Sortie effective : " + updatedStay.getDischargeDateEffective());
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la sortie du séjour id={}: {}", stayId, e.getMessage());
            logger.debug("Stacktrace discharge stay id=" + stayId, e);
            out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la sortie du séjour id={}: {}", stayId, e.getMessage());
            logger.debug("Stacktrace discharge stay id=" + stayId, e);
            out.println("Erreur inattendue : " + e.getMessage());
        }
    }

    public void listAllStays() {
        var stays = hospitalStayRepository.findAll();
        if (stays.isEmpty()) {
            out.println("Aucun séjour enregistré.");
            return;
        }
        out.println("--- Liste de tous les séjours ---");
        stays.forEach(s -> out.println(
                "- " + s.getId()
                        + " | patient=" + s.getPatientId()
                        + " | lit=" + s.getBedId()
                        + " | type=" + s.getStayType()
                        + " | admission=" + s.getAdmissionDate()
                        + " | sortie prévue=" + s.getDischargeDatePlanned()
                        + " | sortie effective=" + s.getDischargeDateEffective()
        ));
    }

    public void listActiveStaysForDate() {
        LocalDate date = input.readMandatoryDate("Date de référence");
        var stays = hospitalStayRepository.findActiveStaysOn(date);
        if (stays.isEmpty()) {
            out.println("Aucun séjour actif à la date " + date + ".");
            return;
        }
        out.println("--- Séjours actifs au " + date + " ---");
        stays.forEach(s -> out.println(
                "- " + s.getId()
                        + " | patient=" + s.getPatientId()
                        + " | lit=" + s.getBedId()
                        + " | type=" + s.getStayType()
                        + " | admission=" + s.getAdmissionDate()
                        + " | sortie prévue=" + s.getDischargeDatePlanned()
        ));
    }
}

