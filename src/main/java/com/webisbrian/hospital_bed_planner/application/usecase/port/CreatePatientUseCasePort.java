package com.webisbrian.hospital_bed_planner.application.usecase.port;

import com.webisbrian.hospital_bed_planner.domain.model.Patient;
import com.webisbrian.hospital_bed_planner.domain.model.Sex;

import java.time.LocalDate;

public interface CreatePatientUseCasePort {
    Patient createPatient(String id,
                          String firstName,
                          String lastName,
                          LocalDate birthDate,
                          Sex sex,
                          boolean pmr,
                          boolean isolationRequired,
                          String phone,
                          String notes);
}