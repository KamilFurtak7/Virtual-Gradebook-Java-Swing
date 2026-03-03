/**
 * Plik: LoginPanel.java
 *
 * Opis:
 *  Panel logowania interfejsu graficznego systemu dziennika
 *  elektronicznego. Klasa odpowiada za wyświetlenie formularza
 *  logowania oraz obsługę zdarzeń użytkownika, takich jak
 *  kliknięcie przycisku lub naciśnięcie klawisza Enter.
 *
 * Główne elementy:
 *  - Pola: loginField, passwordField, loginButton, exitButton.
 *  - Konstruktor LoginPanel(...) – buduje interfejs logowania
 *    oraz rejestruje obsługę zdarzeń.
 *  - Metoda pomocnicza addFormField(...) – upraszcza dodawanie
 *    pól formularza do layoutu.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Dziedziczenie:
 *      Klasa LoginPanel dziedziczy po JPanel.
 *
 *  - Wzorzec MVC:
 *      Panel pełni rolę View i komunikuje się z warstwą
 *      Controller (LoginController, AppController).
 *
 *  - Klasy anonimowe:
 *      Anonimowe implementacje ActionListener oraz KeyAdapter
 *      do obsługi zdarzeń interfejsu użytkownika.
 *
 *  - Obsługa zdarzeń (Event Handling):
 *      Reakcja na kliknięcia przycisków oraz naciśnięcie
 *      klawisza Enter.
 */

package pl.dziennik.view;

import pl.dziennik.controller.AppController;
import pl.dziennik.controller.LoginController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Panel logowania aplikacji.
 */
public class LoginPanel extends JPanel {

    // Pole tekstowe do wprowadzania loginu
    private final JTextField loginField = new JTextField(20);

    // Pole hasła użytkownika
    private final JPasswordField passwordField = new JPasswordField(20);

    // Przycisk logowania
    private final JButton loginButton = new JButton("Zaloguj się");

    // Przycisk zamknięcia aplikacji
    private final JButton exitButton = new JButton("Wyjdź");

    /**
     * Tworzy panel logowania i konfiguruje jego wygląd
     * oraz obsługę zdarzeń użytkownika.
     *
     * @param controller     kontroler odpowiedzialny za logikę logowania
     * @param appController  główny kontroler aplikacji
     */
    public LoginPanel(LoginController controller,
                      AppController appController) {

        setLayout(new GridBagLayout());
        setBackground(new Color(240, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 50, 5, 50);

        // --- Tytuł ---
        JLabel titleLabel =
                new JLabel("Wirtualny Dziennik", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(50, 50, 100));

        gbc.insets = new Insets(20, 50, 40, 50);
        add(titleLabel, gbc);

        // --- Pola formularza ---
        gbc.insets = new Insets(5, 50, 5, 50);
        addFormField("Login:", loginField, gbc);
        addFormField("Hasło:", passwordField, gbc);

        // --- Przycisk logowania ---
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);

        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(150, 35));
        add(loginButton, gbc);

        // --- Przycisk wyjścia ---
        gbc.insets = new Insets(5, 5, 50, 5);
        exitButton.setPreferredSize(new Dimension(150, 35));
        add(exitButton, gbc);

        // =====================================================
        // ================== LOGIKA ZDARZEŃ ===================
        // =====================================================

        // Obsługa kliknięcia przycisku logowania
        loginButton.addActionListener(e -> {
            String login = loginField.getText();
            String password = new String(passwordField.getPassword());

            controller.attemptLogin(login, password);
        });

        // Obsługa zamknięcia aplikacji
        exitButton.addActionListener(
                e -> appController.shutdownApplication());

        // Obsługa klawisza Enter (logowanie z klawiatury)
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick(); // Symulacja kliknięcia
                }
            }
        };

        loginField.addKeyListener(enterListener);
        passwordField.addKeyListener(enterListener);
    }

    /**
     * Dodaje pole formularza (etykieta + komponent)
     * do panelu z użyciem GridBagLayout.
     *
     * @param labelText tekst etykiety
     * @param field     komponent formularza
     * @param gbc       constraints układu
     */
    private void addFormField(String labelText,
                              JComponent field,
                              GridBagConstraints gbc) {

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        add(label, gbc);

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(field, gbc);
    }
}
