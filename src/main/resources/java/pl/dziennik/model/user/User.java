/**
 * Plik: User.java
 *
 * Opis:
 *  Reprezentuje abstrakcyjnego użytkownika systemu dziennika
 *  elektronicznego. Klasa stanowi bazę dla wszystkich typów
 *  użytkowników (np. uczeń, nauczyciel, dyrektor) i definiuje
 *  wspólne cechy oraz zachowania.
 *
 * Główne elementy:
 *  - Pola: login, password, firstName, lastName.
 *  - Konstruktor User(...) – inicjalizuje podstawowe dane użytkownika.
 *  - Metoda abstrakcyjna getRole() – wymusza określenie roli użytkownika
 *    w klasach pochodnych.
 *  - Metody equals() i hashCode() – umożliwiają logiczne porównywanie
 *    użytkowników.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Klasa abstrakcyjna:
 *      Klasa User jest klasą abstrakcyjną i nie może być
 *      bezpośrednio instancjonowana.
 *
 *  - Dziedziczenie:
 *      Klasa User stanowi klasę bazową dla klas
 *      Student, Teacher oraz Principal.
 *
 *  - Polimorfizm:
 *      Metoda abstrakcyjna getRole() jest implementowana
 *      w klasach pochodnych, co pozwala na rozróżnianie
 *      typów użytkowników w czasie działania programu.
 *
 *  - Enkapsulacja:
 *      Pola klasy są prywatne i dostępne wyłącznie
 *      poprzez metody dostępowe.
 *
 *  - Interfejsy:
 *      Implementacja interfejsu Serializable umożliwia
 *      zapis i odczyt obiektów użytkowników.
 */

package pl.dziennik.model.user;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstrakcyjna klasa bazowa reprezentująca użytkownika systemu.
 */
public abstract class User implements Serializable {

    // Wersja klasy używana przy serializacji (zapis/odczyt obiektów)
    private static final long serialVersionUID = 1L;

    // Login użytkownika (unikalny identyfikator)
    private String login;

    // Hasło użytkownika
    private String password;

    // Imię użytkownika
    private String firstName;

    // Nazwisko użytkownika
    private String lastName;

    /**
     * Tworzy nowego użytkownika z podanymi danymi.
     *
     * @param login     login użytkownika
     * @param password  hasło użytkownika
     * @param firstName imię użytkownika
     * @param lastName  nazwisko użytkownika
     */
    public User(String login, String password,
                String firstName, String lastName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Zwraca rolę użytkownika w systemie.
     * Metoda musi być zaimplementowana
     * w klasach pochodnych.
     *
     * @return nazwa roli użytkownika
     */
    public abstract String getRole();

    // --- Gettery ---

    /** Zwraca login użytkownika. */
    public String getLogin() { return login; }

    /** Zwraca hasło użytkownika. */
    public String getPassword() { return password; }

    /** Zwraca imię użytkownika. */
    public String getFirstName() { return firstName; }

    /** Zwraca nazwisko użytkownika. */
    public String getLastName() { return lastName; }

    /** Zwraca imię i nazwisko użytkownika w jednej postaci tekstowej. */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Sprawdza równość dwóch użytkowników.
     * Użytkownicy są uznawani za równych,
     * jeśli posiadają ten sam login.
     *
     * @param o obiekt do porównania
     * @return true, jeśli obiekty są równe; false w przeciwnym razie
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(login, user.login);
    }

    /**
     * Zwraca kod haszujący użytkownika.
     * Metoda jest zgodna z implementacją equals().
     *
     * @return kod haszujący użytkownika
     */
    @Override
    public int hashCode() {
        return Objects.hash(login);
    }
}
