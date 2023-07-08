package tasks;

import java.time.Duration;
import java.time.LocalTime;

public class Subtask extends Task {
    protected int epicID;

    public Subtask(Epic epic, String title, String description, int id, Status status, Duration duration,
                   LocalTime localTime) {
        super(title, description, id, status, duration, localTime);
        epicID = epic.getId();
        type = Type.SUBTASK;
    }

    public Subtask(int epicID, String title, String description, int id, Status status, Duration duration,
                   LocalTime localTime){
        super(title, description, id, status, duration, localTime);
        this.epicID = epicID;
        type = Type.SUBTASK;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString(){
        return id + ","
                + type + ","
                + title + ","
                + status + ","
                + description + ","
                + duration + ","
                + getStartTime() + ","
                + getEndTime() + ","
                + epicID + "\n";
    }

}

