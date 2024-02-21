import DbContext.*;
import Interface.MenuCode;
import Interface.UserInterface;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Couldn't find driver class!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static Connection getConnection() {
        Connection postGresConn = null;
        try {
            postGresConn = DriverManager.getConnection("jdbc:postgresql://pgsql3.mif/studentu", "emkl9266", "Idijotas1");
        } catch (SQLException sqle) {
            System.out.println("Couldn't connect to database!");
            sqle.printStackTrace();
            return null;
        }
        System.out.println("Successfully connected to Postgres Database");

        return postGresConn;
    }

    public static void main(String[] args) {
        loadDriver();
        Connection con = getConnection();

        ClientContext clientContext;
        TrainerContext trainerContext;
        PaymentContext paymentContext;
        VisitContext visitContext;
        WorkoutContext workoutContext;

        if (null != con) {
            try {
                clientContext = new ClientContext(con);
                trainerContext = new TrainerContext(con);
                paymentContext = new PaymentContext(con);
                visitContext = new VisitContext(con);
                workoutContext = new WorkoutContext(con);

                UserInterface userInterface = new UserInterface(clientContext, paymentContext, trainerContext, visitContext, workoutContext);

                while (true)
                {
                    MenuCode mc = userInterface.getMenuCode();
                    switch (mc) {
                        case client -> userInterface.printClientMenu();
                        case workout -> userInterface.printWorkoutMenu();
                    }
                }

            } catch (SQLException e)
            {
                System.out.println("Kazkas ne taip ups");
            }
        }
        if (null != con) {
            try {
                con.close();
            } catch (SQLException exp) {
                System.out.println("Can not close connection!");
                exp.printStackTrace();
            }
        }
    }
}