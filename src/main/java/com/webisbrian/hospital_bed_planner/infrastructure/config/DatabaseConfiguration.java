package com.webisbrian.hospital_bed_planner.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lecture de la configuration de base de données depuis db.properties.
 * Ce composant peut être instancié en tests via le constructeur acceptant Properties.
 */
public class DatabaseConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private final String url;
    private final String user;
    private final String password;

    /**
     * Constructeur par défaut : charge les propriétés depuis db.properties.
     */
    public DatabaseConfiguration() {
        this(loadProperties());
    }

    /**
     * Constructeur pour tests / injection : accepte un objet Properties.
     *
     * @param props propriétés contenant db.url, db.user, db.password
     */
    public DatabaseConfiguration(Properties props) {
        if (props == null) {
            throw new IllegalArgumentException("Properties ne peut pas être null");
        }
        this.url = props.getProperty("db.url");
        this.user = props.getProperty("db.user");
        this.password = props.getProperty("db.password");
        validateAndLog();
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConfiguration.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                logger.error("db.properties not found on classpath");
                throw new IllegalStateException("Fichier db.properties introuvable sur le classpath");
            }
            properties.load(input);
            logger.info("Loaded db.properties");
            return properties;
        } catch (IOException e) {
            logger.error("Error loading db.properties", e);
            throw new IllegalStateException("Impossible de charger db.properties", e);
        }
    }

    private void validateAndLog() {
        if (isBlank(url)) {
            throw new IllegalStateException("Configuration manquante : db.url");
        }
        if (isBlank(user)) {
            throw new IllegalStateException("Configuration manquante : db.user");
        }
        if (isBlank(password)) {
            throw new IllegalStateException("Configuration manquante : db.password");
        }
        // Ne jamais logger le password ; on masque éventuellement la partie sensible de l'URL.
        logger.info("DatabaseConfiguration initialisée : url={} user={}", maskUrl(url), user);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Masque les paramètres password/pwd dans l'URL si présents.
     */
    private static String maskUrl(String url) {
        if (url == null) return null;
        try {
            Pattern p = Pattern.compile("(?i)(password|pwd)=([^&;]+)");
            Matcher m = p.matcher(url);
            return m.replaceAll("$1=****");
        } catch (Exception e) {
            // En cas d'erreur de masquage, retourner l'URL d'origine mais ne pas divulguer de password via logs
            return url;
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    /**
     * Retourne le password en mémoire. Attention : ne pas logger cette valeur.
     */
    public String getPassword() {
        return password;
    }
}
