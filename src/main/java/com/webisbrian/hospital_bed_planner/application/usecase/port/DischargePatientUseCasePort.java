package com.webisbrian.hospital_bed_planner.application.usecase.port;

import com.webisbrian.hospital_bed_planner.domain.model.HospitalStay;

import java.time.LocalDate;

public interface DischargePatientUseCasePort {
    HospitalStay discharge(String stayId, LocalDate dischargeDate);
}