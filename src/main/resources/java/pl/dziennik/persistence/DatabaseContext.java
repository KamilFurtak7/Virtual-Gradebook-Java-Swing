/**
 * Plik: DatabaseContext.java
 *
 * Opis:
 *  Reprezentuje kontekst danych aplikacji dziennika
 *  elektronicznego. Klasa przechowuje kolekcje
 *  najważniejszych obiektów systemu, takich jak użytkownicy,
 *  klasy szkolne oraz wiadomości.
 *
 * Główne elementy:
 *  - Pola: users, classes, messages.
 *  - Gettery – umożliwiają dostęp do kolekcji danych
 *    przechowywanych w kontekście.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Kolekcje obiektów:
 *      Wykorzystanie list List<User>, List<SchoolClass>
 *      oraz List<Message> do przechowywania danych systemu.
 *
 *  - Agregacja obiektów:
 *      Klasa DatabaseContext agreguje obiekty domenowe
 *      i pełni rolę centralnego kontenera danych aplikacji.
 */

package pl.dziennik.persistence;

import pl.dziennik.model.communication.Message;
import pl.dziennik.model.school.SchoolClass;
import pl.dziennik.model.user.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezentująca kontekst danych aplikacji.
 */
public class DatabaseContext {

    // Lista użytkowników systemu
    private final List<User> users = new ArrayList<>();

    // Lista klas szkolnych
    private final List<SchoolClass> classes = new ArrayList<>();

    // Lista wiadomości w systemie
    private final List<Message> messages = new ArrayList<>();

    // --- Gettery ---

    /** Zwraca listę użytkowników systemu. */
    public List<User> getUsers() { return users; }

    /** Zwraca listę klas szkolnych. */
    public List<SchoolClass> getClasses() { return classes; }

    /** Zwraca listę wiadomości w systemie. */
    public List<Message> getMessages() { return messages; }
}
