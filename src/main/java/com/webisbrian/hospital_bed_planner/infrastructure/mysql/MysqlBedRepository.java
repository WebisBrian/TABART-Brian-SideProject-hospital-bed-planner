package com.webisbrian.hospital_bed_planner.infrastructure.mysql;

import com.webisbrian.hospital_bed_planner.domain.model.Bed;
import com.webisbrian.hospital_bed_planner.domain.model.BedStatus;
import com.webisbrian.hospital_bed_planner.domain.repository.BedRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de BedRepository basée sur MySQL via JDBC.
 */
public class MysqlBedRepository implements BedRepository {

    private final String url;
    private final String user;
    private final String password;

    public MysqlBedRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Bed save(Bed bed) {
        if (bed == null) {
            throw new IllegalArgumentException("Bed cannot be null");
        }
        if (bed.getId() == null || bed.getId().isBlank()) {
            throw new IllegalArgumentException("Bed id cannot be null or blank");
        }

        String sql = """
                INSERT INTO bed (id, room_id, code, status, isolation_capable)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    room_id = VALUES(room_id),
                    code = VALUES(code),
                    status = VALUES(status),
                    isolation_capable = VALUES(isolation_capable)
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bed.getId());
            ps.setString(2, bed.getRoomId());
            ps.setString(3, bed.getCode());
            ps.setString(4, bed.getStatus().name());
            ps.setBoolean(5, bed.isIsolationCapable());

            ps.executeUpdate();
            return bed;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save bed with id " + bed.getId(), e);
        }
    }

    @Override
    public Optional<Bed> findById(String id) {
        String sql = "SELECT * FROM bed WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToBed(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find bed with id " + id, e);
        }
    }

    @Override
    public List<Bed> findAll() {
        String sql = "SELECT * FROM bed ORDER BY room_id, code";
        List<Bed> beds = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                beds.add(mapRowToBed(rs));
            }
            return beds;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load all beds", e);
        }
    }

    @Override
    public List<Bed> findByStatus(BedStatus status) {
        String sql = "SELECT * FROM bed WHERE status = ? ORDER BY room_id, code";
        List<Bed> beds = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    beds.add(mapRowToBed(rs));
                }
            }
            return beds;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load beds by status " + status, e);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM bed WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete bed with id " + id, e);
        }
    }

    private Bed mapRowToBed(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String roomId = rs.getString("room_id");
        String code = rs.getString("code");
        BedStatus status = BedStatus.valueOf(rs.getString("status"));
        boolean isolationCapable = rs.getBoolean("isolation_capable");

        return new Bed(
                id,
                roomId,
                code,
                status,
                isolationCapable
        );
    }
}
