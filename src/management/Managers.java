package management;

public class Managers {
    public static HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    public static InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    public static TaskManager getDefault(){
        return inMemoryTaskManager;
    }

    public static HistoryManager getDefaultHistory(){
        return inMemoryHistoryManager;
    }
}
