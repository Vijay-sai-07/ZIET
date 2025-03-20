package database_handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DBReader extends DBExecutor{

    public DBReader(String configFilePath) {
        super(configFilePath);
    }

    public DBReader(String classPath, String url, String username, String password) {
        super(classPath, url, username, password);
    }

    public DBReader(Connection connection){
        super(connection);
    }

    public ResultSet read(String query) throws SQLException {
        DBLogger.logQuery(query, printStream);
        return connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(query);
    }
    public ResultSet prepareAndRead(String query, Object... params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        // Bind parameters
        for (int parameterIndex = 1; parameterIndex <= params.length; parameterIndex++) {
            Object arg = params[parameterIndex - 1];
            if (arg == null) {
                preparedStatement.setNull(parameterIndex, java.sql.Types.NULL);
            } else if (arg instanceof Integer) {
                preparedStatement.setInt(parameterIndex, (Integer) arg);
            } else if (arg instanceof Long) {
                preparedStatement.setLong(parameterIndex, (Long) arg);
            } else if (arg instanceof Short) {
                preparedStatement.setShort(parameterIndex, (Short) arg);
            } else if (arg instanceof Byte) {
                preparedStatement.setByte(parameterIndex, (Byte) arg);
            } else if (arg instanceof String) {
                preparedStatement.setString(parameterIndex, (String) arg);
            } else if (arg instanceof Double) {
                preparedStatement.setDouble(parameterIndex, (Double) arg);
            } else if (arg instanceof Float) {
                preparedStatement.setFloat(parameterIndex, (Float) arg);
            } else if (arg instanceof Boolean) {
                preparedStatement.setBoolean(parameterIndex, (Boolean) arg);
            } else if (arg instanceof java.math.BigDecimal) {
                preparedStatement.setBigDecimal(parameterIndex, (java.math.BigDecimal) arg);
            } else if (arg instanceof java.sql.Date) {
                preparedStatement.setDate(parameterIndex, (java.sql.Date) arg);
            } else if (arg instanceof java.sql.Time) {
                preparedStatement.setTime(parameterIndex, (java.sql.Time) arg);
            } else if (arg instanceof java.sql.Timestamp) {
                preparedStatement.setTimestamp(parameterIndex, (java.sql.Timestamp) arg);
            } else if (arg instanceof java.sql.Blob) {
                preparedStatement.setBlob(parameterIndex, (java.sql.Blob) arg);
            } else if (arg instanceof java.sql.Clob) {
                preparedStatement.setClob(parameterIndex, (java.sql.Clob) arg);
            } else if (arg instanceof byte[]) {
                preparedStatement.setBytes(parameterIndex, (byte[]) arg);
            } else {
                System.err.println("Unsupported parameter type: " + arg.getClass().getName());
            }
        }

        DBLogger.logQuery(preparedStatement, printStream);
        return preparedStatement.executeQuery();
    }
}
