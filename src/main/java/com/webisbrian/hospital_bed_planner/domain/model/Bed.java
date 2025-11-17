package com.webisbrian.hospital_bed_planner.domain.model;

import java.util.Objects;

/**
 * Modèle de domaine représentant un lit d'hospitalisation.
 * Utilisé pour gérer la disponibilité et l'affectation des patients.
 */
public class Bed {
    private final String id;                  // identifiant technique unique du lit (ex: "BED-001")
    private final String roomId;              // identifiant de la chambre à laquelle appartient le lit
    private final String code;                // code lisible dans le service (ex: "A12-1")
    private final BedStatus status;
    private final boolean isolationCapable;

    public Bed(String id,
               String roomId,
               String code,
               BedStatus status,
               boolean isolationCapable) {
        this.id = id;
        this.roomId = roomId;
        this.code = code;
        this.status = status;
        this.isolationCapable = isolationCapable;
    }

    /* GETTERS */

    public String getId() {
        return id;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getCode() {
        return code;
    }

    public BedStatus getStatus() {
        return status;
    }

    public boolean isIsolationCapable() {
        return isolationCapable;
    }

    /* METHODS AND OVERRIDES */

    @Override
    public String toString() {
        return "Bed{" +
                "id='" + id + '\'' +
                ", roomId='" + roomId + '\'' +
                ", code='" + code + '\'' +
                ", status=" + status +
                ", isolationCapable=" + isolationCapable +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bed bed = (Bed) o;
        return Objects.equals(id, bed.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
