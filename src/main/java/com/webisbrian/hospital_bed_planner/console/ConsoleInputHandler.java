package com.webisbrian.hospital_bed_planner.console;

import com.webisbrian.hospital_bed_planner.util.DateParser;
import java.time.LocalDate;
import java.util.Scanner;
import java.io.PrintWriter;

/**
 * Handler d'entrée console : centralise la lecture sécurisée depuis un Scanner et
 * l'affichage via un PrintWriter injecté.
 *
 * Les méthodes flushent le PrintWriter après un prompt. Le Scanner et le PrintWriter
 * ne sont pas fermés par ce handler (gestion des ressources par l'appelant).
 */
public class ConsoleInputHandler {
    private final Scanner scanner;
    private final DateParser dateParser;
    private final PrintWriter out;

    public ConsoleInputHandler(Scanner scanner, PrintWriter out) {
        this.scanner = scanner;
        this.dateParser = new DateParser();
        this.out = out;
    }

    /**
     * Lit une ligne après affichage d'un prompt.
     * @param prompt texte du prompt (court)
     * @return ligne saisie, trimée
     */
    public String readLine(String prompt) {
        out.print(prompt + " : ");
        out.flush();
        return scanner.nextLine().trim();
    }

    /**
     * Lit une réponse oui/non (boucle jusqu'à obtention d'une réponse valide).
     * Retourne true pour 'oui' (o/oui/y/yes), false pour 'non'.
     */
    public boolean readYesNo(String prompt) {
        while (true) {
            out.print(prompt + " (o/n) : ");
            out.flush();
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty()) continue;
            switch (input) {
                case "o", "oui", "y", "yes" -> { return true; }
                case "n", "non", "no" -> { return false; }
                default -> out.println("Réponse invalide. Répondez par 'o'/'n' (oui/non).");
            }
        }
    }

    /**
     * Lit une date obligatoire en essayant plusieurs formats.
     * Boucle jusqu'à obtenir une date valide.
     */
    public LocalDate readMandatoryDate(String label) {
        while (true) {
            out.print(label + " (YYYY-MM-DD ou DD/MM/YYYY) : ");
            out.flush();
            String input = scanner.nextLine().trim();
            try {
                return dateParser.parseFlexible(input);
            } catch (Exception e) {
                out.println("Date invalide, merci de réessayer.");
            }
        }
    }

    /**
     * Lit une date optionnelle ; retourne null si l'utilisateur tape ENTER.
     */
    public LocalDate readOptionalDate(String label) {
        while (true) {
            out.print(label + " (optionnel, ENTER pour ignorer) : ");
            out.flush();
            String input = scanner.nextLine().trim();
            if (input.isBlank()) return null;
            try {
                return dateParser.parseFlexible(input);
            } catch (Exception e) {
                out.println("Date invalide, merci de réessayer.");
            }
        }
    }
}
