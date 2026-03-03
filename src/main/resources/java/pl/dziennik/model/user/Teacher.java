/**
 * Plik: Teacher.java
 *
 * Opis:
 *  Reprezentuje nauczyciela w systemie dziennika elektronicznego.
 *  Klasa rozszerza klasę User i przechowuje informacje
 *  o konsultacjach prowadzonych przez nauczyciela.
 *
 * Główne elementy:
 *  - Dziedziczenie po klasie User.
 *  - Pole consultations – lista konsultacji nauczyciela.
 *  - Metody do zarządzania konsultacjami.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Dziedziczenie:
 *      Klasa Teacher rozszerza klasę User.
 *
 *  - Polimorfizm:
 *      Nadpisanie metody getRole() z klasy User,
 *      umożliwiające identyfikację roli nauczyciela.
 *
 *  - Kolekcje obiektów:
 *      Wykorzystanie listy List<Consultation> do przechowywania
 *      konsultacji przypisanych do nauczyciela.
 */

package pl.dziennik.model.user;

import pl.dziennik.model.school.Consultation;
import java.util.ArrayList;
import java.util.List;

/**
 * Model reprezentujący nauczyciela.
 */
public class Teacher extends User {

    // Wersja klasy używana przy serializacji (dziedziczona po klasie User)
    private static final long serialVersionUID = 1L;

    // Lista konsultacji prowadzonych przez nauczyciela
    private final List<Consultation> consultations = new ArrayList<>();

    /**
     * Tworzy nowego użytkownika o roli nauczyciela.
     *
     * @param login     login użytkownika
     * @param password  hasło użytkownika
     * @param firstName imię nauczyciela
     * @param lastName  nazwisko nauczyciela
     */
    public Teacher(String login, String password,
                   String firstName, String lastName) {
        super(login, password, firstName, lastName);
    }

    /**
     * Zwraca rolę użytkownika w systemie.
     *
     * @return stała wartość "TEACHER"
     */
    @Override
    public String getRole() {
        return "TEACHER";
    }

    // --- Konsultacje ---

    /**
     * Dodaje konsultację do listy konsultacji nauczyciela.
     *
     * @param consultation konsultacja do dodania
     */
    public void addConsultation(Consultation consultation) {
        this.consultations.add(consultation);
    }

    /** Zwraca listę konsultacji nauczyciela. */
    public List<Consultation> getConsultations() {
        return consultations;
    }
}
