/**
 * Plik: MessageServiceImpl.java
 *
 * Opis:
 *  Implementacja usługi obsługi wiadomości w systemie
 *  dziennika elektronicznego. Klasa umożliwia wysyłanie
 *  wiadomości pomiędzy użytkownikami, pobieranie list
 *  wiadomości odebranych i wysłanych oraz oznaczanie
 *  wiadomości jako przeczytanych.
 *
 * Główne elementy:
 *  - Implementacja interfejsu MessageService.
 *  - Współpraca z DatabaseContext jako magazynem wiadomości.
 *  - Wykorzystanie strumieni (Stream API) do filtrowania
 *    i sortowania wiadomości.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Interfejsy:
 *      Implementacja interfejsu MessageService.
 *
 *  - Polimorfizm:
 *      Metody klasy są implementacją metod zadeklarowanych
 *      w interfejsie MessageService.
 *
 *  - Kolekcje obiektów:
 *      Operacje na liście List<Message> przechowywanej
 *      w DatabaseContext.
 *
 *  - Programowanie funkcyjne:
 *      Wykorzystanie Stream API, wyrażeń lambda
 *      oraz klasy Comparator do sortowania danych.
 *
 *  - Obsługa wyjątków:
 *      Zgłaszanie wyjątku IllegalArgumentException
 *      w przypadku nieprawidłowych danych wejściowych.
 */

package pl.dziennik.service.impl;

import pl.dziennik.model.communication.Message;
import pl.dziennik.model.user.User;
import pl.dziennik.persistence.DatabaseContext;
import pl.dziennik.service.interfaces.MessageService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementacja usługi obsługi wiadomości.
 */
public class MessageServiceImpl implements MessageService {

    // Kontekst danych aplikacji
    private final DatabaseContext dbContext;

    /**
     * Tworzy usługę obsługi wiadomości.
     *
     * @param dbContext kontekst danych aplikacji
     */
    public MessageServiceImpl(DatabaseContext dbContext) {
        this.dbContext = dbContext;
    }

    /**
     * Wysyła nową wiadomość od nadawcy do odbiorcy.
     *
     * @param sender    nadawca wiadomości
     * @param recipient odbiorca wiadomości
     * @param title     tytuł wiadomości
     * @param content   treść wiadomości
     * @throws IllegalArgumentException gdy którykolwiek z parametrów
     *         jest pusty lub niepoprawny
     */
    @Override
    public void sendMessage(User sender, User recipient,
                            String title, String content) {

        if (sender == null || recipient == null
                || title == null || title.trim().isEmpty()
                || content == null || content.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Nadawca, odbiorca, tytuł i treść nie mogą być puste.");
        }

        // Zamiana znaków nowej linii na sekwencję tekstową
        // ułatwiającą zapis do pliku
        String processedContent = content.replace("\n", "\\n");

        Message newMessage = new Message(
                sender,
                recipient,
                title,
                processedContent,
                LocalDateTime.now(),
                false
        );

        dbContext.getMessages().add(newMessage);
    }

    /**
     * Zwraca listę wiadomości odebranych przez użytkownika,
     * posortowanych malejąco według daty wysłania.
     *
     * @param user użytkownik, którego wiadomości mają zostać pobrane
     * @return lista odebranych wiadomości
     */
    @Override
    public List<Message> getReceivedMessages(User user) {
        return dbContext.getMessages().stream()
                .filter(msg -> msg.getRecipient().equals(user))
                .sorted(Comparator
                        .comparing(Message::getTimestamp)
                        .reversed())
                .collect(Collectors.toList());
    }

    /**
     * Zwraca listę wiadomości wysłanych przez użytkownika,
     * posortowanych malejąco według daty wysłania.
     *
     * @param user użytkownik, którego wysłane wiadomości
     *             mają zostać pobrane
     * @return lista wysłanych wiadomości
     */
    @Override
    public List<Message> getSentMessages(User user) {
        return dbContext.getMessages().stream()
                .filter(msg -> msg.getSender().equals(user))
                .sorted(Comparator
                        .comparing(Message::getTimestamp)
                        .reversed())
                .collect(Collectors.toList());
    }

    /**
     * Oznacza wiadomość jako przeczytaną.
     *
     * @param message wiadomość do oznaczenia
     */
    @Override
    public void markAsRead(Message message) {
        if (message != null && !message.isRead()) {
            message.setRead(true);
        }
    }
}
