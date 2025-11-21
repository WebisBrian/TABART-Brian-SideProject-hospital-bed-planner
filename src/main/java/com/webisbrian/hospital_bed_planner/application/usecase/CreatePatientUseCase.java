package com.webisbrian.hospital_bed_planner.application.usecase;

import com.webisbrian.hospital_bed_planner.application.usecase.port.CreatePatientUseCasePort;
import com.webisbrian.hospital_bed_planner.domain.model.Patient;
import com.webisbrian.hospital_bed_planner.domain.model.Sex;
import com.webisbrian.hospital_bed_planner.domain.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class CreatePatientUseCase implements CreatePatientUseCasePort {

    private static final Logger logger = LoggerFactory.getLogger(CreatePatientUseCase.class);

    private final PatientRepository patientRepository;

    public CreatePatientUseCase(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient createPatient(String id,
                                 String firstname,
                                 String lastname,
                                 LocalDate birthDate,
                                 Sex sex,
                                 boolean pmr,
                                 boolean isolationRequired,
                                 String phoneNumber,
                                 String notes) {

        logger.info("Creating patient with id: {}", id);

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Patient id cannot be null or blank");
        }

        if (patientRepository.existsById(id)) {
            logger.warn("Attempt to create patient with existing id={}", id);
            throw new IllegalArgumentException("Patient with id " + id + " already exists");
        }

        if (firstname == null || firstname.isBlank()) {
            throw new IllegalArgumentException("Patient firstname cannot be null or blank");
        }

        if (lastname == null || lastname.isBlank()) {
            throw new IllegalArgumentException("Patient lastname cannot be null or blank");
        }

        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Patient birthdate cannot be null or in the future");
        }

        if (sex == null) {
            throw new IllegalArgumentException("Patient sex cannot be null");
        }

        Patient patient = new Patient(id,
                firstname,
                lastname,
                birthDate,
                sex,
                pmr,
                isolationRequired,
                phoneNumber,
                notes);

        patientRepository.save(patient);

        logger.info("Patient created successfully id={}", id);
        return patient;
    }
}


