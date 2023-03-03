package tasks;

import java.util.ArrayList;

public class Epic extends Task{
    ArrayList<Integer> subtasksIDsList = new ArrayList<>();

    public Epic(String title, String description, int id) {
        super(title, description, id);
        taskType = TaskType.EPIC;
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

//    public Epic fromString(String value){
//        String[] epicFields = value.split(",");
//        try{
//            int importedEpicsID = Integer.parseInt(epicFields[0]);
//            return new Epic(epicFields[2], epicFields[4], importedEpicsID);
//        }
//        catch (NumberFormatException ex){
//            ex.printStackTrace();
//        }
//        System.out.println("Не удалось импортировать задачу!");
//        return null;
//    }
}
