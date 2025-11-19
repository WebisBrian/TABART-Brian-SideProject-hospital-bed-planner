package com.webisbrian.hospital_bed_planner.domain.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Modèle de domaine représentant un patient hospitalisé.
 * Contient les informations d'identification et les caractéristiques utiles pour l'attribution des lits.
 */
public class Patient {
    private final String id;                // dossier patient (identifiant métier)
    private final String firstName;
    private final String lastName;
    private final LocalDate birthDate;
    private final Sex sex;
    private final boolean pmr;              // Personne à Mobilité Réduite
    private final boolean isolationRequired;
    private final String phoneNumber;
    private final String notes;

    public Patient(String id,
                   String firstName,
                   String lastName,
                   LocalDate birthDate,
                   Sex sex,
                   boolean pmr,
                   boolean isolationRequired,
                   String phoneNumber,
                   String notes) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.sex = sex;
        this.pmr = pmr;
        this.isolationRequired = isolationRequired;
        this.phoneNumber = phoneNumber;
        this.notes = notes;
    }

    /* GETTERS */

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public Sex getSex() {
        return sex;
    }

    public boolean isPmr() {
        return pmr;
    }

    public boolean isIsolationRequired() {
        return isolationRequired;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getNotes() {
        return notes;
    }

    /* METHODS AND OVERRIDES */

    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", sex=" + sex +
                ", pmr=" + pmr +
                ", isolationRequired=" + isolationRequired +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Objects.equals(id, patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
