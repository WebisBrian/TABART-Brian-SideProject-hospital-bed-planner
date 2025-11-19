package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.domain.model.*;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryBedRepository;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryHospitalStayRepository;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryPatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CreateStayUseCaseTest {

    private InMemoryHospitalStayRepository hospitalStayRepository;
    private InMemoryPatientRepository patientRepository;
    private InMemoryBedRepository bedRepository;
    private CreateStayUseCase createStayUseCase;

    @BeforeEach
    void setUpPerTest() {
        hospitalStayRepository = new InMemoryHospitalStayRepository();
        patientRepository = new InMemoryPatientRepository();
        bedRepository = new InMemoryBedRepository();
        createStayUseCase = new CreateStayUseCase(hospitalStayRepository, patientRepository, bedRepository);
    }

    @Test
    void createStay_shouldCreateAndPersistStay_whenDataIsValid() {
        // Arrange
        String stayId = "STAY-1";
        String patientId = "P-001";
        String bedId = "BED-1";

        Patient patient = new Patient(
                patientId,
                "Alice",
                "Martin",
                LocalDate.of(1980, 1, 1),
                Sex.FEMALE,
                false,
                false,
                "0102030405",
                null
        );
        patientRepository.save(patient);

        Bed bed = new Bed(
                bedId,
                "ROOM-1",
                "A01-1",
                BedStatus.AVAILABLE,
                false
        );
        bedRepository.save(bed);

        LocalDate admissionDate = LocalDate.of(2025, 1, 15);
        LocalDate plannedDischargeDate = LocalDate.of(2025, 1, 20);

        // Act
        HospitalStay stay = createStayUseCase.createStay(
                stayId,
                patientId,
                bedId,
                admissionDate,
                plannedDischargeDate,
                StayType.WEEK
        );

        // Assert
        assertNotNull(stay);
        assertEquals(stayId, stay.getId());
        assertEquals(patientId, stay.getPatientId());
        assertEquals(bedId, stay.getBedId());
        assertEquals(StayType.WEEK, stay.getStayType());
        assertEquals(admissionDate, stay.getAdmissionDate());
        assertEquals(plannedDischargeDate, stay.getDischargeDatePlanned());
        assertNull(stay.getDischargeDateEffective(), "La date de sortie effective doit être null à la création");

        assertTrue(hospitalStayRepository.findById(stayId).isPresent(),
                "Le séjour devrait être enregistré dans le repository");
    }

    @Test
    void createStay_shouldThrowException_whenPatientDoesNotExist() {
        // Arrange
        String stayId = "STAY-1";
        String unknownPatientId = "UNKNOWN";
        String bedId = "BED-1";

        Bed bed = new Bed(
                bedId,
                "ROOM-1",
                "A01-1",
                BedStatus.AVAILABLE,
                false
        );
        bedRepository.save(bed);

        LocalDate admissionDate = LocalDate.of(2025, 1, 15);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> createStayUseCase.createStay(
                        stayId,
                        unknownPatientId,
                        bedId,
                        admissionDate,
                        null,
                        StayType.WEEK
                )
        );

        assertTrue(ex.getMessage().contains("Patient with id " + unknownPatientId + " does not exist"));
    }

    @Test
    void createStay_shouldThrowException_whenBedDoesNotExist() {
        // Arrange
        String stayId = "STAY-1";
        String patientId = "P-001";
        String unknownBedId = "UNKNOWN-BED";

        Patient patient = new Patient(
                patientId,
                "Alice",
                "Martin",
                LocalDate.of(1980, 1, 1),
                Sex.FEMALE,
                false,
                false,
                "0102030405",
                null
        );
        patientRepository.save(patient);

        LocalDate admissionDate = LocalDate.of(2025, 1, 15);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> createStayUseCase.createStay(
                        stayId,
                        patientId,
                        unknownBedId,
                        admissionDate,
                        null,
                        StayType.WEEK
                )
        );

        assertTrue(ex.getMessage().contains("Bed with id " + unknownBedId + " does not exist"));
    }

    @Test
    void createStay_shouldThrowException_whenPlannedDischargeDateBeforeAdmissionDate() {
        // Arrange
        String stayId = "STAY-1";
        String patientId = "P-001";
        String bedId = "BED-1";

        Patient patient = new Patient(
                patientId,
                "Alice",
                "Martin",
                LocalDate.of(1980, 1, 1),
                Sex.FEMALE,
                false,
                false,
                "0102030405",
                null
        );
        patientRepository.save(patient);

        Bed bed = new Bed(
                bedId,
                "ROOM-1",
                "A01-1",
                BedStatus.AVAILABLE,
                false
        );
        bedRepository.save(bed);

        LocalDate admissionDate = LocalDate.of(2025, 1, 15);
        LocalDate invalidPlannedDischargeDate = LocalDate.of(2025, 1, 10); // avant l'admission

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> createStayUseCase.createStay(
                        stayId,
                        patientId,
                        bedId,
                        admissionDate,
                        invalidPlannedDischargeDate,
                        StayType.WEEK
                )
        );

        assertTrue(ex.getMessage().contains("Planned discharge date cannot be before admission date"));
    }
}
