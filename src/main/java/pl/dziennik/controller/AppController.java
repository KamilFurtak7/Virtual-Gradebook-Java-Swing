/**
 * Plik: AppController.java
 *
 * Opis:
 *  Główny kontroler aplikacji „Wirtualny Dziennik”.
 *  Uruchamia program, obsługuje logowanie, przełącza widoki użytkowników
 *  (uczeń, nauczyciel, dyrektor) i zamyka aplikację.
 *  Łączy główne okno (MainFrame) z serwisami odpowiedzialnymi za dane,
 *  raporty, wiadomości i logowanie.
 *
 * Główne metody:
 *  - startApplication() – ładuje dane przy starcie aplikacji.
 *  - onLoginSuccess(User user) – co się dzieje po poprawnym zalogowaniu.
 *  - shutdownApplication() – zapisuje dane i zamyka program.
 *  - logout() – wylogowuje użytkownika i wraca do ekranu logowania.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Dziedziczenie i polimorfizm:
 *      Użycie klasy bazowej User oraz jej podklas Student, Teacher, Principal
 *      w metodzie onLoginSuccess().
 */

package pl.dziennik.controller;

import pl.dziennik.model.user.*;
import pl.dziennik.persistence.DatabaseContext;
import pl.dziennik.service.interfaces.*;
import pl.dziennik.view.MainFrame;
import javax.swing.JOptionPane;

public class AppController {

    // Główne okno aplikacji (widok dla użytkownika)
    private MainFrame mainFrame;

    // Serwis odpowiedzialny za wczytywanie i zapisywanie danych
    private final DataAccessService dataService;

    // Serwis zapisujący informacje o działaniach użytkowników i aplikacji
    private final ReportService reportService;

    // Obiekt zapewniający dostęp do bazy danych
    private final DatabaseContext dbContext;

    // Serwis do obsługi wiadomości między użytkownikami
    private final MessageService messageService;

    // Aktualnie zalogowany użytkownik (może to być uczeń, nauczyciel lub dyrektor)
    private User currentUser;

    // Serwis odpowiedzialny za logowanie i sprawdzanie użytkowników
    private final AuthenticationService authService;

    /**
     * Konstruktor kontrolera aplikacji.
     * Ustawia główne okno oraz wszystkie serwisy potrzebne do działania aplikacji.
     */
    public AppController(MainFrame mainFrame, DataAccessService dataService, ReportService reportService, DatabaseContext dbContext, MessageService messageService, AuthenticationService authService) {
        this.mainFrame = mainFrame;
        this.dataService = dataService;
        this.reportService = reportService;
        this.dbContext = dbContext;
        this.messageService = messageService;
        this.authService = authService;
    }

    /** Ustawia nowe główne okno aplikacji (jeśli trzeba je podmienić). */
    public void setMainFrame(MainFrame mainFrame) { this.mainFrame = mainFrame; }

    /** Zwraca aktualnie używane główne okno aplikacji. */
    public MainFrame getMainFrame() { return mainFrame; }

    /**
     * Uruchamia aplikację.
     *  - wczytuje dane z bazy lub plików,
     *  - zapisuje w logach, że aplikacja została uruchomiona.
     * W razie poważnego błędu pokazuje komunikat i zamyka program.
     */
    public void startApplication() {
        try {
            // Wczytanie danych początkowych (np. użytkownicy, oceny, klasy)
            dataService.loadData();
            // Zapis informacji, że aplikacja została uruchomiona
            reportService.logAction("Aplikacja uruchomiona. Dane załadowane.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Krytyczny błąd wczytywania danych: " + e.getMessage(),
                    "Błąd krytyczny",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
    }

    /**
     * Wywoływana po poprawnym zalogowaniu.
     *
     *  - zapisuje zalogowanego użytkownika w polu currentUser,
     *  - ustawia tekst powitania,
     *  - wybiera odpowiedni panel (dla ucznia, nauczyciela lub dyrektora),
     *  - przekazuje do panelu potrzebne dane i serwisy,
     *  - pokazuje główne okno.
     *
     * W tym miejscu używany jest polimorfizm:
     *  User może być Student, Teacher albo Principal.
     */
    public void onLoginSuccess(User user) {
        // Zapamiętujemy, kto jest teraz zalogowany
        this.currentUser = user;
        String userRole = user.getRole();

        // Podstawowe powitanie z imieniem i nazwiskiem
        String welcomeText = "Witaj, " + user.getFullName();

        // Jeśli zalogowany jest uczeń, dopisujemy jego klasę
        if (user instanceof Student && ((Student) user).getSchoolClass() != null) {
            welcomeText += " (Klasa: " + ((Student) user).getSchoolClass().getName() + ")";
        }

        // Ustawiamy tekst powitania i tytuł okna
        mainFrame.getWelcomeLabel().setText(welcomeText);
        mainFrame.setTitle("Wirtualny Dziennik");

        // Wyświetlamy panel odpowiedni dla roli użytkownika (np. STUDENT, TEACHER, PRINCIPAL)
        mainFrame.showPanel(userRole);

        // W zależności od typu użytkownika ustawiamy dane w odpowiednim panelu
        if (user instanceof Student) {
            // Panel ucznia dostaje obiekt ucznia, serwis wiadomości i kontekst bazy
            mainFrame.getStudentPanel().setStudentData((Student) user, messageService, dbContext);
        } else if (user instanceof Teacher) {
            // Panel nauczyciela dostaje obiekt nauczyciela, kontekst bazy i serwis wiadomości
            mainFrame.getTeacherPanel().setTeacherData((Teacher) user, dbContext, messageService);
        } else if (user instanceof Principal) {
            // Panel dyrektora dostaje obiekt dyrektora, kontekst bazy, serwis raportów i wiadomości
            mainFrame.getPrincipalPanel().setPrincipalData((Principal) user, dbContext, reportService, messageService);
        }

        // Upewniamy się, że okno jest widoczne
        mainFrame.setVisible(true);
    }

    /**
     * Zamyka aplikację.
     *  - próbuje zapisać dane,
     *  - zapisuje w logach, że aplikacja została zamknięta,
     *  - w razie błędu pokazuje komunikat,
     *  - na końcu kończy działanie programu.
     */
    public void shutdownApplication() {
        try {
            // Zapis danych (np. po zmianach w dzienniku)
            dataService.saveData();
            // Zapis informacji, że aplikacja została zamknięta
            reportService.logAction("Aplikacja zamknięta. Dane zapisane pomyślnie.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Błąd podczas zapisywania danych: " + e.getMessage(),
                    "Błąd krytyczny",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        // Zakończenie działania programu
        System.exit(0);
    }

    /**
     * Wylogowuje aktualnego użytkownika.
     *  - zapisuje w logach, że użytkownik się wylogował,
     *  - czyści informację o zalogowanym użytkowniku,
     *  - przywraca domyślny tekst powitalny i tytuł okna,
     *  - przełącza widok na panel logowania.
     */
    public void logout() {
        if (currentUser != null) {
            // Zapis w logach, kto się wylogował
            reportService.logAction("Użytkownik " + currentUser.getLogin() + " wylogował się.");
        }

        // Zerujemy aktualnego użytkownika
        currentUser = null;

        // Przywracamy domyślne napisy w widoku
        mainFrame.getWelcomeLabel().setText("Witaj!");
        mainFrame.setTitle("Wirtualny Dziennik - Logowanie");

        // Pokazujemy panel logowania
        mainFrame.showPanel("LOGIN");
    }
}
