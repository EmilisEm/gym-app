package DbContext;

import Models.Payment;
import Models.Visit;

import java.sql.*;
import java.util.ArrayList;

public class VisitContext {
    Connection postGresConn = null;
    ResultSet result = null;
    PreparedStatement statement = null;

    public VisitContext(Connection posGresConn) throws SQLException {
        if (posGresConn == null) {
            System.out.println("We should never get here.");
            throw new SQLException();
        }
        postGresConn = posGresConn;

    }

    public ArrayList<Visit> getClientVisitsByClientId(int id) throws SQLException
    {
        ArrayList<Visit> visits = new ArrayList<Visit>();

        statement = postGresConn.prepareStatement("SELECT * FROM Apsilankymas WHERE Naudotojo_id = ?");
        statement.setInt(1, id);

        result = statement.executeQuery();

        while (result.next())
        {
            visits.add(parseVisit(result));
        }

        closeStatementAndResult(statement, result);

        return visits;
    }

    public Visit makeVisit(Visit visit) throws SQLException
    {
        statement = postGresConn.prepareStatement("INSERT INTO Apsilankymas (Naudotojo_id, Nuo) VALUES (?, ?)");
        statement.setInt(1, visit.clientId);
        statement.setTimestamp(2, Timestamp.valueOf(visit.from));
        int insertCount = statement.executeUpdate();

        closeStatementAndResult(statement, result);

        return insertCount == 0 ? visit : null;
    }

    private Visit parseVisit(ResultSet resultSet) throws SQLException
    {
        Visit payment = new Visit();
        payment.clientId = resultSet.getInt("Naudotojo_id");
        payment.from = resultSet.getTimestamp("Nuo").toLocalDateTime();
        return payment;
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
