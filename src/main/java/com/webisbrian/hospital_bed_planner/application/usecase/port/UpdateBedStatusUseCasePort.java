package com.webisbrian.hospital_bed_planner.application.usecase.port;

import com.webisbrian.hospital_bed_planner.domain.model.Bed;
import com.webisbrian.hospital_bed_planner.domain.model.BedStatus;

public interface UpdateBedStatusUseCasePort {
    Bed updateStatus(String bedId, BedStatus status);
}
