/**
 * Plik: AuthenticationException.java
 *
 * Opis:
 *  Własny wyjątek sygnalizujący błąd uwierzytelniania
 *  w systemie dziennika elektronicznego. Wyjątek jest
 *  zgłaszany w przypadku nieprawidłowych danych logowania
 *  lub nieudanej próby logowania.
 *
 * Główne elementy:
 *  - Dziedziczenie po klasie Exception.
 *  - Konstruktor AuthenticationException(...) – umożliwia
 *    przekazanie komunikatu o błędzie.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Dziedziczenie:
 *      Klasa AuthenticationException rozszerza klasę Exception.
 *
 *  - Obsługa wyjątków:
 *      Własny wyjątek domenowy wykorzystywany
 *      w warstwie serwisowej aplikacji.
 */

package pl.dziennik.util;

/**
 * Wyjątek sygnalizujący błąd uwierzytelniania użytkownika.
 */
public class AuthenticationException extends Exception {

    /**
     * Tworzy nowy wyjątek uwierzytelniania z podanym komunikatem.
     *
     * @param message komunikat opisujący przyczynę błędu
     */
    public AuthenticationException(String message) {
        super(message);
    }
}
