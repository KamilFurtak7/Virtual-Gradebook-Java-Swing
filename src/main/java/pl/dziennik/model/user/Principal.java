/**
 * Plik: Principal.java
 *
 * Opis:
 *  Reprezentuje dyrektora szkoły w systemie dziennika
 *  elektronicznego. Klasa rozszerza klasę User i określa
 *  rolę użytkownika jako dyrektor.
 *
 * Główne elementy:
 *  - Dziedziczenie po klasie User.
 *  - Konstruktor Principal(...) – tworzy użytkownika
 *    o roli dyrektora.
 *  - Metoda getRole() – zwraca stałą wartość określającą
 *    rolę użytkownika w systemie.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Dziedziczenie:
 *      Klasa Principal rozszerza klasę User.
 *
 *  - Polimorfizm:
 *      Nadpisanie metody getRole() z klasy User,
 *      umożliwiające rozróżnianie ról użytkowników
 *      w systemie.
 */

package pl.dziennik.model.user;

/**
 * Model reprezentujący dyrektora szkoły.
 */
public class Principal extends User {

    // Wersja klasy używana przy serializacji (dziedziczona po klasie User)
    private static final long serialVersionUID = 1L;

    /**
     * Tworzy nowego użytkownika o roli dyrektora szkoły.
     *
     * @param login     login użytkownika
     * @param password  hasło użytkownika
     * @param firstName imię użytkownika
     * @param lastName  nazwisko użytkownika
     */
    public Principal(String login, String password,
                     String firstName, String lastName) {
        super(login, password, firstName, lastName);
    }

    /**
     * Zwraca rolę użytkownika w systemie.
     *
     * @return stała wartość "PRINCIPAL"
     */
    @Override
    public String getRole() {
        return "PRINCIPAL";
    }
}
