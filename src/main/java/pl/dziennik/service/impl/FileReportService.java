/**
 * Plik: FileReportService.java
 *
 * Opis:
 *  Implementacja usługi raportowania zdarzeń systemowych
 *  oparta na zapisie do pliku tekstowego. Klasa odpowiada
 *  za rejestrowanie komunikatów wraz z datą i godziną
 *  ich wystąpienia.
 *
 * Główne elementy:
 *  - Implementacja interfejsu ReportService.
 *  - Zapis zdarzeń do pliku raportu.
 *  - Automatyczne dodawanie znacznika czasu do logów.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Interfejsy:
 *      Implementacja interfejsu ReportService.
 *
 *  - Polimorfizm:
 *      Metoda logAction(...) jest implementacją
 *      metody zadeklarowanej w interfejsie ReportService.
 *
 *  - Obsługa wyjątków:
 *      Obsługa wyjątku IOException podczas zapisu
 *      do pliku raportu.
 */

package pl.dziennik.service.impl;

import pl.dziennik.service.interfaces.ReportService;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementacja usługi raportowania zapisującej
 * zdarzenia do pliku tekstowego.
 */
public class FileReportService implements ReportService {

    // Ścieżka do pliku raportu
    private final String reportFilePath;

    // Formatter daty i godziny używany w logach
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Tworzy usługę raportowania opartą na pliku.
     *
     * @param reportFilePath ścieżka do pliku raportu
     */
    public FileReportService(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }

    /**
     * Zapisuje pojedyncze zdarzenie do pliku raportu.
     * Każdy wpis zawiera znacznik czasu oraz treść komunikatu.
     *
     * @param message treść komunikatu do zapisania
     */
    @Override
    public void logAction(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[%s] - %s%n", timestamp, message);

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(reportFilePath, true))) {

            writer.write(logEntry);

        } catch (IOException e) {
            System.err.println(
                    "Błąd zapisu do pliku raportu: " + e.getMessage());
        }
    }
}
