/**
 * Plik: StudentPanel.java
 *
 * Opis:
 *  Panel interfejsu użytkownika przeznaczony dla roli UCZNIA
 *  w aplikacji „Wirtualny Dziennik”.
 *
 *  Panel udostępnia uczniowi następujące funkcjonalności:
 *   - podgląd własnych ocen,
 *   - podgląd planu lekcji,
 *   - podgląd nauczycieli,
 *   - obsługę systemu wiadomości.
 *
 *  Klasa pełni rolę View w architekturze MVC.
 */

package pl.dziennik.view.panel;

import pl.dziennik.model.user.Student;
import pl.dziennik.persistence.DatabaseContext;
import pl.dziennik.service.interfaces.MessageService;

import javax.swing.*;
import java.awt.*;

/**
 * Panel główny ucznia.
 * Dziedziczy po klasie JPanel.
 */
public class StudentPanel extends JPanel {

    /* ===================== PODPANELE ===================== */

    // Panel wyświetlający oceny ucznia
    private final GradesDisplayPanel gradesDisplayPanel;

    // Panel planu lekcji
    private final TimetablePanel timetablePanel;

    // Panel wyświetlający nauczycieli ucznia
    private final TeachersDisplayPanel teachersDisplayPanel;

    // Panel systemu wiadomości
    private final MessagingPanel messagingPanel;

    /**
     * Konstruktor – buduje interfejs ucznia.
     */
    public StudentPanel() {

        // Główny układ panelu
        setLayout(new BorderLayout());

        // Brak marginesów – panel wypełnia całą przestrzeń
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Zakładki interfejsu ucznia
        JTabbedPane tabbedPane = new JTabbedPane();

        /* ===================================================== */
        /* ZAKŁADKA: MOJE OCENY                                  */
        /* ===================================================== */

        gradesDisplayPanel = new GradesDisplayPanel();
        JScrollPane gradesScrollPane = new JScrollPane(gradesDisplayPanel);
        gradesScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        gradesScrollPane.setBorder(null);
        tabbedPane.addTab("Moje oceny", gradesScrollPane);

        /* ===================================================== */
        /* ZAKŁADKA: PLAN LEKCJI                                 */
        /* ===================================================== */

        timetablePanel = new TimetablePanel();
        tabbedPane.addTab("Plan lekcji", timetablePanel);

        /* ===================================================== */
        /* ZAKŁADKA: MOI NAUCZYCIELE                              */
        /* ===================================================== */

        teachersDisplayPanel = new TeachersDisplayPanel();
        JScrollPane teachersScrollPane = new JScrollPane(teachersDisplayPanel);
        teachersScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        teachersScrollPane.setBorder(null);
        tabbedPane.addTab("Moi nauczyciele", teachersScrollPane);

        /* ===================================================== */
        /* ZAKŁADKA: WIADOMOŚCI                                  */
        /* ===================================================== */

        messagingPanel = new MessagingPanel();
        tabbedPane.addTab("Wiadomości", messagingPanel);

        // Dodanie zakładek do panelu głównego
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Ustawia dane ucznia w panelu oraz przekazuje
     * wymagane zależności do podpaneli.
     *
     * @param student        aktualnie zalogowany uczeń
     * @param messageService serwis obsługi wiadomości
     * @param dbContext      kontekst bazy danych
     */
    public void setStudentData(Student student,
                               MessageService messageService,
                               DatabaseContext dbContext) {

        // Wyświetlenie ocen ucznia
        gradesDisplayPanel.displayGrades(student.getGrades());

        // Jeżeli uczeń jest przypisany do klasy – pokaż plan i nauczycieli
        if (student.getSchoolClass() != null) {
            timetablePanel.setTimetableData(student.getSchoolClass());
            teachersDisplayPanel.displayTeachers(student);
        }

        // Przekazanie danych do panelu wiadomości
        messagingPanel.setMessagingData(student, messageService, dbContext);
    }
}
