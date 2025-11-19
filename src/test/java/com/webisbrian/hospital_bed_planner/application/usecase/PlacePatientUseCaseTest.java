package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.domain.model.*;
import com.webisbrian.hospital_bed_planner.domain.service.PlacementService;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryBedRepository;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryHospitalStayRepository;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryPatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PlacePatientUseCaseTest {

    private InMemoryPatientRepository patientRepository;
    private InMemoryBedRepository bedRepository;
    private InMemoryHospitalStayRepository hospitalStayRepository;
    private PlacementService placementService;
    private PlacePatientUseCase placePatientUseCase;

    @BeforeEach
    void setUpPerTest() {
        patientRepository = new InMemoryPatientRepository();
        bedRepository = new InMemoryBedRepository();
        hospitalStayRepository = new InMemoryHospitalStayRepository();
        placementService = new PlacementService(patientRepository, bedRepository, hospitalStayRepository);
        placePatientUseCase = new PlacePatientUseCase(placementService, hospitalStayRepository);
    }

    @Test
    void placePatient_shouldCreateAndPersistStay_whenBedIsAvailable() {
        // Arrange
        String stayId = "STAY-1";
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

        // Un lit disponible, aucun séjour actif
        Bed bed = new Bed("BED-1", "ROOM-1", "A01-1", BedStatus.AVAILABLE, false);
        bedRepository.save(bed);

        LocalDate admissionDate = LocalDate.of(2025, 1, 15);
        LocalDate plannedDischargeDate = LocalDate.of(2025, 1, 20);

        // Act
        Optional<HospitalStay> result = placePatientUseCase.placePatient(
                stayId,
                patientId,
                admissionDate,
                plannedDischargeDate,
                StayType.WEEK
        );

        // Assert
        assertTrue(result.isPresent(), "Un séjour devrait être créé");
        HospitalStay stay = result.get();

        assertEquals(stayId, stay.getId());
        assertEquals(patientId, stay.getPatientId());
        assertEquals("BED-1", stay.getBedId());
        assertEquals(StayType.WEEK, stay.getStayType());
        assertEquals(admissionDate, stay.getAdmissionDate());
        assertEquals(plannedDischargeDate, stay.getDischargeDatePlanned());
        assertNull(stay.getDischargeDateEffective(), "La date de sortie effective doit être null à la création");

        // Vérifier que le séjour est bien persisté
        assertTrue(hospitalStayRepository.findById(stayId).isPresent(),
                "Le séjour devrait être stocké dans le repository");
    }

    @Test
    void placePatient_shouldReturnEmpty_whenNoBedIsAvailable() {
        // Arrange
        String stayId = "STAY-1";
        String patientId = "P-001";

        Patient patient = new Patient(
                patientId,
                "Claire",
                "Dupont",
                LocalDate.of(1990, 3, 15),
                Sex.FEMALE,
                false,
                false,
                "0700000000",
                null
        );
        patientRepository.save(patient);

        // Un seul lit disponible par statut...
        Bed bed = new Bed("BED-1", "ROOM-1", "A01-1", BedStatus.AVAILABLE, false);
        bedRepository.save(bed);

        LocalDate date = LocalDate.of(2025, 1, 15);

        // ... mais occupé par un autre patient via un séjour actif
        String occupantId = "P-002";
        Patient occupant = new Patient(
                occupantId,
                "Bob",
                "Durand",
                LocalDate.of(1975, 5, 10),
                Sex.MALE,
                false,
                false,
                "0600000000",
                null
        );
        patientRepository.save(occupant);

        HospitalStay stay = new HospitalStay(
                "STAY-OCCUPANT",
                occupantId,
                "BED-1",
                StayType.WEEK,
                LocalDate.of(2025, 1, 10),   // avant la date
                LocalDate.of(2025, 1, 20),   // après la date
                null                         // pas encore sorti => séjour actif
        );
        hospitalStayRepository.save(stay);

        // Act
        Optional<HospitalStay> result = placePatientUseCase.placePatient(
                stayId,
                patientId,
                date,
                LocalDate.of(2025, 1, 20),
                StayType.WEEK
        );

        // Assert
        assertTrue(result.isEmpty(), "Aucun séjour ne doit être créé s'il n'y a aucun lit disponible");
        assertTrue(hospitalStayRepository.findById(stayId).isEmpty(),
                "Aucun séjour avec ce stayId ne doit être persisté");
    }

    @Test
    void placePatient_shouldThrowException_whenStayIdIsBlank() {
        // Arrange
        String blankStayId = "   ";
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

        LocalDate admissionDate = LocalDate.of(2025, 1, 15);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> placePatientUseCase.placePatient(
                        blankStayId,
                        patientId,
                        admissionDate,
                        null,
                        StayType.WEEK
                )
        );

        assertTrue(ex.getMessage().contains("Stay id cannot be null or blank"));
    }
}
