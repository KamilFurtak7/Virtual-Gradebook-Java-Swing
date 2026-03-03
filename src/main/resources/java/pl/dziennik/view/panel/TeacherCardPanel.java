/**
 * Plik: TeacherCardPanel.java
 *
 * Opis:
 *  Panel interfejsu użytkownika wyświetlający informacje
 *  o pojedynczym nauczycielu w formie karty.
 *
 *  Karta nauczyciela zawiera:
 *   - imię i nazwisko nauczyciela,
 *   - listę prowadzonych przedmiotów lub informację o funkcji wychowawcy,
 *   - godziny konsultacji (jeśli są dostępne).
 *
 *  Klasa pełni rolę elementu widoku (View) w architekturze MVC.
 */

package pl.dziennik.view.panel;

import pl.dziennik.model.school.Consultation;
import pl.dziennik.model.school.Subject;
import pl.dziennik.model.user.Teacher;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Panel prezentujący kartę nauczyciela.
 * Dziedziczy po klasie JPanel.
 */
public class TeacherCardPanel extends JPanel {

    /**
     * Formatter czasu używany do wyświetlania godzin konsultacji.
     */
    private final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Tworzy kartę nauczyciela wraz z informacjami o przedmiotach
     * oraz konsultacjach.
     *
     * @param teacher        obiekt nauczyciela
     * @param subjectsTaught zbiór przedmiotów prowadzonych przez nauczyciela
     */
    public TeacherCardPanel(Teacher teacher, Set<Subject> subjectsTaught) {

        // Główny układ karty
        setLayout(new BorderLayout());

        // Obramowanie karty (linia + wewnętrzny margines)
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(300, 150));

        // Panel z informacjami tekstowymi
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Imię i nazwisko nauczyciela
        JLabel nameLabel = new JLabel(teacher.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Lista przedmiotów prowadzonych przez nauczyciela
        String subjects = subjectsTaught.stream()
                .map(Subject::getName)
                .collect(Collectors.joining(", "));

        boolean isHomeroomTeacher = false;

        // Obsługa sytuacji, gdy nauczyciel nie prowadzi zajęć,
        // ale pełni funkcję wychowawcy
        if (subjects.isEmpty() && !teacher.getConsultations().isEmpty()) {
            subjects = "Wychowawca";
            isHomeroomTeacher = true;
        } else if (subjects.isEmpty()) {
            subjects = "Brak przypisanych zajęć";
        }

        JLabel subjectsLabel = new JLabel(
                (isHomeroomTeacher ? "Funkcja: " : "Przedmioty: ") + subjects
        );
        subjectsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subjectsLabel.setForeground(Color.GRAY);

        infoPanel.add(nameLabel);
        infoPanel.add(subjectsLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        // Sekcja konsultacji (jeśli istnieją)
        if (!teacher.getConsultations().isEmpty()) {

            JLabel consultationHeader = new JLabel("Konsultacje:");
            consultationHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
            infoPanel.add(consultationHeader);

            for (Consultation consultation : teacher.getConsultations()) {

                String text = String.format(
                        "%s, %s - %s (s. %s)",
                        consultation.getDayOfWeek(),
                        consultation.getStartTime().format(timeFormatter),
                        consultation.getEndTime().format(timeFormatter),
                        consultation.getRoom()
                );

                JLabel consultationLabel = new JLabel(text);
                consultationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                infoPanel.add(consultationLabel);
            }
        }

        // Dodanie panelu informacji do karty
        add(infoPanel, BorderLayout.CENTER);
    }
}
