/**
 * Plik: Message.java
 *
 * Opis:
 *  Reprezentuje jedną wiadomość w systemie (nadawca, odbiorca, tytuł, treść,
 *  data wysłania i informacja, czy została przeczytana).
 *
 * Główne elementy:
 *  - Pola: sender, recipient, title, content, timestamp, isRead.
 *  - Konstruktor Message(...) – tworzy nową wiadomość z podanymi danymi.
 *  - getFormattedHeader() – zwraca krótki opis wiadomości (np. do listy).
 *  - getFormattedSenderRecipient(boolean showSender) – zwraca nadawcę lub
 *    odbiorcę w czytelnej formie (imię i nazwisko + login).
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Serializacja:
 *      Klasa Message może być zapisywana i odczytywana z pliku,
 *      ponieważ implementuje Serializable i ma stałą serialVersionUID.
 */

package pl.dziennik.model.communication;

import pl.dziennik.model.user.User;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model reprezentujący pojedynczą wiadomość w systemie.
 */
public class Message implements Serializable {

    // Wersja klasy używana przy serializacji (zapis/odczyt obiektów)
    private static final long serialVersionUID = 1L;

    // Formatter do zapisu/odczytu daty w pliku w formacie ISO (np. 2025-11-13T14:30:00)
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Użytkownik, który wysłał wiadomość
    private final User sender;

    // Użytkownik, który otrzymuje wiadomość
    private final User recipient;

    // Tytuł wiadomości
    private final String title;

    // Treść wiadomości
    private final String content;

    // Data i godzina wysłania wiadomości
    private final LocalDateTime timestamp;

    // Informacja, czy wiadomość została już przeczytana
    private boolean isRead;

    /**
     * Tworzy nową wiadomość z podanymi danymi.
     *
     * @param sender    nadawca wiadomości
     * @param recipient odbiorca wiadomości
     * @param title     tytuł wiadomości
     * @param content   treść wiadomości
     * @param timestamp data i godzina wysłania
     * @param isRead    czy wiadomość jest już przeczytana
     */
    public Message(User sender, User recipient, String title, String content, LocalDateTime timestamp, boolean isRead) {
        this.sender = sender;
        this.recipient = recipient;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // --- Gettery ---

    /** Zwraca nadawcę wiadomości. */
    public User getSender() { return sender; }

    /** Zwraca odbiorcę wiadomości. */
    public User getRecipient() { return recipient; }

    /** Zwraca tytuł wiadomości. */
    public String getTitle() { return title; }

    /** Zwraca treść wiadomości. */
    public String getContent() { return content; }

    /** Zwraca datę i godzinę wysłania wiadomości. */
    public LocalDateTime getTimestamp() { return timestamp; }

    /** Zwraca informację, czy wiadomość została już przeczytana. */
    public boolean isRead() { return isRead; }

    // Setter tylko dla statusu odczytania

    /**
     * Ustawia status przeczytania wiadomości.
     *
     * @param read true – jeśli wiadomość ma być oznaczona jako przeczytana,
     *             false – jeśli ma być oznaczona jako nieprzeczytana.
     */
    public void setRead(boolean read) { isRead = read; }

    /**
     * Zwraca krótki tekst opisujący wiadomość,
     * np. do wyświetlenia na liście wiadomości.
     *
     * Przykład formatu:
     *   "* Tytuł wiadomości (13.11.2025 14:30)"
     *   Gwiazdka oznacza, że wiadomość jest nieprzeczytana.
     */
    public String getFormattedHeader() {
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return String.format("%s %s (%s)",
                isRead ? " " : "*", // Gwiazdka dla nieprzeczytanych
                title,
                timestamp.format(displayFormatter));
    }

    /**
     * Zwraca czytelny opis nadawcy lub odbiorcy,
     * w zależności od wartości showSender.
     *
     * @param showSender true  – zwróć dane nadawcy,
     *                   false – zwróć dane odbiorcy.
     * @return Tekst w formacie: "Imię Nazwisko (login)"
     */
    public String getFormattedSenderRecipient(boolean showSender) {
        User userToShow = showSender ? sender : recipient;
        return userToShow.getFullName() + " (" + userToShow.getLogin() + ")";
    }
}
