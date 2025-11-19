package com.webisbrian.hospital_bed_planner.domain.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Modèle de domaine représentant un séjour d'hospitalisation.
 * Lie un patient à un lit, un type de séjour et une période (dates).
 */
public class HospitalStay {
    private final String id;                          // identifiant unique du séjour (ex: "STAY-0001" ou UUID)
    private final String patientId;                   // référence vers Patient.id
    private final String bedId;                       // référence vers Bed.id (lit actuel)
    private final StayType stayType;
    private final LocalDate admissionDate;            // date d'entrée
    private final LocalDate dischargeDatePlanned;     // date de sortie
    private final LocalDate dischargeDateEffective;   // date de sortie effective (peut être null)

    public HospitalStay(String id,
                        String patientId,
                        String bedId,
                        StayType stayType,
                        LocalDate admissionDate,
                        LocalDate dischargeDatePlanned,
                        LocalDate dischargeDateEffective) {
        this.id = id;
        this.patientId = patientId;
        this.bedId = bedId;
        this.stayType = stayType;
        this.admissionDate = admissionDate;
        this.dischargeDatePlanned = dischargeDatePlanned;
        this.dischargeDateEffective = dischargeDateEffective;
    }

    /* GETTERS */

    public String getId() {
        return id;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getBedId() {
        return bedId;
    }

    public StayType getStayType() {
        return stayType;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public LocalDate getDischargeDatePlanned() {
        return dischargeDatePlanned;
    }

    public LocalDate getDischargeDateEffective() {
        return dischargeDateEffective;
    }

    /* METHODS AND OVERRIDES */

    @Override
    public String toString() {
        return "HospitalStay{" +
                "id='" + id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", bedId='" + bedId + '\'' +
                ", admissionDate=" + admissionDate +
                ", stayType=" + stayType +
                ", dischargeDatePlanned=" + dischargeDatePlanned +
                ", dischargeDateEffective=" + dischargeDateEffective +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HospitalStay that = (HospitalStay) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
