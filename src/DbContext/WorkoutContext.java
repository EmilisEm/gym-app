package DbContext;

import Models.Visit;
import Models.Workout;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class WorkoutContext {
    Connection postGresConn = null;
    ResultSet result = null;
    PreparedStatement statement = null;

    public WorkoutContext(Connection posGresConn) throws SQLException {
        if (posGresConn == null) {
            System.out.println("We should never get here.");
            throw new SQLException();
        }

        postGresConn = posGresConn;

    }

    public ArrayList<Workout> getClientWorkoutsByClientId(int id) throws SQLException
    {
        ArrayList<Workout> workouts = new ArrayList<Workout>();

        statement = postGresConn.prepareStatement("SELECT * FROM Treniruote WHERE Kliento_id = ?");
        statement.setInt(1, id);

        result = statement.executeQuery();

        while (result.next())
        {
            workouts.add(parseWorkout(result));
        }

        closeStatementAndResult(statement, result);

        return workouts;
    }

    public ArrayList<Workout> getTrainerWorkoutsByTrainerId(int id) throws SQLException
    {
        ArrayList<Workout> workouts = new ArrayList<Workout>();

        statement = postGresConn.prepareStatement("SELECT * FROM Treniruote WHERE Trenerio_id = ?");
        statement.setInt(1, id);

        result = statement.executeQuery();

        while (result.next())
        {
            workouts.add(parseWorkout(result));
        }

        closeStatementAndResult(statement, result);

        return workouts;
    }

    public Workout makeWorkout(Workout workout) throws SQLException
    {
        statement = postGresConn.prepareStatement("INSERT INTO Treniruote (Kliento_id, Trenerio_id, Pradzia, Pabaiga) VALUES (?, ?, ?, ?)");
        statement.setInt(1, workout.clientId);
        statement.setInt(2, workout.trainerId);
        statement.setTimestamp(3, Timestamp.valueOf(workout.start));
        statement.setTimestamp(4, Timestamp.valueOf(workout.end));
        int insertCount = statement.executeUpdate();

        closeStatementAndResult(statement, result);

        return insertCount == 1 ? workout : null;
    }

    private Workout parseWorkout(ResultSet resultSet) throws SQLException
    {
        Workout workout = new Workout();
        workout.clientId = resultSet.getInt("Kliento_id");
        workout.trainerId = resultSet.getInt("Trenerio_id");
        workout.start = resultSet.getTimestamp("Pradzia").toLocalDateTime();
        workout.end = resultSet.getTimestamp("Pabaiga").toLocalDateTime();
        return workout;
    }

    private void closeStatementAndResult(PreparedStatement statement, ResultSet result) throws SQLException {
        if (statement != null)
        {
            statement.close();
        }

        if (result != null)
        {
            result.close();
        }
    }
}
