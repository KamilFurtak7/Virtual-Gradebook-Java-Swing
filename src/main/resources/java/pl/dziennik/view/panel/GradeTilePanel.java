/**
 * Plik: GradeTilePanel.java
 *
 * Opis:
 *  Panel interfejsu użytkownika reprezentujący pojedynczą ocenę
 *  w formie kafelka. Klasa odpowiada za wizualną prezentację
 *  informacji o ocenie, takich jak jej wartość, opis, nauczyciel,
 *  waga oraz data wystawienia. Kolor kafelka jest dynamicznie
 *  dobierany na podstawie wartości oceny.
 *
 * Główne elementy:
 *  - Dziedziczenie po klasie JPanel.
 *  - Konstruktor GradeTilePanel(...) – buduje kafelek oceny
 *    na podstawie przekazanego obiektu Grade.
 *  - Metoda getColorForGrade(...) – dobiera kolor kafelka
 *    w zależności od wartości oceny.
 *  - Klasa RoundedBorder – niestandardowa ramka kafelka.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Dziedziczenie:
 *      Klasa GradeTilePanel dziedziczy po JPanel.
 *
 *  - Kompozycja obiektów:
 *      Panel składa się z wielu komponentów Swing
 *      (JLabel, JTextArea, JPanel), tworzących spójną całość.
 *
 *  - Klasy wewnętrzne:
 *      Klasa RoundedBorder implementuje interfejs Border
 *      i definiuje niestandardową ramkę komponentu.
 *
 *  - Enkapsulacja:
 *      Logika doboru koloru kafelka została wydzielona
 *      do osobnej metody pomocniczej.
 *
 *  - Wzorzec MVC:
 *      Klasa pełni rolę View i prezentuje dane modelu Grade.
 */

package pl.dziennik.view.panel;

import pl.dziennik.model.school.Grade;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Panel reprezentujący pojedynczą ocenę w formie kafelka.
 */
public class GradeTilePanel extends JPanel {

    // Formatter daty wyświetlanej na kafelku
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Tworzy kafelek reprezentujący pojedynczą ocenę.
     *
     * @param grade obiekt oceny do wyświetlenia
     */
    public GradeTilePanel(Grade grade) {

        setLayout(new BorderLayout(15, 0));

        // Stały rozmiar kafelka
        Dimension tileSize = new Dimension(350, 120);
        setPreferredSize(tileSize);
        setMaximumSize(tileSize);
        setMinimumSize(tileSize);

        // Kolor kafelka zależny od wartości oceny
        Color baseColor = getColorForGrade(grade.getValue());

        // Ramka z zaokrąglonymi rogami
        setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, 1, baseColor.darker()),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        setBackground(baseColor);

        // =====================================================
        // ================== PANEL OCENY ======================
        // =====================================================

        JPanel gradePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        gradePanel.setOpaque(false);

        JLabel gradeLabel =
                new JLabel(String.valueOf(grade.getValue()));
        gradeLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        gradeLabel.setForeground(Color.WHITE);

        gradePanel.add(gradeLabel);

        // =====================================================
        // ================= PANEL SZCZEGÓŁÓW ==================
        // =====================================================

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        // Opis oceny
        JTextArea descriptionArea =
                new JTextArea(grade.getDescription());
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setOpaque(false);
        descriptionArea.setEditable(false);
        descriptionArea.setFocusable(false);
        descriptionArea.setFont(new Font("Segoe UI", Font.BOLD, 16));
        descriptionArea.setForeground(Color.WHITE);

        // Linia z nauczycielem
        JPanel teacherLine =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        teacherLine.setOpaque(false);

        JLabel teacherLabel = new JLabel(
                "Nauczyciel: " + grade.getTeacher().getFullName());
        teacherLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        teacherLabel.setForeground(new Color(240, 240, 240));

        teacherLine.add(teacherLabel);

        // Linia z wagą i datą
        JPanel subDetailsLine =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        subDetailsLine.setOpaque(false);

        JLabel subDetailsLabel = new JLabel(
                "Waga: " + grade.getWeight() +
                "  |  Data: " +
                grade.getDate().format(dateFormatter)
        );
        subDetailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subDetailsLabel.setForeground(new Color(240, 240, 240));

        subDetailsLine.add(subDetailsLabel);

        // Składanie panelu szczegółów
        detailsPanel.add(descriptionArea);
        detailsPanel.add(Box.createVerticalGlue());
        detailsPanel.add(teacherLine);
        detailsPanel.add(subDetailsLine);

        // Dodanie paneli do głównego kafelka
        add(gradePanel, BorderLayout.WEST);
        add(detailsPanel, BorderLayout.CENTER);
    }

    /**
     * Zwraca kolor odpowiadający wartości oceny.
     *
     * @param value wartość oceny
     * @return kolor kafelka
     */
    private Color getColorForGrade(double value) {
        if (value >= 4.5) return new Color(46, 139, 87);
        if (value >= 3.5) return new Color(60, 179, 113);
        if (value >= 2.5) return new Color(255, 140, 0);
        if (value >= 2.0) return new Color(255, 69, 0);
        return new Color(220, 20, 60);
    }
}

/**
 * Niestandardowa ramka z zaokrąglonymi rogami
 * wykorzystywana przez kafelki ocen.
 */
class RoundedBorder implements Border {

    private final int radius;
    private final int stroke;
    private final Color color;

    /**
     * Tworzy ramkę o zadanych parametrach.
     *
     * @param radius promień zaokrąglenia rogów
     * @param stroke grubość obramowania
     * @param color  kolor ramki
     */
    RoundedBorder(int radius, int stroke, Color color) {
        this.radius = radius;
        this.stroke = stroke;
        this.color = color;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius, radius, radius, radius);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void paintBorder(Component c,
                            Graphics g,
                            int x,
                            int y,
                            int width,
                            int height) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(stroke));
        g2d.drawRoundRect(
                x + stroke,
                y + stroke,
                width - stroke * 2,
                height - stroke * 2,
                radius,
                radius
        );
    }
}
