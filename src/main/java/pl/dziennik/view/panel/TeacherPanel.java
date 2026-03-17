/**
 * Plik: TeacherPanel.java
 *
 * Opis:
 *  Panel interfejsu użytkownika przeznaczony dla roli NAUCZYCIELA
 *  w aplikacji „Wirtualny Dziennik”.
 *
 *  Panel umożliwia:
 *   - podgląd własnego planu zajęć,
 *   - przegląd klas prowadzonych przez nauczyciela,
 *   - przegląd uczniów w klasach,
 *   - wystawianie i przegląd ocen,
 *   - obsługę systemu wiadomości.
 *
 *  Klasa pełni rolę View w architekturze MVC.
 */

package pl.dziennik.view.panel;

import pl.dziennik.model.school.*;
import pl.dziennik.model.user.*;
import pl.dziennik.persistence.DatabaseContext;
import pl.dziennik.service.interfaces.MessageService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Panel główny nauczyciela.
 * Dziedziczy po klasie JPanel.
 */
public class TeacherPanel extends JPanel {

    /* ===================== KOMPONENTY GUI ===================== */

    // Lista klas prowadzonych przez nauczyciela
    private final JList<SchoolClass> groupsList;
    private final DefaultListModel<SchoolClass> groupsListModel;

    // Lista uczniów w wybranej klasie
    private final JList<Student> studentsList;
    private final DefaultListModel<Student> studentsListModel;

    // Tabela ocen ucznia
    private final JTable gradesTable;
    private final DefaultTableModel gradesTableModel;

    // Przycisk dodawania nowej oceny
    private final JButton addGradeButton;

    // Panel planu zajęć nauczyciela
    private final TimetablePanel timetablePanel;

    // Panel wiadomości
    private final MessagingPanel messagingPanel;

    /* ===================== DANE APLIKACJI ===================== */

    private Teacher currentTeacher;
    private DatabaseContext dbContext;
    private MessageService messageService;

    /**
     * Konstruktor – buduje interfejs nauczyciela.
     */
    public TeacherPanel() {

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JTabbedPane tabbedPane = new JTabbedPane();

        /* ===================================================== */
        /* ZAKŁADKA 1: PLAN ZAJĘĆ                                */
        /* ===================================================== */

        timetablePanel = new TimetablePanel();
        tabbedPane.addTab("Plan zajęć", timetablePanel);

        /* ===================================================== */
        /* ZAKŁADKA 2: KLASY, UCZNIOWIE I OCENY                  */
        /* ===================================================== */

        groupsListModel = new DefaultListModel<>();
        groupsList = new JList<>(groupsListModel);
        groupsList.setCellRenderer(new SchoolClassListCellRenderer());
        groupsList.setBorder(BorderFactory.createTitledBorder("Moje klasy"));

        studentsListModel = new DefaultListModel<>();
        studentsList = new JList<>(studentsListModel);
        studentsList.setCellRenderer(new StudentListCellRenderer());
        studentsList.setBorder(BorderFactory.createTitledBorder("Uczniowie w klasie"));

        String[] gradeColumns = {"Przedmiot", "Ocena", "Waga", "Opis", "Data"};
        gradesTableModel = new DefaultTableModel(gradeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        gradesTable = new JTable(gradesTableModel);

        addGradeButton = new JButton("Dodaj ocenę dla wybranego ucznia");
        addGradeButton.setEnabled(false);

        JPanel gradesPanel = new JPanel(new BorderLayout(5, 5));
        gradesPanel.setBorder(BorderFactory.createTitledBorder("Oceny ucznia"));
        gradesPanel.add(new JScrollPane(gradesTable), BorderLayout.CENTER);
        gradesPanel.add(addGradeButton, BorderLayout.SOUTH);

        JSplitPane studentGradeSplit =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        new JScrollPane(studentsList),
                        gradesPanel);
        studentGradeSplit.setDividerLocation(200);
        studentGradeSplit.setResizeWeight(0.3);

        JSplitPane mainSplit =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        new JScrollPane(groupsList),
                        studentGradeSplit);
        mainSplit.setDividerLocation(150);
        mainSplit.setResizeWeight(0.2);

        tabbedPane.addTab("Klasy, Uczniowie i Oceny", mainSplit);

        /* ===================================================== */
        /* ZAKŁADKA 3: WIADOMOŚCI                                */
        /* ===================================================== */

        messagingPanel = new MessagingPanel();
        tabbedPane.addTab("Wiadomości", messagingPanel);

