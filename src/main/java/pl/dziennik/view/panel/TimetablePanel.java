/**
 * Plik: TimetablePanel.java
 *
 * Opis:
 *  Niestandardowy panel Swing odpowiedzialny za graficzne
 *  rysowanie planu lekcji (zarówno dla ucznia, jak i nauczyciela).
 *
 *  Plan lekcji rysowany jest w sposób dynamiczny:
 *   - czas przeliczany jest na piksele,
 *   - lekcje mają różną wysokość w zależności od czasu trwania,
 *   - obsługiwane są przerwy i niestandardowe godziny.
 *
 * Rola w architekturze:
 *  - WARSTWA WIDOKU (View – MVC)
 */

package pl.dziennik.view.panel;

import pl.dziennik.model.school.SchoolClass;
import pl.dziennik.model.school.TimetableEntry;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Niestandardowy panel do rysowania graficznego planu lekcji.
 *
 * Dziedziczenie:
 *  - JPanel → TimetablePanel
 *
 * Polimorfizm:
 *  - nadpisanie metody paintComponent(Graphics)
 */
public class TimetablePanel extends JPanel {

    /** Aktualnie wyświetlana klasa (lub sztuczna klasa dla nauczyciela) */
    private SchoolClass schoolClass;

    /** Dni tygodnia wyświetlane w planie */
    private final String[] days = {
            "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek"
    };

    /** Godzina rozpoczęcia siatki */
    private final LocalTime dayStart = LocalTime.of(7, 45);

    /** Godzina zakończenia siatki */
    private final LocalTime dayEnd = LocalTime.of(16, 0);

    // === USTAWIENIA WIZUALNE ===

    /** Wysokość nagłówka z dniami */
    private static final int HEADER_HEIGHT = 40;

    /** Szerokość kolumny z godzinami */
    private static final int HOUR_WIDTH = 70;

    /** Przelicznik minut na piksele */
    private static final double PIXELS_PER_MINUTE = 1.2;

    /**
     * Mapowanie nazw przedmiotów na kolory.
     * Dzięki temu każdy przedmiot ma stały kolor.
     *
     * Kolekcje:
     *  - Map<String, Color>
     */
    private final Map<String, Color> subjectColors = new HashMap<>();

    /** Generator losowych kolorów */
    private final Random random = new Random();

    /** Formatter godzin */
    private final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Konstruktor – ustawia podstawowe właściwości panelu.
     */
    public TimetablePanel() {
        setBackground(Color.WHITE);
    }

    /**
     * Główna metoda rysująca komponent.
     * Wywoływana automatycznie przez Swing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Rzutowanie na Graphics2D (polimorfizm)
        Graphics2D g2d = (Graphics2D) g;

        // Włączenie antyaliasingu
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Rysowanie siatki i nagłówków
        drawGridAndHours(g2d);
        drawHeaders(g2d);

        // Rysowanie lekcji (jeśli są dane)
        if (schoolClass != null) {
            drawLessons(g2d);
        }
    }

    /**
     * Rysuje poziome linie godzin oraz pionowe linie dni.
     */
    private void drawGridAndHours(Graphics2D g2d) {
        int panelWidth = getWidth();

        // Poziome linie co 15 minut
        for (LocalTime time = dayStart;
             !time.isAfter(dayEnd);
             time = time.plusMinutes(15)) {

            int y = HEADER_HEIGHT +
                    (int) (ChronoUnit.MINUTES
                            .between(dayStart, time)
                            * PIXELS_PER_MINUTE);

            // Pełne godziny – grubsza linia + etykieta
            if (time.getMinute() == 0) {
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawLine(HOUR_WIDTH, y, panelWidth, y);

                g2d.setColor(Color.DARK_GRAY);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String hourText = time.format(timeFormatter);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(
                        hourText,
                        HOUR_WIDTH - fm.stringWidth(hourText) - 10,
                        y + fm.getAscent() / 2
                );
            }
            // Kwadranse – cienka linia
            else {
                g2d.setStroke(new BasicStroke(0.5f));
                g2d.setColor(new Color(240, 240, 240));
                g2d.drawLine(HOUR_WIDTH, y, panelWidth, y);
            }
        }

        // Reset grubości linii
        g2d.setStroke(new BasicStroke(1f));

        // Pionowe linie dni tygodnia
        int dayWidth = (panelWidth - HOUR_WIDTH) / days.length;
        for (int i = 0; i <= days.length; i++) {
            int x = HOUR_WIDTH + i * dayWidth;
            g2d.setColor(new Color(220, 220, 220));
            g2d.drawLine(x, 0, x, getHeight());
        }
    }

