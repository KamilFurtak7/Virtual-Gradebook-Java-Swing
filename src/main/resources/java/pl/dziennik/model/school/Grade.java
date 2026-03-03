/**
 * Plik: Grade.java
 *
 * Opis:
 *  Reprezentuje pojedynczą ocenę wystawioną w systemie dziennika
 *  elektronicznego. Klasa przechowuje informacje o wartości oceny,
 *  jej wadze, opisie, przedmiocie, nauczycielu oraz dacie wystawienia.
 *
 * Główne elementy:
 *  - Pola: value, weight, description, subject, teacher, date.
 *  - Konstruktor Grade(...) – tworzy nową ocenę i automatycznie ustawia
 *    datę jej wystawienia na bieżący dzień.
 *  - Gettery – umożliwiają odczyt wszystkich danych oceny.
 */

package pl.dziennik.model.school;

import pl.dziennik.model.user.Teacher;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Model reprezentujący pojedynczą ocenę w systemie szkolnym.
 */
public class Grade implements Serializable {

    // Wersja klasy używana przy serializacji (zapis/odczyt obiektów)
    private static final long serialVersionUID = 1L;

    // Wartość liczbowa oceny (np. 1–6)
    private final double value;

    // Waga oceny (np. sprawdzian, kartkówka)
    private final int weight;

    // Opis lub komentarz do oceny
    private final String description;

    // Przedmiot, z którego wystawiono ocenę
    private final Subject subject;

    // Nauczyciel wystawiający ocenę
    private final Teacher teacher;

    // Data wystawienia oceny
    private final LocalDate date;

    /**
     * Tworzy nową ocenę z podanymi danymi.
     * Data wystawienia ustawiana jest automatycznie
     * na bieżący dzień.
     *
     * @param value       wartość liczbowa oceny
     * @param weight      waga oceny
     * @param description opis lub komentarz do oceny
     * @param subject     przedmiot, z którego wystawiono ocenę
     * @param teacher     nauczyciel wystawiający ocenę
     */
    public Grade(double value, int weight, String description,
                 Subject subject, Teacher teacher) {
        this.value = value;
        this.weight = weight;
        this.description = description;
        this.subject = subject;
        this.teacher = teacher;
        this.date = LocalDate.now();
    }

    // --- Gettery ---

    /** Zwraca wartość liczbową oceny. */
    public double getValue() { return value; }

    /** Zwraca wagę oceny. */
    public int getWeight() { return weight; }

    /** Zwraca opis lub komentarz do oceny. */
    public String getDescription() { return description; }

    /** Zwraca przedmiot, z którego wystawiono ocenę. */
    public Subject getSubject() { return subject; }

    /** Zwraca nauczyciela wystawiającego ocenę. */
    public Teacher getTeacher() { return teacher; }

    /** Zwraca datę wystawienia oceny. */
    public LocalDate getDate() { return date; }
}
