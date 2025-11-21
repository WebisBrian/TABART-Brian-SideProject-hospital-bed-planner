package com.webisbrian.hospital_bed_planner.application;

import com.webisbrian.hospital_bed_planner.application.usecase.*;
import com.webisbrian.hospital_bed_planner.application.usecase.port.*;
import com.webisbrian.hospital_bed_planner.domain.repository.*;
import com.webisbrian.hospital_bed_planner.domain.service.PlacementService;

/**
 * Conteneur d'application minimal : expose les repositories et les ports (use cases).
 * Le wiring des implémentations se fait depuis la composition root (ApplicationComposition).
 */
public class ApplicationContext {
    private final PatientRepository patientRepository;
    private final BedRepository bedRepository;
    private final HospitalStayRepository hospitalStayRepository;

    private final PlacementService placementService;

    private final CreatePatientUseCasePort createPatientUseCase;
    private final CreateStayUseCasePort createStayUseCase;
    private final PlacePatientUseCasePort placePatientUseCase;
    private final DischargePatientUseCasePort dischargePatientUseCase;
    private final CreateBedUseCasePort createBedUseCase;
    private final UpdateBedStatusUseCasePort updateBedStatusUseCase;
    private final DeleteBedUseCasePort deleteBedUseCase;

    /**
     * Crée le contexte d'application à partir des repositories fournis.
     * Le constructeur initialise les services et use-cases qui seront exposés via les getters.
     *
     * @param patientRepository repository d'accès aux patients
     * @param bedRepository repository d'accès aux lits
     * @param hospitalStayRepository repository d'accès aux séjours
     */
    public ApplicationContext(PatientRepository patientRepository, BedRepository bedRepository, HospitalStayRepository hospitalStayRepository) {
        this.patientRepository = patientRepository;
        this.bedRepository = bedRepository;
        this.hospitalStayRepository = hospitalStayRepository;

        this.placementService = new PlacementService(patientRepository, bedRepository, hospitalStayRepository);

        this.createPatientUseCase = new CreatePatientUseCase(patientRepository);
        this.createStayUseCase = new CreateStayUseCase(hospitalStayRepository, patientRepository, bedRepository);
        this.placePatientUseCase = new PlacePatientUseCase(placementService, hospitalStayRepository);
        this.dischargePatientUseCase = new DischargePatientUseCase(hospitalStayRepository);
        this.createBedUseCase = new CreateBedUseCase(bedRepository);
        this.updateBedStatusUseCase = new UpdateBedStatusUseCase(bedRepository);
        this.deleteBedUseCase = new DeleteBedUseCase(bedRepository);
    }

    /**
     * Retourne le repository patient (utilisé par les use-cases / handlers).
     */
    public PatientRepository getPatientRepository() {
        return patientRepository;
    }

    /**
     * Retourne le repository bed.
     */
    public BedRepository getBedRepository() {
        return bedRepository;
    }

    /**
     * Retourne le repository hospital stay.
     */
    public HospitalStayRepository getHospitalStayRepository() {
        return hospitalStayRepository;
    }

    public CreatePatientUseCasePort getCreatePatientUseCase() {
        return createPatientUseCase;
    }

    public CreateStayUseCasePort getCreateStayUseCase() {
        return createStayUseCase;
    }

    public PlacePatientUseCasePort getPlacePatientUseCase() {
        return placePatientUseCase;
    }

    public DischargePatientUseCasePort getDischargePatientUseCase() {
        return dischargePatientUseCase;
    }

    public CreateBedUseCasePort getCreateBedUseCase() {
        return createBedUseCase;
    }

    public UpdateBedStatusUseCasePort getUpdateBedStatusUseCase() {
        return updateBedStatusUseCase;
    }

    public DeleteBedUseCasePort getDeleteBedUseCase() {
        return deleteBedUseCase;
    }
}
