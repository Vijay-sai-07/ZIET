package data_reader;

import database_handler.DBReader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBRowWiseReader extends DBReader {
    public DBRowWiseReader(String configFilePath) {
        super(configFilePath);
    }

    public DBRowWiseReader(String classPath, String url, String username, String password) {
        super(classPath, url, username, password);
    }

    public DBRowWiseReader(Connection connection){
        super(connection);
    }

    public ArrayList<ArrayList<?>> readRowsWithColumnNames(String query) throws SQLException {
        ArrayList<ArrayList<?>> data = extractData(read(query));
        return extractData(read(query));
    }
    public ArrayList<ArrayList<?>> readRowsWithoutColumnNames(String query) throws SQLException {
        ArrayList<ArrayList<?>> data = extractData(read(query));
        return new ArrayList<>(data.subList(1, data.size()));
    }
    public ArrayList<ArrayList<?>> prepareAndReadRowsWithColumnNames(String query, Object... args) throws SQLException {
        ArrayList<ArrayList<?>> data = extractData(prepareAndRead(query, args));
        return extractData(prepareAndRead(query, args));
    }
    public ArrayList<ArrayList<?>> prepareAndReadRowsWithoutColumnNames(String query, Object... args) throws SQLException {
        ArrayList<ArrayList<?>> data = extractData(prepareAndRead(query, args));
        return new ArrayList<>(data.subList(1, data.size()));
    }
     public ArrayList<ArrayList<?>> extractData(ResultSet data) throws SQLException {
        ArrayList<ArrayList<?>> dataInArraylist = new ArrayList<>();
        ResultSetMetaData metaData = data.getMetaData();

        ArrayList<Object> headers = new ArrayList<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            headers.add(metaData.getColumnName(i));
        }
        dataInArraylist.add(headers);

        while (data.next()) {
            ArrayList<Object> row = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                Object value = data.getObject(i);
                if (value == null) {
                    row.add("NULL");
                } else {
                    row.add(value);
                }
            }
            dataInArraylist.add(row);
        }

        return dataInArraylist;
    }

}
