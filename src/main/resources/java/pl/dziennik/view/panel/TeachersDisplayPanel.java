/**
 * Plik: TeachersDisplayPanel.java
 *
 * Opis:
 *  Panel widoku odpowiedzialny za prezentację nauczycieli uczących
 *  danego ucznia w formie kart (TeacherCardPanel).
 *
 *  Panel:
 *   - analizuje plan lekcji klasy ucznia,
 *   - grupuje nauczycieli wraz z przedmiotami,
 *   - uwzględnia wychowawcę klasy,
 *   - dynamicznie generuje karty nauczycieli.
 *
 *  Rola w architekturze:
 *   - WARSTWA WIDOKU (View – MVC)
 */

package pl.dziennik.view.panel;

import pl.dziennik.model.school.Subject;
import pl.dziennik.model.user.Student;
import pl.dziennik.model.user.Teacher;
import pl.dziennik.model.school.TimetableEntry;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Panel wyświetlający nauczycieli przypisanych do ucznia.
 *
 * Dziedziczenie:
 *  - JPanel → TeachersDisplayPanel
 */
public class TeachersDisplayPanel extends JPanel {

    /**
     * Konstruktor – konfiguruje wygląd i układ panelu.
     */
    public TeachersDisplayPanel() {

        /*
         * Użycie niestandardowego WrapLayout (rozszerzenie FlowLayout),
         * który automatycznie zawija komponenty do nowej linii.
         *
         * -> przykład POLIMORFIZMU:
         *    WrapLayout używany jako LayoutManager
         */
        setLayout(new WrapLayout(FlowLayout.LEFT, 20, 20));

        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Główna metoda odpowiedzialna za:
     *  - zebranie danych o nauczycielach ucznia,
     *  - pogrupowanie ich według przedmiotów,
     *  - wygenerowanie kart nauczycieli.
     *
     * @param student aktualnie zalogowany uczeń
     */
    public void displayTeachers(Student student) {

        // Czyścimy panel przed ponownym generowaniem kart
        this.removeAll();

        // Jeśli uczeń nie jest przypisany do klasy – brak danych
        if (student.getSchoolClass() == null) {
            return;
        }

        /*
         * === KROK 1: Zbieranie nauczycieli z planu lekcji ===
         *
         * Wykorzystane elementy:
         *  - Kolekcje (Map, Set)
         *  - Stream API
         *  - groupingBy + mapping
         *
         * Tworzymy mapę:
         *   Teacher -> Set<Subject>
         */
        Map<Teacher, Set<Subject>> teacherSubjectsMap =
                student.getSchoolClass()
                       .getTimetable()
                       .values()
                       .stream()
                       .flatMap(List::stream)
                       .collect(Collectors.groupingBy(
                               TimetableEntry::getTeacher,
                               Collectors.mapping(
                                       TimetableEntry::getSubject,
                                       Collectors.toSet()
                               )
                       ));

        /*
         * === KROK 2: Dodanie wychowawcy klasy ===
         *
         * Nawet jeśli wychowawca nie prowadzi lekcji w planie,
         * powinien być widoczny na liście nauczycieli.
         */
        Teacher homeroomTeacher = student.getSchoolClass().getHomeroomTeacher();
        if (homeroomTeacher != null) {
            teacherSubjectsMap.computeIfAbsent(
                    homeroomTeacher,
                    k -> new HashSet<>()
            );
        }

        /*
         * === KROK 3: Generowanie kart nauczycieli ===
         *
         * Kompozycja:
         *  - TeachersDisplayPanel zawiera wiele TeacherCardPanel
         */
        if (teacherSubjectsMap.isEmpty()) {

            // Brak nauczycieli – komunikat informacyjny
            add(new JLabel("Nie znaleziono nauczycieli dla tej klasy."));

        } else {

            // Dla każdego nauczyciela tworzymy kartę
            for (Map.Entry<Teacher, Set<Subject>> entry : teacherSubjectsMap.entrySet()) {

                /*
                 * Tworzymy nowy komponent TeacherCardPanel
                 * i dodajemy go do panelu.
                 */
                add(new TeacherCardPanel(
                        entry.getKey(),
                        entry.getValue()
                ));
            }
        }

        // Odświeżenie widoku
        revalidate();
        repaint();
    }
}