        add(tabbedPane, BorderLayout.CENTER);
        setupListeners();
    }

    /**
     * Główna metoda inicjalizująca widok nauczyciela.
     */
    public void setTeacherData(Teacher teacher,
                               DatabaseContext dbContext,
                               MessageService messageService) {

        this.currentTeacher = teacher;
        this.dbContext = dbContext;
        this.messageService = messageService;

        populateTeacherTimetable();
        populateTeacherGroups();

        studentsListModel.clear();
        gradesTableModel.setRowCount(0);
        addGradeButton.setEnabled(false);

        messagingPanel.setMessagingData(teacher, messageService, dbContext);
    }

    /* ===================== LISTENERY ===================== */

    private void setupListeners() {

        groupsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                SchoolClass selectedGroup = groupsList.getSelectedValue();
                studentsListModel.clear();
                gradesTableModel.setRowCount(0);
                addGradeButton.setEnabled(false);

                if (selectedGroup != null) {
                    populateStudentsList(selectedGroup);
                }
            }
        });

        studentsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Student selectedStudent = studentsList.getSelectedValue();
                gradesTableModel.setRowCount(0);
                addGradeButton.setEnabled(
                        selectedStudent != null && groupsList.getSelectedValue() != null
                );

                if (selectedStudent != null) {
                    populateGradesTable(selectedStudent);
                }
            }
        });

        addGradeButton.addActionListener(e -> addGradeForSelectedStudent());
    }

    /* ===================== PLAN ZAJĘĆ NAUCZYCIELA ===================== */

    private void populateTeacherTimetable() {

        SchoolClass teacherSchedule =
                new SchoolClass("PlanZajec_" + currentTeacher.getLogin());

        if (dbContext == null || dbContext.getClasses() == null) {
            timetablePanel.setTimetableData(teacherSchedule);
            return;
        }

        for (SchoolClass group : dbContext.getClasses()) {
            if (group.getTimetable() == null) continue;

            for (Map.Entry<String, List<TimetableEntry>> dayEntry : group.getTimetable().entrySet()) {
                String day = dayEntry.getKey();
                List<TimetableEntry> lessons = dayEntry.getValue();
                if (lessons == null) continue;

                for (TimetableEntry lesson : lessons) {
                    if (lesson.getTeacher().equals(currentTeacher)) {
                        teacherSchedule.addTimetableEntry(day, lesson);
                    }
                }
            }
        }

        timetablePanel.setTimetableData(teacherSchedule);
    }

    /* ===================== DANE KLAS I UCZNIÓW ===================== */

    private void populateTeacherGroups() {

        groupsListModel.clear();
        Set<SchoolClass> taughtGroups = new HashSet<>();

        if (dbContext != null && dbContext.getClasses() != null) {
            for (SchoolClass group : dbContext.getClasses()) {

                if (group.getHomeroomTeacher() != null &&
                        group.getHomeroomTeacher().equals(currentTeacher)) {
                    taughtGroups.add(group);
                }

                if (group.getTimetable() != null) {
                    for (List<TimetableEntry> lessons : group.getTimetable().values()) {
                        if (lessons.stream()
                                .anyMatch(l -> l.getTeacher().equals(currentTeacher))) {
                            taughtGroups.add(group);
                            break;
                        }
                    }
                }
            }
        }

        new ArrayList<>(taughtGroups).stream()
                .sorted(Comparator.comparing(SchoolClass::getName))
                .forEach(groupsListModel::addElement);
    }

    private void populateStudentsList(SchoolClass group) {

        studentsListModel.clear();
        if (group != null && group.getStudents() != null) {
            group.getStudents().stream()
                    .sorted(Comparator.comparing(Student::getLastName)
                            .thenComparing(Student::getFirstName))
                    .forEach(studentsListModel::addElement);
        }
    }

    private void populateGradesTable(Student student) {

        gradesTableModel.setRowCount(0);

        if (student != null && student.getGrades() != null) {
            for (Grade grade : student.getGrades()) {
                if (grade.getTeacher().equals(currentTeacher)) {
                    gradesTableModel.addRow(new Object[]{
                            grade.getSubject().getName(),
                            grade.getValue(),
                            grade.getWeight(),
                            grade.getDescription(),
                            grade.getDate().toString()
                    });
                }
            }
        }
    }

    /* ===================== DODAWANIE OCEN ===================== */

    private void addGradeForSelectedStudent() {

        Student selectedStudent = studentsList.getSelectedValue();
        SchoolClass selectedGroup = groupsList.getSelectedValue();
        if (selectedStudent == null || selectedGroup == null) return;

        Set<Subject> subjects =
                selectedGroup.getTimetable().values().stream()
                        .flatMap(List::stream)
                        .filter(e -> e.getTeacher().equals(currentTeacher))
                        .map(TimetableEntry::getSubject)
                        .collect(Collectors.toSet());

        if (subjects.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nie prowadzisz żadnych zajęć z tą grupą.",
                    "Informacja",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        JComboBox<Subject> subjectComboBox =
                new JComboBox<>(subjects.toArray(new Subject[0]));
        subjectComboBox.setRenderer(new SubjectComboBoxRenderer());

        JTextField gradeField = new JTextField();
        JTextField weightField = new JTextField("1");
        JTextField descriptionField = new JTextField();

        panel.add(new JLabel("Przedmiot:")); panel.add(subjectComboBox);
        panel.add(new JLabel("Ocena:")); panel.add(gradeField);
        panel.add(new JLabel("Waga:")); panel.add(weightField);
        panel.add(new JLabel("Opis:")); panel.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Dodawanie oceny",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                Subject subject = (Subject) subjectComboBox.getSelectedItem();
                double value = Double.parseDouble(gradeField.getText().replace(',', '.'));
                int weight = Integer.parseInt(weightField.getText());
                String description = descriptionField.getText();

                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Opis nie może być pusty.",
                            "Błąd",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Grade grade = new Grade(value, weight, description, subject, currentTeacher);
                selectedStudent.addGrade(grade);
                populateGradesTable(selectedStudent);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Nieprawidłowe dane liczbowe.",
                        "Błąd",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /* ===================== RENDERERY ===================== */

    private static class SchoolClassListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof SchoolClass) {
                setText(((SchoolClass) value).getName());
            }
            return this;
        }
    }

    private static class StudentListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Student) {
                setText(((Student) value).getFullName());
            }
            return this;
        }
    }

    private static class SubjectComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Subject) {
                setText(((Subject) value).getName());
            }
            return this;
        }
    }
}
