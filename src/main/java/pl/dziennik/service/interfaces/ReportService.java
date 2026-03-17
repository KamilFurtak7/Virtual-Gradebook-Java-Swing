/**
 * Plik: ReportService.java
 *
 * Opis:
 *  Interfejs definiujący usługę raportowania zdarzeń
 *  w systemie dziennika elektronicznego. Określa
 *  kontrakt dla mechanizmu rejestrowania komunikatów
 *  i działań wykonywanych w systemie.
 *
 * Główne elementy:
 *  - Metoda logAction(...) – zapisuje pojedyncze
 *    zdarzenie lub komunikat systemowy.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Interfejs:
 *      Definicja kontraktu dla usług raportowania.
 *
 *  - Polimorfizm:
 *      Umożliwia stosowanie różnych implementacji
 *      interfejsu ReportService (np. zapis do pliku,
 *      konsoli lub systemu logowania).
 */

package pl.dziennik.service.interfaces;

/**
 * Interfejs usługi raportowania zdarzeń systemowych.
 */
public interface ReportService {

    /**
     * Rejestruje zdarzenie lub komunikat systemowy.
     *
     * @param message treść komunikatu do zapisania
     */
    void logAction(String message);
}
