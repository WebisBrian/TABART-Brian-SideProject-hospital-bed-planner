package com.webisbrian.hospital_bed_planner.console;

import com.webisbrian.hospital_bed_planner.application.ApplicationContext;
import com.webisbrian.hospital_bed_planner.application.usecase.*;
import com.webisbrian.hospital_bed_planner.config.ApplicationComposition;
import com.webisbrian.hospital_bed_planner.domain.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Application console : orchestration minimale du menu.
 *
 * La classe reçoit un PrintWriter et un Scanner (fournis par main) et ne ferme
 * pas ces ressources (gestion de la fermeture laissée à l'appelant / JVM).
 */
public class HospitalBedPlannerConsoleApp {
    private static final Logger logger = LoggerFactory.getLogger(HospitalBedPlannerConsoleApp.class);

    private final Scanner scanner;
    private final PrintWriter out;
    private final ConsoleInputHandler inputHandler;
    private final PatientMenuHandler patientHandler;
    private final StayMenuHandler stayHandler;
    private final BedMenuHandler bedHandler;

    public static void main(String[] args) {
        // Crée un PrintWriter autour de System.Out avec autoFlush.
        // Ne pas fermer ce PrintWriter car il wrap System.out (fermer fermerait la sortie JVM).
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true);

        ApplicationContext context = createApplicationContext();
        // Ne pas fermer le Scanner(System.in) car cela fermerait System.in pour l'environnement JVM.
        Scanner scanner = new Scanner(System.in);

        new HospitalBedPlannerConsoleApp(context, scanner, out).run();
    }

    private static ApplicationContext createApplicationContext() {
        return ApplicationComposition.createFromDefaultConfiguration();
    }

    public HospitalBedPlannerConsoleApp(ApplicationContext context, Scanner scanner, PrintWriter out) {
        this.scanner = scanner;
        this.out = out;
        this.inputHandler = new ConsoleInputHandler(scanner, out);
        this.patientHandler = new PatientMenuHandler(context.getCreatePatientUseCase(), context.getPatientRepository(), inputHandler, out);
        this.stayHandler = new StayMenuHandler(context.getCreateStayUseCase(), context.getPlacePatientUseCase(), context.getDischargePatientUseCase(), context.getHospitalStayRepository(), inputHandler, out);
        this.bedHandler = new BedMenuHandler(context.getCreateBedUseCase(), context.getUpdateBedStatusUseCase(), context.getDeleteBedUseCase(), context.getBedRepository(), inputHandler, out);
    }

    public void run() {
        logger.info("Starting HospitalBedPlannerConsoleApp");
        boolean running = true;

        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> patientHandler.handleCreate();
                    case "2" -> stayHandler.handleCreateStay();
                    case "3" -> stayHandler.handlePlacePatient();
                    case "4" -> stayHandler.handleDischargePatient();
                    case "5" -> handleVisualisationMenu();
                    case "6" -> handleBedManagementMenu();
                    case "0" -> {
                        out.println("Au revoir.");
                        logger.info("Application stopped by user");
                        running = false;
                    }
                    default -> out.println("Choix invalide, merci de réessayer.");
                }
            } catch (Exception e) {
                // message d'erreur visible pour l'utilisateur + stacktrace en debug
                logger.error("Unexpected error in main loop: {}", e.getMessage());
                logger.debug("Unexpected error stacktrace", e);
                out.println("Une erreur inattendue est survenue. Consultez les logs.");
            }
        }
    }

    private void printMainMenu() {
        out.println("=== Hospital Bed Planner ===");
        out.println("1. Créer un patient");
        out.println("2. Créer un séjour (lit choisi manuellement)");
        out.println("3. Placer un patient (lit proposé automatiquement)");
        out.println("4. Enregistrer une sortie (discharge)");
        out.println("5. Visualisation");
        out.println("6. Gestion des lits");
        out.println("0. Quitter");
        out.print("Votre choix : ");
        out.flush();
    }

    private void handleVisualisationMenu() {
        boolean back = false;
        while (!back) {
            out.println("=== Visualisation ===");
            out.println("1. Lister tous les patients");
            out.println("2. Lister tous les séjours");
            out.println("3. Lister les séjours actifs à une date donnée");
            out.println("0. Retour au menu principal");
            out.print("Votre choix : ");
            out.flush();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> patientHandler.listPatients();
                case "2" -> stayHandler.listAllStays();
                case "3" -> stayHandler.listActiveStaysForDate();
                case "0" -> back = true;
                default -> out.println("Choix invalide, merci de réessayer.");
            }
        }
    }

    private void handleBedManagementMenu() {
        boolean back = false;
        while (!back) {
            out.println("=== Gestion des lits ===");
            out.println("1. Créer un lit");
            out.println("2. Changer le statut d'un lit");
            out.println("3. Supprimer un lit");
            out.println("4. Lister les lits");
            out.println("0. Retour au menu principal");
            out.print("Votre choix : ");
            out.flush();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> bedHandler.handleCreateBed();
                case "2" -> bedHandler.handleUpdateBedStatus();
                case "3" -> bedHandler.handleDeleteBed();
                case "4" -> bedHandler.listBeds();
                case "0" -> back = true;
                default -> out.println("Choix invalide, merci de réessayer.");
            }
        }
    }
}