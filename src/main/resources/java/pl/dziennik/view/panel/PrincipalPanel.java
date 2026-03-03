/**
 * Plik: PrincipalPanel.java
 *
 * Opis:
 *  Panel interfejsu użytkownika przeznaczony dla roli DYREKTORA
 *  w aplikacji „Wirtualny Dziennik”.
 *
 *  Panel umożliwia:
 *   - zarządzanie nauczycielami,
 *   - zarządzanie klasami,
 *   - przypisywanie i przenoszenie uczniów,
 *   - ustawianie wychowawców klas,
 *   - obsługę systemu wiadomości.
 *
 *  Klasa pełni rolę View w architekturze MVC.
 */


package pl.dziennik.view.panel;

import pl.dziennik.model.school.SchoolClass;
import pl.dziennik.model.user.Principal;
import pl.dziennik.model.user.Student;
import pl.dziennik.model.user.Teacher;
import pl.dziennik.model.user.User;
import pl.dziennik.persistence.DatabaseContext;
import pl.dziennik.service.interfaces.ReportService;
import pl.dziennik.service.interfaces.MessageService; // Dodaj import

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Główny panel dyrektora szkoły.
 */
public class PrincipalPanel extends JPanel {


    private final JList<Teacher> teachersListManage;
    private final DefaultListModel<Teacher> teachersListManageModel;
    private final JButton addTeacherButton, removeTeacherButton;
    private final JTextArea teacherDetailsArea;
    private final JList<SchoolClass> classesListManage;
    private final DefaultListModel<SchoolClass> classesListManageModel;
    private final JButton addClassButton, removeClassButton;
    private final JList<Student> studentsInClassList;
    private final DefaultListModel<Student> studentsInClassListModel;
    private final JButton removeStudentButton;
    private final JList<Student> unassignedStudentsList;
    private final DefaultListModel<Student> unassignedStudentsListModel;
    private final JComboBox<SchoolClass> targetClassComboBox;
    private final JButton moveStudentButton;
    private final JButton assignStudentButton;
    private final JButton addStudentButton;
    private final JLabel currentHomeroomLabel;
    private final JComboBox<Teacher> teacherComboBoxAssign;
    private final JButton assignHomeroomButton;
    private final MessagingPanel messagingPanel;
    
    // Dane aplikacji
    private Principal currentPrincipal;
    private DatabaseContext dbContext;
    private ReportService reportService;
    private MessageService messageService; 

    public PrincipalPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JTabbedPane tabbedPane = new JTabbedPane();

        // === 1. Zakładka: Zarządzanie Nauczycielami ===
        teachersListManageModel = new DefaultListModel<>();
        teachersListManage = new JList<>(teachersListManageModel);
        teachersListManage.setCellRenderer(new TeacherListCellRenderer());
        teachersListManage.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addTeacherButton = new JButton("Dodaj Nauczyciela");
        removeTeacherButton = new JButton("Usuń Nauczyciela");
        removeTeacherButton.setEnabled(false);
        JPanel teacherButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        teacherButtonsPanel.add(addTeacherButton);
        teacherButtonsPanel.add(removeTeacherButton);

        JPanel teachersListPanel = new JPanel(new BorderLayout());
        teachersListPanel.setBorder(BorderFactory.createTitledBorder("Nauczyciele"));
        teachersListPanel.add(new JScrollPane(teachersListManage), BorderLayout.CENTER);
        teachersListPanel.add(teacherButtonsPanel, BorderLayout.SOUTH);

        teacherDetailsArea = new JTextArea("Wybierz nauczyciela, aby zobaczyć szczegóły.");
        teacherDetailsArea.setEditable(false);
        teacherDetailsArea.setLineWrap(true);
        teacherDetailsArea.setWrapStyleWord(true);
        teacherDetailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        teacherDetailsArea.setBorder(BorderFactory.createTitledBorder("Informacje o nauczycielu"));

