package com.webisbrian.hospital_bed_planner.console;

import com.webisbrian.hospital_bed_planner.application.usecase.CreatePatientUseCase;
import com.webisbrian.hospital_bed_planner.application.usecase.CreateStayUseCase;
import com.webisbrian.hospital_bed_planner.application.usecase.DischargePatientUseCase;
import com.webisbrian.hospital_bed_planner.application.usecase.PlacePatientUseCase;
import com.webisbrian.hospital_bed_planner.domain.model.HospitalStay;
import com.webisbrian.hospital_bed_planner.domain.model.Sex;
import com.webisbrian.hospital_bed_planner.domain.model.StayType;
import com.webisbrian.hospital_bed_planner.domain.service.PlacementService;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryBedRepository;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryHospitalStayRepository;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryPatientRepository;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Point d'entrée console de l'application Hospital Bed Planner.
 */
public class HospitalBedPlannerConsoleApp {

    private final Scanner scanner = new Scanner(System.in);

    // Repositories
    private final InMemoryPatientRepository patientRepository = new InMemoryPatientRepository();
    private final InMemoryBedRepository bedRepository = new InMemoryBedRepository();
    private final InMemoryHospitalStayRepository hospitalStayRepository = new InMemoryHospitalStayRepository();

    // Domain services
    private final PlacementService placementService = new PlacementService(
            patientRepository,
            bedRepository,
            hospitalStayRepository
    );

    private final CreatePatientUseCase createPatientUseCase = new CreatePatientUseCase(patientRepository);
    private final CreateStayUseCase createStayUseCase = new CreateStayUseCase(hospitalStayRepository, patientRepository, bedRepository);
    private final PlacePatientUseCase placePatientUseCase = new PlacePatientUseCase(placementService, hospitalStayRepository);
    private final DischargePatientUseCase dischargePatientUseCase = new DischargePatientUseCase(hospitalStayRepository);

    public static void main(String[] args) {
        new HospitalBedPlannerConsoleApp().run();
    }

