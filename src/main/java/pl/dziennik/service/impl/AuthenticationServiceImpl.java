/**
 * Plik: AuthenticationServiceImpl.java
 *
 * Opis:
 *  Implementacja usługi uwierzytelniania użytkowników w systemie
 *  dziennika elektronicznego. Klasa odpowiada za proces logowania
 *  użytkownika na podstawie danych przechowywanych w kontekście
 *  bazy danych oraz rejestrowanie zdarzeń w systemie raportowania.
 *
 * Główne elementy:
 *  - Implementacja interfejsu AuthenticationService.
 *  - Współpraca z DatabaseContext w celu dostępu do danych użytkowników.
 *  - Wykorzystanie ReportService do logowania zdarzeń systemowych.
 *  - Metoda login(...) – realizuje proces logowania użytkownika.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Interfejsy:
 *      Klasa implementuje interfejs AuthenticationService,
 *      co umożliwia stosowanie różnych implementacji usługi
 *      uwierzytelniania.
 *
 *  - Polimorfizm:
 *      Metoda login(...) jest implementacją metody
 *      zadeklarowanej w interfejsie AuthenticationService.
 *
 *  - Wstrzykiwanie zależności (Dependency Injection):
 *      Zależności DatabaseContext oraz ReportService
 *      są przekazywane przez konstruktor.
 *
 *  - Obsługa wyjątków:
 *      Zgłaszanie własnego wyjątku AuthenticationException
 *      w przypadku nieudanego logowania.
 */

package pl.dziennik.service.impl;

import pl.dziennik.model.user.User;
import pl.dziennik.persistence.DatabaseContext;
import pl.dziennik.service.interfaces.AuthenticationService;
import pl.dziennik.service.interfaces.ReportService;
import pl.dziennik.util.AuthenticationException;

/**
 * Implementacja usługi uwierzytelniania użytkowników.
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    // Kontekst danych aplikacji
    private final DatabaseContext dbContext;

    // Usługa raportowania zdarzeń systemowych
    private final ReportService reportService;

    /**
     * Tworzy implementację usługi uwierzytelniania.
     *
     * @param dbContext     kontekst danych aplikacji
     * @param reportService usługa raportowania zdarzeń
     */
    public AuthenticationServiceImpl(DatabaseContext dbContext,
                                     ReportService reportService) {
        this.dbContext = dbContext;
        this.reportService = reportService;
    }

    /**
     * Realizuje proces logowania użytkownika na podstawie
     * loginu i hasła.
     *
     * @param login    login użytkownika
     * @param password hasło użytkownika
     * @return zalogowany użytkownik
     * @throws AuthenticationException gdy dane logowania są nieprawidłowe
     */
    @Override
    public User login(String login, String password)
            throws AuthenticationException {

        reportService.logAction("Próba logowania dla użytkownika: " + login);

        for (User user : dbContext.getUsers()) {
            if (user.getLogin().equals(login)
                    && user.getPassword().equals(password)) {

                reportService.logAction(
                        "Użytkownik " + login + " zalogował się pomyślnie.");
                return user;
            }
        }

        reportService.logAction(
                "Nieudana próba logowania dla: " + login);
        throw new AuthenticationException(
                "Nieprawidłowy login lub hasło.");
    }
}
