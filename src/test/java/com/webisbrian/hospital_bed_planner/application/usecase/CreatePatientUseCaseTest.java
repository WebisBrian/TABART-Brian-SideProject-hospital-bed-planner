package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.domain.model.Patient;
import com.webisbrian.hospital_bed_planner.domain.model.Sex;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryPatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CreatePatientUseCaseTest {
    private InMemoryPatientRepository patientRepository;
    private CreatePatientUseCase createPatientUseCase;

    @BeforeEach
    void setUpPerTest() {
        patientRepository = new InMemoryPatientRepository();
        createPatientUseCase = new CreatePatientUseCase(patientRepository);
    }

    @Test
    void createPatient_shouldCreateAndPersistPatient_whenDataIsValid() {
        // Arrange
        String id = "P-001";

        // Act
        Patient patient = createPatientUseCase.createPatient(
                id,
                "Alice",
                "Martin",
                LocalDate.of(1980, 1, 1),
                Sex.FEMALE,
                false,
                false,
                "0102030405",
                "Notes"
        );

        // Assert
        assertNotNull(patient);
        assertEquals(id, patient.getId());
        assertEquals("Alice", patient.getFirstName());
        assertEquals("Martin", patient.getLastName());
        assertTrue(patientRepository.existsById(id));
    }

    @Test
    void createPatient_shouldThrowException_whenIdAlreadyExists() {
        // Arrange
        String id = "P-001";
        Patient existing = new Patient(
                id,
                "Bob",
                "Durand",
                LocalDate.of(1975, 5, 10),
                Sex.MALE,
                false,
                false,
                "0102030405",
                null
        );
        patientRepository.save(existing);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> createPatientUseCase.createPatient(
                        id,
                        "Alice",
                        "Martin",
                        LocalDate.of(1980, 1, 1),
                        Sex.FEMALE,
                        false,
                        false,
                        "0102030405",
                        null
                )
        );

        assertTrue(ex.getMessage().contains("Patient with id P-001 already exists"));
    }

    @Test
    void createPatient_shouldThrowException_whenFirstnameIsBlank() {
        // Arrange
        String id = "P-002";

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> createPatientUseCase.createPatient(
                        id,
                        "  ", // firstname vide/blanc
                        "Martin",
                        LocalDate.of(1980, 1, 1),
                        Sex.FEMALE,
                        false,
                        false,
                        "0102030405",
                        null
                )
        );

        assertTrue(ex.getMessage().contains("Patient firstname cannot be null or blank"));
    }
}