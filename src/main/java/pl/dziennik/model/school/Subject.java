/**
 * Plik: Subject.java
 *
 * Opis:
 *  Reprezentuje przedmiot szkolny w systemie dziennika
 *  elektronicznego. Klasa przechowuje nazwę przedmiotu
 *  i umożliwia poprawne porównywanie obiektów typu Subject.
 *
 * Główne elementy:
 *  - Pole: name.
 *  - Konstruktor Subject(...) – tworzy nowy przedmiot o podanej nazwie.
 *  - Metody equals() i hashCode() – umożliwiają logiczne porównywanie
 *    obiektów oraz ich poprawne użycie w kolekcjach.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Polimorfizm:
 *      Nadpisanie metod equals() oraz hashCode()
 *      z klasy Object.
 */

package pl.dziennik.model.school;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model reprezentujący przedmiot szkolny.
 */
public class Subject implements Serializable {

    // Wersja klasy używana przy serializacji (zapis/odczyt obiektów)
    private static final long serialVersionUID = 1L;

    // Nazwa przedmiotu (np. "Matematyka")
    private final String name;

    /**
     * Tworzy nowy przedmiot o podanej nazwie.
     *
     * @param name nazwa przedmiotu
     */
    public Subject(String name) {
        this.name = name;
    }

    // --- Gettery ---

    /** Zwraca nazwę przedmiotu. */
    public String getName() { return name; }

    /**
     * Sprawdza równość dwóch obiektów Subject.
     * Dwa przedmioty są uznawane za równe, jeśli mają
     * taką samą nazwę.
     *
     * @param o obiekt do porównania
     * @return true, jeśli obiekty są równe; false w przeciwnym razie
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(name, subject.name);
    }

    /**
     * Zwraca kod haszujący obiektu Subject.
     * Metoda jest zgodna z implementacją equals().
     *
     * @return kod haszujący obiektu
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
