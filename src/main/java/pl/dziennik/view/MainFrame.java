/**
 * Plik: MainFrame.java
 *
 * Opis:
 *  Główne okno aplikacji „Wirtualny Dziennik”.
 *  Klasa odpowiada za zarządzanie widokami interfejsu
 *  użytkownika w zależności od roli zalogowanego użytkownika
 *  (uczeń, nauczyciel, dyrektor) oraz za obsługę zdarzeń
 *  związanych z cyklem życia aplikacji.
 *
 * Główne elementy:
 *  - Dziedziczenie po klasie JFrame.
 *  - Wykorzystanie CardLayout do przełączania widoków.
 *  - Górny pasek z informacją powitalną i przyciskiem wylogowania.
 *  - Integracja z AppController oraz LoginController.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Dziedziczenie:
 *      Klasa MainFrame dziedziczy po JFrame.
 *
 *  - Wzorzec MVC:
 *      Klasa pełni rolę View i komunikuje się z warstwą
 *      Controller (AppController, LoginController).
 *
 *  - Zarządzanie widokami (CardLayout):
 *      Dynamiczne przełączanie paneli w zależności
 *      od aktualnego stanu aplikacji.
 *
 *  - Klasy anonimowe:
 *      Anonimowa implementacja WindowAdapter do obsługi
 *      zdarzenia zamykania okna.
 */

package pl.dziennik.view;

import pl.dziennik.controller.AppController;
import pl.dziennik.controller.LoginController;
import pl.dziennik.view.panel.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Główne okno aplikacji.
 */
public class MainFrame extends JFrame {

    // Układ kart umożliwiający przełączanie paneli
    private final CardLayout cardLayout = new CardLayout();

    // Panel główny zawierający wszystkie widoki
    private final JPanel mainContentPanel = new JPanel(cardLayout);

    // Widoki aplikacji
    private final LoginPanel loginPanel;
    private final StudentPanel studentPanel;
    private final TeacherPanel teacherPanel;
    private final PrincipalPanel principalPanel;

    // Górny pasek aplikacji
    private final JPanel topPanel = new JPanel(new BorderLayout(10, 0));
    private final JLabel welcomeLabel =
            new JLabel("Witaj!", SwingConstants.LEFT);
    private final JButton logoutButton = new JButton("Wyloguj");

    // Główny kontroler aplikacji
    private final AppController appController;

    /**
     * Tworzy główne okno aplikacji i inicjalizuje
     * wszystkie widoki oraz elementy interfejsu.
     *
     * @param appController   główny kontroler aplikacji
     * @param loginController kontroler odpowiedzialny
     *                        za proces logowania
     */
    public MainFrame(AppController appController,
                     LoginController loginController) {

        this.appController = appController;

        // --- Konfiguracja okna ---
        setTitle("Wirtualny Dziennik");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // =====================================================
        // ================== GÓRNY PASEK ======================
        // =====================================================

        topPanel.setBackground(new Color(235, 240, 245));
        topPanel.setBorder(new EmptyBorder(8, 15, 8, 15));
        topPanel.setVisible(false);

        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        JPanel topRightButtons =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRightButtons.setOpaque(false);

        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> appController.logout());
        topRightButtons.add(logoutButton);

        topPanel.add(topRightButtons, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // =====================================================
        // =============== PANEL GŁÓWNY (KARTY) =================
        // =====================================================

        mainContentPanel.setBackground(new Color(250, 250, 250));

        loginPanel = new LoginPanel(loginController, appController);
        studentPanel = new StudentPanel();
        teacherPanel = new TeacherPanel();
        principalPanel = new PrincipalPanel();

        mainContentPanel.add(loginPanel, "LOGIN");
        mainContentPanel.add(studentPanel, "STUDENT");
        mainContentPanel.add(teacherPanel, "TEACHER");
        mainContentPanel.add(principalPanel, "PRINCIPAL");

        add(mainContentPanel, BorderLayout.CENTER);

        // =====================================================
        // ============== OBSŁUGA ZAMYKANIA OKNA ================
        // =====================================================

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                appController.shutdownApplication();
            }
        });
    }

    /**
     * Wyświetla wskazany panel w głównym obszarze okna.
     * Dodatkowo pokazuje lub ukrywa górny pasek
     * w zależności od widoku.
     *
     * @param name nazwa panelu (LOGIN, STUDENT, TEACHER, PRINCIPAL)
     */
    public void showPanel(String name) {
        topPanel.setVisible(!name.equals("LOGIN"));
        cardLayout.show(mainContentPanel, name);
    }

    /**
     * Wyświetla ekran logowania.
     */
    public void showLoginScreen() {
        showPanel("LOGIN");
    }

    // =====================================================
    // ===================== GETTERY ========================
    // =====================================================

    /** Zwraca etykietę powitalną górnego paska. */
    public JLabel getWelcomeLabel() {
        return welcomeLabel;
    }

    /** Zwraca panel ucznia. */
    public StudentPanel getStudentPanel() {
        return studentPanel;
    }

    /** Zwraca panel nauczyciela. */
    public TeacherPanel getTeacherPanel() {
        return teacherPanel;
    }

    /** Zwraca panel dyrektora. */
    public PrincipalPanel getPrincipalPanel() {
        return principalPanel;
    }
}
