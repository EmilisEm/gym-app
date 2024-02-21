package Interface;

import DbContext.*;
import Models.Client;
import Models.Workout;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class UserInterface {
    ClientContext clientContext;
    PaymentContext paymentContext;
    TrainerContext trainerContext;
    VisitContext visitContext;
    WorkoutContext workoutContext;
    Scanner textScanner;
    public UserInterface(ClientContext cc, PaymentContext pc, TrainerContext tc, VisitContext vc, WorkoutContext wc)
    {
        clientContext = cc;
        paymentContext = pc;
        trainerContext = tc;
        visitContext = vc;
        workoutContext = wc;
        textScanner = new Scanner(System.in);
    }

    private void printMenus()
    {
        System.out.println("1. Klientu meniu");
        System.out.println("2. Treneriu meniu");
        System.out.println("3. Treniruociu meniu");
        System.out.println("4. Apsilankymu meniu");
        System.out.println("5. Mokejimu meniu");

    }
    public MenuCode getMenuCode() {
        printMenus();
        int menu;
        try {
            menu = textScanner.nextInt();
        }
        catch (Exception e){
            return MenuCode.invalid;
        }

        MenuCode menuCode = switch (menu) {
            case 1 -> MenuCode.client;
            case 2 -> MenuCode.trainer;
            case 3 -> MenuCode.workout;
            case 4 -> MenuCode.visit;
            case 5 -> MenuCode.payment;
            default -> MenuCode.invalid;
        };

        return menuCode;
    }

    public boolean printClientMenu()
    {
        System.out.println("1. Gauti klienta palei ID");
        System.out.println("2. Gauti klienta palei el. pasta");
        System.out.println("3. Uzregistruoti klienta");
        System.out.println("4. Pakeisti kliento duomenis palei ID");
        System.out.println("5. Istrinti klienta palei ID");


        Integer option = getIntInput();
        Integer id;

        if (option == null)
        {
            System.out.println("Iveskite leidziama skaiciu");
            return false;
        }

        switch (option) {
            case 1 -> printClientById();
            case 2 -> printClientByEmail();
            case 3 -> registerNewClient();
            case 4 -> {
                id = getIntInput();
                if (id > 0) {
                    updateClient(id);
                    break;
                }
                clearScreen();
                System.out.println("Neteisingai ivestas vartotojo id");
            }
            case 5 -> {
                id = getIntInput();
                if (id > 0) {
                    deleteClient(id);
                    break;
                }
                clearScreen();
                System.out.println("Neteisingai ivestas vartotojo id");
            }
            default -> {
                System.out.println("Iveskite tinkama pasirinkima");
                return false;
            }
        }
        return true;
    }

    private Integer getIntInput()
    {
        int value;
        try {
            value = textScanner.nextInt();
        }
        catch (Exception e)
        {
            return null;
        }

        return value;
    }

    private void clearScreen()
    {
        System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    private void printClientById()
    {
        Integer inputNum = getIntInput();
        if (inputNum == null || inputNum < 1)
        {
            clearScreen();
            System.out.println("Neteisingai ivestas skaicius");
        }

        Client client = null;
        try {
            client = clientContext.getClientById(inputNum);
        }
        catch (SQLException e)
        {
            e.mess
            clearScreen();
            System.out.println("Ivyko klaida ieskant vartotojo");
        }

        if (client == null) {
            clearScreen();
            System.out.println("Nera tokio vartotojo");
        }

        clearScreen();
        System.out.println(client.toString());
    }

    private void printClientByEmail()
    {
        String email = "";
        email = textScanner.nextLine();
        if (email.equals(""))
        {
            clearScreen();
            System.out.println("Nebuvo ivestas el pastas");
            return;
        }

        Client client = null;
        try {
            client = clientContext.getClientByEmail(email);
        }
        catch (SQLException e)
        {
            clearScreen();
            System.out.println(e.getSQLState());
            System.out.println("Ivyko klaida ieskant vartotojo");
            return;
        }

        if (client == null) {
            clearScreen();
            System.out.println("Nera tokio vartotojo");
            return;
        }

        clearScreen();
        System.out.println(client.toString());
    }

    private void registerNewClient()
    {
        Client client = getClient();

        try {
            clientContext.registerClient(client);
        } catch (SQLException e) {
           clearScreen();
           System.out.println("Klaida registruojnt klienta");
           return;
        }
        System.out.println("Vartotojas uzregistruotas: " + client);
    }

    private void updateClient(int id)
    {
        Client client = getClient();
        client.id = id;

        try {
            clientContext.updateClientById(client);
        } catch (SQLException e)
        {
            clearScreen();
            System.out.println("Nepavyko atnaujinti kliento. " + e.getErrorCode());
            return;
        }

        clearScreen();
        System.out.println("Vartotojas atnaujintas sekmingai: " + client);
    }

    private void deleteClient(int id)
    {
        Client client = new Client();
        client.id = id;

        try {
            clientContext.deleteClientById(client);
        } catch (SQLException e)
        {
            clearScreen();
            System.out.println("Nepavyko istrinti kliento. " + e.getErrorCode());
            return;
        }

        clearScreen();
        System.out.println("Klientas istrintas sekmingai");

    }
    private Client getClient()
    {
        Client client = new Client();
        do {
            System.out.print("Iveskite kliento varda: ");
            client.name = textScanner.nextLine().strip();
        } while (client.name.isEmpty());

        do {
            System.out.print("Iveskite kliento pavarde: ");
            client.surname = textScanner.nextLine().strip();
        } while (client.surname.isEmpty());

        do {
            System.out.print("Iveskite kliento el pasta: ");
            client.email = textScanner.nextLine().strip();
        } while (client.email.isEmpty());

        do {
            System.out.print("Iveskite kliento lyti(vyras/moteris/kita): ");
            client.gender = textScanner.nextLine().strip().toLowerCase();

        } while (client.gender.isEmpty() || !client.gender.equals("vyras") && !client.gender.equals("moteris") && !client.gender.equals("kita"));

        do {
            System.out.print("Iveskite kliento gimimo data(YYYY-MM-DD): ");
            String input = textScanner.nextLine();

            try {
                client.birthDate = LocalDate.parse(input);
            } catch (Exception e) {
                System.out.println("Neteisingas datos formatas");
            }
        } while (client.birthDate == null);

        return client;
    }

    public boolean printWorkoutMenu()
    {
        System.out.println("1. Gauti treniruotes palei kliento ID");
        System.out.println("2. Gauti treniruotes palei trenerio ID");
        System.out.println("3. Uzregistruoti treniruote");

        Integer option = getIntInput();

        if (option == null)
        {
            System.out.println("Iveskite leidziama skaiciu");
            return false;
        }

        switch (option) {
            case 1 -> getClientWorkouts();
            case 2 -> getTrainerWorkouts();
            case 3 -> registerNewWorkout();
            default -> {
                System.out.println("Iveskite tinkama pasirinkima");
                return false;
            }
        }
        return true;
    }

    private void getClientWorkouts()
    {
        clearScreen();
        System.out.print("Iveskite kliento id: ");
        Integer option = getIntInput();
        List<Workout> workouts;
        if (option == null)
        {
            clearScreen();
            System.out.println("Iveskite leidziama skaiciu");
            return;
        }

        try {
            workouts = workoutContext.getClientWorkoutsByClientId(option);

        } catch (SQLException e) {
            clearScreen();
            System.out.println("Ivyko klaida gaunant treniruotes");
            return;
        }

        for (Workout workout : workouts) {
            System.out.println(workout);
        }

    }

    private void getTrainerWorkouts()
    {
        clearScreen();
        System.out.print("Iveskite trenerio id: ");
        Integer option = getIntInput();
        List<Workout> workouts;
        if (option == null)
        {
            clearScreen();
            System.out.println("Iveskite leidziama skaiciu");
            return;
        }

        try {
            workouts = workoutContext.getTrainerWorkoutsByTrainerId(option);

        } catch (SQLException e) {
            clearScreen();
            System.out.println("Ivyko klaida gaunant treniruotes");
            return;
        }

        for (Workout workout : workouts) {
            System.out.println(workout);
        }
    }

    private void registerNewWorkout()
    {
        Workout workout = getWorkout();


        try {
            workoutContext.makeWorkout(workout);
        } catch (SQLException e) {
            clearScreen();
            e.printStackTrace();
            System.out.println("Klaida registruojnt treniruote");
            return;
        }
        System.out.println("Treniruote uzregistruotas: " + workout);
    }

    private Workout getWorkout()
    {
        Workout workout = new Workout();

        do {
            System.out.print("Iveskite kliento id: ");
            workout.clientId = textScanner.nextInt();
            textScanner.nextLine();
        } while (workout.clientId < 1);

        do {
            System.out.print("Iveskite trenerio id: ");
            workout.trainerId = textScanner.nextInt();
            textScanner.nextLine();
        } while (workout.trainerId < 1);

        do {
            System.out.print("Iveskite pradzios data ir laika: ");
            String start = textScanner.nextLine();

            try {
                workout.start = LocalDateTime.parse(start);
            } catch (Exception e) {
                System.out.println("Nepavyko nuskaityti laiko. Bandykite dar karta");
            }
        } while (workout.start == null);

        do {
            System.out.print("Iveskite pabaigos data ir laika: ");
            String end = textScanner.nextLine();

            try {
                workout.end = LocalDateTime.parse(end);
            } catch (Exception e) {
                System.out.println("Nepavyko nuskaityti laiko. Bandykite dar karta");
            }
        } while (workout.start == null);

        return workout;

    }
}