    public void run() {
        boolean running = true;

        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleCreatePatient();
                case "2" -> handleCreateStay();
                case "3" -> handlePlacePatient();
                case "4" -> handleDischargePatient();
                case "5" -> handleVisualisationMenu();
                case "0" -> {
                    System.out.println("Au revoir.");
                    running = false;
                }
                default -> System.out.println("Choix invalide, merci de réessayer.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("=== Hospital Bed Planner ===");
        System.out.println("1. Créer un patient");
        System.out.println("2. Créer un séjour (lit choisi manuellement)");
        System.out.println("3. Placer un patient (lit proposé automatiquement)");
        System.out.println("4. Enregistrer une sortie (discharge)");
        System.out.println("5. Visualisation");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }

    private void handleCreatePatient() {
        System.out.println("--- Création d'un patient ---");

        System.out.println("Id patient : ");
        String id = scanner.nextLine().trim();

        System.out.print("Prénom : ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Nom : ");
        String lastName = scanner.nextLine().trim();

        LocalDate birthDate = readDateFlexible("Date de naissance");

        System.out.print("Sexe (MALE/FEMALE/OTHER) : ");
        Sex sex;
        try {
            sex = Sex.valueOf(scanner.nextLine().trim().toUpperCase());
        } catch (Exception e) {
            System.out.println("Sexe invalide.");
            return;
        }

        System.out.print("PMR ? (o/n) : ");
        boolean pmr = scanner.nextLine().trim().equalsIgnoreCase("o");

        System.out.print("Isolement ? (o/n) : ");
        boolean isolation = scanner.nextLine().trim().equalsIgnoreCase("o");

        System.out.print("Téléphone : ");
        String phone = scanner.nextLine().trim();

        System.out.print("Notes (optionnel) : ");
        String notes = scanner.nextLine().trim();
        if (notes.isEmpty()) notes = null;

        try {
            createPatientUseCase.createPatient(
                    id,
                    firstName,
                    lastName,
                    birthDate,
                    sex,
                    pmr,
                    isolation,
                    phone,
                    notes
            );
            System.out.println("Patient créé avec succès !");
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void handleCreateStay() {
        System.out.println("--- Création d'un séjour ---");

        System.out.print("ID du séjour : ");
        String stayId = scanner.nextLine().trim();

        System.out.print("ID patient : ");
        String patientId = scanner.nextLine().trim();

        System.out.print("ID du lit : ");
        String bedId = scanner.nextLine().trim();

        LocalDate admissionDate = readDateFlexible("Date d'admission");

        LocalDate dischargePlanned = readOptionalDateFlexible("Date de sortie prévue");

        System.out.print("Type de séjour (WEEK/DAY) : ");
        StayType stayType;
        try {
            stayType = StayType.valueOf(scanner.nextLine().trim().toUpperCase());
        } catch (Exception e) {
            System.out.println("Type invalide.");
            return;
        }

        try {
            createStayUseCase.createStay(
                    stayId,
                    patientId,
                    bedId,
                    admissionDate,
                    dischargePlanned,
                    stayType
            );
            System.out.println("Séjour créé avec succès !");
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }


    private void handlePlacePatient() {
        System.out.println("--- Placement automatique d'un patient ---");

        System.out.print("ID du séjour : ");
        String stayId = scanner.nextLine().trim();

        System.out.print("ID patient : ");
        String patientId = scanner.nextLine().trim();

        LocalDate admissionDate = readDateFlexible("Date d'admission");

        LocalDate dischargePlanned = readOptionalDateFlexible("Date de sortie prévue");

        System.out.print("Type de séjour (WEEK/DAY) : ");
        StayType stayType;
        try {
            stayType = StayType.valueOf(scanner.nextLine().trim().toUpperCase());
        } catch (Exception e) {
            System.out.println("Type de séjour invalide.");
            return;
        }

        try {
            var result = placePatientUseCase.placePatient(
                    stayId,
                    patientId,
                    admissionDate,
                    dischargePlanned,
                    stayType
            );

            if (result.isEmpty()) {
                System.out.println("Aucun lit disponible pour ce patient à cette date.");
                return;
            }

            var stay = result.get();
            System.out.println("Séjour créé et lit attribué avec succès !");
            System.out.println("  - ID séjour : " + stay.getId());
            System.out.println("  - ID patient : " + stay.getPatientId());
            System.out.println("  - ID lit : " + stay.getBedId());
            System.out.println("  - Type : " + stay.getStayType());
            System.out.println("  - Admission : " + stay.getAdmissionDate());
            System.out.println("  - Sortie prévue : " + stay.getDischargeDatePlanned());
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void handleDischargePatient() {
        System.out.println("--- Enregistrer une sortie de patient ---");

        System.out.print("ID du séjour : ");
        String stayId = scanner.nextLine().trim();

        LocalDate dischargeDate = readDateFlexible("Date de sortie effective");

        try {
            HospitalStay updatedStay = dischargePatientUseCase.discharge(stayId, dischargeDate);

            System.out.println("Sortie enregistrée avec succès !");
            System.out.println("  - ID séjour : " + updatedStay.getId());
            System.out.println("  - ID patient : " + updatedStay.getPatientId());
            System.out.println("  - ID lit : " + updatedStay.getBedId());
            System.out.println("  - Admission : " + updatedStay.getAdmissionDate());
            System.out.println("  - Sortie prévue : " + updatedStay.getDischargeDatePlanned());
            System.out.println("  - Sortie effective : " + updatedStay.getDischargeDateEffective());
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void handleVisualisationMenu() {
        boolean back = false;

        while (!back) {
            printVisualisationMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> listPatients();
                case "2" -> listAllStays();
                case "3" -> listActiveStaysForDate();
                case "0" -> back = true;
                default -> System.out.println("Choix invalide, merci de réessayer.");
            }
        }
    }
    private void printVisualisationMenu() {
        System.out.println("=== Visualisation ===");
        System.out.println("1. Lister tous les patients");
        System.out.println("2. Lister tous les séjours");
        System.out.println("3. Lister les séjours actifs à une date donnée");
        System.out.println("0. Retour au menu principal");
        System.out.print("Votre choix : ");
    }

    private void listPatients() {
        var patients = patientRepository.findAll();

        if (patients.isEmpty()) {
            System.out.println("Aucun patient enregistré.");
            return;
        }

        System.out.println("--- Liste des patients ---");
        patients.forEach(p -> System.out.println(
                "- " + p.getId() + " : " + p.getFullName()
                        + " (" + p.getBirthDate() + ", " + p.getSex() + ")"
        ));
    }

    private void listAllStays() {
        var stays = hospitalStayRepository.findAll();

        if (stays.isEmpty()) {
            System.out.println("Aucun séjour enregistré.");
            return;
        }

        System.out.println("--- Liste de tous les séjours ---");
        stays.forEach(s -> System.out.println(
                "- " + s.getId()
                        + " | patient=" + s.getPatientId()
                        + " | lit=" + s.getBedId()
                        + " | type=" + s.getStayType()
                        + " | admission=" + s.getAdmissionDate()
                        + " | sortie prévue=" + s.getDischargeDatePlanned()
                        + " | sortie effective=" + s.getDischargeDateEffective()
        ));
    }

    private void listActiveStaysForDate() {
        LocalDate date = readDateFlexible("Date de référence");

        var stays = hospitalStayRepository.findActiveStaysOn(date);

        if (stays.isEmpty()) {
            System.out.println("Aucun séjour actif à la date " + date + ".");
            return;
        }

        System.out.println("--- Séjours actifs au " + date + " ---");
        stays.forEach(s -> System.out.println(
                "- " + s.getId()
                        + " | patient=" + s.getPatientId()
                        + " | lit=" + s.getBedId()
                        + " | type=" + s.getStayType()
                        + " | admission=" + s.getAdmissionDate()
                        + " | sortie prévue=" + s.getDischargeDatePlanned()
        ));
    }

    // --- Méthodes utilitaires de lecture de dates ---

    /**
     * Tente de parser une date avec plusieurs formats :
     * - YYYY-MM-DD (ISO)      ex: 2025-01-15
     * - DD/MM/YYYY            ex: 15/01/2025
     * - DD-MM-YYYY            ex: 15-01-2025
     */
    private LocalDate parseFlexibleDate(String input) {
        // ISO : 2025-01-15
        if (input.contains("-") && input.indexOf('-') == 4) {
            return LocalDate.parse(input);
        }

        // DD/MM/YYYY
        if (input.contains("/")) {
            String[] parts = input.split("/");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Format de date invalide");
            }
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            return LocalDate.of(year, month, day);
        }

        // DD-MM-YYYY
        if (input.contains("-")) {
            String[] parts = input.split("-");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Format de date invalide");
            }
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            return LocalDate.of(year, month, day);
        }

        throw new IllegalArgumentException("Format de date invalide");
    }

    /**
     * Lit une date obligatoire, avec formats flexibles.
     */
    private LocalDate readDateFlexible(String label) {
        while (true) {
            System.out.print(label + " (YYYY-MM-DD ou DD/MM/YYYY) : ");
            String input = scanner.nextLine().trim();
            try {
                return parseFlexibleDate(input);
            } catch (Exception e) {
                System.out.println("Date invalide, merci de réessayer.");
            }
        }
    }

    /**
     * Lit une date optionnelle, avec formats flexibles.
     * Retourne null si l'utilisateur ne saisit rien.
     */
    private LocalDate readOptionalDateFlexible(String label) {
        while (true) {
            System.out.print(label + " (optionnel, ENTER pour ignorer) : ");
            String input = scanner.nextLine().trim();

            if (input.isBlank()) {
                return null;
            }

            try {
                return parseFlexibleDate(input);
            } catch (Exception e) {
                System.out.println("Date invalide, merci de réessayer.");
            }
        }
    }


}
