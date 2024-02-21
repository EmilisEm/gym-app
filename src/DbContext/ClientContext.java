package DbContext;

import Models.Client;
import java.sql.*;

public class ClientContext {
    Connection postGresConn = null;
    ResultSet result = null;
    PreparedStatement statement = null;

    public ClientContext(Connection posGresConn) throws SQLException {
        if (posGresConn == null) {
            System.out.println("We should never get here.");
            throw new SQLException();
        }
        postGresConn = posGresConn;
    }

    public Client getClientById(int id) throws SQLException {
        Client client;

        statement = postGresConn.prepareStatement("SELECT * FROM Klientas WHERE Id = ?");
        statement.setInt(1, id);

        result = statement.executeQuery();

        if (!result.next()) {
            return null;
        }
        client = parseClient(result);
        closeStatementAndResult(statement, result);

        return client;
    }

    public Client getClientByEmail(String email) throws SQLException {
        Client client;

        statement = postGresConn.prepareStatement("SELECT * FROM Klientas WHERE El_pastas = ?");
        statement.setString(1, email);

        result = statement.executeQuery();

        if (!result.next()) {
            throw new SQLException("No matches found in database");
        }

        client = parseClient(result);
        closeStatementAndResult(statement, result);

        return client;
    }

    public Client registerClient(Client client) throws SQLException {
        statement = postGresConn.prepareStatement(
                "INSERT INTO Klientas (Vardas, Pavarde, Gimimo_metai, El_pastas, Lytis) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, client.name);
        statement.setString(2, client.surname);
        statement.setDate(3, Date.valueOf(client.birthDate));
        statement.setString(4, client.email);
        statement.setString(5, client.gender);

        int insertCount = statement.executeUpdate();

        closeStatementAndResult(statement, result);

        return insertCount > 1 ? client : null;
    }

    public boolean updateClientById(Client client) throws SQLException {
        statement = postGresConn.prepareStatement(
                "UPDATE Klientas SET Vardas = ?, Pavarde = ?, Gimimo_metai = ?, El_pastas = ?, Lytis = ? where Id = ?");
        statement.setString(1, client.name);
        statement.setString(2, client.surname);
        statement.setDate(3, Date.valueOf(client.birthDate));
        statement.setString(4, client.email);
        statement.setString(5, client.gender);
        statement.setInt(6, client.id);

        int updatedRowsCount = statement.executeUpdate();
        closeStatementAndResult(statement, result);

        return updatedRowsCount > 0;
    }

    public boolean deleteClientById(Client client) throws SQLException {
        postGresConn.setAutoCommit(false);
        int deleteUsersCount = 0, deleteWorkoutCount = 0;
        try {
            statement = postGresConn.prepareStatement("DELETE FROM Klientas WHERE Id = ?");
            statement.setInt(1, client.id);
            deleteUsersCount = statement.executeUpdate();

            closeStatementAndResult(statement, null);

            statement = postGresConn.prepareStatement("DELETE FROM Treniruote WHERE Kliento_id = ?");
            statement.setInt(1, client.id);
            deleteWorkoutCount = statement.executeUpdate();
        } catch (SQLException e) {
            postGresConn.rollback();
            e.printStackTrace();
        } finally {
            postGresConn.setAutoCommit(true);
        }
        return deleteUsersCount > 0 && deleteWorkoutCount > 0;
    }

    private Client parseClient(ResultSet resultSet) throws SQLException {
        Client client = new Client();
        client.name = resultSet.getString("Vardas");
        client.surname = resultSet.getString("Pavarde");
        client.email = resultSet.getString("El_pastas");
        client.gender = resultSet.getString("Lytis");
        client.birthDate = resultSet.getDate("Gimimo_metai").toLocalDate();
        client.id = resultSet.getInt("Id");
        return client;
    }

    private void closeStatementAndResult(PreparedStatement statement, ResultSet result) throws SQLException {
        if (statement != null) {
            statement.close();
        }

        if (result != null) {
            result.close();
        }
    }

}
