package com.webisbrian.hospital_bed_planner.console;

import com.webisbrian.hospital_bed_planner.application.usecase.CreatePatientUseCase;
import com.webisbrian.hospital_bed_planner.application.usecase.port.CreatePatientUseCasePort;
import com.webisbrian.hospital_bed_planner.domain.model.Patient;
import com.webisbrian.hospital_bed_planner.domain.model.Sex;
import com.webisbrian.hospital_bed_planner.domain.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.io.PrintWriter;

/**
 * Menu handler pour les opérations sur les patients (création, listing).
 *
 * Ne loggue pas d'informations sensibles (téléphone, notes). Seul l'ID patient
 * est loggé lorsqu'il est pertinent.
 */
public class PatientMenuHandler {
    private static final Logger logger = LoggerFactory.getLogger(PatientMenuHandler.class);

    private final CreatePatientUseCasePort createPatientUseCase;
    private final PatientRepository patientRepository;
    private final ConsoleInputHandler input;
    private final PrintWriter out;

    public PatientMenuHandler(CreatePatientUseCasePort createPatientUseCase,
                              PatientRepository patientRepository,
                              ConsoleInputHandler input,
                              PrintWriter out) {
        this.createPatientUseCase = createPatientUseCase;
        this.patientRepository = patientRepository;
        this.input = input;
        this.out = out;
    }

    /**
     * Crée un patient à partir des saisies console.
     * Les erreurs de validation sont présentées à l'utilisateur ; la stacktrace
     * est loggée uniquement en debug.
     */
    public void handleCreate() {
        out.println("--- Création d'un patient ---");
        String id = input.readLine("Id patient");
        String firstName = input.readLine("Prénom");
        String lastName = input.readLine("Nom");
        LocalDate birthDate = input.readMandatoryDate("Date de naissance");
        Sex sex;
        try {
            sex = Sex.valueOf(input.readLine("Sexe (MALE/FEMALE/OTHER)").toUpperCase());
        } catch (IllegalArgumentException e) {
            out.println("Sexe invalide.");
            return;
        } catch (Exception e) {
            logger.debug("Erreur lors du parsing du sexe pour le patient id=" + id, e);
            out.println("Erreur inattendue : " + e.getMessage());
            return;
        }
        boolean pmr = input.readYesNo("PMR ?");
        boolean isolation = input.readYesNo("Isolement ?");
        String phone = input.readLine("Téléphone");
        String notes = input.readLine("Notes (optionnel)");
        if (notes.isEmpty()) notes = null;

        try {
            logger.info("Console: creating patient id={}", id);
            createPatientUseCase.createPatient(id, firstName, lastName, birthDate, sex, pmr, isolation, phone, notes);
            out.println("Patient créé avec succès !");
        } catch (IllegalArgumentException e) {
            // message d'erreur métier attendu
            logger.error("Erreur lors de la création du patient id={}: {}", id, e.getMessage());
            logger.debug("Stacktrace création patient id=" + id, e);
            out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            // erreur inattendue
            logger.error("Erreur inattendue lors de la création du patient id={}: {}", id, e.getMessage());
            logger.debug("Stacktrace création patient id=" + id, e);
            out.println("Erreur inattendue : " + e.getMessage());
        }
    }

    /**
     * Liste les patients. Affiche des informations non sensibles.
     */
    public void listPatients() {
        var patients = patientRepository.findAll();
        if (patients.isEmpty()) {
            out.println("Aucun patient enregistré.");
            return;
        }
        out.println("--- Liste des patients ---");
        for (Patient p : patients) {
            out.println("- " + p.getId() + " : " + p.getFullName() + " (" + p.getBirthDate() + ", " + p.getSex() + ")");
        }
    }
}