        JSplitPane teacherManagementSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                teachersListPanel, new JScrollPane(teacherDetailsArea));
        teacherManagementSplit.setDividerLocation(300);
        teacherManagementSplit.setResizeWeight(0.4);
        tabbedPane.addTab("Zarządzanie Nauczycielami", teacherManagementSplit);


        // === 2. Zakładka: Zarządzanie Klasami i Uczniami ===
        classesListManageModel = new DefaultListModel<>();
        classesListManage = new JList<>(classesListManageModel);
        classesListManage.setCellRenderer(new SchoolClassListCellRenderer());
        classesListManage.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classesListManage.setBorder(BorderFactory.createTitledBorder("Klasy"));

        addClassButton = new JButton("Dodaj Klasę");
        removeClassButton = new JButton("Usuń Klasę");
        removeClassButton.setEnabled(false);
        JPanel classButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        classButtonsPanel.add(addClassButton);
        classButtonsPanel.add(removeClassButton);

        JPanel classesListPanel = new JPanel(new BorderLayout());
        classesListPanel.add(new JScrollPane(classesListManage), BorderLayout.CENTER);
        classesListPanel.add(classButtonsPanel, BorderLayout.SOUTH);

        studentsInClassListModel = new DefaultListModel<>();
        studentsInClassList = new JList<>(studentsInClassListModel);
        studentsInClassList.setCellRenderer(new StudentListCellRenderer());
        studentsInClassList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsInClassList.setBorder(BorderFactory.createTitledBorder("Uczniowie w wybranej klasie"));

        removeStudentButton = new JButton("Usuń z klasy");
        removeStudentButton.setEnabled(false);

        JPanel studentsInClassPanel = new JPanel(new BorderLayout());
        studentsInClassPanel.add(new JScrollPane(studentsInClassList), BorderLayout.CENTER);
        studentsInClassPanel.add(removeStudentButton, BorderLayout.SOUTH);

        unassignedStudentsListModel = new DefaultListModel<>();
        unassignedStudentsList = new JList<>(unassignedStudentsListModel);
        unassignedStudentsList.setCellRenderer(new StudentListCellRenderer());
        unassignedStudentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        unassignedStudentsList.setBorder(BorderFactory.createTitledBorder("Uczniowie nieprzypisani"));

        assignStudentButton = new JButton("Przypisz do wybranej klasy");
        assignStudentButton.setEnabled(false);
        addStudentButton = new JButton("Dodaj nowego ucznia");

        JPanel unassignedStudentsPanel = new JPanel(new BorderLayout());
        unassignedStudentsPanel.add(new JScrollPane(unassignedStudentsList), BorderLayout.CENTER);
        JPanel unassignedButtons = new JPanel(new GridLayout(0, 1, 5, 5));
        unassignedButtons.add(assignStudentButton);
        unassignedButtons.add(addStudentButton);
        unassignedStudentsPanel.add(unassignedButtons, BorderLayout.SOUTH);

        targetClassComboBox = new JComboBox<>();
        targetClassComboBox.setRenderer(new SchoolClassListCellRenderer());
        moveStudentButton = new JButton("Przenieś ucznia z klasy do:");
        moveStudentButton.setEnabled(false);

        currentHomeroomLabel = new JLabel("Wybierz klasę...");
        currentHomeroomLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        teacherComboBoxAssign = new JComboBox<>();
        teacherComboBoxAssign.setRenderer(new TeacherListCellRenderer());
        assignHomeroomButton = new JButton("Ustaw jako wychowawcę");
        assignHomeroomButton.setEnabled(false);

        JPanel assignHomeroomPanel = new JPanel(new BorderLayout(5,5));
        assignHomeroomPanel.setBorder(BorderFactory.createTitledBorder("Wychowawca klasy"));
        assignHomeroomPanel.add(currentHomeroomLabel, BorderLayout.NORTH);
        assignHomeroomPanel.add(teacherComboBoxAssign, BorderLayout.CENTER);
        assignHomeroomPanel.add(assignHomeroomButton, BorderLayout.SOUTH);

        JPanel movePanel = new JPanel(new BorderLayout(5, 5));
        movePanel.setBorder(BorderFactory.createTitledBorder("Przenieś ucznia"));
        JPanel moveButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        moveButtonsPanel.add(moveStudentButton);
        moveButtonsPanel.add(targetClassComboBox);
        movePanel.add(moveButtonsPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.add(assignHomeroomPanel);
        actionPanel.add(Box.createVerticalStrut(10));
        actionPanel.add(movePanel);

        JSplitPane studentsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                      studentsInClassPanel,
                                                      unassignedStudentsPanel);
        studentsSplitPane.setDividerLocation(300);
        studentsSplitPane.setResizeWeight(0.5);

        JPanel rightSidePanel = new JPanel(new BorderLayout(0, 10));
        rightSidePanel.add(studentsSplitPane, BorderLayout.CENTER);
        rightSidePanel.add(actionPanel, BorderLayout.SOUTH);

        JSplitPane mainClassStudentSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                         classesListPanel,
                                                         rightSidePanel);
        mainClassStudentSplit.setDividerLocation(200);
        mainClassStudentSplit.setResizeWeight(0.25);

        tabbedPane.addTab("Zarządzanie Klasami/Uczniami", mainClassStudentSplit);

        messagingPanel = new MessagingPanel();
        tabbedPane.addTab("Wiadomości", messagingPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        setupListeners();
    }

    public void setPrincipalData(Principal principal, DatabaseContext dbContext, ReportService reportService, MessageService messageService2) {
        this.currentPrincipal = principal;
        this.dbContext = dbContext;
        this.reportService = reportService;
        this.messageService = messageService;

        refreshAllLists();
        messagingPanel.setMessagingData(principal, messageService, dbContext);
    }

    private void refreshAllLists() {
        populateTeachersDisplayList();
        populateClassesManageList();
        populateTargetClassComboBox();
        populateTeacherComboBoxAssign();
        populateUnassignedStudentsList();
        studentsInClassListModel.clear();
        removeStudentButton.setEnabled(false);
        moveStudentButton.setEnabled(false);
        assignStudentButton.setEnabled(false);
        updateCurrentHomeroomLabel(null);
        teacherDetailsArea.setText("Wybierz nauczyciela...");
        removeTeacherButton.setEnabled(false);
        removeClassButton.setEnabled(false);
        assignHomeroomButton.setEnabled(false);
    }

    private void setupListeners() {
        teachersListManage.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Teacher selectedTeacher = teachersListManage.getSelectedValue();
                removeTeacherButton.setEnabled(selectedTeacher != null);
                displayTeacherDetails(selectedTeacher);
            }
        });
        addTeacherButton.addActionListener(e -> addTeacher());
        removeTeacherButton.addActionListener(e -> removeTeacher());

        classesListManage.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                SchoolClass selectedClass = classesListManage.getSelectedValue();
                boolean classSelected = selectedClass != null;
                removeClassButton.setEnabled(classSelected && (selectedClass.getStudents() == null || selectedClass.getStudents().isEmpty()));
                studentsInClassListModel.clear();
                removeStudentButton.setEnabled(false);
                moveStudentButton.setEnabled(false);
                assignStudentButton.setEnabled(classSelected && unassignedStudentsList.getSelectedValue() != null);
                assignHomeroomButton.setEnabled(classSelected && teacherComboBoxAssign.getSelectedItem() != null && teacherComboBoxAssign.getSelectedIndex() != 0); // Sprawdź czy nie jest pusta opcja
                updateCurrentHomeroomLabel(selectedClass);
                if (classSelected) {
                    populateStudentsInClassList(selectedClass);
                }
            }
        });
        addClassButton.addActionListener(e -> addClass());
        removeClassButton.addActionListener(e -> removeClass());

        studentsInClassList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean studentSelected = studentsInClassList.getSelectedValue() != null;
                removeStudentButton.setEnabled(studentSelected);
                moveStudentButton.setEnabled(studentSelected);
                if (studentSelected) unassignedStudentsList.clearSelection();
            }
        });

        unassignedStudentsList.addListSelectionListener(e -> {
             if (!e.getValueIsAdjusting()) {
                 boolean studentSelected = unassignedStudentsList.getSelectedValue() != null;
                 assignStudentButton.setEnabled(studentSelected && classesListManage.getSelectedValue() != null);
                 if (studentSelected) studentsInClassList.clearSelection();
             }
        });

        removeStudentButton.addActionListener(e -> removeStudentFromClass());
        moveStudentButton.addActionListener(e -> moveStudentToClass());
        assignStudentButton.addActionListener(e -> assignStudentToClass());
        addStudentButton.addActionListener(e -> addStudent());

        teacherComboBoxAssign.addActionListener(e -> {
            
             assignHomeroomButton.setEnabled(teacherComboBoxAssign.getSelectedItem() != null && teacherComboBoxAssign.getSelectedIndex() != 0 && classesListManage.getSelectedValue() != null);
        });
        assignHomeroomButton.addActionListener(e -> assignHomeroomTeacher());
    }

    // --- Metody do populacji danymi ---
    private void populateTeachersDisplayList() {
        teachersListManageModel.clear();
        if (dbContext != null && dbContext.getUsers() != null) {
            dbContext.getUsers().stream()
                .filter(user -> user instanceof Teacher)
                .map(user -> (Teacher) user)
                .sorted(Comparator.comparing(User::getLastName).thenComparing(User::getFirstName))
                .forEach(teachersListManageModel::addElement);
        }
        teachersListManage.clearSelection();
        removeTeacherButton.setEnabled(false); 
        teacherDetailsArea.setText("Wybierz nauczyciela..."); 
    }

    private void populateClassesManageList() {
        classesListManageModel.clear();
        if (dbContext != null && dbContext.getClasses() != null) {
            dbContext.getClasses().stream()
                .sorted(Comparator.comparing(SchoolClass::getName))
                .forEach(classesListManageModel::addElement);
        }
         classesListManage.clearSelection();
         removeClassButton.setEnabled(false);
    }

    private void populateTargetClassComboBox() {
        SchoolClass selectedItem = (SchoolClass) targetClassComboBox.getSelectedItem();
        targetClassComboBox.removeAllItems();
        targetClassComboBox.addItem(null);
         if (dbContext != null && dbContext.getClasses() != null) {
            dbContext.getClasses().stream()
                .sorted(Comparator.comparing(SchoolClass::getName))
                .forEach(targetClassComboBox::addItem);
        }
        targetClassComboBox.setSelectedItem(selectedItem);
    }

    private void populateTeacherComboBoxAssign() {
        Teacher selectedItem = (Teacher) teacherComboBoxAssign.getSelectedItem();
        teacherComboBoxAssign.removeAllItems();
        teacherComboBoxAssign.addItem(null);
        if (dbContext != null && dbContext.getUsers() != null) {
            dbContext.getUsers().stream()
                .filter(user -> user instanceof Teacher)
                .map(user -> (Teacher) user)
                .sorted(Comparator.comparing(User::getLastName).thenComparing(User::getFirstName))
                .forEach(teacherComboBoxAssign::addItem);
        }
        teacherComboBoxAssign.setSelectedItem(selectedItem);
    }


    private void populateStudentsInClassList(SchoolClass selectedClass) {
        studentsInClassListModel.clear();
        if (selectedClass != null && selectedClass.getStudents() != null) {
            selectedClass.getStudents().stream()
                .sorted(Comparator.comparing(User::getLastName).thenComparing(User::getFirstName))
                .forEach(studentsInClassListModel::addElement);
        }
        studentsInClassList.clearSelection();
        removeStudentButton.setEnabled(false);
        moveStudentButton.setEnabled(false);
    }

    private void populateUnassignedStudentsList() {
        unassignedStudentsListModel.clear();
        if (dbContext != null && dbContext.getUsers() != null) {
            dbContext.getUsers().stream()
                .filter(user -> user instanceof Student && ((Student) user).getSchoolClass() == null)
                .map(user -> (Student) user)
                .sorted(Comparator.comparing(User::getLastName).thenComparing(User::getFirstName))
                .forEach(unassignedStudentsListModel::addElement);
        }
        unassignedStudentsList.clearSelection();
        assignStudentButton.setEnabled(false); 
    }

     private void updateCurrentHomeroomLabel(SchoolClass selectedClass) {
        if (selectedClass != null) {
            Teacher currentTeacher = selectedClass.getHomeroomTeacher();
            if (currentTeacher != null) {
                currentHomeroomLabel.setText("Obecny wychowawca: " + currentTeacher.getFullName());
                currentHomeroomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                teacherComboBoxAssign.setSelectedItem(currentTeacher); 
            } else {
                currentHomeroomLabel.setText("Klasa nie ma przypisanego wychowawcy.");
                currentHomeroomLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                teacherComboBoxAssign.setSelectedItem(null); 
            }
        } else {
            currentHomeroomLabel.setText("Wybierz klasę...");
            currentHomeroomLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            teacherComboBoxAssign.setSelectedItem(null);
        }
        // Zaktualizuj stan przycisku przypisania wychowawcy
        assignHomeroomButton.setEnabled(selectedClass != null && teacherComboBoxAssign.getSelectedItem() != null && teacherComboBoxAssign.getSelectedIndex() != 0);
    }

    private void displayTeacherDetails(Teacher teacher) {
        if (teacher == null) {
            teacherDetailsArea.setText("Wybierz nauczyciela...");
            return;
        }
        StringBuilder details = new StringBuilder();
        details.append("Imię i Nazwisko: ").append(teacher.getFullName()).append("\n");
        details.append("Login: ").append(teacher.getLogin()).append("\n\n");

        details.append("Klasy, w których uczy (wg planu zajęć):\n");
        List<String> taughtClasses = dbContext.getClasses().stream()
                .filter(sc -> sc.getTimetable() != null && sc.getTimetable().values().stream()
                        .flatMap(List::stream)
                        .anyMatch(entry -> entry.getTeacher() != null && entry.getTeacher().equals(teacher)))
                .map(SchoolClass::getName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        if (taughtClasses.isEmpty()) {
            details.append("- Brak przypisanych klas w planie\n");
        } else {
            taughtClasses.forEach(className -> details.append("- ").append(className).append("\n"));
        }

        teacherDetailsArea.setText(details.toString());
        teacherDetailsArea.setCaretPosition(0);
    }

    // --- Metody obsługujące akcje ---

    private void addTeacher() {
        JTextField loginField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Login:")); panel.add(loginField);
        panel.add(new JLabel("Hasło:")); panel.add(passwordField);
        panel.add(new JLabel("Imię:")); panel.add(firstNameField);
        panel.add(new JLabel("Nazwisko:")); panel.add(lastNameField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Dodaj nowego nauczyciela",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String login = loginField.getText().trim();
            String password = new String(passwordField.getPassword());
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();

            if (login.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Wszystkie pola muszą być wypełnione.", "Błąd", JOptionPane.ERROR_MESSAGE); return;
            }
            if (dbContext.getUsers().stream().anyMatch(u -> u.getLogin().equals(login))) {
                 JOptionPane.showMessageDialog(this, "Nauczyciel o podanym loginie już istnieje.", "Błąd", JOptionPane.ERROR_MESSAGE); return;
            }

            Teacher newTeacher = new Teacher(login, password, firstName, lastName);
            dbContext.getUsers().add(newTeacher);
            populateTeachersDisplayList();
            populateTeacherComboBoxAssign(); // Odśwież też ComboBox
            reportService.logAction("Dyrektor dodał nauczyciela: " + newTeacher.getFullName() + " (login: " + login + ")");
        }
    }

    private void removeTeacher() {
        Teacher selectedTeacher = teachersListManage.getSelectedValue();
        if (selectedTeacher == null) return;

        // Sprawdzenie, czy nauczyciel jest wychowawcą jakiejś klasy
        boolean isHomeroom = dbContext.getClasses().stream()
                                .anyMatch(sc -> selectedTeacher.equals(sc.getHomeroomTeacher()));
        // Sprawdzenie, czy nauczyciel prowadzi jakieś zajęcia
        boolean teachesClasses = dbContext.getClasses().stream()
                                .anyMatch(sc -> sc.getTimetable().values().stream()
                                    .flatMap(List::stream)
                                    .anyMatch(entry -> selectedTeacher.equals(entry.getTeacher())));

        String warningMessage = "";
        if (isHomeroom) warningMessage += "- Jest wychowawcą co najmniej jednej klasy.\n";
        if (teachesClasses) warningMessage += "- Prowadzi zajęcia w planie lekcji.\n";

        int confirm = JOptionPane.showConfirmDialog(this,
                "Czy na pewno chcesz usunąć nauczyciela: " + selectedTeacher.getFullName() + "?\n" +
                (warningMessage.isEmpty() ? "" : "UWAGA:\n" + warningMessage) +
                "Usunięcie może spowodować niespójności danych!\nTej operacji nie można cofnąć!",
                "Potwierdź usunięcie", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Zdejmij wychowawstwo, jeśli był wychowawcą
             dbContext.getClasses().stream()
                .filter(sc -> selectedTeacher.equals(sc.getHomeroomTeacher()))
                .forEach(sc -> sc.setHomeroomTeacher(null));
            dbContext.getUsers().remove(selectedTeacher);
            populateTeachersDisplayList();
            populateTeacherComboBoxAssign(); // Odśwież ComboBox
            populateClassesManageList(); // Odśwież listę klas (może zniknąć wychowawca)
            reportService.logAction("Dyrektor usunął nauczyciela: " + selectedTeacher.getFullName() + " (login: " + selectedTeacher.getLogin() + ")");
        }
    }

    private void addClass() {
        String inputClassName = JOptionPane.showInputDialog(this, "Podaj nazwę nowej klasy (np. 1Tb):", "Dodaj klasę", JOptionPane.PLAIN_MESSAGE);
        if (inputClassName != null && !inputClassName.trim().isEmpty()) {
            final String finalClassName = inputClassName.trim();
            if (dbContext.getClasses().stream().anyMatch(sc -> sc.getName().equalsIgnoreCase(finalClassName))) {
                 JOptionPane.showMessageDialog(this, "Klasa o podanej nazwie już istnieje.", "Błąd", JOptionPane.ERROR_MESSAGE); return;
            }
            SchoolClass newClass = new SchoolClass(finalClassName);
            dbContext.getClasses().add(newClass);
            populateClassesManageList();
            populateTargetClassComboBox();
            reportService.logAction("Dyrektor dodał nową klasę: " + finalClassName);
        }
    }

    private void removeClass() {
         SchoolClass selectedClass = classesListManage.getSelectedValue();
         if (selectedClass == null) return;

         if (selectedClass.getStudents() != null && !selectedClass.getStudents().isEmpty()) {
             JOptionPane.showMessageDialog(this, "Nie można usunąć klasy, która ma przypisanych uczniów.\nNajpierw przenieś lub usuń uczniów z klasy.", "Błąd", JOptionPane.ERROR_MESSAGE); return;
         }

         int confirm = JOptionPane.showConfirmDialog(this,
                "Czy na pewno chcesz usunąć klasę: " + selectedClass.getName() + "?\n" +
                "UWAGA: Tej operacji nie można cofnąć!",
                "Potwierdź usunięcie", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

         if (confirm == JOptionPane.YES_OPTION) {
             dbContext.getClasses().remove(selectedClass);
             populateClassesManageList();
             populateTargetClassComboBox();
             reportService.logAction("Dyrektor usunął klasę: " + selectedClass.getName());
         }
    }

    private void removeStudentFromClass() {
         Student selectedStudent = studentsInClassList.getSelectedValue();
         SchoolClass selectedClass = classesListManage.getSelectedValue();
         if (selectedStudent == null || selectedClass == null) return;

         int confirm = JOptionPane.showConfirmDialog(this,
                "Czy na pewno chcesz usunąć ucznia " + selectedStudent.getFullName() + " z klasy " + selectedClass.getName() + "?\n" +
                "(Uczeń pozostanie w systemie jako nieprzypisany)",
                "Potwierdź usunięcie", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

         if (confirm == JOptionPane.YES_OPTION) {
             selectedClass.removeStudent(selectedStudent);
             selectedStudent.setSchoolClass(null);
             populateStudentsInClassList(selectedClass);
             populateUnassignedStudentsList(); // Odśwież listę nieprzypisanych
             reportService.logAction("Dyrektor usunął ucznia " + selectedStudent.getFullName() + " z klasy " + selectedClass.getName());
         }
    }

    private void moveStudentToClass() {
        Student selectedStudent = studentsInClassList.getSelectedValue();
        SchoolClass sourceClass = classesListManage.getSelectedValue();
        SchoolClass targetClass = (SchoolClass) targetClassComboBox.getSelectedItem();

        if (selectedStudent == null || sourceClass == null || targetClass == null || targetClassComboBox.getSelectedIndex() == 0) { // Sprawdź, czy nie wybrano pustej opcji
             JOptionPane.showMessageDialog(this, "Wybierz ucznia (z listy 'w klasie'), klasę źródłową i klasę docelową.", "Błąd", JOptionPane.WARNING_MESSAGE); return;
        }
        if (sourceClass.equals(targetClass)) {
             JOptionPane.showMessageDialog(this, "Klasa źródłowa i docelowa są takie same.", "Informacja", JOptionPane.INFORMATION_MESSAGE); return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Czy na pewno chcesz przenieść ucznia " + selectedStudent.getFullName() + "\n" +
                "z klasy " + sourceClass.getName() + " do klasy " + targetClass.getName() + "?",
                "Potwierdź przeniesienie", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            sourceClass.removeStudent(selectedStudent);
            targetClass.addStudent(selectedStudent);
            populateStudentsInClassList(sourceClass);
            if (targetClass.equals(classesListManage.getSelectedValue())) {
                populateStudentsInClassList(targetClass);
            }
            reportService.logAction("Dyrektor przeniósł ucznia " + selectedStudent.getFullName() + " z klasy " + sourceClass.getName() + " do " + targetClass.getName());
        }
    }

    private void addStudent() {
        JTextField loginField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Login:")); panel.add(loginField);
        panel.add(new JLabel("Hasło:")); panel.add(passwordField);
        panel.add(new JLabel("Imię:")); panel.add(firstNameField);
        panel.add(new JLabel("Nazwisko:")); panel.add(lastNameField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Dodaj nowego ucznia",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String login = loginField.getText().trim();
            String password = new String(passwordField.getPassword());
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();

            if (login.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Wszystkie pola muszą być wypełnione.", "Błąd", JOptionPane.ERROR_MESSAGE); return;
            }
            if (dbContext.getUsers().stream().anyMatch(u -> u.getLogin().equals(login))) {
                 JOptionPane.showMessageDialog(this, "Użytkownik o podanym loginie już istnieje.", "Błąd", JOptionPane.ERROR_MESSAGE); return;
            }

            Student newStudent = new Student(login, password, firstName, lastName);
            dbContext.getUsers().add(newStudent);
            populateUnassignedStudentsList();
            reportService.logAction("Dyrektor dodał ucznia: " + newStudent.getFullName() + " (login: " + login + ")");
        }
    }

    private void assignStudentToClass() {
        Student selectedStudent = unassignedStudentsList.getSelectedValue();
        SchoolClass targetClass = classesListManage.getSelectedValue();

        if (selectedStudent == null || targetClass == null) {
             JOptionPane.showMessageDialog(this, "Wybierz ucznia z listy nieprzypisanych oraz klasę docelową.", "Błąd", JOptionPane.WARNING_MESSAGE); return;
        }

         int confirm = JOptionPane.showConfirmDialog(this,
                "Czy na pewno chcesz przypisać ucznia " + selectedStudent.getFullName() + "\n" +
                "do klasy " + targetClass.getName() + "?",
                "Potwierdź przypisanie", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            targetClass.addStudent(selectedStudent);
            populateUnassignedStudentsList();
            populateStudentsInClassList(targetClass);
            reportService.logAction("Dyrektor przypisał ucznia " + selectedStudent.getFullName() + " do klasy " + targetClass.getName());
        }
    }

    private void assignHomeroomTeacher() {
        SchoolClass selectedClass = classesListManage.getSelectedValue();
        Teacher selectedTeacher = (Teacher) teacherComboBoxAssign.getSelectedItem();

        // Dodatkowa walidacja - nie można przypisać pustej opcji
        if (selectedClass == null || selectedTeacher == null || teacherComboBoxAssign.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Proszę wybrać klasę oraz nauczyciela (innego niż pusta opcja).", "Błąd", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Teacher oldTeacher = selectedClass.getHomeroomTeacher();

        if(selectedTeacher.equals(oldTeacher)){
            JOptionPane.showMessageDialog(this, "Wybrany nauczyciel jest już wychowawcą tej klasy.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Czy na pewno chcesz przypisać nauczyciela:\n" + selectedTeacher.getFullName() + "\n" +
                "jako wychowawcę klasy " + selectedClass.getName() + "?",
                "Potwierdź przypisanie wychowawcy", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            selectedClass.setHomeroomTeacher(selectedTeacher);
            updateCurrentHomeroomLabel(selectedClass); // Odśwież etykietę

            String logMessage = String.format("Dyrektor (%s) przypisał nauczyciela %s jako wychowawcę klasy %s.",
                    currentPrincipal.getLogin(), selectedTeacher.getFullName(), selectedClass.getName());
            if (oldTeacher != null) {
                 logMessage += " (Poprzednio: " + oldTeacher.getFullName() + ")";
            }
            reportService.logAction(logMessage);

            JOptionPane.showMessageDialog(this, "Przypisano wychowawcę pomyślnie.", "Sukces", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    // --- Renderery list ---
    private static class TeacherListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Teacher) setText(((Teacher) value).getFullName());
            else if (value == null && index == -1) setText(" -- Wybierz nauczyciela -- "); // Podpowiedź w ComboBox
            else if (value == null) setText(""); // Dla pustej opcji w JList
            return this;
        }
    }
    private static class SchoolClassListCellRenderer extends DefaultListCellRenderer {
         @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof SchoolClass) setText(((SchoolClass) value).getName());
             else if (value == null && index == -1) setText(" -- Wybierz klasę -- "); // Podpowiedź w ComboBox
             else if (value == null) setText(""); // Dla pustej opcji w JList
            return this;
        }
    }
    private static class StudentListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Student) setText(((Student) value).getFullName());
            else if (value == null) setText("");
            return this;
        }
    }
}