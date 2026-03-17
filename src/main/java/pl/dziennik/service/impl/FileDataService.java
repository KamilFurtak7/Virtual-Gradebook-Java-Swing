/**
 * Plik: FileDataService.java
 *
 * Opis:
 *  Implementacja warstwy dostępu do danych oparta na plikach tekstowych.
 *  Klasa odpowiada za wczytywanie oraz zapisywanie danych systemu
 *  dziennika elektronicznego, takich jak użytkownicy, klasy szkolne,
 *  oceny, plan lekcji, konsultacje oraz wiadomości.
 *
 * Główne elementy:
 *  - Implementacja interfejsu DataAccessService.
 *  - Współpraca z DatabaseContext jako centralnym magazynem danych.
 *  - Odczyt i zapis danych do plików tekstowych.
 *  - Mapy pomocnicze do wiązania obiektów podczas wczytywania danych.
 *
 * Elementy programowania obiektowego w tym pliku:
 *  - Interfejsy:
 *      Implementacja interfejsu DataAccessService.
 *
 *  - Polimorfizm:
 *      Metody loadData() i saveData() jako implementacje
 *      metod zdefiniowanych w interfejsie.
 *
 *  - Kolekcje obiektów:
 *      Wykorzystanie List oraz Map do przechowywania
 *      i przetwarzania danych systemowych.
 *
 *  - Obsługa wyjątków:
 *      Obsługa i propagowanie wyjątków typu IOException
 *      podczas operacji wejścia/wyjścia.
 *
 *  - Rzutowanie i instanceof:
 *      Sprawdzanie rzeczywistego typu obiektów User
 *      (Student, Teacher, Principal) podczas przetwarzania danych.
 */

package pl.dziennik.service.impl;

import pl.dziennik.model.communication.Message;
import pl.dziennik.model.school.Consultation;
import pl.dziennik.model.school.Grade;
import pl.dziennik.model.school.SchoolClass;
import pl.dziennik.model.school.Subject;
import pl.dziennik.model.school.TimetableEntry;
import pl.dziennik.model.user.Principal;
import pl.dziennik.model.user.Student;
import pl.dziennik.model.user.Teacher;
import pl.dziennik.model.user.User;
import pl.dziennik.persistence.DatabaseContext;
import pl.dziennik.service.interfaces.DataAccessService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Implementacja dostępu do danych oparta na plikach tekstowych.
 */
public class FileDataService implements DataAccessService {

    // Kontekst danych aplikacji
    private final DatabaseContext dbContext;

    // Ścieżki do plików danych
    private final String usersFile;
    private final String classesFile;
    private final String gradesFile;
    private final String timetableFile;
    private final String consultationsFile;
    private final String messagesFile;

    // Mapy pomocnicze do wiązania obiektów podczas wczytywania
    private final Map<String, User> userMap = new HashMap<>();
    private final Map<String, SchoolClass> classMap = new HashMap<>();
    private final Map<String, Subject> subjectMap = new HashMap<>();

    /**
     * Tworzy usługę dostępu do danych opartą na plikach tekstowych.
     *
     * @param dbContext         kontekst danych aplikacji
     * @param usersFile         plik użytkowników
     * @param classesFile       plik klas szkolnych
     * @param gradesFile        plik ocen
     * @param timetableFile     plik planu lekcji
     * @param consultationsFile plik konsultacji
     * @param messagesFile      plik wiadomości
     */
    public FileDataService(DatabaseContext dbContext,
                           String usersFile,
                           String classesFile,
                           String gradesFile,
                           String timetableFile,
                           String consultationsFile,
                           String messagesFile) {
        this.dbContext = dbContext;
        this.usersFile = usersFile;
        this.classesFile = classesFile;
        this.gradesFile = gradesFile;
        this.timetableFile = timetableFile;
        this.consultationsFile = consultationsFile;
        this.messagesFile = messagesFile;
    }

