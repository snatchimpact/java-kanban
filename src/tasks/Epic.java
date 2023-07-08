package tasks;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

public class Epic extends Task{
    ArrayList<Integer> subtasksIDsList = new ArrayList<>();
    protected LocalTime endTime;

    public Epic(String title, String description, int id) {
        super(title, description, id);
        type = Type.EPIC;
    }
    public Epic(String title, String description, int id, Status status, Duration duration, LocalTime startTime){
        super(title, description, id, status, duration, startTime);
        type = Type.EPIC;
    }

    public Epic(String title, String description, int id, Status status, Duration duration, LocalTime startTime,
                LocalTime endTime){
        super(title, description, id, status, duration, startTime);
        this.endTime = endTime;
        type = Type.EPIC;
    }

    public void addSubtaskToSubtasksList(Subtask subtask){
        if(!subtasksIDsList.contains(subtask.getId())){
            subtasksIDsList.add(subtask.getId());
        }
    }

    public ArrayList<Integer> getSubtasksIDsList() {
        return subtasksIDsList;
    }

    public void clearSubtasksIDsList(){
        subtasksIDsList.clear();
    }

    public void removeSubtaskFromSubtasksIDsList(int subtaskID){
        subtasksIDsList.remove(subtaskID);
    }
    public void setEndTime(LocalTime endTime){
        this.endTime = endTime;
    }

    @Override
    public LocalTime getEndTime(){
        return endTime;
    }
    @Override
    public String toString(){
        return id + ","
                + type + ","
                + title + ","
                + status + ","
                + description + ","
                + duration +  ","
                + getStartTime() + ","
                + getEndTime() + "\n";
    }
}
