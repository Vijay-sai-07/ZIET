package servlet;

import com.google.gson.Gson;
import data_reader.DBColumnWiseReader;
import data_reader.DBRowWiseReader;
import data_reader.TableMapper;
import database_handler.DBConnector;

public class ServletUtil {
    private static final DBConnector dbConnector = new DBConnector(DBConnector.publicConnection);
    protected static final DBRowWiseReader dbRowWiseReader = new DBRowWiseReader(dbConnector.getConnection());
    protected static final DBColumnWiseReader dbColumnWiseReader = new DBColumnWiseReader(dbConnector.getConnection());
    protected static final TableMapper tableMapper = new TableMapper(dbConnector.getConnection());
    protected static final Gson gson = new Gson();
}
