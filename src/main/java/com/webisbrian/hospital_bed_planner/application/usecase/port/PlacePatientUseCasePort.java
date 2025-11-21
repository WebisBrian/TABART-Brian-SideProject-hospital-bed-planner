package com.webisbrian.hospital_bed_planner.application.usecase.port;

import com.webisbrian.hospital_bed_planner.domain.model.HospitalStay;
import com.webisbrian.hospital_bed_planner.domain.model.StayType;

import java.time.LocalDate;
import java.util.Optional;

public interface PlacePatientUseCasePort {
    Optional<HospitalStay> placePatient(String stayId,
                                        String patientId,
                                        LocalDate admissionDate,
                                        LocalDate dischargePlanned,
                                        StayType stayType);
}