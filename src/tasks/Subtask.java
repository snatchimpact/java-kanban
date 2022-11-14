package tasks;

public class Subtask extends Task {
    protected int epicID;

    public Subtask(Epic epic, String title, String description, int id, String status) {
        super(title, description, id, status);
        epicID = epic.getId();
    }

    public int getEpicID() {
        return epicID;
    }
}

