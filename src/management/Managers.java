package management;

public class Managers {
    public static HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    public TaskManager getDefault(){
        TaskManager manager = new InMemoryTaskManager();
        return manager;
    };

    public static HistoryManager getDefaultHistory(){
        return inMemoryHistoryManager;
    };
}
