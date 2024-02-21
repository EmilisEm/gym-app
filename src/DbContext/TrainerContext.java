package DbContext;

import Models.Client;
import Models.Trainer;

import java.sql.*;

public class TrainerContext {
    Connection postGresConn = null;
    ResultSet result = null;
    PreparedStatement statement = null;

    public TrainerContext(Connection posGresConn) throws SQLException {
        if (posGresConn == null) {
            System.out.println("We should never get here.");
            throw new SQLException();
        }
        postGresConn = posGresConn;

    }
    public Trainer getTrainerById(int id) throws SQLException
    {
        Trainer trainer;

        statement = postGresConn.prepareStatement("SELECT * FROM Treneris WHERE Id = ?");
        statement.setInt(1, id);

        result = statement.executeQuery();

        if (!result.next()) {
            throw new SQLException("No matches found in database");
        }

        trainer = parseTrainer(result);
        closeStatementAndResult(statement, result);

        return trainer;
    }

    public Trainer getTrainerByEmail(String email) throws SQLException
    {
        Trainer trainer;

        statement = postGresConn.prepareStatement("SELECT * FROM Treneris WHERE El_pastas = ?");
        statement.setString(1, email);

        result = statement.executeQuery();

        if (!result.next()) {
            throw new SQLException("No matches found in database");
        }

        trainer = parseTrainer(result);
        closeStatementAndResult(statement, result);

        return trainer;
    }

    public Trainer registerTrainer(Trainer trainer) throws SQLException
    {
        statement = postGresConn.prepareStatement("INSERT INTO Treneris (Vardas, Pavarde, Gimimo_metai, El_pastas, Lytis, Darbo_pradzia) VALUES (?, ?, ?, ?, ?, ?)");
        statement.setString(1, trainer.name);
        statement.setString(2, trainer.surname);
        statement.setDate(3, Date.valueOf(trainer.birthDate));
        statement.setString(4, trainer.email);
        statement.setString(5, trainer.gender);
        statement.setDate(6, Date.valueOf(trainer.startOfWork));

        int insertCount = statement.executeUpdate();

        closeStatementAndResult(statement, result);

        return insertCount == 0 ? trainer : null;
    }

    public boolean updateTrainerById(Trainer trainer) throws SQLException
    {
        statement = postGresConn.prepareStatement("UPDATE Treneris SET Vardas = ?, Pavarde = ?, Gimimo_metai = ?, El_pastas = ?, Lytis = ?, Darbo_pradzia = ? where Id = ?");
        statement.setString(1, trainer.name);
        statement.setString(2, trainer.surname);
        statement.setDate(3, Date.valueOf(trainer.birthDate));
        statement.setString(4, trainer.email);
        statement.setString(5, trainer.gender);
        statement.setDate(6, Date.valueOf(trainer.startOfWork));
        statement.setInt(7, trainer.id);


        int updatedRowsCount = statement.executeUpdate();
        closeStatementAndResult(statement, result);

        return updatedRowsCount > 0;
    }

    public boolean deleteTrainerById(Trainer trainer) throws SQLException
    {
        statement = postGresConn.prepareStatement("DELETE FROM Treneris where Id = ?");
        statement.setInt(1, trainer.id);

        statement.executeUpdate();

        int deleteCount = statement.executeUpdate();
        return deleteCount >  0;
    }

    private Trainer parseTrainer(ResultSet resultSet) throws SQLException
    {
        Trainer trainer = new Trainer();
        trainer.name = resultSet.getString("Vardas");
        trainer.surname = resultSet.getString("Pavarde");
        trainer.email = resultSet.getString("El_pastas");
        trainer.gender = resultSet.getString("Lytis");
        trainer.birthDate = resultSet.getDate("Gimimo_metai").toLocalDate();
        trainer.startOfWork = resultSet.getDate("Darbo_pradzia").toLocalDate();
        trainer.id = resultSet.getInt("Id");
        return trainer;
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
