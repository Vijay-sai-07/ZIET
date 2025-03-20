package database_handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DBExecutor extends DBConnector{
    public DBExecutor(String configFilePath) {
        super(configFilePath);
    }
    public DBExecutor(String classPath, String url, String username, String password){
        super(classPath, url, username, password);
    }
    public DBExecutor(Connection connection){
        super(connection);
    }
    public void execute(String query) throws SQLException {
        DBLogger.logQuery(query, printStream);
        super.connection.createStatement().execute(query);
    }
    public void prepareAndExecute(String query, Object... args) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        for (int parameterIndex = 1; parameterIndex <= args.length; parameterIndex++) {
            Object arg = args[parameterIndex - 1];
            if (arg == null) {
                preparedStatement.setNull(parameterIndex, java.sql.Types.NULL);
            } else if (arg instanceof Integer) {
                preparedStatement.setInt(parameterIndex, (Integer) arg); // INT, INTEGER
            } else if (arg instanceof Long) {
                preparedStatement.setLong(parameterIndex, (Long) arg); // BIGINT
            } else if (arg instanceof Short) {
                preparedStatement.setShort(parameterIndex, (Short) arg); // SMALLINT
            } else if (arg instanceof Byte) {
                preparedStatement.setByte(parameterIndex, (Byte) arg); // TINYINT
            } else if (arg instanceof String) {
                preparedStatement.setString(parameterIndex, (String) arg); // CHAR, VARCHAR, TEXT
            } else if (arg instanceof Double) {
                preparedStatement.setDouble(parameterIndex, (Double) arg); // DOUBLE, FLOAT
            } else if (arg instanceof Float) {
                preparedStatement.setFloat(parameterIndex, (Float) arg); // REAL
            } else if (arg instanceof Boolean) {
                preparedStatement.setBoolean(parameterIndex, (Boolean) arg); // BOOLEAN, BIT
            } else if (arg instanceof java.math.BigDecimal) {
                preparedStatement.setBigDecimal(parameterIndex, (java.math.BigDecimal) arg); // DECIMAL, NUMERIC
            } else if (arg instanceof java.sql.Date) {
                preparedStatement.setDate(parameterIndex, (java.sql.Date) arg); // DATE
            } else if (arg instanceof java.sql.Time) {
                preparedStatement.setTime(parameterIndex, (java.sql.Time) arg); // TIME
            } else if (arg instanceof java.sql.Timestamp) {
                preparedStatement.setTimestamp(parameterIndex, (java.sql.Timestamp) arg); // TIMESTAMP, DATETIME
            } else if (arg instanceof java.sql.Blob) {
                preparedStatement.setBlob(parameterIndex, (java.sql.Blob) arg); // BLOB
            } else if (arg instanceof java.sql.Clob) {
                preparedStatement.setClob(parameterIndex, (java.sql.Clob) arg); // CLOB
            } else if (arg instanceof byte[]) {
                preparedStatement.setBytes(parameterIndex, (byte[]) arg); // BINARY, VARBINARY
            } else {
                System.err.println("Unsupported parameter type: " + arg.getClass().getName());
            }
        }
        DBLogger.logQuery(preparedStatement, printStream);
        preparedStatement.execute();
    }
}
