package observations;

import java.io.FileWriter;
import java.io.IOException;

public class ObsLogger {
    private static final String observationPath = "observations1";
    private static final String resultsPath = "results";
    private static final String promptsPath = "prompts";
    public static final String queriesPath = "queries";

    private static void log(String str, String path){
        try (FileWriter fileWriter = new FileWriter(path, true)) {
            fileWriter.append("\n").append(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void logQuery(String query) {
        log(query, queriesPath);
    }
    public static void logObservation(String observations) {
        log(observations, observationPath);
    }
    public static void logResults(String results){
        log(results, resultsPath);
    }
    public static void logPrompts(String prompt){
        log(prompt,promptsPath);
    }
}
