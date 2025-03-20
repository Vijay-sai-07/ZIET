package food;

import com.Zeat.api.ApiConnection;
import observations.ObsLogger;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainFoodListener implements Runnable {
    private final ApiConnection apiConnection = new ApiConnection();
    private final FoodProcessor foodProcessor = new FoodProcessor();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final String[] queries = {
            "SELECT COUNT(*) FROM food_items WHERE mealId = 1",
            "SELECT COUNT(*) FROM food_items WHERE mealId = 2 OR mealId = 3 OR mealId = 4",
            "SELECT COUNT(*) FROM food_items WHERE mealId = 5"
    };


    @Override
    public void run() {
        LocalTime time = LocalTime.now().plusSeconds(2);
        scheduleDailyTask(time.getHour(), time.getMinute(), time.getSecond(), 1);
        time = time.plusSeconds(10);
        scheduleDailyTask(time.getHour(), time.getMinute(), time.getSecond(), 2);
        time = time.plusSeconds(10);
        scheduleDailyTask(time.getHour(), time.getMinute(), time.getSecond(), 3);
//        scheduleDailyTask(7, 30, 0, 1);  // 7:30 AM (Breakfast)
//        scheduleDailyTask(11, 30, 0, 2); // 11:30 AM (Lunch)
//        scheduleDailyTask(23, 30, 0, 3);  // 11:30 PM (Dinner)
    }

    private void scheduleDailyTask(int hour, int minute, int second, int taskNum) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = now.withHour(hour).withMinute(minute).withSecond(second);
        // If target time has passed, schedule for the next day
        if (targetTime.isBefore(now)) {
            targetTime = targetTime.plusDays(1);
        }
        long delay = now.until(targetTime, ChronoUnit.SECONDS);
        scheduler.schedule(() -> executeScheduledTask(taskNum), delay, TimeUnit.SECONDS);
    }

    private void executeScheduledTask(int taskNum) {
        long start = System.currentTimeMillis();
//        long count = countGetter(taskNum);
        apiConnection.run();
//        while (count<1) {
//            System.out.println("NO FOOD");
//            try {
//                Thread.sleep(30000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            count = countGetter(taskNum);
//        }
        foodProcessor.executeTask(taskNum);
        long duration = (System.currentTimeMillis()-start);
        ObsLogger.logObservation("Total Execution Time : "+duration+"ms/"+(duration/1000)+"s");
    }

//    private long countGetter(long taskNumber) {
//        long count = 0;
//        try {
//            count = (long) dbRowWiseReader.read(queries[(int) taskNumber - 1], false).getFirst(0).get(0);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return count;
//    }
}
