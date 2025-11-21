package com.webisbrian.hospital_bed_planner.console;

import com.webisbrian.hospital_bed_planner.application.usecase.port.CreateBedUseCasePort;
import com.webisbrian.hospital_bed_planner.application.usecase.port.DeleteBedUseCasePort;
import com.webisbrian.hospital_bed_planner.application.usecase.port.UpdateBedStatusUseCasePort;
import com.webisbrian.hospital_bed_planner.domain.model.Bed;
import com.webisbrian.hospital_bed_planner.domain.model.BedStatus;
import com.webisbrian.hospital_bed_planner.domain.repository.BedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;

/**
 * Handler pour la gestion des lits : création, mise à jour de statut, suppression, listing.
 *
 * Les messages de logs n'exposent que l'ID des lits et messages non sensibles.
 */
public class BedMenuHandler {
    private static final Logger logger = LoggerFactory.getLogger(BedMenuHandler.class);

    private final CreateBedUseCasePort createBedUseCase;
    private final UpdateBedStatusUseCasePort updateBedStatusUseCase;
    private final DeleteBedUseCasePort deleteBedUseCase;
    private final BedRepository bedRepository;
    private final ConsoleInputHandler input;
    private final PrintWriter out;

    public BedMenuHandler(CreateBedUseCasePort createBedUseCase,
                          UpdateBedStatusUseCasePort updateBedStatusUseCase,
                          DeleteBedUseCasePort deleteBedUseCase,
                          BedRepository bedRepository,
                          ConsoleInputHandler input,
                          PrintWriter out) {
        this.createBedUseCase = createBedUseCase;
        this.updateBedStatusUseCase = updateBedStatusUseCase;
        this.deleteBedUseCase = deleteBedUseCase;
        this.bedRepository = bedRepository;
        this.input = input;
        this.out = out;
    }

    public void handleCreateBed() {
        out.println("--- Création d'un lit ---");
        String id = input.readLine("ID du lit");
        String roomId = input.readLine("ID de la chambre");
        String code = input.readLine("Code du lit (ex: A01-1)");
        String statusInput = input.readLine("Statut (AVAILABLE/OCCUPIED/CLEANING/OUT_OF_ORDER, ENTER pour AVAILABLE)");
        BedStatus status = null;
        if (!statusInput.isBlank()) {
            try {
                status = BedStatus.valueOf(statusInput.toUpperCase());
            } catch (IllegalArgumentException e) {
                out.println("Statut invalide, le lit sera créé en statut AVAILABLE.");
            }
        }
        boolean isolationCapable = input.readYesNo("Lit compatible isolement ?");
        try {
            logger.info("Console: creating bed id={}", id);
            Bed bed = createBedUseCase.createBed(id, roomId, code, status, isolationCapable);
            out.println("Lit créé avec succès : " + bed.getId() + " (" + bed.getCode() + ")");
        } catch (IllegalArgumentException e) {
            logger.error("Erreur création lit id={}: {}", id, e.getMessage());
            logger.debug("Stacktrace création lit id=" + id, e);
            out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue création lit id={}: {}", id, e.getMessage());
            logger.debug("Stacktrace création lit id=" + id, e);
            out.println("Erreur inattendue : " + e.getMessage());
        }
    }

    public void handleUpdateBedStatus() {
        out.println("--- Changer le statut d'un lit ---");
        String bedId = input.readLine("ID du lit");
        String statusInput = input.readLine("Nouveau statut (AVAILABLE/OCCUPIED/CLEANING/OUT_OF_ORDER)");
        BedStatus newStatus;
        try {
            newStatus = BedStatus.valueOf(statusInput.toUpperCase());
        } catch (IllegalArgumentException e) {
            out.println("Statut invalide.");
            return;
        }
        try {
            logger.info("Console: updating bed status id={} to {}", bedId, newStatus);
            Bed updated = updateBedStatusUseCase.updateStatus(bedId, newStatus);
            out.println("Statut mis à jour : " + updated.getId() + " -> " + updated.getStatus());
        } catch (IllegalArgumentException e) {
            logger.error("Statut invalide pour le lit id={}: {}", bedId, e.getMessage());
            logger.debug("Stacktrace update status bed id=" + bedId, e);
            out.println("Statut invalide.");
        } catch (Exception e) {
            logger.error("Erreur inattendue update statut lit id={}: {}", bedId, e.getMessage());
            logger.debug("Stacktrace update status bed id=" + bedId, e);
            out.println("Erreur : " + e.getMessage());
        }
    }

    public void handleDeleteBed() {
        out.println("--- Supprimer un lit ---");
        String bedId = input.readLine("ID du lit");
        try {
            logger.info("Console: deleting bed id={}", bedId);
            deleteBedUseCase.deleteBed(bedId);
            out.println("Lit supprimé avec succès.");
        } catch (IllegalArgumentException e) {
            logger.error("Erreur suppression lit id={}: {}", bedId, e.getMessage());
            logger.debug("Stacktrace delete bed id=" + bedId, e);
            out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue suppression lit id={}: {}", bedId, e.getMessage());
            logger.debug("Stacktrace delete bed id=" + bedId, e);
            out.println("Erreur : " + e.getMessage());
        }
    }

    public void listBeds() {
        var beds = bedRepository.findAll();
        if (beds.isEmpty()) {
            out.println("Aucun lit enregistré.");
            return;
        }
        out.println("--- Liste des lits ---");
        for (Bed bed : beds) {
            out.println(
                    "- " + bed.getId()
                            + " | chambre=" + bed.getRoomId()
                            + " | code=" + bed.getCode()
                            + " | statut=" + bed.getStatus()
                            + " | isolement=" + (bed.isIsolationCapable() ? "oui" : "non")
            );
        }
    }
}
