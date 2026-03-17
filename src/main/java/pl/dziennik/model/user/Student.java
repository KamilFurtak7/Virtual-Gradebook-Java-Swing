/**
 * Plik: Student.java
 *
 * Opis:
 *  Reprezentuje ucznia w systemie dziennika elektronicznego.
 *  Klasa rozszerza klasę User i przechowuje informacje
 *  o ocenach ucznia oraz klasie szkolnej, do której należy.
 *
 * Główne elementy:
 *  - Dziedziczenie po klasie User.
 *  - Pole grades – lista ocen ucznia.
 *  - Pole schoolClass – klasa szkolna ucznia.
 *  - Metody zarządzające ocenami i przypisaniem do klasy.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Dziedziczenie:
 *      Klasa Student rozszerza klasę User.
 *
 *  - Polimorfizm:
 *      Nadpisanie metody getRole() z klasy User,
 *      umożliwiające identyfikację roli ucznia w systemie.
 *
 *  - Kolekcje obiektów:
 *      Wykorzystanie listy List<Grade> do przechowywania
 *      ocen przypisanych do ucznia.
 *
 *  - Relacje między obiektami (asocjacja):
 *      Klasa Student przechowuje referencję do obiektu
 *      SchoolClass, reprezentując przynależność ucznia
 *      do klasy szkolnej.
 */

package pl.dziennik.model.user;

import pl.dziennik.model.school.Grade;
import pl.dziennik.model.school.SchoolClass;
import java.util.ArrayList;
import java.util.List;

/**
 * Model reprezentujący ucznia.
 */
public class Student extends User {

    // Wersja klasy używana przy serializacji (dziedziczona po klasie User)
    private static final long serialVersionUID = 1L;

    // Lista ocen przypisanych do ucznia
    private final List<Grade> grades = new ArrayList<>();

    // Klasa szkolna, do której należy uczeń
    private SchoolClass schoolClass;

    /**
     * Tworzy nowego użytkownika o roli ucznia.
     *
     * @param login     login użytkownika
     * @param password  hasło użytkownika
     * @param firstName imię ucznia
     * @param lastName  nazwisko ucznia
     */
    public Student(String login, String password,
                   String firstName, String lastName) {
        super(login, password, firstName, lastName);
    }

    /**
     * Zwraca rolę użytkownika w systemie.
     *
     * @return stała wartość "STUDENT"
     */
    @Override
    public String getRole() {
        return "STUDENT";
    }

    // --- Oceny ucznia ---

    /** Zwraca listę ocen ucznia. */
    public List<Grade> getGrades() {
        return grades;
    }

    /**
     * Dodaje ocenę do listy ocen ucznia.
     *
     * @param grade ocena do dodania
     */
    public void addGrade(Grade grade) {
        this.grades.add(grade);
    }

    // --- Klasa szkolna ---

    /** Zwraca klasę szkolną, do której należy uczeń. */
    public SchoolClass getSchoolClass() {
        return schoolClass;
    }

    /**
     * Ustawia klasę szkolną ucznia.
     *
     * @param schoolClass klasa szkolna
     */
    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }
}
