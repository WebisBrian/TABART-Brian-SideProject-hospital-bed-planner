package com.webisbrian.hospital_bed_planner.infrastructure.mysql;

import com.webisbrian.hospital_bed_planner.domain.model.Patient;
import com.webisbrian.hospital_bed_planner.domain.model.Sex;
import com.webisbrian.hospital_bed_planner.domain.repository.PatientRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de PatientRepository basée sur MySQL via JDBC.
 */
public class MysqlPatientRepository implements PatientRepository {

    private final String url;
    private final String user;
    private final String password;

    /**
     * @param url      URL JDBC, ex: jdbc:mysql://localhost:3306/hospital_bed_planner
     * @param user     utilisateur MySQL
     * @param password mot de passe MySQL
     */
    public MysqlPatientRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Patient save(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }

        String sql = """
                INSERT INTO patient (id, first_name, last_name, birth_date, sex, pmr, isolation, phone_number, notes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    first_name = VALUES(first_name),
                    last_name = VALUES(last_name),
                    birth_date = VALUES(birth_date),
                    sex = VALUES(sex),
                    pmr = VALUES(pmr),
                    isolation = VALUES(isolation),
                    phone_number = VALUES(phone_number),
                    notes = VALUES(notes)
                """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, patient.getId());
            ps.setString(2, patient.getFirstName());
            ps.setString(3, patient.getLastName());
            ps.setDate(4, Date.valueOf(patient.getBirthDate()));
            ps.setString(5, patient.getSex().name());
            ps.setBoolean(6, patient.isPmr());
            ps.setBoolean(7, patient.isIsolationRequired());
            ps.setString(8, patient.getPhoneNumber());
            ps.setString(9, patient.getNotes());

            ps.executeUpdate();
            return patient;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save patient with id " + patient.getId(), e);
        }
    }

    @Override
    public Optional<Patient> findById(String id) {
        String sql = "SELECT * FROM patient WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToPatient(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find patient with id " + id, e);
        }
    }

    @Override
    public boolean existsById(String id) {
        String sql = "SELECT 1 FROM patient WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to check existence of patient with id " + id, e);
        }
    }

    @Override
    public List<Patient> findAll() {
        String sql = "SELECT * FROM patient ORDER BY last_name, first_name";
        List<Patient> patients = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                patients.add(mapRowToPatient(rs));
            }
            return patients;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load all patients", e);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM patient WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete patient with id " + id, e);
        }
    }

    private Patient mapRowToPatient(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        LocalDate birthDate = rs.getDate("birth_date").toLocalDate();
        Sex sex = Sex.valueOf(rs.getString("sex"));
        boolean pmr = rs.getBoolean("pmr");
        boolean isolation = rs.getBoolean("isolation");
        String phone = rs.getString("phone_number");
        String notes = rs.getString("notes");

        return new Patient(id, firstName, lastName, birthDate, sex, pmr, isolation, phone, notes);
    }
}
