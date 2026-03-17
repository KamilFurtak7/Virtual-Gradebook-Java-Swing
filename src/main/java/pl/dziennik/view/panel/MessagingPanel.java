/**
 * Plik: MessagingPanel.java
 *
 * Opis:
 *  Panel interfejsu użytkownika odpowiedzialny za obsługę
 *  systemu wiadomości w aplikacji „Wirtualny Dziennik”.
 *  Klasa umożliwia przeglądanie wiadomości odebranych
 *  i wysłanych, podgląd treści, wysyłanie nowych wiadomości
 *  oraz usuwanie istniejących.
 *
 * Główne elementy:
 *  - Dziedziczenie po klasie JPanel.
 *  - Dwie listy wiadomości (odebrane i wysłane).
 *  - Podgląd treści wiadomości.
 *  - Przyciski akcji: nowa wiadomość, odpowiedź, usunięcie.
 *  - Współpraca z MessageService oraz DatabaseContext.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Dziedziczenie:
 *      Klasa MessagingPanel dziedziczy po JPanel.
 *
 *  - Wzorzec MVC:
 *      Panel pełni rolę View i komunikuje się z warstwą
 *      Service (MessageService) oraz Persistence (DatabaseContext).
 *
 *  - Kolekcje obiektów:
 *      Wykorzystanie List<Message> oraz modeli list
 *      (DefaultListModel) do przechowywania danych.
 *
 *  - Programowanie funkcyjne:
 *      Wykorzystanie Stream API, Comparator oraz Collectors
 *      do filtrowania i sortowania danych.
 *
 *  - Klasy wewnętrzne:
 *      Wewnętrzne klasy rendererów list i combo boxa
 *      do niestandardowej prezentacji danych.
 *
 *  - Obsługa zdarzeń:
 *      Reakcja na wybór elementów listy oraz akcje przycisków.
 */

package pl.dziennik.view.panel;

import pl.dziennik.model.communication.Message;
import pl.dziennik.model.user.User;
import pl.dziennik.persistence.DatabaseContext;
import pl.dziennik.service.interfaces.MessageService;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel obsługi wiadomości użytkownika.
 */
public class MessagingPanel extends JPanel {

    // Listy wiadomości
    private final JList<Message> inboxList;
    private final DefaultListModel<Message> inboxListModel;

    private final JList<Message> sentList;
    private final DefaultListModel<Message> sentListModel;

    // Podgląd treści wiadomości
    private final JTextArea messageContentView;

    // Przyciski akcji
    // UWAGA: w oryginalnym kodzie występuje literówka na końcu linii
    private final JButton composeButton, replyButton, deleteButton;

    // Dane kontekstowe
    private User currentUser;
    private MessageService messageService;
    private DatabaseContext dbContext;

    /**
     * Tworzy panel wiadomości i buduje interfejs użytkownika.
     */
    public MessagingPanel() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // =====================================================
        // ================= LISTY WIADOMOŚCI ==================
        // =====================================================

        inboxListModel = new DefaultListModel<>();
        inboxList = new JList<>(inboxListModel);
        inboxList.setCellRenderer(new MessageListCellRenderer(true));
        inboxList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane inboxScrollPane = new JScrollPane(inboxList);
        inboxScrollPane.setBorder(
                BorderFactory.createTitledBorder("Odebrane"));

        sentListModel = new DefaultListModel<>();
        sentList = new JList<>(sentListModel);
        sentList.setCellRenderer(new MessageListCellRenderer(false));
        sentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane sentScrollPane = new JScrollPane(sentList);
        sentScrollPane.setBorder(
                BorderFactory.createTitledBorder("Wysłane"));

