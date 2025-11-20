USE hospital_bed_manager;

-- Quelques patients de test
INSERT INTO patient (id, first_name, last_name, birth_date, sex, pmr, isolation, phone_number, notes) VALUES
                                                                                                          ('P-001', 'Alice',  'Martin',  '1980-01-01', 'FEMALE', 0, 0, '0102030405', 'Patiente standard'),
                                                                                                          ('P-002', 'Bob',    'Durand',  '1975-05-10', 'MALE',   0, 1, '0102030406', 'Isolement nécessaire'),
                                                                                                          ('P-003', 'Claire', 'Dupont',  '1940-03-15', 'FEMALE', 1, 0, '0102030407', 'PMR, âgée');

-- Quelques lits de test
INSERT INTO bed (id, room_id, code, status, isolation_capable) VALUES
                                                                   ('BED-001', 'ROOM-101', 'A01-1', 'AVAILABLE', 0),
                                                                   ('BED-002', 'ROOM-101', 'A01-2', 'AVAILABLE', 1),
                                                                   ('BED-003', 'ROOM-102', 'A02-1', 'CLEANING',  0),
                                                                   ('BED-004', 'ROOM-103', 'B01-1', 'OUT_OF_ORDER', 0);

-- Séjour en cours pour tester les visualisations et le placement
INSERT INTO hospital_stay (
    id,
    patient_id,
    bed_id,
    stay_type,
    admission_date,
    discharge_date_planned,
    discharge_date_effective
) VALUES
    ('STAY-001', 'P-001', 'BED-001', 'WEEK', '2025-01-10', '2025-01-17', NULL);
