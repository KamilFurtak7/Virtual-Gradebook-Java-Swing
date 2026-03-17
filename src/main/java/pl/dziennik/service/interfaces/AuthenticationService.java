/**
 * Plik: AuthenticationService.java
 *
 * Opis:
 *  Interfejs definiujący usługę uwierzytelniania użytkowników
 *  w systemie dziennika elektronicznego. Określa kontrakt,
 *  który muszą spełniać wszystkie implementacje mechanizmu
 *  logowania.
 *
 * Główne elementy:
 *  - Metoda login(...) – realizuje proces logowania użytkownika
 *    na podstawie loginu i hasła.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Interfejs:
 *      Definicja kontraktu dla usług uwierzytelniania.
 *
 *  - Polimorfizm:
 *      Umożliwia stosowanie różnych implementacji
 *      interfejsu AuthenticationService.
 */

package pl.dziennik.service.interfaces;

import pl.dziennik.model.user.User;
import pl.dziennik.util.AuthenticationException;

/**
 * Interfejs usługi uwierzytelniania użytkowników.
 */
public interface AuthenticationService {

    /**
     * Realizuje proces logowania użytkownika.
     *
     * @param login    login użytkownika
     * @param password hasło użytkownika
     * @return zalogowany użytkownik
     * @throws AuthenticationException gdy dane logowania są nieprawidłowe
     */
    User login(String login, String password)
            throws AuthenticationException;
}
