/**
 * Plik: SchoolClass.java
 *
 * Opis:
 *  Reprezentuje klasę szkolną w systemie dziennika elektronicznego.
 *  Klasa przechowuje informacje o nazwie klasy, wychowawcy,
 *  liście uczniów oraz planie lekcji.
 *
 * Główne elementy:
 *  - Pola: name, homeroomTeacher, students, timetable.
 *  - Konstruktor SchoolClass(...) – tworzy nową klasę szkolną
 *    o podanej nazwie.
 *  - Metody zarządzające uczniami – dodawanie i usuwanie uczniów.
 *  - Metody zarządzające planem lekcji – dodawanie wpisów do planu.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Kolekcje obiektów:
 *      Wykorzystanie listy List<Student> do przechowywania uczniów
 *      oraz mapy Map<String, List<TimetableEntry>> do planu lekcji.
 *
 *  - Relacje między obiektami (kompozycja/asocjacja):
 *      Klasa SchoolClass przechowuje referencje do obiektów
 *      Teacher, Student oraz TimetableEntry.
 *
 *  - Spójność relacji obiektów:
 *      Podczas dodawania ucznia ustawiana jest również
 *      referencja do klasy szkolnej po stronie obiektu Student.
 *
 *  - Serializacja:
 *      Implementacja interfejsu Serializable umożliwia
 *      zapis i odczyt obiektów klasy SchoolClass.
 */

package pl.dziennik.model.school;

import pl.dziennik.model.user.Student;
import pl.dziennik.model.user.Teacher;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model reprezentujący klasę szkolną w systemie.
 */
public class SchoolClass implements Serializable {

    // Wersja klasy używana przy serializacji (zapis/odczyt obiektów)
    private static final long serialVersionUID = 1L;

    // Nazwa klasy (np. "3A")
    private final String name;

    // Wychowawca klasy
    private Teacher homeroomTeacher;

    // Lista uczniów należących do klasy
    private final List<Student> students = new ArrayList<>();

    // Plan lekcji – mapowanie dnia tygodnia na listę wpisów planu
    private final Map<String, List<TimetableEntry>> timetable = new HashMap<>();

    /**
     * Tworzy nową klasę szkolną o podanej nazwie.
     *
     * @param name nazwa klasy (np. "1B", "3A")
     */
    public SchoolClass(String name) {
        this.name = name;
    }

    // --- Gettery ---

    /** Zwraca nazwę klasy. */
    public String getName() { return name; }

    /** Zwraca wychowawcę klasy. */
    public Teacher getHomeroomTeacher() { return homeroomTeacher; }

    /** Zwraca listę uczniów należących do klasy. */
    public List<Student> getStudents() { return students; }

    /** Zwraca plan lekcji klasy. */
    public Map<String, List<TimetableEntry>> getTimetable() { return timetable; }

    // --- Settery i metody logiczne ---

    /**
     * Ustawia wychowawcę klasy.
     *
     * @param teacher nauczyciel pełniący rolę wychowawcy
     */
    public void setHomeroomTeacher(Teacher teacher) {
        this.homeroomTeacher = teacher;
    }

    /**
     * Dodaje ucznia do klasy.
     * Jeśli uczeń już należy do klasy, nie zostaje dodany ponownie.
     * Dodatkowo ustawia klasę szkolną po stronie obiektu Student.
     *
     * @param student uczeń, który ma zostać dodany do klasy
     */
    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
            student.setSchoolClass(this);
        }
    }

    /**
     * Dodaje wpis do planu lekcji dla danego dnia tygodnia.
     *
     * @param day   dzień tygodnia (np. "Monday")
     * @param entry wpis planu lekcji
     */
    public void addTimetableEntry(String day, TimetableEntry entry) {
        timetable.computeIfAbsent(day, k -> new ArrayList<>()).add(entry);
    }

    /**
     * Usuwa ucznia z klasy.
     *
     * @param student uczeń do usunięcia
     */
    public void removeStudent(Student student) {
        students.remove(student);
    }
}
