package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.domain.model.HospitalStay;
import com.webisbrian.hospital_bed_planner.domain.model.StayType;
import com.webisbrian.hospital_bed_planner.infrastructure.inmemory.InMemoryHospitalStayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DischargePatientUseCaseTest {

    private InMemoryHospitalStayRepository hospitalStayRepository;
    private DischargePatientUseCase dischargePatientUseCase;

    @BeforeEach
    void setUpPerTest() {
        hospitalStayRepository = new InMemoryHospitalStayRepository();
        dischargePatientUseCase = new DischargePatientUseCase(hospitalStayRepository);
    }

    @Test
    void discharge_shouldUpdateEffectiveDischargeDate_whenStayExistsAndIsOpen() {
        // Arrange
        String stayId = "STAY-1";
        HospitalStay stay = new HospitalStay(
                stayId,
                "P-001",
                "BED-1",
                StayType.WEEK,
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 20),
                null // pas encore de sortie effective
        );
        hospitalStayRepository.save(stay);

        LocalDate dischargeDate = LocalDate.of(2025, 1, 18);

        // Act
        HospitalStay updated = dischargePatientUseCase.discharge(stayId, dischargeDate);

        // Assert
        assertEquals(dischargeDate, updated.getDischargeDateEffective());
        // On vérifie aussi que le repo contient bien la version mise à jour
        assertTrue(hospitalStayRepository.findById(stayId).isPresent());
        assertEquals(dischargeDate,
                hospitalStayRepository.findById(stayId).get().getDischargeDateEffective());
    }

    @Test
    void discharge_shouldThrowException_whenStayDoesNotExist() {
        // Arrange
        String unknownStayId = "UNKNOWN";
        LocalDate dischargeDate = LocalDate.of(2025, 1, 18);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> dischargePatientUseCase.discharge(unknownStayId, dischargeDate)
        );

        assertTrue(ex.getMessage().contains("does not exist"));
    }

    @Test
    void discharge_shouldThrowException_whenStayAlreadyDischarged() {
        // Arrange
        String stayId = "STAY-2";
        HospitalStay alreadyDischargedStay = new HospitalStay(
                stayId,
                "P-002",
                "BED-2",
                StayType.WEEK,
                LocalDate.of(2025, 1, 5),
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 10) // déjà sorti
        );
        hospitalStayRepository.save(alreadyDischargedStay);

        LocalDate dischargeDate = LocalDate.of(2025, 1, 12);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> dischargePatientUseCase.discharge(stayId, dischargeDate)
        );

        assertTrue(ex.getMessage().contains("already discharged"));
    }

    @Test
    void discharge_shouldThrowException_whenDischargeDateIsBeforeAdmissionDate() {
        // Arrange
        String stayId = "STAY-3";
        HospitalStay stay = new HospitalStay(
                stayId,
                "P-003",
                "BED-3",
                StayType.WEEK,
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 20),
                null
        );
        hospitalStayRepository.save(stay);

        LocalDate invalidDischargeDate = LocalDate.of(2025, 1, 5); // avant l'admission

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> dischargePatientUseCase.discharge(stayId, invalidDischargeDate)
        );

        assertTrue(ex.getMessage().contains("cannot be before admission date"));
    }
}
