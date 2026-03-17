package pl.dziennik.main;

import pl.dziennik.controller.AppController;

import com.formdev.flatlaf.FlatLightLaf;
import pl.dziennik.controller.AppController;
import pl.dziennik.controller.LoginController;
import pl.dziennik.persistence.DatabaseContext;
import pl.dziennik.service.impl.*;
import pl.dziennik.service.interfaces.*;
import pl.dziennik.view.MainFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Plik: Main.java
 *
 * Opis:
 *  Główna klasa uruchamiająca aplikację „Wirtualny Dziennik”.
 *  Ustawia wygląd okien (FlatLaf), czcionkę, tworzy obiekty serwisów,
 *  kontrolery oraz główne okno (MainFrame), a następnie startuje aplikację.
 *
 * Główne elementy:
 *  - Metoda main(String[] args) – punkt startowy programu.
 *  - Tworzenie obiektów: bazy danych, serwisów, kontrolerów i okna.
 *  - Uruchomienie logiki w wątku Swing (SwingUtilities.invokeLater).
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Polimorfizm:
 *      Zmienne typu DataAccessService, ReportService, AuthenticationService,
 *      MessageService przechowują obiekty konkretnych klas (FileDataService,
 *      FileReportService, AuthenticationServiceImpl, MessageServiceImpl).
 *  - Klasa anonimowa / lambda:
 *      Użycie wyrażenia lambda w SwingUtilities.invokeLater(...) do uruchomienia
 *      kodu w wątku GUI.
 */
public class Main {

    /**
     * Główna metoda uruchamiająca aplikację.
     */
    public static void main(String[] args) {
    	
    	try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    	
        try {
            // Ustawienie jasnego motywu FlatLaf dla aplikacji
            FlatLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("Nie udało się zainicjować FlatLaf:");
            ex.printStackTrace();
        }

        // Ustawienie domyślnej czcionki w całej aplikacji
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));

        // Uruchomienie tworzenia okna i reszty logiki w wątku Swing
        SwingUtilities.invokeLater(() -> {
            // Tworzymy kontekst bazy danych (połączenie z warstwą danych)
            DatabaseContext dbContext = new DatabaseContext();

            // Tworzymy serwis dostępu do danych oparty na plikach tekstowych
            DataAccessService dataService = new FileDataService(
                    dbContext,
                    "users.txt",
                    "classes.txt",
                    "grades.txt",
                    "timetable.txt",
                    "consultations.txt",
                    "messages.txt"
            );

            // Serwis zapisujący logi/raporty do pliku
            ReportService reportService = new FileReportService("report.log");

            // Serwis odpowiedzialny za logowanie użytkowników
            AuthenticationService authService = new AuthenticationServiceImpl(dbContext, reportService);

            // Serwis do obsługi wiadomości pomiędzy użytkownikami
            MessageService messageService = new MessageServiceImpl(dbContext);

            // Główny kontroler aplikacji
            AppController appController = new AppController(null, dataService, reportService, dbContext, messageService, authService);

            // Kontroler logowania – używa serwisu logowania i głównego kontrolera
            LoginController loginController = new LoginController(authService, appController);

            // Główne okno aplikacji – dostaje oba kontrolery
            MainFrame mainFrame = new MainFrame(appController, loginController);

            // Tutaj przekazujemy okno do AppController
            appController.setMainFrame(mainFrame);
            
            // Start aplikacji: wczytanie danych itp.
            appController.startApplication();

            // Pokazanie ekranu logowania jako pierwszego widoku
            mainFrame.showLoginScreen();

            // Ustawienie okna jako widocznego
            mainFrame.setVisible(true);
        });
    }
}
