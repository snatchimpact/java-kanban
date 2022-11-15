package tasks;

import java.util.ArrayList;

public class Epic extends Task{
    ArrayList<Integer> subtasksIDsList = new ArrayList<>();

    public Epic(String title, String description, int id) {
        super(title, description, id);
    }

    public void addSubtaskToSubtasksList(Subtask subtask){
        if(!subtasksIDsList.contains(subtask.getId())){
            subtasksIDsList.add(subtask.getId());
        }
    }

    public ArrayList<Integer> getSubtasksIDsList() {
        ArrayList<Integer> returnableSubtasksIDsList = subtasksIDsList;
        return returnableSubtasksIDsList;
    }

    public void clearSubtasksIDsList(){
        subtasksIDsList.clear();
    }

    public void removeSubtaskFromSubtasksIDsList(int subtaskID){
        subtasksIDsList.remove(subtaskID);
    }


    @Override
    public String toString() {
        return "Epic{" +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                "subtasksList=" + subtasksIDsList +
                ", status='" + status + '\'' +
                '}';
    }
}
