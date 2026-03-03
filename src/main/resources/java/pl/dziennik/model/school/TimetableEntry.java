/**
 * Plik: TimetableEntry.java
 *
 * Opis:
 *  Reprezentuje pojedynczy wpis w planie lekcji.
 *  Klasa przechowuje informacje o czasie rozpoczęcia i zakończenia
 *  zajęć, przedmiocie, nauczycielu oraz sali lekcyjnej.
 *
 * Główne elementy:
 *  - Pola: startTime, endTime, subject, teacher, classroom.
 *  - Konstruktor TimetableEntry(...) – tworzy nowy wpis planu lekcji
 *    z podanymi danymi.
 *  - Gettery – umożliwiają odczyt wszystkich informacji o zajęciach.
 */

package pl.dziennik.model.school;

import pl.dziennik.model.user.Teacher;
import java.io.Serializable;
import java.time.LocalTime;

/**
 * Model reprezentujący wpis w planie lekcji.
 */
public class TimetableEntry implements Serializable {

    // Wersja klasy używana przy serializacji (zapis/odczyt obiektów)
    private static final long serialVersionUID = 1L;

    // Godzina rozpoczęcia zajęć
    private final LocalTime startTime;

    // Godzina zakończenia zajęć
    private final LocalTime endTime;

    // Przedmiot, którego dotyczą zajęcia
    private final Subject subject;

    // Nauczyciel prowadzący zajęcia
    private final Teacher teacher;

    // Sala lekcyjna, w której odbywają się zajęcia
    private final String classroom;

    /**
     * Tworzy nowy wpis planu lekcji z podanymi danymi.
     *
     * @param startTime godzina rozpoczęcia zajęć
     * @param endTime   godzina zakończenia zajęć
     * @param subject   przedmiot zajęć
     * @param teacher   nauczyciel prowadzący zajęcia
     * @param classroom sala lekcyjna
     */
    public TimetableEntry(LocalTime startTime, LocalTime endTime,
                          Subject subject, Teacher teacher, String classroom) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.teacher = teacher;
        this.classroom = classroom;
    }

    // --- Gettery ---

    /** Zwraca godzinę rozpoczęcia zajęć. */
    public LocalTime getStartTime() { return startTime; }

    /** Zwraca godzinę zakończenia zajęć. */
    public LocalTime getEndTime() { return endTime; }

    /** Zwraca przedmiot zajęć. */
    public Subject getSubject() { return subject; }

    /** Zwraca nauczyciela prowadzącego zajęcia. */
    public Teacher getTeacher() { return teacher; }

    /** Zwraca salę lekcyjną. */
    public String getClassroom() { return classroom; }
}
