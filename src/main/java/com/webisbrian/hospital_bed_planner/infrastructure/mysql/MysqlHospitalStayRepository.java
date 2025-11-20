package com.webisbrian.hospital_bed_planner.infrastructure.mysql;

import com.webisbrian.hospital_bed_planner.domain.model.HospitalStay;
import com.webisbrian.hospital_bed_planner.domain.model.StayType;
import com.webisbrian.hospital_bed_planner.domain.repository.HospitalStayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Implémentation de HospitalStayRepository basée sur MySQL via JDBC.
 */
public class MysqlHospitalStayRepository implements HospitalStayRepository {

    private static final Logger logger = LoggerFactory.getLogger(MysqlHospitalStayRepository.class);

    private final String url;
    private final String user;
    private final String password;

    public MysqlHospitalStayRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public HospitalStay save(HospitalStay hospitalStay) {
        if (hospitalStay == null) {
            throw new IllegalArgumentException("HospitalStay cannot be null");
        }
        if (hospitalStay.getId() == null || hospitalStay.getId().isBlank()) {
            throw new IllegalArgumentException("HospitalStay id cannot be null or blank");
        }

        String sql = """
                INSERT INTO hospital_stay (
                    id,
                    patient_id,
                    bed_id,
                    stay_type,
                    admission_date,
                    discharge_date_planned,
                    discharge_date_effective
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    patient_id = VALUES(patient_id),
                    bed_id = VALUES(bed_id),
                    stay_type = VALUES(stay_type),
                    admission_date = VALUES(admission_date),
                    discharge_date_planned = VALUES(discharge_date_planned),
                    discharge_date_effective = VALUES(discharge_date_effective)
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hospitalStay.getId());
            ps.setString(2, hospitalStay.getPatientId());
            ps.setString(3, hospitalStay.getBedId());
            ps.setString(4, hospitalStay.getStayType().name());
            ps.setDate(5, Date.valueOf(hospitalStay.getAdmissionDate()));

            if (hospitalStay.getDischargeDatePlanned() != null) {
                ps.setDate(6, Date.valueOf(hospitalStay.getDischargeDatePlanned()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }

            if (hospitalStay.getDischargeDateEffective() != null) {
                ps.setDate(7, Date.valueOf(hospitalStay.getDischargeDateEffective()));
            } else {
                ps.setNull(7, java.sql.Types.DATE);
            }

            ps.executeUpdate();
            logger.info("Hospital stay with id={} saved successfully", hospitalStay.getId());
            return hospitalStay;
        } catch (SQLException e) {
            logger.error("Failed to save hospital stay with id={}", hospitalStay.getId(), e);
            throw new IllegalStateException("Failed to save hospital stay with id " + hospitalStay.getId(), e);
        }
    }

    @Override
    public Optional<HospitalStay> findById(String id) {
        logger.debug("Looking for hospital stay with id={}", id);
        String sql = "SELECT * FROM hospital_stay WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Hospital stay with id={} found", id);
                    return Optional.of(mapRowToHospitalStay(rs));
                }
                logger.debug("Hospital stay with id={} not found", id);
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Failed to find hospital stay with id={}", id, e);
            throw new IllegalStateException("Failed to find hospital stay with id " + id, e);
        }
    }

    @Override
    public List<HospitalStay> findAllByPatientId(String patientId) {
        logger.debug("Looking for all stays for patient with id={}", patientId);
        String sql = "SELECT * FROM hospital_stay WHERE patient_id = ? ORDER BY admission_date DESC";
        List<HospitalStay> stays = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stays.add(mapRowToHospitalStay(rs));
                }
            }
            logger.debug("Found {} stays for patient with id={}", stays.size(), patientId);
            return stays;
        } catch (SQLException e) {
            logger.error("Failed to load stays for patient with id={}", patientId, e);
            throw new IllegalStateException("Failed to load stays for patient " + patientId, e);
        }
    }

    @Override
    public List<HospitalStay> findAll() {
        logger.info("Loading all hospital stays");
        String sql = "SELECT * FROM hospital_stay ORDER BY admission_date DESC";
        List<HospitalStay> stays = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                stays.add(mapRowToHospitalStay(rs));
            }
            logger.info("Loaded {} hospital stays", stays.size());
            return stays;
        } catch (SQLException e) {
            logger.error("Failed to load all hospital stays", e);
            throw new IllegalStateException("Failed to load all hospital stays", e);
        }
    }

    @Override
    public List<HospitalStay> findActiveStaysOn(LocalDate date) {
        logger.info("Loading all active stays on {}", date);
        // Séjours actifs:
        // admission_date <= date
        // AND (discharge_date_effective IS NULL OR discharge_date_effective >= date)
        String sql = """
                SELECT *
                FROM hospital_stay
                WHERE admission_date <= ?
                  AND (discharge_date_effective IS NULL OR discharge_date_effective >= ?)
                """;

        List<HospitalStay> stays = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            Date sqlDate = Date.valueOf(date);
            ps.setDate(1, sqlDate);
            ps.setDate(2, sqlDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stays.add(mapRowToHospitalStay(rs));
                }
            }
            logger.info("Loaded {} active stays on {}", stays.size(), date);
            return stays;
        } catch (SQLException e) {
            logger.error("Failed to load active stays on {}", date, e);
            throw new IllegalStateException("Failed to load active stays on " + date, e);
        }
    }

    private HospitalStay mapRowToHospitalStay(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String patientId = rs.getString("patient_id");
        String bedId = rs.getString("bed_id");
        StayType stayType = StayType.valueOf(rs.getString("stay_type"));

        LocalDate admissionDate = rs.getDate("admission_date").toLocalDate();

        Date planned = rs.getDate("discharge_date_planned");
        LocalDate dischargePlanned = (planned != null) ? planned.toLocalDate() : null;

        Date effective = rs.getDate("discharge_date_effective");
        LocalDate dischargeEffective = (effective != null) ? effective.toLocalDate() : null;

        return new HospitalStay(
                id,
                patientId,
                bedId,
                stayType,
                admissionDate,
                dischargePlanned,
                dischargeEffective
        );
    }
}
