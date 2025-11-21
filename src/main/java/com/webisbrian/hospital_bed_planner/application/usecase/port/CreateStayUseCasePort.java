package com.webisbrian.hospital_bed_planner.application.usecase.port;

import com.webisbrian.hospital_bed_planner.domain.model.HospitalStay;
import com.webisbrian.hospital_bed_planner.domain.model.StayType;

import java.time.LocalDate;

public interface CreateStayUseCasePort {
    HospitalStay createStay(String stayId,
                            String patientId,
                            String bedId,
                            LocalDate admissionDate,
                            LocalDate dischargeDatePlanned,
                            StayType stayType);
}