        JSplitPane listsSplitPane =
                new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                        inboxScrollPane, sentScrollPane);
        listsSplitPane.setResizeWeight(0.5);
        listsSplitPane.setPreferredSize(new Dimension(300, 0));

        // =====================================================
        // ================= PODGLĄD TREŚCI ====================
        // =====================================================

        messageContentView =
                new JTextArea("Wybierz wiadomość, aby zobaczyć jej treść.");
        messageContentView.setEditable(false);
        messageContentView.setLineWrap(true);
        messageContentView.setWrapStyleWord(true);

        JScrollPane contentScrollPane =
                new JScrollPane(messageContentView);
        contentScrollPane.setBorder(
                BorderFactory.createTitledBorder("Treść wiadomości"));

        // =====================================================
        // ================= PRZYCISKI AKCJI ===================
        // =====================================================

        composeButton = new JButton("Napisz nową");
        replyButton = new JButton("Odpowiedz");
        deleteButton = new JButton("Usuń");

        replyButton.setEnabled(false);
        deleteButton.setEnabled(false);

        JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(composeButton);
        buttonPanel.add(replyButton);
        buttonPanel.add(deleteButton);

        JPanel rightPanel =
                new JPanel(new BorderLayout(0, 10));
        rightPanel.add(contentScrollPane, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        JSplitPane mainSplitPane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        listsSplitPane, rightPanel);
        mainSplitPane.setDividerLocation(310);

        add(mainSplitPane, BorderLayout.CENTER);

        setupListeners();
    }

    /**
     * Ustawia dane kontekstowe panelu wiadomości.
     *
     * @param user       aktualnie zalogowany użytkownik
     * @param msgService serwis obsługi wiadomości
     * @param dbCtx      kontekst danych aplikacji
     */
    public void setMessagingData(User user,
                                 MessageService msgService,
                                 DatabaseContext dbCtx) {

        this.currentUser = user;
        this.messageService = msgService;
        this.dbContext = dbCtx;

        refreshMessageLists();
        clearMessageView();
    }

    /**
     * Rejestruje obsługę zdarzeń komponentów.
     */
    private void setupListeners() {

        inboxList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Message selected = inboxList.getSelectedValue();
                if (selected != null) {
                    sentList.clearSelection();
                    displayMessageContent(selected);
                    messageService.markAsRead(selected);
                    inboxList.repaint();
                    replyButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
            }
        });

        sentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Message selected = sentList.getSelectedValue();
                if (selected != null) {
                    inboxList.clearSelection();
                    displayMessageContent(selected);
                    replyButton.setEnabled(false);
                    deleteButton.setEnabled(true);
                }
            }
        });

        composeButton.addActionListener(e -> composeNewMessage());
        replyButton.addActionListener(e -> replyToMessage());
        deleteButton.addActionListener(e -> deleteMessage());
    }

    /**
     * Odświeża listy wiadomości odebranych i wysłanych.
     */
    private void refreshMessageLists() {

        inboxListModel.clear();
        if (messageService != null && currentUser != null) {
            messageService.getReceivedMessages(currentUser)
                    .forEach(inboxListModel::addElement);
        }

        sentListModel.clear();
        if (messageService != null && currentUser != null) {
            messageService.getSentMessages(currentUser)
                    .forEach(sentListModel::addElement);
        }
    }

    /**
     * Wyświetla treść wybranej wiadomości.
     */
    private void displayMessageContent(Message message) {

        if (message == null) {
            clearMessageView();
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("Od: ").append(message.getSender().getFullName()).append("\n");
        content.append("Do: ").append(message.getRecipient().getFullName()).append("\n");
        content.append("Data: ")
                .append(message.getTimestamp().format(
                        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                .append("\n");
        content.append("Temat: ").append(message.getTitle()).append("\n\n");
        content.append(message.getContent().replace("\\n", "\n"));

        messageContentView.setText(content.toString());
        messageContentView.setCaretPosition(0);
    }

    /**
     * Czyści widok treści wiadomości.
     */
    private void clearMessageView() {
        messageContentView.setText(
                "Wybierz wiadomość, aby zobaczyć jej treść.");
        replyButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    /**
     * Obsługuje tworzenie nowej wiadomości.
     */
    private void composeNewMessage() {

        List<User> users = dbContext.getUsers().stream()
                .filter(u -> !u.equals(currentUser))
                .sorted(Comparator
                        .comparing(User::getLastName)
                        .thenComparing(User::getFirstName))
                .collect(Collectors.toList());

        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Brak innych użytkowników w systemie.",
                    "Błąd",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JComboBox<User> recipientComboBox =
                new JComboBox<>(users.toArray(new User[0]));
        recipientComboBox.setRenderer(new UserComboBoxRenderer());

        JTextField titleField = new JTextField();
        JTextArea contentArea = new JTextArea(10, 40);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        JPanel topPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        topPanel.add(new JLabel("Odbiorca:"));
        topPanel.add(recipientComboBox);
        topPanel.add(new JLabel("Tytuł:"));
        topPanel.add(titleField);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(contentArea), BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Nowa wiadomość",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                messageService.sendMessage(
                        currentUser,
                        (User) recipientComboBox.getSelectedItem(),
                        titleField.getText(),
                        contentArea.getText());

                refreshMessageLists();

                JOptionPane.showMessageDialog(
                        this,
                        "Wiadomość wysłana pomyślnie.",
                        "Sukces",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Błąd wysyłania",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Obsługa odpowiedzi na wiadomość (placeholder).
     */
    private void replyToMessage() {
        JOptionPane.showMessageDialog(
                this,
                "Funkcja 'Odpowiedz' niezaimplementowana.",
                "Informacja",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Usuwa wybraną wiadomość.
     */
    private void deleteMessage() {

        Message toDelete =
                inboxList.getSelectedValue() != null
                        ? inboxList.getSelectedValue()
                        : sentList.getSelectedValue();

        if (toDelete == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Czy na pewno chcesz usunąć tę wiadomość?",
                "Potwierdź usunięcie",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            dbContext.getMessages().remove(toDelete);
            refreshMessageLists();
            clearMessageView();

            JOptionPane.showMessageDialog(
                    this,
                    "Wiadomość usunięta.",
                    "Informacja",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // =====================================================
    // ================== KLASY WEWNĘTRZNE ==================
    // =====================================================

    /**
     * Renderer elementów listy wiadomości.
     */
    private static class MessageListCellRenderer
            extends DefaultListCellRenderer {

        private final boolean isInbox;

        MessageListCellRenderer(boolean isInbox) {
            this.isInbox = isInbox;
        }

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            JLabel label = (JLabel)
                    super.getListCellRendererComponent(
                            list, value, index,
                            isSelected, cellHasFocus);

            if (value instanceof Message) {
                Message msg = (Message) value;

                label.setText("<html>"
                        + (msg.isRead()
                           ? ""
                           : "<b style='color:blue;'>*</b> ")
                        + msg.getTitle()
                        + "<br><i style='font-size: smaller; color: gray;'>"
                        + (isInbox
                           ? "Od: " + msg.getSender().getLastName()
                           : "Do: " + msg.getRecipient().getLastName())
                        + " ("
                        + msg.getTimestamp().format(
                                DateTimeFormatter.ofPattern("dd.MM HH:mm"))
                        + ")</i></html>");

                label.setBorder(
                        BorderFactory.createEmptyBorder(5, 5, 5, 5));
            }
            return label;
        }
    }

    /**
     * Renderer elementów listy rozwijanej użytkowników.
     */
    private static class UserComboBoxRenderer
            extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            super.getListCellRendererComponent(
                    list, value, index,
                    isSelected, cellHasFocus);

            if (value instanceof User) {
                User user = (User) value;
                setText(user.getFullName()
                        + " (" + user.getRole() + ")");
            } else {
                setText(" -- Wybierz odbiorcę -- ");
            }
            return this;
        }
    }
}
