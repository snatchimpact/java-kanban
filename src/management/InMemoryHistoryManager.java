package management;

import tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    public static List<Task> viewingHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (viewingHistory.size() > 10) {
            viewingHistory.remove(0);
        }
        viewingHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return viewingHistory;
    }

}