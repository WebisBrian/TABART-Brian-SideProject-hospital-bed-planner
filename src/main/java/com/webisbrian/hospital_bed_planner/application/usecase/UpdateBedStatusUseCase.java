package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.domain.model.Bed;
import com.webisbrian.hospital_bed_planner.domain.model.BedStatus;
import com.webisbrian.hospital_bed_planner.domain.repository.BedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cas d'usage permettant de mettre à jour le statut d'un lit.
 */
public class UpdateBedStatusUseCase {

    private static final Logger logger = LoggerFactory.getLogger(UpdateBedStatusUseCase.class);

    private final BedRepository bedRepository;

    public UpdateBedStatusUseCase(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    /**
     * Met à jour le statut d'un lit existant.
     *
     * @param bedId     identifiant du lit
     * @param newStatus nouveau statut
     * @return le lit mis à jour
     */
    public Bed updateStatus(String bedId, BedStatus newStatus) {
        logger.info("Updating status for bed with id={} to {}", bedId, newStatus);

        if (bedId == null || bedId.isBlank()) {
            throw new IllegalArgumentException("Bed id cannot be null or blank");
        }

        if (newStatus == null) {
            throw new IllegalArgumentException("New bed status cannot be null");
        }

        Bed existing = bedRepository.findById(bedId)
                .orElseThrow(() -> {
                    logger.warn("Attempt to update status of non-existing bed id={}", bedId);
                    return new IllegalArgumentException("Bed with id " + bedId + " does not exist");
                });

        Bed updated = new Bed(
                existing.getId(),
                existing.getRoomId(),
                existing.getCode(),
                newStatus,
                existing.isIsolationCapable()
        );

        bedRepository.save(updated);

        logger.info("Bed status updated successfully id={} newStatus={}", bedId, newStatus);
        return updated;
    }
}
