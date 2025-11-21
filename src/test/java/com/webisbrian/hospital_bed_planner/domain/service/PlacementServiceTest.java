package com.webisbrian.hospital_bed_planner.domain.service;

import com.webisbrian.hospital_bed_planner.domain.model.*;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryBedRepository;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryHospitalStayRepository;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryPatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PlacementServiceTest {

    private InMemoryPatientRepository patientRepository;
    private InMemoryBedRepository bedRepository;
    private InMemoryHospitalStayRepository hospitalStayRepository;
    private PlacementService placementService;

    @BeforeEach
    void setUpPerTest() {
        patientRepository = new InMemoryPatientRepository();
        bedRepository = new InMemoryBedRepository();
        hospitalStayRepository = new InMemoryHospitalStayRepository();
        placementService = new PlacementService(patientRepository, bedRepository, hospitalStayRepository);
    }

    @Test
    void suggestBedForPatient_shouldReturnFirstAvailableBed_whenNoSpecialConstraints() {
        // Arrange
        String patientId = "P-001";
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

        Bed bed1 = new Bed("BED-1", "ROOM-1", "A01-1", BedStatus.AVAILABLE, false);
        Bed bed2 = new Bed("BED-2", "ROOM-1", "A02-1", BedStatus.AVAILABLE, false);
        bedRepository.save(bed1);
        bedRepository.save(bed2);

        LocalDate date = LocalDate.of(2025, 1, 15);

        // Act
        Optional<Bed> result = placementService.suggestBedForPatient(patientId, date);

        // Assert
        assertTrue(result.isPresent(), "Un lit devrait être proposé");
        assertEquals("BED-1", result.get().getId());
        assertEquals("A01-1", result.get().getCode());
    }

    @Test
    void suggestBedForPatient_shouldThrowException_whenPatientDoesNotExist() {
        // Arrange
        String unknownPatientId = "UNKNOWN";
        LocalDate date = LocalDate.of(2025, 1, 15);

        // Act + Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> placementService.suggestBedForPatient(unknownPatientId, date),
                "Une IllegalArgumentException devrait être levée si le patient n'existe pas"
        );
        assertTrue(exception.getMessage().contains("Patient with id " + unknownPatientId + " does not exist"));
    }

    @Test
    void suggestBedForPatient_shouldNotReturnOccupiedBed_whenStayIsActiveOnDate() {
        // Arrange
        String occupantId = "P-002";
        Patient occupant = new Patient(
                occupantId,
                "Bob",
                "Durand",
                LocalDate.of(1975, 5, 10),
                Sex.MALE,
                false,
                false,
                "0102030405",
                null
        );
        patientRepository.save(occupant);

        String targetPatientId = "P-001";
        Patient targetPatient = new Patient(
                targetPatientId,
                "Alice",
                "Martin",
                LocalDate.of(1980, 1, 1),
                Sex.FEMALE,
                false,
                false,
                "0102030405",
                null
        );
        patientRepository.save(targetPatient);

        Bed bed1 = new Bed("BED-1", "ROOM-1", "A01-1", BedStatus.AVAILABLE, false);
        Bed bed2 = new Bed("BED-2", "ROOM-1", "A02-1", BedStatus.AVAILABLE, false);
        bedRepository.save(bed1);
        bedRepository.save(bed2);

        LocalDate date = LocalDate.of(2025, 1, 15);

        HospitalStay hospitalStay = new HospitalStay(
                "STAY-1",
                occupantId,
                "BED-1",
                StayType.WEEK,
                LocalDate.of(2025, 1, 10),   // admission avant la date
                LocalDate.of(2025, 1, 20),   // sortie prévue après la date
                null                         // sortie effective non encore enregistrée => encore présent
        );
        hospitalStayRepository.save(hospitalStay);

        // Act
        Optional<Bed> result = placementService.suggestBedForPatient(targetPatientId, date);

        // Assert
        assertTrue(result.isPresent(), "Un lit devrait être proposé");
        assertEquals("BED-2", result.get().getId(), "Le lit occupé ne doit pas être proposé");
        assertEquals("A02-1", result.get().getCode());
    }

    @Test
    void suggestBedForPatient_shouldReturnEmpty_whenNoBedIsAvailable() {
        // Arrange
        String patientId = "P-001";
        Patient patient = new Patient(
                patientId,
                "Claire",
                "Dupont",
                LocalDate.of(1990, 3, 15),
                Sex.FEMALE,
                false,
                false,
                "0102030405",
                null
        );
        patientRepository.save(patient);

        Bed bed1 = new Bed("BED-1", "ROOM-1", "A01-1", BedStatus.OCCUPIED, false);
        Bed bed2 = new Bed("BED-2", "ROOM-1", "A02-1", BedStatus.AVAILABLE, false);
        bedRepository.save(bed1);
        bedRepository.save(bed2);

        LocalDate date = LocalDate.of(2025, 1, 15);

        // Séjour qui occupe BED-2, actif à la date
        String occupantId = "P-002";
        Patient occupant = new Patient(
                occupantId,
                "Bob",
                "Durand",
                LocalDate.of(1975, 5, 10),
                Sex.MALE,
                false,
                false,
                "0102030405",
                null
        );
        patientRepository.save(occupant);

        HospitalStay stay = new HospitalStay(
                "STAY-1",
                occupantId,
                "BED-2",
                StayType.WEEK,
                LocalDate.of(2025, 1, 10),   // admission avant la date
                LocalDate.of(2025, 1, 20),   // sortie prévue après la date
                null                         // sortie effective non encore fixée -> toujours présent
        );
        hospitalStayRepository.save(stay);

        // Act
        Optional<Bed> result = placementService.suggestBedForPatient(patientId, date);

        // Assert
        assertTrue(result.isEmpty(), "Aucun lit ne devrait être proposé quand tous les lits sont indisponibles");
    }

    @Test
    void suggestBedForPatient_shouldPreferIsolationCapableBed_whenPatientNeedsIsolation() {
        // Arrange
        String patientId = "P-ISO";
        Patient patient = new Patient(
                patientId,
                "Bob",
                "Durand",
                LocalDate.of(1975, 5, 10),
                Sex.MALE,
                false,
                true,   // needIsolation
                "0102030406",
                null
        );
        patientRepository.save(patient);

        // Lit normal disponible
        Bed bed1 = new Bed("BED-1", "ROOM-1", "A01-1", BedStatus.AVAILABLE, false);
        // Lit d'isolement disponible
        Bed bed2 = new Bed("BED-2", "ROOM-2", "A01-2", BedStatus.AVAILABLE, true);
        bedRepository.save(bed1);
        bedRepository.save(bed2);

        LocalDate date = LocalDate.of(2025, 1, 15);

        // Act
        Optional<Bed> result = placementService.suggestBedForPatient(patientId, date);

        // Assert
        assertTrue(result.isPresent(), "Un lit devrait être proposé pour un patient en isolement");
        Bed chosen = result.get();
        assertEquals("BED-2", chosen.getId(), "Le lit d'isolement devrait être choisi");
        assertTrue(chosen.isIsolationCapable());
    }

    @Test
    void suggestBedForPatient_shouldReturnEmpty_whenPatientNeedsIsolationAndNoIsolationCapableBedAvailable() {
        // Arrange
        String patientId = "P-ISO";
        Patient patient = new Patient(
                patientId,
                "Claire",
                "Dupont",
                LocalDate.of(1990, 3, 15),
                Sex.FEMALE,
                false,
                true,   // needIsolation
                "0102030407",
                null
        );
        patientRepository.save(patient);

        // Lits disponibles mais aucun compatible isolement
        Bed bed1 = new Bed("BED-1", "ROOM-1", "A01-1", BedStatus.AVAILABLE, false);
        Bed bed2 = new Bed("BED-2", "ROOM-2", "A02-1", BedStatus.AVAILABLE, false);
        bedRepository.save(bed1);
        bedRepository.save(bed2);

        LocalDate date = LocalDate.of(2025, 1, 15);

        // Act
        Optional<Bed> result = placementService.suggestBedForPatient(patientId, date);

        // Assert
        assertTrue(result.isEmpty(), "Aucun lit ne devrait être proposé sans lit d'isolement disponible");
    }
}