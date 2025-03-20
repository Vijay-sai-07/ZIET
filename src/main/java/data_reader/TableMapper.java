package data_reader;

import database_handler.DBReader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableMapper extends DBReader {
    public TableMapper(String configFilePath) {
        super(configFilePath);
    }

    public TableMapper(String classPath, String url, String username, String password) {
        super(classPath, url, username, password);
    }

    public TableMapper(Connection connection) {
        super(connection);
    }

    public ArrayList<Map<String, ?>> readMap(String query) throws SQLException {
        return extractData(super.read(query));
    }

    public ArrayList<Map<String, ?>> prepareAndReadMap(String query, Object... params) throws SQLException {
        return extractData(super.prepareAndRead(query,params));
    }

    private ArrayList<Map<String, ?>> extractData(ResultSet data) throws SQLException {
        ResultSetMetaData metaData = data.getMetaData();
        ArrayList<Map<String, ?>> result = new ArrayList<>();
        ArrayList<String> columnNames = new ArrayList<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columnNames.add(metaData.getColumnLabel(i));
        }
        int fields = columnNames.size();
        while (data.next()) {
            LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
            for (int i = 0; i < fields; i++) {
                hashMap.put(columnNames.get(i), data.getObject(i + 1));
            }
            result.add(hashMap);
        }
        return result;
    }
}
