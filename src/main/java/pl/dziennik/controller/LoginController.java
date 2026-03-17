package pl.dziennik.controller;

import pl.dziennik.model.user.User;
import pl.dziennik.service.interfaces.AuthenticationService;
import pl.dziennik.util.AuthenticationException;
import javax.swing.JOptionPane;

/**
 * Plik: LoginController.java
 *
 * Opis:
 *  Kontroler odpowiedzialny za logowanie użytkownika.
 *  Korzysta z serwisu logowania (AuthenticationService) i głównego
 *  kontrolera aplikacji (AppController), aby po poprawnym logowaniu
 *  przejść do dalszej części programu.
 *
 * Główne metody:
 *  - attemptLogin(String login, String password) – próbuje zalogować użytkownika
 *    na podstawie loginu i hasła.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Dziedziczenie i polimorfizm:
 *      Metoda login(...) zwraca obiekt typu User, który w programie może być
 *      różnymi konkretnymi klasami (np. Student, Teacher, Principal).
 */
public class LoginController {

    // Serwis służący do sprawdzania loginu i hasła użytkownika
    private final AuthenticationService authService;

    // Główny kontroler aplikacji – używany, gdy logowanie się powiedzie
    private final AppController appController;

    /**
     * Konstruktor kontrolera logowania.
     * Ustawia serwis logowania i główny kontroler aplikacji.
     */
    public LoginController(AuthenticationService authService, AppController appController) {
        this.authService = authService;
        this.appController = appController;
    }

    /**
     * Próbuje zalogować użytkownika na podstawie loginu i hasła.
     *
     *  - jeśli dane są poprawne:
     *      otrzymujemy obiekt User i przekazujemy go do AppController,
     *      aby dokończyć proces logowania (np. przełączyć widok).
     *  - jeśli dane są błędne:
     *      wyświetlany jest prosty komunikat o błędzie logowania.
     *  - jeśli wystąpi inny, nieoczekiwany błąd:
     *      pokazujemy komunikat o błędzie krytycznym.
     */
    public void attemptLogin(String login, String password) {
        try {
            // Próba zalogowania użytkownika (sprawdzenie loginu i hasła)
            User user = authService.login(login, password);

            // Jeśli logowanie się powiodło, przekazujemy użytkownika dalej
            appController.onLoginSuccess(user);

        } catch (AuthenticationException e) {
            // Logowanie nieudane z powodu błędnych danych (np. zły login lub hasło)
            JOptionPane.showMessageDialog(
                    appController.getMainFrame(),
                    e.getMessage(),
                    "Błąd logowania",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {
            // Inny, nieoczekiwany błąd podczas logowania
            e.printStackTrace(); // Wypisz szczegóły błędu w konsoli
            JOptionPane.showMessageDialog(
                    appController.getMainFrame(),
                    "Wystąpił nieoczekiwany błąd podczas logowania: " + e.getMessage(),
                    "Błąd krytyczny",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
