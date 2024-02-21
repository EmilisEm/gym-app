package DbContext;

import Models.Payment;
import Models.Trainer;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PaymentContext {
    Connection postGresConn = null;
    ResultSet result = null;
    PreparedStatement statement = null;

    public PaymentContext(Connection posGresConn) throws SQLException {
        if (posGresConn == null) {
            System.out.println("We should never get here.");
            throw new SQLException();
        }

        postGresConn = posGresConn;

    }

    public ArrayList<Payment> getClientPaymentsByClientId(int id) throws SQLException
    {
        ArrayList<Payment> payments = new ArrayList<Payment>();

        statement = postGresConn.prepareStatement("SELECT * FROM Mokejimas WHERE Kliento_id = ?");
        statement.setInt(1, id);

        result = statement.executeQuery();

        while (result.next())
        {
            payments.add(parsePayment(result));
        }

        closeStatementAndResult(statement, result);

        return payments;
    }

    public Payment makePayment(Payment payment) throws SQLException
    {
        statement = postGresConn.prepareStatement("INSERT INTO Treneris (Kliento_id, Nuo, Iki, Suma) VALUES (?, ?, ?, ?)");
        statement.setInt(1, payment.clientId);
        statement.setDate(2, Date.valueOf(payment.from));
        statement.setDate(3, Date.valueOf(payment.to));
        statement.setFloat(4, payment.payedAmount);
        int insertCount = statement.executeUpdate();

        closeStatementAndResult(statement, result);

        return insertCount == 0 ? payment : null;
    }

    private Payment parsePayment(ResultSet resultSet) throws SQLException
    {
        Payment payment = new Payment();
        payment.clientId = resultSet.getInt("Kliento_id");
        payment.payedAmount = resultSet.getFloat("Suma");
        payment.from = resultSet.getDate("Nuo").toLocalDate();
        payment.to = resultSet.getDate("Iki").toLocalDate();
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
