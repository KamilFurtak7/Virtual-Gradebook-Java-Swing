/**
 * Plik: DataAccessService.java
 *
 * Opis:
 *  Interfejs definiujący usługę dostępu do danych w systemie
 *  dziennika elektronicznego. Określa kontrakt dla klas
 *  odpowiedzialnych za wczytywanie i zapisywanie danych
 *  aplikacji.
 *
 * Główne elementy:
 *  - Metoda loadData() – wczytuje dane systemu z nośnika danych.
 *  - Metoda saveData() – zapisuje aktualny stan danych systemu.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Interfejs:
 *      Definicja kontraktu dla warstwy dostępu do danych.
 *
 *  - Polimorfizm:
 *      Umożliwia stosowanie różnych implementacji
 *      interfejsu DataAccessService (np. pliki, baza danych).
 */

package pl.dziennik.service.interfaces;

/**
 * Interfejs usługi dostępu do danych.
 */
public interface DataAccessService {

    /**
     * Wczytuje dane systemu z nośnika danych
     * do pamięci aplikacji.
     *
     * @throws Exception w przypadku błędów wczytywania danych
     */
    void loadData() throws Exception;

    /**
     * Zapisuje aktualny stan danych systemu
     * na nośnik danych.
     *
     * @throws Exception w przypadku błędów zapisu danych
     */
    void saveData() throws Exception;
}