    /**
     * Rysuje nagłówki z nazwami dni tygodnia.
     */
    private void drawHeaders(Graphics2D g2d) {
        int dayWidth = (getWidth() - HOUR_WIDTH) / days.length;
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2d.setColor(Color.BLACK);

        for (int i = 0; i < days.length; i++) {
            int x = HOUR_WIDTH + i * dayWidth + dayWidth / 2;
            drawCenteredString(g2d, days[i], x, HEADER_HEIGHT / 2);
        }
    }

    /**
     * Rysuje kolorowe kafelki reprezentujące lekcje.
     */
    private void drawLessons(Graphics2D g2d) {
        int dayWidth = (getWidth() - HOUR_WIDTH) / days.length;
        Map<String, List<TimetableEntry>> timetable =
                schoolClass.getTimetable();

        for (int i = 0; i < days.length; i++) {
            String day = days[i];

            if (timetable.containsKey(day)) {
                for (TimetableEntry lesson : timetable.get(day)) {

                    long startOffset =
                            ChronoUnit.MINUTES.between(
                                    dayStart,
                                    lesson.getStartTime()
                            );

                    long duration =
                            ChronoUnit.MINUTES.between(
                                    lesson.getStartTime(),
                                    lesson.getEndTime()
                            );

                    int x = HOUR_WIDTH + i * dayWidth + 3;
                    int y = HEADER_HEIGHT +
                            (int) (startOffset * PIXELS_PER_MINUTE);
                    int width = dayWidth - 6;
                    int height =
                            (int) (duration * PIXELS_PER_MINUTE);

                    // Stały kolor dla przedmiotu
                    Color subjectColor =
                            subjectColors.computeIfAbsent(
                                    lesson.getSubject().getName(),
                                    k -> new Color(
                                            random.nextInt(180) + 50,
                                            random.nextInt(180) + 50,
                                            random.nextInt(180) + 50
                                    )
                            );

                    // Cień
                    g2d.setColor(subjectColor.darker());
                    g2d.fillRoundRect(x, y, width, height, 10, 10);

                    // Kafelek
                    g2d.setColor(subjectColor);
                    g2d.fillRoundRect(
                            x + 1, y + 1,
                            width - 2, height - 2,
                            8, 8
                    );

                    // Tekst
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(
                            new Font("Segoe UI", Font.BOLD, 13)
                    );
                    g2d.drawString(
                            lesson.getSubject().getName(),
                            x + 10, y + 20
                    );

                    g2d.setFont(
                            new Font("Segoe UI", Font.PLAIN, 11)
                    );
                    g2d.drawString(
                            lesson.getTeacher().getFullName(),
                            x + 10, y + 35
                    );

                    String timeAndRoom =
                            lesson.getStartTime().format(timeFormatter)
                                    + " - "
                                    + lesson.getEndTime().format(timeFormatter)
                                    + " (s. " + lesson.getClassroom() + ")";

                    g2d.drawString(
                            timeAndRoom,
                            x + 10, y + 50
                    );
                }
            }
        }
    }

    /**
     * Metoda pomocnicza do centrowania tekstu.
     */
    private void drawCenteredString(
            Graphics2D g2d,
            String text,
            int x,
            int y) {

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2d.drawString(
                text,
                x - textWidth / 2,
                y + fm.getAscent() / 2
        );
    }

    /**
     * Ustawia dane planu lekcji i wymusza przerysowanie panelu.
     */
    public void setTimetableData(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
        this.subjectColors.clear();
        repaint(); // Kluczowe odświeżenie widoku
    }
}
