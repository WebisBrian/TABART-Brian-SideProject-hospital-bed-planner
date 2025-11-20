package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.domain.repository.BedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cas d'usage permettant de supprimer un lit.
 *
 * Règles actuelles :
 * - l'identifiant ne peut pas être null ou vide,
 * - le lit doit exister, sinon une exception est levée.
 *
 */
public class DeleteBedUseCase {

    private static final Logger logger = LoggerFactory.getLogger(DeleteBedUseCase.class);

    private final BedRepository bedRepository;

    public DeleteBedUseCase(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    /**
     * Supprime un lit par son identifiant.
     *
     * @param bedId identifiant du lit
     */
    public void deleteBed(String bedId) {
        if (bedId == null || bedId.isBlank()) {
            throw new IllegalArgumentException("Bed id cannot be null or blank");
        }

        var existing = bedRepository.findById(bedId);
        if (existing.isEmpty()) {
            logger.warn("Attempt to delete non-existing bed id={}", bedId);
            throw new IllegalArgumentException("Bed with id " + bedId + " does not exist");
        }

        logger.info("Deleting bed id={}", bedId);
        bedRepository.deleteById(bedId);
        logger.info("Bed deleted successfully id={}", bedId);
    }
}
