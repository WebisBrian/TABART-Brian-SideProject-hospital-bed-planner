package com.webisbrian.hospital_bed_planner.application.usecase.port;

import com.webisbrian.hospital_bed_planner.domain.model.Bed;
import com.webisbrian.hospital_bed_planner.domain.model.BedStatus;

public interface CreateBedUseCasePort {
    Bed createBed(String id, String roomId, String code, BedStatus status, boolean isolationCapable);
}