/**
 * Plik: MessageService.java
 *
 * Opis:
 *  Interfejs definiujący usługę obsługi wiadomości
 *  w systemie dziennika elektronicznego. Określa
 *  operacje związane z wysyłaniem, odbieraniem
 *  oraz zarządzaniem stanem wiadomości.
 *
 * Główne elementy:
 *  - Metoda sendMessage(...) – wysyła nową wiadomość.
 *  - Metody getReceivedMessages(...) oraz getSentMessages(...)
 *    – pobierają listy wiadomości użytkownika.
 *  - Metoda markAsRead(...) – oznacza wiadomość jako przeczytaną.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Interfejs:
 *      Definicja kontraktu dla usługi obsługi wiadomości.
 *
 *  - Polimorfizm:
 *      Umożliwia stosowanie różnych implementacji
 *      interfejsu MessageService.
 */

package pl.dziennik.service.interfaces;

import pl.dziennik.model.communication.Message;
import pl.dziennik.model.user.User;
import java.util.List;

/**
 * Interfejs usługi obsługi wiadomości.
 */
public interface MessageService {

    /**
     * Wysyła nową wiadomość od nadawcy do odbiorcy.
     *
     * @param sender    nadawca wiadomości
     * @param recipient odbiorca wiadomości
     * @param title     tytuł wiadomości
     * @param content   treść wiadomości
     */
    void sendMessage(User sender, User recipient,
                     String title, String content);

    /**
     * Zwraca listę wiadomości odebranych przez użytkownika.
     *
     * @param user użytkownik, którego wiadomości
     *             mają zostać pobrane
     * @return lista odebranych wiadomości
     */
    List<Message> getReceivedMessages(User user);

    /**
     * Zwraca listę wiadomości wysłanych przez użytkownika.
     *
     * @param user użytkownik, którego wysłane wiadomości
     *             mają zostać pobrane
     * @return lista wysłanych wiadomości
     */
    List<Message> getSentMessages(User user);

    /**
     * Oznacza wiadomość jako przeczytaną.
     *
     * @param message wiadomość do oznaczenia
     */
    void markAsRead(Message message);
}