    /**
     * Wczytuje wszystkie dane systemu z plików tekstowych
     * do kontekstu danych aplikacji.
     *
     * @throws IOException w przypadku błędów odczytu plików
     */
    @Override
    public void loadData() throws IOException {

        // Czyszczenie struktur pomocniczych
        userMap.clear();
        classMap.clear();
        subjectMap.clear();

        // Czyszczenie danych w kontekście
        dbContext.getUsers().clear();
        dbContext.getClasses().clear();
        dbContext.getMessages().clear();

        // Wczytanie danych z poszczególnych plików
        loadUsers();
        loadClassesAndAssignStudents();
        loadGrades();
        loadTimetable();
        loadConsultations();
        loadMessages();

        System.out.println("Wczytywanie danych z wszystkich plików zakończone pomyślnie.");
    }

    /**
     * Zapisuje wszystkie dane systemu do plików tekstowych.
     *
     * @throws IOException w przypadku błędów zapisu plików
     */
    @Override
    public void saveData() throws IOException {
        saveUsers();
        saveClasses();
        saveGrades();
        saveMessages();

        System.out.println("Zapisywanie danych do plików zakończone pomyślnie.");
    }

    // =========================================================
    // =============== WCZYTYWANIE DANYCH ======================
    // =========================================================

    /**
     * Wczytuje użytkowników z pliku użytkowników.
     */
    private void loadUsers() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(";", -1);
                if (parts.length < 5) continue;

                String role = parts[0];
                String login = parts[1];
                String password = parts[2];
                String firstName = parts[3];
                String lastName = parts[4];

                User user = null;

                switch (role) {
                    case "STUDENT":
                        user = new Student(login, password, firstName, lastName);
                        break;
                    case "TEACHER":
                        user = new Teacher(login, password, firstName, lastName);
                        break;
                    case "PRINCIPAL":
                        user = new Principal(login, password, firstName, lastName);
                        break;
                }

