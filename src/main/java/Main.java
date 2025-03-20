import com.Zeat.api.DBInsertions;
import com.Zeat.formData.FormData;
import food.MainFoodListener;

public class Main {
    public static void main(String[] args) {
        Thread t1 = new Thread(new FormData());
        Thread t2 = new Thread(new MainFoodListener());
        t1.start();
        t2.start();
    }
}