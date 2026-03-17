/**
 * Plik: Consultation.java
 *
 * Opis:
 *  Reprezentuje jedne konsultacje nauczyciela — dzień tygodnia, godziny
 *  rozpoczęcia i zakończenia oraz salę, w której odbywają się konsultacje.
 *
 * Główne elementy:
 *  - Pola: dayOfWeek, startTime, endTime, room.
 *  - Konstruktor Consultation(...) – tworzy obiekt konsultacji z podanymi danymi.
 *  - Gettery – pozwalają odczytać informacje o konsultacjach.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Serializacja:
 *      Klasa implementuje Serializable, co pozwala zapisywać konsultacje
 *      do plików i odczytywać je później.
 */

package pl.dziennik.model.school;

import java.io.Serializable;
import java.time.LocalTime;

public class Consultation implements Serializable {

    // Dzień tygodnia, np. "Poniedziałek"
    private final String dayOfWeek;

    // Godzina rozpoczęcia konsultacji
    private final LocalTime startTime;

    // Godzina zakończenia konsultacji
    private final LocalTime endTime;

    // Sala, w której odbywają się konsultacje
    private final String room;

    /**
     * Tworzy obiekt konsultacji z podanymi informacjami.
     *
     * @param dayOfWeek dzień tygodnia
     * @param startTime godzina rozpoczęcia
     * @param endTime   godzina zakończenia
     * @param room      sala, w której odbywają się konsultacje
     */
    public Consultation(String dayOfWeek, LocalTime startTime, LocalTime endTime, String room) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
    }

    /** Zwraca dzień tygodnia. */
    public String getDayOfWeek() { return dayOfWeek; }

    /** Zwraca godzinę rozpoczęcia konsultacji. */
    public LocalTime getStartTime() { return startTime; }

    /** Zwraca godzinę zakończenia konsultacji. */
    public LocalTime getEndTime() { return endTime; }

    /** Zwraca numer lub nazwę sali. */
    public String getRoom() { return room; }
}
