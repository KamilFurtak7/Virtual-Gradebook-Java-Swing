/**
 * Plik: GradesDisplayPanel.java
 *
 * Opis:
 *  Panel interfejsu użytkownika odpowiedzialny za wyświetlanie
 *  ocen ucznia w sposób pogrupowany według przedmiotów.
 *  Oceny prezentowane są w formie kafelków, zorganizowanych
 *  w sekcje odpowiadające poszczególnym przedmiotom.
 *
 * Główne elementy:
 *  - Dziedziczenie po klasie JPanel.
 *  - Metoda displayGrades(...) – grupuje i wyświetla oceny.
 *  - Wykorzystanie Stream API do grupowania ocen.
 *  - Kompozycja widoków (nagłówki, separatory, kafelki ocen).
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Dziedziczenie:
 *      Klasa GradesDisplayPanel dziedziczy po JPanel.
 *
 *  - Kolekcje obiektów:
 *      Wykorzystanie List<Grade> oraz Map<Subject, List<Grade>>
 *      do przetwarzania i organizacji danych.
 *
 *  - Programowanie funkcyjne:
 *      Zastosowanie Stream API oraz Collectors.groupingBy(...)
 *      do grupowania ocen według przedmiotu.
 *
 *  - Kompozycja obiektów:
 *      Panel składa się z wielu komponentów Swing,
 *      w tym GradeTilePanel reprezentujących pojedyncze oceny.
 *
 *  - Wzorzec MVC:
 *      Klasa pełni rolę View i prezentuje dane przekazane
 *      z warstwy kontrolera lub serwisu.
 */

package pl.dziennik.view.panel;

import pl.dziennik.model.school.Grade;
import pl.dziennik.model.school.Subject;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Panel wyświetlający oceny ucznia pogrupowane według przedmiotów.
 */
public class GradesDisplayPanel extends JPanel {

    /**
     * Tworzy panel do wyświetlania ocen.
     * Panel używa układu BoxLayout, aby sekcje
     * przedmiotów były rozmieszczone pionowo.
     */
    public GradesDisplayPanel() {

        // Układ pionowy – każda sekcja przedmiotu pod sobą
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        // Marginesy wewnętrzne panelu
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    /**
     * Wyświetla listę ocen pogrupowanych według przedmiotów.
     * Panel jest każdorazowo czyszczony i budowany od nowa
     * na podstawie przekazanych danych.
     *
     * @param grades lista ocen ucznia
     */
    public void displayGrades(List<Grade> grades) {

        // Usunięcie poprzedniej zawartości panelu
        this.removeAll();

        // Grupowanie ocen według przedmiotu z użyciem Stream API
        Map<Subject, List<Grade>> groupedGrades = grades.stream()
                .collect(Collectors.groupingBy(Grade::getSubject));

        // Tworzenie sekcji dla każdego przedmiotu
        for (Map.Entry<Subject, List<Grade>> entry : groupedGrades.entrySet()) {

            Subject subject = entry.getKey();
            List<Grade> subjectGrades = entry.getValue();

            // --- Nagłówek przedmiotu (np. "Matematyka") ---
            JLabel subjectHeader = new JLabel(subject.getName());
            subjectHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
            subjectHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(subjectHeader);

            // --- Separator ---
            add(Box.createRigidArea(new Dimension(0, 5)));
            add(new JSeparator());
            add(Box.createRigidArea(new Dimension(0, 10)));

            // --- Panel kafelków z ocenami ---
            JPanel tilesPanel =
                    new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            tilesPanel.setBackground(Color.WHITE);
            tilesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Dodawanie kafelków ocen
            for (Grade grade : subjectGrades) {
                tilesPanel.add(new GradeTilePanel(grade));
            }

            add(tilesPanel);

            // Odstęp między sekcjami przedmiotów
            add(Box.createRigidArea(new Dimension(0, 25)));
        }

        // Odświeżenie widoku po zmianach
        revalidate();
        repaint();
    }
}
