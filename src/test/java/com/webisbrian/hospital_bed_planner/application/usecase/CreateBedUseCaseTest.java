package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.domain.model.Bed;
import com.webisbrian.hospital_bed_planner.domain.model.BedStatus;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryBedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateBedUseCaseTest {
    private InMemoryBedRepository bedRepository;
    private CreateBedUseCase createBedUseCase;

    @BeforeEach
    void setUpPerTest() {
        bedRepository = new InMemoryBedRepository();
        createBedUseCase = new CreateBedUseCase(bedRepository);
    }

    @Test
    void createBed_shouldCreateAndPersistBed_whenDataIsValid() {
        // Arrange
        String id = "BED-001";
        String roomId = "ROOM-101";
        String code = "A01-1";
        BedStatus status = BedStatus.AVAILABLE;
        boolean isolationCapable = false;

        // Act
        Bed bed = createBedUseCase.createBed(
                id,
                roomId,
                code,
                status,
                isolationCapable
        );

        // Assert
        assertNotNull(bed);
        assertEquals(id, bed.getId());
        assertEquals(roomId, bed.getRoomId());
        assertEquals(code, bed.getCode());
        assertEquals(status, bed.getStatus());
        assertEquals(isolationCapable, bed.isIsolationCapable());

        assertTrue(bedRepository.findById(id).isPresent());
    }

    @Test
    void createBed_shouldThrowException_whenIdAlreadyExists() {
        // Arrange
        String id = "BED-001";
        Bed existing = new Bed(
                id,
                "ROOM-101",
                "A01-1",
                BedStatus.AVAILABLE,
                false
        );
        bedRepository.save(existing);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> createBedUseCase.createBed(
                        id,
                        "ROOM-102",
                        "A02-1",
                        BedStatus.AVAILABLE,
                        false
                )
        );

        assertTrue(ex.getMessage().contains("Bed with id " + id + " already exists"));
    }

    @Test
    void createBed_shouldDefaultStatusToAvailable_whenStatusIsNull() {
        // Arrange
        String id = "BED-002";

        // Act
        Bed bed = createBedUseCase.createBed(
                id,
                "ROOM-201",
                "B01-1",
                null,       // status null -> doit devenir AVAILABLE
                true
        );

        // Assert
        assertNotNull(bed);
        assertEquals(BedStatus.AVAILABLE, bed.getStatus());
    }

}