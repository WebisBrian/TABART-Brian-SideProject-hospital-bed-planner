package com.webisbrian.hospital_bed_planner.config;

import com.webisbrian.hospital_bed_planner.application.ApplicationContext;
import com.webisbrian.hospital_bed_planner.infrastructure.config.DatabaseConfiguration;
import com.webisbrian.hospital_bed_planner.infrastructure.mysql.MysqlBedRepository;
import com.webisbrian.hospital_bed_planner.infrastructure.mysql.MysqlHospitalStayRepository;
import com.webisbrian.hospital_bed_planner.infrastructure.mysql.MysqlPatientRepository;
import com.webisbrian.hospital_bed_planner.domain.repository.BedRepository;
import com.webisbrian.hospital_bed_planner.domain.repository.HospitalStayRepository;
import com.webisbrian.hospital_bed_planner.domain.repository.PatientRepository;

/**
 * Composition root : crée et assemble les implémentations d'infrastructure (DB)
 * et construit un ApplicationContext prêt à l'emploi.
 *
 * Fournit également une factory pour tests permettant d'injecter des repositories
 * alternatifs (in-memory, mocks).
 */
public final class ApplicationComposition {
    private ApplicationComposition() {}

    public static ApplicationContext createFromDefaultConfiguration() {
        DatabaseConfiguration dbConfig = new DatabaseConfiguration();
        return create(dbConfig);
    }

    public static ApplicationContext create(DatabaseConfiguration dbConfig) {
        PatientRepository patientRepository = new MysqlPatientRepository(dbConfig.getUrl(), dbConfig.getUser(), dbConfig.getPassword());
        BedRepository bedRepository = new MysqlBedRepository(dbConfig.getUrl(), dbConfig.getUser(), dbConfig.getPassword());
        HospitalStayRepository hospitalStayRepository = new MysqlHospitalStayRepository(dbConfig.getUrl(), dbConfig.getUser(), dbConfig.getPassword());

        return new ApplicationContext(patientRepository, bedRepository, hospitalStayRepository);
    }

    /**
     * Utilitaire pour tests : permet d'injecter des repositories alternatifs (in-memory, mocks...).
     */
    public static ApplicationContext createWithRepositories(PatientRepository p, BedRepository b, HospitalStayRepository s) {
        return new ApplicationContext(p, b, s);
    }
}