                if (user != null) {
                    dbContext.getUsers().add(user);
                    userMap.put(login, user);
                }
            }
        }
    }

    /**
     * Wczytuje klasy szkolne oraz przypisuje uczniów do klas.
     */
    private void loadClassesAndAssignStudents() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(classesFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(";", -1);
                String type = parts[0];

                if ("KLASA".equals(type)) {
                    SchoolClass schoolClass = new SchoolClass(parts[1]);
                    User teacher = userMap.get(parts[2]);

                    if (teacher instanceof Teacher) {
                        schoolClass.setHomeroomTeacher((Teacher) teacher);
                    }

                    classMap.put(parts[1], schoolClass);
                    dbContext.getClasses().add(schoolClass);

                } else if ("PRZYPISZ_UCZNIA".equals(type)) {
                    SchoolClass schoolClass = classMap.get(parts[1]);
                    User student = userMap.get(parts[2]);

                    if (schoolClass != null && student instanceof Student) {
                        schoolClass.addStudent((Student) student);
                    }
                }
            }
        }
    }

    /**
     * Wczytuje oceny uczniów.
     */
    private void loadGrades() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(gradesFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(";", -1);
                if (parts.length < 6) continue;

                User student = userMap.get(parts[0]);
                Subject subject = subjectMap.computeIfAbsent(parts[1], Subject::new);
                double value = Double.parseDouble(parts[2].replace(',', '.'));
                int weight = Integer.parseInt(parts[3]);
                User teacher = userMap.get(parts[4]);
                String description = parts[5];

                if (student instanceof Student && teacher instanceof Teacher) {
                    Grade grade = new Grade(value, weight, description, subject, (Teacher) teacher);
                    ((Student) student).addGrade(grade);
                }
            }
        }
    }

    /**
     * Wczytuje plan lekcji klas.
     */
    private void loadTimetable() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        try (BufferedReader reader = new BufferedReader(new FileReader(timetableFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;

                String[] p = line.split(";", -1);
                SchoolClass schoolClass = classMap.get(p[0]);
                String day = p[1];

                LocalTime start = LocalTime.parse(p[2], formatter);
                LocalTime end = LocalTime.parse(p[3], formatter);

                Subject subject = subjectMap.computeIfAbsent(p[4], Subject::new);
                User teacher = userMap.get(p[5]);

                if (schoolClass != null && teacher instanceof Teacher) {
                    TimetableEntry entry = new TimetableEntry(start, end, subject, (Teacher) teacher, p[6]);
                    schoolClass.addTimetableEntry(day, entry);
                }
            }
        }
    }

    /**
     * Wczytuje konsultacje nauczycieli.
     */
    private void loadConsultations() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        try (BufferedReader reader = new BufferedReader(new FileReader(consultationsFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;

                String[] p = line.split(";", -1);
                User user = userMap.get(p[0]);

                if (user instanceof Teacher) {
                    LocalTime start = LocalTime.parse(p[2], formatter);
                    LocalTime end = LocalTime.parse(p[3], formatter);
                    ((Teacher) user).addConsultation(
                            new Consultation(p[1], start, end, p[4])
                    );
                }
            }
        }
    }

    /**
     * Wczytuje wiadomości użytkowników.
     */
    private void loadMessages() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(messagesFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;

                String[] p = line.split(";", 6);
                User sender = userMap.get(p[0]);
                User recipient = userMap.get(p[1]);

                if (sender != null && recipient != null) {
                    Message msg = new Message(
                            sender,
                            recipient,
                            p[2],
                            p[3].replace("\\n", "\n"),
                            LocalDateTime.parse(p[4], Message.DATE_TIME_FORMATTER),
                            Boolean.parseBoolean(p[5])
                    );
                    dbContext.getMessages().add(msg);
                }
            }
        }
    }

    // =========================================================
    // ================= ZAPIS DANYCH ==========================
    // =========================================================

    /**
     * Zapisuje użytkowników do pliku.
     */
    private void saveUsers() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile))) {
            for (User user : dbContext.getUsers()) {
                StringJoiner sj = new StringJoiner(";");
                sj.add(user.getRole())
                  .add(user.getLogin())
                  .add(user.getPassword())
                  .add(user.getFirstName())
                  .add(user.getLastName());
                writer.write(sj + "\n");
            }
        }
    }

    /**
     * Zapisuje klasy szkolne oraz przypisania uczniów.
     */
    private void saveClasses() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(classesFile))) {
            for (SchoolClass sc : dbContext.getClasses()) {
                writer.write("KLASA;" + sc.getName() + ";" +
                        (sc.getHomeroomTeacher() != null ? sc.getHomeroomTeacher().getLogin() : "") + "\n");

                for (Student s : sc.getStudents()) {
                    writer.write("PRZYPISZ_UCZNIA;" + sc.getName() + ";" + s.getLogin() + "\n");
                }
            }
        }
    }

    /**
     * Zapisuje oceny uczniów.
     */
    private void saveGrades() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(gradesFile))) {
            for (User u : dbContext.getUsers()) {
                if (u instanceof Student) {
                    Student s = (Student) u;
                    for (Grade g : s.getGrades()) {
                        writer.write(String.format("%s;%s;%.1f;%d;%s;%s\n",
                                s.getLogin(),
                                g.getSubject().getName(),
                                g.getValue(),
                                g.getWeight(),
                                g.getTeacher().getLogin(),
                                g.getDescription()).replace('.', ','));
                    }
                }
            }
        }
    }

    /**
     * Zapisuje wiadomości użytkowników.
     */
    private void saveMessages() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(messagesFile))) {
            for (Message m : dbContext.getMessages()) {
                writer.write(String.format("%s;%s;%s;%s;%s;%s\n",
                        m.getSender().getLogin(),
                        m.getRecipient().getLogin(),
                        m.getTitle(),
                        m.getContent().replace("\n", "\\n"),
                        m.getTimestamp().format(Message.DATE_TIME_FORMATTER),
                        m.isRead()));
            }
        }
    }
}
