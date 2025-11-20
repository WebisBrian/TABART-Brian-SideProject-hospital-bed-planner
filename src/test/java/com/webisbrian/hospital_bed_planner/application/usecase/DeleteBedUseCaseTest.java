package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.domain.model.Bed;
import com.webisbrian.hospital_bed_planner.domain.model.BedStatus;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryBedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeleteBedUseCaseTest {

    private InMemoryBedRepository bedRepository;
    private DeleteBedUseCase deleteBedUseCase;

    @BeforeEach
    void setUpPerTest() {
        bedRepository = new InMemoryBedRepository();
        deleteBedUseCase = new DeleteBedUseCase(bedRepository);
    }

    @Test
    void deleteBed_shouldRemoveBed_whenBedExists() {
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
        assertTrue(bedRepository.findById(bedId).isPresent());

        // Act
        deleteBedUseCase.deleteBed(bedId);

        // Assert
        assertTrue(bedRepository.findById(bedId).isEmpty());
    }

    @Test
    void deleteBed_shouldThrowException_whenBedDoesNotExist() {
        // Arrange
        String bedId = "UNKNOWN";

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> deleteBedUseCase.deleteBed(bedId)
        );

        assertTrue(ex.getMessage().contains("Bed with id " + bedId + " does not exist"));
    }

    @Test
    void deleteBed_shouldThrowException_whenIdIsBlank() {
        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> deleteBedUseCase.deleteBed("   ")
        );

        assertTrue(ex.getMessage().contains("Bed id cannot be null or blank"));
    }
}
