package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.domain.model.Bed;
import com.webisbrian.hospital_bed_planner.domain.model.BedStatus;
import com.webisbrian.hospital_bed_planner.domain.repository.BedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateBedUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateBedUseCase.class);

    private final BedRepository bedRepository;

    public CreateBedUseCase(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    public Bed createBed(String id,
                         String roomId,
                         String code,
                         BedStatus bedstatus,
                         boolean isolationCapable) {
        logger.info("Creating bed with id: {}", id);

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Bed id cannot be null or blank");
        }

        if (bedRepository.findById(id).isPresent()) {
            logger.warn("Attempt to create bed with existing id={}", id);
            throw new IllegalArgumentException("Bed with id " + id + " already exists");
        }

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id cannot be null or blank");
        }

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code cannot be null or blank");
        }

        if (bedstatus == null) {
            bedstatus = BedStatus.AVAILABLE;
        }

        Bed bed = new Bed(id,
                roomId,
                code,
                bedstatus,
                isolationCapable
        );

        bedRepository.save(bed);

        logger.info("Bed created successfully id={}", id);
        return bed;
    }
}
