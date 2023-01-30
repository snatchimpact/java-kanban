package management;

public class Managers {
    public static HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    public TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory(){
        return inMemoryHistoryManager;
    }
}
