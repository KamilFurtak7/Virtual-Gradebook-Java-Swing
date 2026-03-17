/**
 * Plik: WrapLayout.java
 *
 * Opis:
 *  Specjalny menedżer układu, który rozszerza standardowy FlowLayout,
 *  dodając poprawne zawijanie komponentów (wrap) wewnątrz JScrollPane.
 *
 *  Standardowy FlowLayout:
 *   - NIE obsługuje poprawnie zawijania w JScrollPane,
 *   - nie przelicza poprawnie preferredSize kontenera.
 *
 *  WrapLayout:
 *   - dynamicznie przelicza szerokość kontenera,
 *   - uwzględnia paski przewijania,
 *   - poprawnie zawija komponenty do kolejnych wierszy.
 *
 * Rola w aplikacji:
 *  - WARSTWA WIDOKU (Swing / UI)
 *  - używany np. w TeachersDisplayPanel
 *
 * Uwaga:
 *  Klasa jest gotowa, stabilna i nie wymaga modyfikacji.
 */

package pl.dziennik.view.panel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * Specjalny menedżer układu rozszerzający FlowLayout.
 *
 * Dziedziczenie:
 *  FlowLayout → WrapLayout
 *
 * Dzięki temu:
 *  - zachowujemy cały mechanizm FlowLayout,
 *  - nadpisujemy jedynie sposób liczenia rozmiaru.
 */
public class WrapLayout extends FlowLayout {

    /**
     * Konstruktor domyślny.
     * Używa domyślnego wyrównania FlowLayout.
     */
    public WrapLayout() {
        super();
    }

    /**
     * Konstruktor z określonym wyrównaniem komponentów.
     *
     * @param align wyrównanie (LEFT, CENTER, RIGHT)
     */
    public WrapLayout(int align) {
        super(align);
    }

    /**
     * Konstruktor z pełną konfiguracją.
     *
     * @param align wyrównanie komponentów
     * @param hgap  odstęp poziomy między komponentami
     * @param vgap  odstęp pionowy między wierszami
     */
    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    /**
     * Zwraca preferowany rozmiar kontenera.
     * Swing wywołuje tę metodę podczas layoutowania UI.
     */
    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    /**
     * Zwraca minimalny rozmiar kontenera.
     */
    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    /**
     * Główna metoda obliczająca rozmiar layoutu.
     *
     * @param target     kontener, którego rozmiar liczymy
     * @param preferred  czy liczyć preferredSize czy minimumSize
     * @return           obliczony rozmiar kontenera
     */
    private Dimension layoutSize(Container target, boolean preferred) {

        synchronized (target.getTreeLock()) {

            int targetWidth = target.getSize().width;
            Container container = target;

            /*
             * Jeśli szerokość kontenera wynosi 0 (częsty przypadek przy JScrollPane),
             * próbujemy pobrać szerokość rodzica.
             */
            while (container.getSize().width == 0 && container.getParent() != null) {
                container = container.getParent();
            }
            targetWidth = container.getSize().width;

            // Jeśli nadal brak szerokości – przyjmujemy maksimum
            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();

            int horizontalInsetsAndGap =
                    insets.left + insets.right + (hgap * 2);

            int maxWidth = targetWidth - horizontalInsetsAndGap;

            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();

            /*
             * Iteracja po wszystkich komponentach
             * i układanie ich w wierszach.
             */
            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);

                if (m.isVisible()) {
                    Dimension d = preferred
                            ? m.getPreferredSize()
                            : m.getMinimumSize();

                    // Jeśli komponent nie mieści się w bieżącym wierszu → nowy wiersz
                    if (rowWidth + d.width > maxWidth) {
                        addRow(dim, rowWidth, rowHeight);
                        rowWidth = 0;
                        rowHeight = 0;
                    }

                    // Dodaj odstęp poziomy
                    if (rowWidth != 0) {
                        rowWidth += hgap;
                    }

                    rowWidth += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }

            // Dodaj ostatni wiersz
            addRow(dim, rowWidth, rowHeight);

            dim.width += horizontalInsetsAndGap;
            dim.height += insets.top + insets.bottom + vgap * 2;

            /*
             * Korekta szerokości, jeśli layout znajduje się w JScrollPane.
             */
            Container scrollPane =
                    SwingUtilities.getAncestorOfClass(JScrollPane.class, target);

            if (scrollPane != null && target.isValid()) {
                dim.width -= (hgap + 1);
            }

            return dim;
        }
    }

    /**
     * Dodaje wiersz do obliczanego rozmiaru.
     *
     * @param dim       aktualny rozmiar
     * @param rowWidth szerokość wiersza
     * @param rowHeight wysokość wiersza
     */
    private void addRow(Dimension dim, int rowWidth, int rowHeight) {
        dim.width = Math.max(dim.width, rowWidth);

        if (dim.height > 0) {
            dim.height += getVgap();
        }

        dim.height += rowHeight;
    }
}