package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.domain.model.Bed;
import com.webisbrian.hospital_bed_planner.domain.model.BedStatus;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryBedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdatedBedStatusUseCaseTest {
    private InMemoryBedRepository bedRepository;
    private UpdateBedStatusUseCase updateBedStatusUseCase;

    @BeforeEach
    void setUpPerTest() {
        bedRepository = new InMemoryBedRepository();
        updateBedStatusUseCase = new UpdateBedStatusUseCase(bedRepository);
    }

    @Test
    void updateStatus_shouldUpdateBedStatus_whenBedExistsAndStatusIsValid() {
        // Arrange
        String bedId = "BED-001";
        Bed bed = new Bed(
                bedId,
                "ROOM-101",
                "A01-1",
                BedStatus.AVAILABLE,
                false
        );
        bedRepository.save(bed);

        // Act
        Bed updated = updateBedStatusUseCase.updateStatus(bedId, BedStatus.CLEANING);

        // Assert
        assertNotNull(updated);
        assertEquals(BedStatus.CLEANING, updated.getStatus());

        // Vérifie la persistance
        Bed fromRepo = bedRepository.findById(bedId).orElseThrow();
        assertEquals(BedStatus.CLEANING, fromRepo.getStatus());
    }

    @Test
    void updateStatus_shouldThrowException_whenBedDoesNotExist() {
        // Arrange
        String bedId = "UNKNOWN";

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> updateBedStatusUseCase.updateStatus(bedId, BedStatus.AVAILABLE)
        );

        assertTrue(ex.getMessage().contains("Bed with id " + bedId + " does not exist"));
    }

    @Test
    void updateStatus_shouldThrowException_whenNewStatusIsNull() {
        // Arrange
        String bedId = "BED-001";
        Bed bed = new Bed(
                bedId,
                "ROOM-101",
                "A01-1",
                BedStatus.AVAILABLE,
                false
        );
        bedRepository.save(bed);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> updateBedStatusUseCase.updateStatus(bedId, null)
        );

        assertTrue(ex.getMessage().contains("New bed status cannot be null"));
    }
}