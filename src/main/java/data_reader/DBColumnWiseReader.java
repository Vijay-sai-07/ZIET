package data_reader;

import database_handler.DBReader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBColumnWiseReader extends DBReader{
    public DBColumnWiseReader(String configFilePath) {
        super(configFilePath);
    }
    public DBColumnWiseReader(String classPath, String url, String username, String password){
        super(classPath, url, username, password);
    }

    public DBColumnWiseReader(Connection connection){
        super(connection);
    }

    public ArrayList<ArrayList<?>> readColumns(String query) throws SQLException {
        return extractData(super.read(query));
    }

    public ArrayList<ArrayList<?>> prepareAndReadColumns(String query, Object... params) throws SQLException {
        return extractData(super.prepareAndRead(query,params));
    }

    public ArrayList<ArrayList<?>> extractData(ResultSet data) throws SQLException {
        ArrayList<ArrayList<?>> dataInArraylist = new ArrayList<>();
        ResultSetMetaData metaData = data.getMetaData();

        // Initialize ArrayLists for each column
        ArrayList<ArrayList<Object>> columns = new ArrayList<>();

        // Create an empty ArrayList for each column and add column names as the first element
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            ArrayList<Object> column = new ArrayList<>();
            column.add(metaData.getColumnName(i));  // Add the column name as the first element
            columns.add(column);
        }

        // Iterate through each row and add the values to respective columns
        while (data.next()) {
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                Object value = data.getObject(i);
                if (value == null) {
                    columns.get(i - 1).add("NULL");
                } else {
                    columns.get(i - 1).add(value);
                }
            }
        }

        // Add column-wise data to the result
        dataInArraylist.addAll(columns);
        return dataInArraylist;
    }

    public static StringBuilder table(ArrayList<ArrayList<?>> columns) {
        if (columns==null){
            return new StringBuilder("Empty Set");
        } else if (columns.get(0).size() < 2) {
            return new StringBuilder("Empty Set");
        }
        StringBuilder tableBuilder = new StringBuilder("\n");

        int[] columnWidths = getColumnWidths(columns);
        int rowCount = columns.get(0).size();

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            if (rowIndex == 0) {
                tableBuilder.append(drawRowSeparator(columnWidths));
            }
            tableBuilder.append(drawRow(columns, rowIndex, columnWidths));
            if (rowIndex == 0) {
                tableBuilder.append(drawRowSeparator(columnWidths));
            }
        }
        tableBuilder.append(drawRowSeparator(columnWidths));
        return tableBuilder;
    }

    private static int[] getColumnWidths(ArrayList<ArrayList<?>> columns) {
        int[] columnWidths = new int[columns.size()];

        for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
            for (Object cell : columns.get(colIndex)) {
                columnWidths[colIndex] = Math.max(columnWidths[colIndex], cell.toString().length());
            }
        }

        return columnWidths;
    }

    private static StringBuilder drawRow(ArrayList<ArrayList<?>> columns, int rowIndex, int[] columnWidths) {
        StringBuilder rowBuilder = new StringBuilder();

        for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
            Object cell = columns.get(colIndex).get(rowIndex);
            rowBuilder.append("| ").append(padRight(cell.toString(), columnWidths[colIndex])).append(" ");
        }

        rowBuilder.append("|\n");
        return rowBuilder;
    }

    private static StringBuilder drawRowSeparator(int[] columnWidths) {
        StringBuilder separatorBuilder = new StringBuilder();

        for (int width : columnWidths) {
            separatorBuilder.append("+").append(repeat("-", width + 2));
        }

        separatorBuilder.append("+\n");
        return separatorBuilder;
    }

    private static String repeat(String str, int times) {
        StringBuilder repeated = new StringBuilder();
        for (int i = 0; i < times; i++) {
            repeated.append(str);
        }
        return repeated.toString();
    }

    private static String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }
}
