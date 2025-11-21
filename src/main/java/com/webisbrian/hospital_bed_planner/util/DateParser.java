package com.webisbrian.hospital_bed_planner.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utilitaire pour parser des dates depuis plusieurs formats courants en {@link LocalDate}.
 * Supporte :
 * - ISO (yyyy-MM-dd)
 * - jour/mois/année avec '/' (d/M/yyyy)
 * - jour-mois-année avec '-' (d-M-yyyy)
 */
public class DateParser {
    private final DateTimeFormatter iso = DateTimeFormatter.ISO_LOCAL_DATE;
    private final DateTimeFormatter ddMMyyyySlash = DateTimeFormatter.ofPattern("d/M/yyyy");
    private final DateTimeFormatter ddMMyyyyDash = DateTimeFormatter.ofPattern("d-M-yyyy");

    /**
     * Parse une chaîne flexible en {@link LocalDate} en essayant plusieurs formats connus.
     *
     * @param input chaîne de date (non nulle, non vide)
     * @return l'objet {@link LocalDate} correspondant
     * @throws IllegalArgumentException si l'entrée est nulle/vide ou si aucun format ne correspond
     */
    public LocalDate parseFlexible(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Date vide");
        }
        String trimmed = input.trim();
        // Essayez plusieurs formats connus
        try {
            // ISO first (yyyy-MM-dd)
            return LocalDate.parse(trimmed, iso);
        } catch (DateTimeParseException ignored) { }

        try {
            if (trimmed.contains("/")) {
                return LocalDate.parse(trimmed, ddMMyyyySlash);
            }
        } catch (DateTimeParseException ignored) { }

        try {
            if (trimmed.contains("-")) {
                return LocalDate.parse(trimmed, ddMMyyyyDash);
            }
        } catch (DateTimeParseException ignored) { }

        throw new IllegalArgumentException("Format de date invalide: " + input);
    }
}
