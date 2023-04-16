package tasks;

public class Subtask extends Task {
    protected int epicID;

    public Subtask(Epic epic, String title, String description, int id, Status status) {
        super(title, description, id, status);
        epicID = epic.getId();
        type = Type.SUBTASK;
    }

    public Subtask(int epicID, String title, String description, int id, Status status){
        super(title, description, id, status);
        this.epicID = epicID;
        type = Type.SUBTASK;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString(){
        return id + "," + type + "," + title + "," + status + "," + description + "," + epicID + "\n";
    }

}

