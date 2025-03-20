package database_handler;

import observations.ObsLogger;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.util.stream.IntStream;

public class DBLogger {
//    public static void logQuery(String query, PrintWriter printWriter){
//        if (printWriter==null) return;
//        printWriter.println(query);
//    }
//
//    public static void logQuery(PreparedStatement preparedStatement, PrintWriter printWriter) {
//        if (printWriter==null) return;
//        String[] array = preparedStatement.toString().split(" ");
//        StringBuilder stringBuilder = new StringBuilder();
//        IntStream.range(0, array.length)
//                .filter(i -> i != 0)
//                .mapToObj(i -> array[i]+" ")
//                .forEach(stringBuilder::append);
//        logQuery(stringBuilder.substring(0,stringBuilder.length()-1),printWriter);
//    }

    public static void logQuery(String query, PrintStream printStream){
        if (printStream==null) return;
        printStream.println(query);
        ObsLogger.logQuery(query);
    }

    public static void logQuery(PreparedStatement preparedStatement, PrintStream printStream) {
        if (printStream==null) return;
        String[] array = preparedStatement.toString().split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        IntStream.range(0, array.length)
                .filter(i -> i != 0)
                .mapToObj(i -> array[i]+" ")
                .forEach(stringBuilder::append);
        logQuery(stringBuilder.substring(0,stringBuilder.length()-1),printStream);
    }
}